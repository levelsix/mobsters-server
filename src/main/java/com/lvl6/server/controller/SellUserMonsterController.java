package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.SellUserMonsterRequestEvent;
import com.lvl6.events.response.SellUserMonsterResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventMonsterProto.SellUserMonsterRequestProto;
import com.lvl6.proto.EventMonsterProto.SellUserMonsterResponseProto;
import com.lvl6.proto.EventMonsterProto.SellUserMonsterResponseProto.Builder;
import com.lvl6.proto.EventMonsterProto.SellUserMonsterResponseProto.SellUserMonsterStatus;
import com.lvl6.proto.MonsterStuffProto.MinimumUserMonsterSellProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.InsertUtils;
import com.lvl6.utils.utilmethods.StringUtils;

@Component
@DependsOn("gameServer")
public class SellUserMonsterController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	public SellUserMonsterController() {
		numAllocatedThreads = 4;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new SellUserMonsterRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_SELL_USER_MONSTER_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		SellUserMonsterRequestProto reqProto = ((SellUserMonsterRequestEvent) event)
				.getSellUserMonsterRequestProto();

		// get values sent from the client (the request proto)
		MinimumUserProto senderProto = reqProto.getSender();
		int userId = senderProto.getUserId();
		List<MinimumUserMonsterSellProto> userMonsters = reqProto.getSalesList();
		Map<Long, Integer> userMonsterIdsToCashAmounts = MonsterStuffUtils
				.convertToMonsterForUserIdToCashAmount(userMonsters);
		Set<Long> userMonsterIdsSet = userMonsterIdsToCashAmounts.keySet();
		List<Long> userMonsterIds = new ArrayList<Long>(userMonsterIdsSet);
		Date deleteDate = new Date();

		// set some values to send to the client (the response proto)
		SellUserMonsterResponseProto.Builder resBuilder = SellUserMonsterResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(SellUserMonsterStatus.FAIL_OTHER); // default

		server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
		try {
			int previousCash = 0;

			User aUser = RetrieveUtils.userRetrieveUtils().getUserById(userId);
			Map<Long, MonsterForUser> idsToUserMonsters = RetrieveUtils
					.monsterForUserRetrieveUtils().getSpecificOrAllUserMonstersForUser(userId,
							userMonsterIds);

			boolean legit = checkLegit(resBuilder, userId, aUser, userMonsterIds,
					idsToUserMonsters);

			boolean successful = false;
			if (legit) {
				previousCash = aUser.getCash();
				successful = writeChangesToDb(aUser, userMonsterIds,
						userMonsterIdsToCashAmounts);
			}

			if (successful) {
				resBuilder.setStatus(SellUserMonsterStatus.SUCCESS);
			}

			SellUserMonsterResponseEvent resEvent = new SellUserMonsterResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setSellUserMonsterResponseProto(resBuilder.build());
			server.writeEvent(resEvent);

			UpdateClientUserResponseEvent resEventUpdate = MiscMethods
					.createUpdateClientUserResponseEventAndUpdateLeaderboard(aUser);
			resEventUpdate.setTag(event.getTag());
			server.writeEvent(resEventUpdate);

			if (successful) {
				writeChangesToHistory(userId, userMonsterIds,
						userMonsterIdsToCashAmounts, idsToUserMonsters, deleteDate);
				// WRITE TO USER CURRENCY HISTORY
				writeToUserCurrencyHistory(aUser, previousCash, deleteDate,
						userMonsterIdsToCashAmounts, userMonsterIds);
			}
		} catch (Exception e) {
			log.error("exception in SellUserMonsterController processEvent", e);
			// don't let the client hang
			try {
				resBuilder.setStatus(SellUserMonsterStatus.FAIL_OTHER);
				SellUserMonsterResponseEvent resEvent = new SellUserMonsterResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setSellUserMonsterResponseProto(resBuilder.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in SellUserMonsterController processEvent", e);
			}
		} finally {
			server.unlockPlayer(senderProto.getUserId(), this.getClass()
					.getSimpleName());
		}
	}

