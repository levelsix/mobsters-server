package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.RefreshMiniJobRequestEvent;
import com.lvl6.events.response.AchievementProgressResponseEvent;
import com.lvl6.events.response.RefreshMiniJobResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.MiniJob;
import com.lvl6.info.MiniJobForUser;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventMiniJobProto.RefreshMiniJobRequestProto;
import com.lvl6.proto.EventMiniJobProto.RefreshMiniJobResponseProto;
import com.lvl6.proto.MiniJobConfigProto.UserMiniJobProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.SharedEnumConfigProto.Quality;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.MiniJobForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.ItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MiniJobRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.RewardRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.RefreshMiniJobAction;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.TimeUtils;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.StringUtils;
import com.lvl6.utils.utilmethods.UpdateUtil;

@Component
public class RefreshMiniJobController extends EventController {

	private static Logger log = LoggerFactory.getLogger(RefreshMiniJobController.class);

	@Autowired
	protected Locker locker;

	@Autowired
	protected MiniJobForUserRetrieveUtil miniJobForUserRetrieveUtil;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	protected ItemForUserRetrieveUtil itemForUserRetrieveUtil;

	@Autowired
	protected ItemRetrieveUtils itemRetrieveUtil;

	@Autowired
	protected RewardRetrieveUtils rewardRetrieveUtil;

	@Autowired
	protected MiniJobRetrieveUtils miniJobRetrieveUtil;

	@Autowired
	protected UpdateUtil updateUtil;

	@Autowired
	protected InsertUtil insertUtil;

	@Autowired
	protected DeleteUtil deleteUtil;
	
	@Autowired
	protected TimeUtils timeUtils;
	
	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;

	public RefreshMiniJobController() {
		
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new RefreshMiniJobRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_REFRESH_MINI_JOB_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		RefreshMiniJobRequestProto reqProto = ((RefreshMiniJobRequestEvent) event)
				.getRefreshMiniJobRequestProto();
		log.info("reqProto={}", reqProto);

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		List<String> deleteUserMiniJobIds = reqProto.getDeleteUserMiniJobIdsList();

		int itemId = reqProto.getItemId();
		int numToSpawn = reqProto.getNumToSpawn();

		int gemsSpent = reqProto.getGemsSpent();
		Quality minQualitySpawned = reqProto.getMinQualitySpawned();
		Date now = new Date(reqProto.getClientTime());
		Timestamp clientTime = new Timestamp(reqProto.getClientTime());
		int structId = reqProto.getStructId();

		RefreshMiniJobResponseProto.Builder resBuilder = RefreshMiniJobResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
		
		if(reqProto.getClientTime() == 0) {
			resBuilder.setStatus(ResponseStatus.FAIL_CLIENT_TIME_NOT_SENT);
			log.error("clientTime not sent");
			RefreshMiniJobResponseEvent resEvent = new RefreshMiniJobResponseEvent(senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}
		
		if(timeUtils.numMinutesDifference(new Date(reqProto.getClientTime()), new Date()) > 
		ControllerConstants.CLIENT_TIME_MINUTES_CONSTANT_CHECK) {
			resBuilder.setStatus(ResponseStatus.FAIL_TIME_OUT_OF_SYNC);
			log.error("time is out of sync > 2 hrs for userId {}", senderProto.getUserUuid());
			RefreshMiniJobResponseEvent resEvent = 
					new RefreshMiniJobResponseEvent(senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		UUID userUuid = null;
		boolean invalidUuids = true;
		try {
			userUuid = UUID.fromString(userId);

			if (null != deleteUserMiniJobIds && !deleteUserMiniJobIds.isEmpty()) {
				StringUtils.convertToUUID(deleteUserMiniJobIds);
			}

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
					"UUID error. incorrect userId=%s,\t userMiniJobIds=%s",
					userId, deleteUserMiniJobIds), e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			RefreshMiniJobResponseEvent resEvent = new RefreshMiniJobResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		locker.lockPlayer(userUuid, this.getClass().getSimpleName());
		try {

			String minQualityToSpawn = null;
			if (!minQualitySpawned.equals(Quality.NO_QUALITY)) {
				minQualityToSpawn = minQualitySpawned.name();
			}

			RefreshMiniJobAction rmja = new RefreshMiniJobAction(userId,
					deleteUserMiniJobIds, itemId, numToSpawn, gemsSpent,
					minQualityToSpawn, now, structId,
					ControllerConstants.RAND, itemForUserRetrieveUtil,
					itemRetrieveUtil, userRetrieveUtil, miniJobRetrieveUtil,
					updateUtil, insertUtil, deleteUtil);

			rmja.execute(resBuilder);

			if (ResponseStatus.SUCCESS.equals(resBuilder.getStatus()))
			{
				List<MiniJobForUser> userMiniJobs = rmja.getUserMiniJobs();
				Map<Integer, MiniJob> mjIdToMj = rmja.getMiniJobIdToMj();
				List<UserMiniJobProto> userMiniJobProtos = createInfoProtoUtils
						.createUserMiniJobProtos(userMiniJobs, mjIdToMj,
								rewardRetrieveUtil);
				resBuilder.addAllMiniJobs(userMiniJobProtos);
			}

			RefreshMiniJobResponseEvent resEvent = new RefreshMiniJobResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);

			if (ResponseStatus.SUCCESS.equals(resBuilder.getStatus()) &&
					rmja.isUsedGems())
			{
				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = miscMethods()
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								rmja.getUser(), null, null);
				resEventUpdate.setTag(event.getTag());
				responses.normalResponseEvents().add(resEventUpdate);

				writeToUserCurrencyHistory(userId, clientTime, rmja);
			}

		} catch (Exception e) {
			log.error("exception in RefreshMiniJobController processEvent", e);
			//don't let the client hang
			try {
				resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
				RefreshMiniJobResponseEvent resEvent = new RefreshMiniJobResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in RefreshMiniJobController processEvent",
						e);
			}
		} finally {
			locker.unlockPlayer(userUuid, this.getClass().getSimpleName());
		}
	}


