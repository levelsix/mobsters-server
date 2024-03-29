package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.SellUserMonsterRequestEvent;
import com.lvl6.events.response.SellUserMonsterResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.MonsterDeleteHistory;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventMonsterProto.SellUserMonsterRequestProto;
import com.lvl6.proto.EventMonsterProto.SellUserMonsterResponseProto;
import com.lvl6.proto.EventMonsterProto.SellUserMonsterResponseProto.Builder;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.MonsterStuffProto.MinimumUserMonsterSellProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.proto.UserProto.MinimumUserProtoWithMaxResources;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.InsertUtils;

@Component

public class SellUserMonsterController extends EventController {

	private static Logger log = LoggerFactory.getLogger(SellUserMonsterController.class);

	@Autowired
	protected Locker locker;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtils;

	@Autowired
	protected MiscMethods miscMethods;

	@Autowired
	protected MonsterStuffUtils monsterStuffUtils;

	@Autowired
	protected MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils;

	public SellUserMonsterController() {

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
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		SellUserMonsterRequestProto reqProto = ((SellUserMonsterRequestEvent) event)
				.getSellUserMonsterRequestProto();

		// get values sent from the client (the request proto)
		MinimumUserProtoWithMaxResources senderResourcesProto = reqProto
				.getSender();
		MinimumUserProto senderProto = senderResourcesProto.getMinUserProto();
		String userId = senderProto.getUserUuid();
		List<MinimumUserMonsterSellProto> userMonsters = reqProto
				.getSalesList();
		Map<String, Integer> userMonsterIdsToCashAmounts = monsterStuffUtils
				.convertToMonsterForUserIdToCashAmount(userMonsters);
		Set<String> userMonsterIdsSet = userMonsterIdsToCashAmounts.keySet();
		List<String> userMonsterIds = new ArrayList<String>(userMonsterIdsSet);
		Date deleteDate = new Date();
		Timestamp deleteTime = new Timestamp(deleteDate.getTime());

		int maxCash = senderResourcesProto.getMaxCash();

		// set some values to send to the client (the response proto)
		SellUserMonsterResponseProto.Builder resBuilder = SellUserMonsterResponseProto
				.newBuilder();
		resBuilder.setSender(senderResourcesProto);
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER); // default

		UUID userUuid = null;
		UUID userMonsterUuid = null;
		boolean invalidUuids = true;
		try {
			userUuid = UUID.fromString(userId);

			if (userMonsterIds != null) {
				for (String userMonsterId : userMonsterIds) {
					userMonsterUuid = UUID.fromString(userMonsterId);
				}
			}

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
					"UUID error. incorrect userId=%s, userMonsterIds=%s",
					userId, userMonsterIds), e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			SellUserMonsterResponseEvent resEvent = new SellUserMonsterResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		boolean gotLock = false;
		try {
			gotLock = locker.lockPlayer(userUuid, this.getClass().getSimpleName());
			int previousCash = 0;

			User aUser = getUserRetrieveUtils().getUserById(userId);
			Map<String, MonsterForUser> idsToUserMonsters = getMonsterForUserRetrieveUtils()
					.getSpecificOrAllUnrestrictedUserMonstersForUser(userId,
							userMonsterIds);

			boolean legit = checkLegit(resBuilder, userId, aUser,
					userMonsterIds, idsToUserMonsters);

			Map<String, Integer> currencyChange = new HashMap<String, Integer>();
			boolean successful = false;
			if (legit) {
				previousCash = aUser.getCash();
				successful = writeChangesToDb(aUser, userMonsterIds,
						userMonsterIdsToCashAmounts, maxCash, currencyChange);
			}

			if (successful) {
				resBuilder.setStatus(ResponseStatus.SUCCESS);
			}

			SellUserMonsterResponseEvent resEvent = new SellUserMonsterResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);

			if (successful) {
				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = miscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								aUser, null, null);
				resEventUpdate.setTag(event.getTag());
				responses.normalResponseEvents().add(resEventUpdate);