	/*
	 * Return true if user request is valid; false otherwise and set the builder
	 * status to the appropriate value. "userMonsterIds" might be modified to
	 * contain only those user monsters that exist (and hence can be sold)
	 * 
	 * Example. client gives ids (a, b, c, d). Let's say 'a,' 'b,' and 'c' don't
	 * exist but 'd' does, so "userMonsterIds" will be modified to only contain
	 * 'd'
	 */
	private boolean checkLegit(Builder resBuilder, int userId, User u,
			List<Long> userMonsterIds, Map<Long, MonsterForUser> idsToUserMonsters) {

		if (null == u) {
			log.error("user is null. no user exists with id=" + userId + "");
			return false;
		}
		if (null == userMonsterIds || userMonsterIds.isEmpty()
				|| idsToUserMonsters.isEmpty()) {
			log.error("no user monsters exist. userMonsterIds=" + userMonsterIds
					+ "\t idsToUserMonsters=" + idsToUserMonsters);
			return false;
		}

		// can only sell the user monsters that exist
		if (userMonsterIds.size() != idsToUserMonsters.size()) {
			log.warn("not all monster_for_user_ids exist. userMonsterIds="
					+ userMonsterIds + "\t idsToUserMonsters=" + idsToUserMonsters
					+ "\t. Will continue processing");

			// retaining only the user monster ids that exist
			userMonsterIds.clear();
			userMonsterIds.addAll(idsToUserMonsters.keySet());
		}

		resBuilder.setStatus(SellUserMonsterStatus.SUCCESS);
		return true;
	}

	private boolean writeChangesToDb(User aUser, List<Long> userMonsterIds,
			Map<Long, Integer> userMonsterIdsToCashAmounts) {
		boolean success = true;

		// sum up the monies and give it to the user
		int sum = MiscMethods.sumMapValues(userMonsterIdsToCashAmounts);
		if (!aUser.updateRelativeCoinsNaive(sum)) {
			log.error("error updating user coins by " + sum + " not deleting "
					+ "userMonstersIdsToCashAmounts=" + userMonsterIdsToCashAmounts);
			return false;
		}

		// delete the user monsters;
		if (null != userMonsterIds && !userMonsterIds.isEmpty()) {
			int num = DeleteUtils.get().deleteMonstersForUser(userMonsterIds);
			log.info("num user monsters deleted: " + num + "\t ids deleted: "
					+ userMonsterIds);
		}
		return success;
	}

	// FOR USER MONSTER HISTORY PURPOSES
	private void writeChangesToHistory(int userId, List<Long> userMonsterIds,
			Map<Long, Integer> userMonsterIdsToCashAmounts,
			Map<Long, MonsterForUser> idsToUserMonsters, Date deleteDate) {

		if (null == userMonsterIds || userMonsterIds.isEmpty()) {
			return;
		}
		String delReason = ControllerConstants.MFUDR__SELL;
		List<String> deleteReasons = new ArrayList<String>();
		List<MonsterForUser> userMonstersList = new ArrayList<MonsterForUser>();

		for (int i = 0; i < userMonsterIds.size(); i++) {
			long userMonsterId = userMonsterIds.get(i);
			int amount = userMonsterIdsToCashAmounts.get(userMonsterId);
			MonsterForUser mfu = idsToUserMonsters.get(userMonsterId);
			userMonstersList.add(mfu);
			deleteReasons.add(delReason + amount);
		}
		//TODO: FIX THIS
//
//		int num = InsertUtils.get().insertIntoMonsterForUserDeleted(userId,
//				deleteReasons, userMonstersList, deleteDate);
//
//		log.info("user monsters that were deleted. userMonsterIds="
//				+ userMonsterIds + "\t idsToCashAmounts=" + userMonsterIdsToCashAmounts
//				+ "\t idsToUserMonsters=" + idsToUserMonsters + "\t numDeleted=" + num);
	}

	//TODO: FIX THIS
	// FOR CURRENCY HISTORY PURPOSES
	public void writeToUserCurrencyHistory(User aUser, int previousCash,
			Date aDate, Map<Long, Integer> userMonsterIdsToCashAmounts,
			List<Long> userMonsterIds) {

//		// figure how much user gets. At the moment, if 0 then nothing is recorded
//		// in db
//		int sum = MiscMethods.sumMapValues(userMonsterIdsToCashAmounts);
//		Timestamp date = new Timestamp(aDate.getTime());
//
//		Map<String, Integer> gemCashChange = new HashMap<String, Integer>();
//		Map<String, Integer> previousGemCash = new HashMap<String, Integer>();
//		Map<String, String> reasonsForChanges = new HashMap<String, String>();
//		String cash = MiscMethods.cash;
//
//		// record the user monster ids that contributed to changing user's currency
//		String reasonForChange = ControllerConstants.UCHRFC__SOLD_USER_MONSTERS
//				+ StringUtils.csvList(userMonsterIds);
//
//		gemCashChange.put(cash, sum);
//		previousGemCash.put(cash, previousCash);
//		reasonsForChanges.put(cash, reasonForChange);
//
//		MiscMethods.writeToUserCurrencyOneUserGemsAndOrCash(aUser, date,
//				gemCashChange, previousGemCash, reasonsForChanges);
	}

}
