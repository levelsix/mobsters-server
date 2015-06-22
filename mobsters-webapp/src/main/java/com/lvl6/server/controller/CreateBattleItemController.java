package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.CreateBattleItemRequestEvent;
import com.lvl6.events.response.CreateBattleItemResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.BattleItemQueueForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.proto.BattleItemsProto.BattleItemQueueForUserProto;
import com.lvl6.proto.EventBattleItemProto.CreateBattleItemRequestProto;
import com.lvl6.proto.EventBattleItemProto.CreateBattleItemResponseProto;
import com.lvl6.proto.EventBattleItemProto.CreateBattleItemResponseProto.CreateBattleItemStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.proto.UserProto.MinimumUserProtoWithMaxResources;
import com.lvl6.retrieveutils.BattleItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.CreateBattleItemAction;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

@Component

public class CreateBattleItemController extends EventController {

	private static Logger log = LoggerFactory.getLogger(CreateBattleItemController.class);

	@Autowired
	protected Locker locker;
	
	@Autowired
	protected MiscMethods miscMethods;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	protected BattleItemForUserRetrieveUtil battleItemForUserRetrieveUtil;

	@Autowired
	protected InsertUtil insertUtil;

	@Autowired
	protected UpdateUtil updateUtil;

	@Autowired
	protected DeleteUtil deleteUtil;