				writeChangesToHistory(userId, userMonsterIds,
						userMonsterIdsToCashAmounts, idsToUserMonsters,
						deleteDate);
				// WRITE TO USER CURRENCY HISTORY
				writeToUserCurrencyHistory(userId, aUser, previousCash,
						deleteTime, userMonsterIdsToCashAmounts,
						userMonsterIds, currencyChange);
			}
		} catch (Exception e) {
			log.error("exception in SellUserMonsterController processEvent", e);
			// don't let the client hang
			try {
				resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
				SellUserMonsterResponseEvent resEvent = new SellUserMonsterResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in SellUserMonsterController processEvent",
						e);
			}
		} finally {
			if (gotLock) {
				locker.unlockPlayer(userUuid, this.getClass().getSimpleName());
			}
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
	private boolean checkLegit(Builder resBuilder, String userId, User u,
			List<String> userMonsterIds,
			Map<String, MonsterForUser> idsToUserMonsters) {

		if (null == u) {
			log.error("user is null. no user exists with id=" + userId + "");
			return false;
		}
		if (null == userMonsterIds || userMonsterIds.isEmpty()
				|| idsToUserMonsters.isEmpty()) {
			log.error("no user monsters exist. userMonsterIds="
					+ userMonsterIds + "\t idsToUserMonsters="
					+ idsToUserMonsters);
			return false;
		}

		// can only sell the user monsters that exist
		if (userMonsterIds.size() != idsToUserMonsters.size()) {
			log.warn("not all monster_for_user_ids exist. userMonsterIds="
					+ userMonsterIds + "\t idsToUserMonsters="
					+ idsToUserMonsters + "\t. Will continue processing");

			// retaining only the user monster ids that exist
			userMonsterIds.clear();
			userMonsterIds.addAll(idsToUserMonsters.keySet());
		}

		resBuilder.setStatus(ResponseStatus.SUCCESS);
		return true;
	}

	private boolean writeChangesToDb(User aUser, List<String> userMonsterIds,
			Map<String, Integer> userMonsterIdsToCashAmounts, int maxCash,
			Map<String, Integer> currencyChange) {
		boolean success = true;

		// sum up the monies and give it to the user
		int sum = miscMethods.sumMapValues(userMonsterIdsToCashAmounts);
		int curCash = Math.min(aUser.getCash(), maxCash); //in case user's cash is more than maxCash
		int maxCashUserCanGain = maxCash - curCash;
		sum = Math.min(sum, maxCashUserCanGain);

		//if user at max resources, user can still delete monster, but won't get any resources
		if (0 != sum) {
			if (!aUser.updateRelativeCashNaive(sum)) {
				log.error(String
						.format("error updating user coins by %s. not deleting userMonstersIdsToCashAmounts=%s",
								sum, userMonsterIdsToCashAmounts));
				return false;
			} else {
				currencyChange.put(miscMethods.cash, sum);
			}
		}

		// delete the user monsters;
		if (null != userMonsterIds && !userMonsterIds.isEmpty()) {
			int num = DeleteUtils.get().deleteMonstersForUser(userMonsterIds);
			log.info(String.format(
					"num user monsters deleted: %s, ids deleted: %s", num,
					userMonsterIds));
		}
		return success;
	}

	// FOR USER MONSTER HISTORY PURPOSES
	private void writeChangesToHistory(String userId,
			List<String> userMonsterIds,
			Map<String, Integer> userMonsterIdsToCashAmounts,
			Map<String, MonsterForUser> idsToUserMonsters, Date deleteDate) {

		List<MonsterDeleteHistory> monsterDeleteHistoryList = new ArrayList<MonsterDeleteHistory>();
		if (null == userMonsterIds || userMonsterIds.isEmpty()) {
			return;
		}

		String deletedReason = ControllerConstants.MFUDR__SELL;
		Timestamp deletedTime = new Timestamp(deleteDate.getTime());
		for (String userMonsterId : idsToUserMonsters.keySet()) {
			String details = "sold for: "
					+ userMonsterIdsToCashAmounts.get(userMonsterId);
			MonsterForUser mfu = idsToUserMonsters.get(userMonsterId);
			MonsterDeleteHistory mdh = new MonsterDeleteHistory(mfu,
					deletedReason, details, deletedTime);
			monsterDeleteHistoryList.add(mdh);
		}

		InsertUtils.get().insertMonsterDeleteHistory(monsterDeleteHistoryList);

		log.info("user monsters added to history table. userMonsterIds:"
				+ userMonsterIds);

	}

	// FOR CURRENCY HISTORY PURPOSES
	public void writeToUserCurrencyHistory(String userId, User aUser,
			int previousCash, Timestamp aDate,
			Map<String, Integer> userMonsterIdsToCashAmounts,
			List<String> userMonsterIds, Map<String, Integer> currencyChange) {

		if (currencyChange.isEmpty()) {
			return;
		}

		// record the user monster ids that contributed to changing user's currency
		StringBuilder detailsSb = new StringBuilder();
		for (String umId : userMonsterIdsToCashAmounts.keySet()) {
			Integer cash = userMonsterIdsToCashAmounts.get(umId);
			detailsSb.append("mfuId=");
			detailsSb.append(umId);
			detailsSb.append(", cash=");
			detailsSb.append(cash);
		}

		// figure how much user gets. At the moment, if 0 then nothing is recorded
		// in db

		Map<String, Integer> previousCurrency = new HashMap<String, Integer>();
		Map<String, Integer> currentCurrency = new HashMap<String, Integer>();
		Map<String, String> reasonsForChanges = new HashMap<String, String>();
		Map<String, String> detailsMap = new HashMap<String, String>();
		String reason = ControllerConstants.UCHRFC__SOLD_USER_MONSTERS;
		String cash = miscMethods.cash;

		previousCurrency.put(cash, previousCash);
		currentCurrency.put(cash, aUser.getCash());
		reasonsForChanges.put(cash, reason);
		detailsMap.put(cash, detailsSb.toString());

		miscMethods.writeToUserCurrencyOneUser(userId, aDate, currencyChange,
				previousCurrency, currentCurrency, reasonsForChanges,
				detailsMap);
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public UserRetrieveUtils2 getUserRetrieveUtils() {
		return userRetrieveUtils;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtils) {
		this.userRetrieveUtils = userRetrieveUtils;
	}

	public MonsterForUserRetrieveUtils2 getMonsterForUserRetrieveUtils() {
		return monsterForUserRetrieveUtils;
	}

	public void setMonsterForUserRetrieveUtils(
			MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils) {
		this.monsterForUserRetrieveUtils = monsterForUserRetrieveUtils;
	}

}