	private void writeToUserCurrencyHistory(String userId,
			Timestamp curTime, RefreshMiniJobAction rmja)
	{
		miscMethods().writeToUserCurrencyOneUser(userId, curTime,
				rmja.getCurrencyDeltas(), rmja.getPreviousCurrencies(),
				rmja.getCurrentCurrencies(), rmja.getReasons(),
				rmja.getDetails());
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}



	public MiniJobForUserRetrieveUtil getMiniJobForUserRetrieveUtil() {
		return miniJobForUserRetrieveUtil;
	}

	public void setMiniJobForUserRetrieveUtil(
			MiniJobForUserRetrieveUtil miniJobForUserRetrieveUtil) {
		this.miniJobForUserRetrieveUtil = miniJobForUserRetrieveUtil;
	}

	public UserRetrieveUtils2 getUserRetrieveUtil() {
		return userRetrieveUtil;
	}

	public void setUserRetrieveUtil(UserRetrieveUtils2 userRetrieveUtil) {
		this.userRetrieveUtil = userRetrieveUtil;
	}

	public ItemForUserRetrieveUtil getItemForUserRetrieveUtil() {
		return itemForUserRetrieveUtil;
	}

	public void setItemForUserRetrieveUtil(
			ItemForUserRetrieveUtil itemForUserRetrieveUtil) {
		this.itemForUserRetrieveUtil = itemForUserRetrieveUtil;
	}

	public ItemRetrieveUtils getItemRetrieveUtil() {
		return itemRetrieveUtil;
	}

	public void setItemRetrieveUtil(ItemRetrieveUtils itemRetrieveUtil) {
		this.itemRetrieveUtil = itemRetrieveUtil;
	}

	public MiniJobRetrieveUtils getMiniJobRetrieveUtil() {
		return miniJobRetrieveUtil;
	}

	public void setMiniJobRetrieveUtil(MiniJobRetrieveUtils miniJobRetrieveUtil) {
		this.miniJobRetrieveUtil = miniJobRetrieveUtil;
	}

	public UpdateUtil getUpdateUtil() {
		return updateUtil;
	}

	public void setUpdateUtil(UpdateUtil updateUtil) {
		this.updateUtil = updateUtil;
	}

	public InsertUtil getInsertUtil() {
		return insertUtil;
	}

	public void setInsertUtil(InsertUtil insertUtil) {
		this.insertUtil = insertUtil;
	}

	public DeleteUtil getDeleteUtil() {
		return deleteUtil;
	}

	public void setDeleteUtil(DeleteUtil deleteUtil) {
		this.deleteUtil = deleteUtil;
	}

	public CreateInfoProtoUtils getCreateInfoProtoUtils() {
		return createInfoProtoUtils;
	}

	public void setCreateInfoProtoUtils(CreateInfoProtoUtils createInfoProtoUtils) {
		this.createInfoProtoUtils = createInfoProtoUtils;
	}


}