	public CreateBattleItemController() {
		
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new CreateBattleItemRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_CREATE_BATTLE_ITEM_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		CreateBattleItemRequestProto reqProto = ((CreateBattleItemRequestEvent) event)
				.getCreateBattleItemRequestProto();
		log.info("reqProto={}", reqProto);
		//get stuff client sent
		MinimumUserProtoWithMaxResources senderMaxResourcesProto = reqProto
				.getSender();
		MinimumUserProto senderProto = senderMaxResourcesProto
				.getMinUserProto();
		String userId = senderProto.getUserUuid();

		int maxCash = senderMaxResourcesProto.getMaxCash();
		int maxOil = senderMaxResourcesProto.getMaxOil();

		//the new items added to queue, updated refers to those finished as well as 
		//priorities changing, deleted refers to those removed from queue and completed

		List<BattleItemQueueForUserProto> deletedBattleItemQueueList = reqProto
				.getBiqfuDeleteList();
		List<BattleItemQueueForUserProto> updatedBattleItemQueueList = reqProto
				.getBiqfuUpdateList();
		List<BattleItemQueueForUserProto> newBattleItemQueueList = reqProto
				.getBiqfuNewList();

		List<BattleItemQueueForUser> deletedList = getIdsToBattleItemQueueForUserFromProto(
				deletedBattleItemQueueList, userId);
		List<BattleItemQueueForUser> updatedList = getIdsToBattleItemQueueForUserFromProto(
				updatedBattleItemQueueList, userId);
		List<BattleItemQueueForUser> newList = getIdsToBattleItemQueueForUserFromProto(
				newBattleItemQueueList, userId);

		int cashChange = reqProto.getCashChange();
		int oilChange = reqProto.getOilChange();
		int gemCostForCreating = reqProto.getGemCostForCreating();

		CreateBattleItemResponseProto.Builder resBuilder = CreateBattleItemResponseProto
				.newBuilder();
		resBuilder.setStatus(CreateBattleItemStatus.FAIL_OTHER);
		resBuilder.setSender(senderProto);

		UUID userUuid = null;
		boolean invalidUuids = true;
		try {
			userUuid = UUID.fromString(userId);

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String
					.format("UUID error. incorrect userId=%s, ", userId));
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(CreateBattleItemStatus.FAIL_OTHER);
			CreateBattleItemResponseEvent resEvent = new CreateBattleItemResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		locker.lockPlayer(userUuid, this.getClass().getSimpleName());
		try {

			CreateBattleItemAction cbia = new CreateBattleItemAction(userId,
					deletedList, updatedList, newList, cashChange, maxCash,
					oilChange, maxOil, gemCostForCreating, userRetrieveUtil,
					battleItemForUserRetrieveUtil, insertUtil, updateUtil,
					deleteUtil, miscMethods);

			cbia.execute(resBuilder);

			CreateBattleItemResponseEvent resEvent = new CreateBattleItemResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);

			if (CreateBattleItemStatus.SUCCESS.equals(resBuilder.getStatus())) {
				User user2 = cbia.getUser();
				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = miscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								user2, null, null);
				resEventUpdate.setTag(event.getTag());
				responses.normalResponseEvents().add(resEventUpdate);

				Date d = new Date();
				Timestamp ts = new Timestamp(d.getTime());
				writeToCurrencyHistory(userId, ts, cbia);
			}
		} catch (Exception e) {
			log.error("exception in CreateBattleItemController processEvent", e);
			//don't let the client hang
			try {
				resBuilder.setStatus(CreateBattleItemStatus.FAIL_OTHER);
				CreateBattleItemResponseEvent resEvent = new CreateBattleItemResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in CreateBattleItemController processEvent",
						e);
			}
		} finally {
			locker.unlockPlayer(userUuid, this.getClass().getSimpleName());
		}
	}

	private List<BattleItemQueueForUser> getIdsToBattleItemQueueForUserFromProto(
			List<BattleItemQueueForUserProto> protosList, String userId) {
		List<BattleItemQueueForUser> idsToBattleItemQueueForUser = null;
		if (protosList == null || protosList.isEmpty()) {
			//not an error
			//			log.error("CreateBattleItem request did not send any battle item queue for user ids");
			return idsToBattleItemQueueForUser;
		}

		idsToBattleItemQueueForUser = new ArrayList<BattleItemQueueForUser>();

		for (BattleItemQueueForUserProto biqfuProto : protosList) {
			BattleItemQueueForUser biqfu = new BattleItemQueueForUser();
			biqfu.setPriority(biqfuProto.getPriority());

			String protoUserId = biqfuProto.getUserUuid();
			if (protoUserId.equals(userId)) {
				biqfu.setUserId(protoUserId);
			} else {
				log.error(
						"CreateBattleItemRequest inconsistent userIds. expected={}, actual={}",
						userId, biqfuProto);
				return new ArrayList<BattleItemQueueForUser>();
			}
			biqfu.setBattleItemId(biqfuProto.getBattleItemId());
			biqfu.setExpectedStartTime(new Timestamp(biqfuProto
					.getExpectedStartTime()));
			biqfu.setPriority(biqfuProto.getPriority());
			biqfu.setElapsedTime(biqfuProto.getElapsedTime());
			idsToBattleItemQueueForUser.add(biqfu);
		}

		return idsToBattleItemQueueForUser;

	}

	private void writeToCurrencyHistory(String userId, Timestamp date,
			CreateBattleItemAction cbia) {
		miscMethods.writeToUserCurrencyOneUser(userId, date,
				cbia.getCurrencyDeltas(), cbia.getPreviousCurrencies(),
				cbia.getCurrentCurrencies(), cbia.getReasons(),
				cbia.getDetails());
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public UserRetrieveUtils2 getUserRetrieveUtil() {
		return userRetrieveUtil;
	}

	public void setUserRetrieveUtil(UserRetrieveUtils2 userRetrieveUtil) {
		this.userRetrieveUtil = userRetrieveUtil;
	}

	public BattleItemForUserRetrieveUtil getBattleItemForUserRetrieveUtil() {
		return battleItemForUserRetrieveUtil;
	}

	public void setBattleItemForUserRetrieveUtil(
			BattleItemForUserRetrieveUtil battleItemForUserRetrieveUtil) {
		this.battleItemForUserRetrieveUtil = battleItemForUserRetrieveUtil;
	}

	public InsertUtil getInsertUtil() {
		return insertUtil;
	}

	public void setInsertUtil(InsertUtil insertUtil) {
		this.insertUtil = insertUtil;
	}

	public UpdateUtil getUpdateUtil() {
		return updateUtil;
	}

	public void setUpdateUtil(UpdateUtil updateUtil) {
		this.updateUtil = updateUtil;
	}

	public DeleteUtil getDeleteUtil() {
		return deleteUtil;
	}

	public void setDeleteUtil(DeleteUtil deleteUtil) {
		this.deleteUtil = deleteUtil;
	}

}
