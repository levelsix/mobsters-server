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
import com.lvl6.events.request.CompleteBattleItemRequestEvent;
import com.lvl6.events.response.AcceptOrRejectClanInviteResponseEvent;
import com.lvl6.events.response.AchievementProgressResponseEvent;
import com.lvl6.events.response.CompleteBattleItemResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.BattleItemForUser;
import com.lvl6.info.BattleItemQueueForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.BattleItemsProto.BattleItemQueueForUserProto;
import com.lvl6.proto.BattleItemsProto.UserBattleItemProto;
import com.lvl6.proto.EventBattleItemProto.CompleteBattleItemRequestProto;
import com.lvl6.proto.EventBattleItemProto.CompleteBattleItemResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.BattleItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.CompleteBattleItemAction;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.TimeUtils;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

@Component

public class CompleteBattleItemController extends EventController {

	private static Logger log = LoggerFactory.getLogger(CompleteBattleItemController.class);

	@Autowired
	protected Locker locker;

	@Autowired
	protected MiscMethods miscMethods;

	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;

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
	
	@Autowired
	protected TimeUtils timeUtils;

	public CompleteBattleItemController() {

	}

	@Override
	public RequestEvent createRequestEvent() {
		return new CompleteBattleItemRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_COMPLETE_BATTLE_ITEM_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		CompleteBattleItemRequestProto reqProto = ((CompleteBattleItemRequestEvent) event)
				.getCompleteBattleItemRequestProto();
		log.info("reqProto={}", reqProto);
		//get stuff client sent
		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		List<BattleItemQueueForUserProto> completedprotoList = reqProto
				.getBiqfuCompletedList();
		
		Date clientTime = new Date(reqProto.getClientTime());

		List<BattleItemQueueForUser> completedList = getIdsToBattleItemQueueForUserFromProto(
				completedprotoList, userId);

		boolean isSpeedUp = reqProto.getIsSpeedup();
		int gemsForSpeedUp = 0;

		if (isSpeedUp) {
			gemsForSpeedUp = reqProto.getGemsForSpeedup();
		}

		CompleteBattleItemResponseProto.Builder resBuilder = CompleteBattleItemResponseProto
				.newBuilder();
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
		resBuilder.setSender(senderProto);
		
		if(reqProto.getClientTime() == 0) {
			resBuilder.setStatus(ResponseStatus.FAIL_CLIENT_TIME_NOT_SENT);
			log.error("clientTime not sent");
			CompleteBattleItemResponseEvent resEvent = new CompleteBattleItemResponseEvent(senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			resEvent.setTag(event.getTag());
			responses.normalResponseEvents().add(resEvent);
			return;
		}
		
		if(timeUtils.numMinutesDifference(clientTime, new Date()) > 
		ControllerConstants.CLIENT_TIME_MINUTES_CONSTANT_CHECK) {
			resBuilder.setStatus(ResponseStatus.FAIL_TIME_OUT_OF_SYNC);
			log.error("time is out of sync > 2 hrs for userId {}", senderProto.getUserUuid());
			CompleteBattleItemResponseEvent resEvent = new CompleteBattleItemResponseEvent(senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			resEvent.setTag(event.getTag());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

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
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			CompleteBattleItemResponseEvent resEvent = new CompleteBattleItemResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		boolean gotLock = false;
		try {
			gotLock = locker.lockPlayer(userUuid, this.getClass().getSimpleName());

			CompleteBattleItemAction cbia = new CompleteBattleItemAction(
					userId, completedList, gemsForSpeedUp, userRetrieveUtil,
					battleItemForUserRetrieveUtil, insertUtil, deleteUtil,
					miscMethods);

			cbia.execute(resBuilder);

			CompleteBattleItemResponseEvent resEvent = new CompleteBattleItemResponseEvent(
					senderProto.getUserUuid());

			User user2 = cbia.getUser();

			if (ResponseStatus.SUCCESS.equals(resBuilder.getStatus())) {
				List<BattleItemForUser> bifuCompletedList = cbia
						.getBifuCompletedList();
				List<UserBattleItemProto> ubipCompletedList = createInfoProtoUtils
						.convertBattleItemForUserListToBattleItemForUserProtoList(bifuCompletedList);
				resBuilder.addAllUbiUpdated(ubipCompletedList);
			}

			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);

			if (ResponseStatus.SUCCESS.equals(resBuilder.getStatus())) {
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
			log.error("exception in CompleteBattleItemController processEvent",
					e);
			//don't let the client hang
			try {
				resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
				CompleteBattleItemResponseEvent resEvent = new CompleteBattleItemResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in CompleteBattleItemController processEvent",
						e);
			}
		} finally {
			if (gotLock) {
				locker.unlockPlayer(userUuid, this.getClass().getSimpleName());
			}
		}
	}

	private List<BattleItemQueueForUser> getIdsToBattleItemQueueForUserFromProto(
			List<BattleItemQueueForUserProto> protosList, String userId) {
		List<BattleItemQueueForUser> idsToBattleItemQueueForUser = null;
		if (protosList == null || protosList.isEmpty()) {
			//not an error
			//			log.error("CompleteBattleItem request did not send any battle item queue for user ids");
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
						"CompleteBattleItemRequest inconsistent userIds. expected={}, actual={}",
						userId, biqfuProto);
				return new ArrayList<BattleItemQueueForUser>();
			}
			biqfu.setBattleItemId(biqfuProto.getBattleItemId());
			biqfu.setExpectedStartTime(biqfu.getExpectedStartTime());
			biqfu.setPriority(biqfuProto.getPriority());
			idsToBattleItemQueueForUser.add(biqfu);
		}

		return idsToBattleItemQueueForUser;

	}

	private void writeToCurrencyHistory(String userId, Timestamp date,
			CompleteBattleItemAction cbia) {
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
