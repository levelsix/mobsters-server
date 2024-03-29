package com.lvl6.server.controller;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.DevRequestEvent;
import com.lvl6.events.response.DevResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.ItemForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.Globals;
import com.lvl6.proto.DevProto.DevRequest;
import com.lvl6.proto.EventDevProto.DevRequestProto;
import com.lvl6.proto.EventDevProto.DevResponseProto;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.ItemsProto.UserItemProto;
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.rarechange.MonsterLevelInfoRetrieveUtils;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.InsertUtils;

@Component

public class DevController extends EventController {

	
	private static final Logger log = LoggerFactory.getLogger(DevController.class);
	
	
	public DevController() {
		
	}

	@Autowired
	protected MiscMethods miscMethods;

	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;

	@Autowired
	protected ItemForUserRetrieveUtil itemForUserRetrieveUtil;

	@Autowired
	protected MonsterStuffUtils monsterStuffUtils;

	@Autowired
	protected MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils;

	@Override
	public RequestEvent createRequestEvent() {
		return new DevRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_DEV_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		DevRequestProto reqProto = ((DevRequestEvent) event)
				.getDevRequestProto();
		log.info("reqProto={}", reqProto);

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		DevRequest request = reqProto.getDevRequest();
		int staticDataId = reqProto.getStaticDataId();
		int quantity = reqProto.getQuantity();

		DevResponseProto.Builder resBuilder = DevResponseProto.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(ResponseStatus.SUCCESS);

		boolean invalidUuids = true;

		try {
			UUID.fromString(userId);
			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format("UUID error. incorrect userId=%s", userId),
					e);
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			DevResponseEvent resEvent = new DevResponseEvent(userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		//    locker.lockPlayer(UUID.fromString(senderProto.getUserUuid()), this.getClass().getSimpleName());
		try {
			User aUser = RetrieveUtils.userRetrieveUtils().getUserById(
					senderProto.getUserUuid());
			//TODO: Consider writing currency history and other history

			log.info("CHEATER DETECTED!!!! {}", aUser);

			if (DevRequest.RESET_ACCOUNT.equals(request)) {
				log.info("resetting user={}", aUser);
				aUser.updateResetAccount();
			} else if (Globals.ALLOW_CHEATS()) {
				cheat(userId, request, staticDataId, quantity, resBuilder,
						aUser);
			} else {
				log.error("azzhole tried cheating: user={}",
						aUser);
				resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			}

			DevResponseProto resProto = resBuilder.build();
			DevResponseEvent resEvent = new DevResponseEvent(
					senderProto.getUserUuid());
			resEvent.setResponseProto(resProto);
			resEvent.setTag(event.getTag());
			responses.normalResponseEvents().add(resEvent);

			UpdateClientUserResponseEvent resEventUpdate = miscMethods
					.createUpdateClientUserResponseEventAndUpdateLeaderboard(
							aUser, null, null);
			resEventUpdate.setTag(event.getTag());
			responses.normalResponseEvents().add(resEventUpdate);

		} catch (Exception e) {
			log.error("exception in DevController processEvent", e);
			try {
				resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
				DevResponseEvent resEvent = new DevResponseEvent(userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in DevController processEvent", e);
			}

		} finally {
			//      locker.unlockPlayer(UUID.fromString(senderProto.getUserUuid()), this.getClass().getSimpleName()); 
			//      server.unlockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName());
		}
	}

	private void cheat(String userId, DevRequest request, int staticDataId,
			int quantity, DevResponseProto.Builder resBuilder, User aUser) {
		switch (request) {
		case RESET_ACCOUNT:
			//				log.info(String.format(
			//					"resetting user=%s", aUser));
			//				aUser.updateResetAccount();
			break;

		case GET_MONZTER:
			log.info("giving user={} monsterId={}, quantity={}",
					new Object[] { aUser, staticDataId, quantity });
			//				Monster monzter = MonsterRetrieveUtils.getMonsterForMonsterId(num);
			//				Map<Integer, Integer> monsterIdToNumPieces = new HashMap<Integer, Integer>();
			//				monsterIdToNumPieces.put(num, monzter.getNumPuzzlePieces());

			Map<Integer, Map<Integer, Integer>> monsterIdToLvlToQuantity = new HashMap<Integer, Map<Integer, Integer>>();
			monsterIdToLvlToQuantity.put(staticDataId,
					Collections.singletonMap(1, quantity));

			String mfusop = "cheater, cheater, pumpkin eater";
			List<FullUserMonsterProto> reward = monsterStuffUtils
					.updateUserMonsters(userId, null, monsterIdToLvlToQuantity,
							mfusop, new Date(), monsterLevelInfoRetrieveUtils);
			resBuilder.addAllFump(reward);
			break;

		case F_B_GET_CASH:
			log.info("giving user={} cash={}",
					aUser, staticDataId);
			aUser.updateRelativeCashAndOilAndGems(quantity, 0, 0, 0);
			break;

		case F_B_GET_OIL:
			log.info("giving user={} oil={}", aUser, staticDataId);
			aUser.updateRelativeCashAndOilAndGems(0, quantity, 0, 0);
			break;

		case F_B_GET_GEMS:
			log.info("giving user={} gems={}",
					aUser, staticDataId);
			aUser.updateRelativeCashAndOilAndGems(0, 0, quantity, 0);
			break;

		case F_B_GET_CASH_OIL_GEMS:
			log.info("giving user={} cash, gems, oil={}", aUser,
					staticDataId);
			aUser.updateRelativeCashAndOilAndGems(quantity, quantity, quantity, 0);
			break;
		case GET_ITEM:
			log.info("giving user={}, itemId={}, quantity={}",
					new Object[] { aUser, staticDataId, quantity });

			int numInserted = InsertUtils.get().insertIntoUpdateUserItem(
					userId, staticDataId, quantity);
			log.info("num rows inserted/updated,{}", numInserted);

			ItemForUser ifu = (itemForUserRetrieveUtil
					.getSpecificOrAllItemForUser(userId,
							Collections.singleton(staticDataId))).get(0);
			UserItemProto uip = createInfoProtoUtils
					.createUserItemProtoFromUserItem(ifu);
			resBuilder.setUip(uip);
			break;
		case GET_GACHA_CREDITS:
			log.info("giving user={} gacha_credits={}",
					aUser, staticDataId);
			aUser.updateRelativeCashAndOilAndGems(0, 0, 0, quantity);
			break;
		default:
			break;
		}
	}

	public ItemForUserRetrieveUtil getItemForUserRetrieveUtil() {
		return itemForUserRetrieveUtil;
	}

	public void setItemForUserRetrieveUtil(
			ItemForUserRetrieveUtil itemForUserRetrieveUtil) {
		this.itemForUserRetrieveUtil = itemForUserRetrieveUtil;
	}

}
