package com.lvl6.server.controller;

import java.sql.Timestamp;
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
import com.lvl6.events.request.UpdateMonsterHealthRequestEvent;
import com.lvl6.events.response.AchievementProgressResponseEvent;
import com.lvl6.events.response.UpdateMonsterHealthResponseEvent;
import com.lvl6.info.MonsterForUser;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventMonsterProto.UpdateMonsterHealthRequestProto;
import com.lvl6.proto.EventMonsterProto.UpdateMonsterHealthResponseProto;
import com.lvl6.proto.EventMonsterProto.UpdateMonsterHealthResponseProto.Builder;
import com.lvl6.proto.MonsterStuffProto.UserMonsterCurrentHealthProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.TimeUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component

public class UpdateMonsterHealthController extends EventController {

	private static final Logger log = LoggerFactory
			.getLogger(UpdateMonsterHealthController.class);

	@Autowired
	protected Locker locker;

	@Autowired
	protected MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils;
	
	@Autowired
	protected MonsterStuffUtils monsterStuffUtils;
	
	@Autowired
	protected TimeUtils timeUtils;

	public UpdateMonsterHealthController() {
		
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new UpdateMonsterHealthRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_UPDATE_MONSTER_HEALTH_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		UpdateMonsterHealthRequestProto reqProto = ((UpdateMonsterHealthRequestEvent) event)
				.getUpdateMonsterHealthRequestProto();
		log.info(String.format("reqProto=%s", reqProto));

		//get values sent from the client (the request proto)
		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		Timestamp curTime = new Timestamp(reqProto.getClientTime());
		List<UserMonsterCurrentHealthProto> umchpList = reqProto.getUmchpList();

		boolean isUpdateTaskStageForUser = reqProto
				.getIsUpdateTaskStageForUser();
		int nuTaskStageId = reqProto.getNuTaskStageId();

		String userTaskId = null;
		if (reqProto.hasUserTaskUuid() && !reqProto.getUserTaskUuid().isEmpty()) {
			userTaskId = reqProto.getUserTaskUuid();
		}

		//make monsterPieceDropped to false in db
		String droplessTsfuId = null;
		if (reqProto.hasDroplessTsfuUuid()
				&& !reqProto.getDroplessTsfuUuid().isEmpty()) {
			droplessTsfuId = reqProto.getDroplessTsfuUuid();
		}

		boolean changeDmgMultipier = reqProto.getChangeNuPvpDmgMultiplier();
		float nuPvpDmgMultiplier = reqProto.getNuPvpDmgMultiplier();

		//set some values to send to the client (the response proto)
		UpdateMonsterHealthResponseProto.Builder resBuilder = UpdateMonsterHealthResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER); //default
		
		if(reqProto.getClientTime() == 0) {
			resBuilder.setStatus(ResponseStatus.FAIL_CLIENT_TIME_NOT_SENT);
			log.error("clientTime not sent");
			UpdateMonsterHealthResponseEvent resEvent = new UpdateMonsterHealthResponseEvent(senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}
		
		if(timeUtils.numMinutesDifference(new Date(reqProto.getClientTime()), new Date()) > 
		ControllerConstants.CLIENT_TIME_MINUTES_CONSTANT_CHECK) {
			resBuilder.setStatus(ResponseStatus.FAIL_TIME_OUT_OF_SYNC);
			log.error("time is out of sync > 2 hrs for userId {}", senderProto.getUserUuid());
			UpdateMonsterHealthResponseEvent resEvent = 
					new UpdateMonsterHealthResponseEvent(senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		UUID userUuid = null;
		boolean invalidUuids = true;
		try {
			userUuid = UUID.fromString(userId);

			if (userTaskId != null) {
				UUID.fromString(userTaskId);
			}

			if (droplessTsfuId != null) {
				UUID.fromString(droplessTsfuId);
			}

			invalidUuids = false;
		} catch (Exception e) {
			log.error(
					String.format(
							"UUID error. incorrect userId=%s, userTaskId=%s, droplessTsfuId=%s",
							userId, userTaskId, droplessTsfuId), e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			UpdateMonsterHealthResponseEvent resEvent = new UpdateMonsterHealthResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		getLocker().lockPlayer(userUuid, this.getClass().getSimpleName());
		try {
			Map<String, Integer> userMonsterIdToExpectedHealth = new HashMap<String, Integer>();

			boolean legit = checkLegit(resBuilder, userId, umchpList,
					isUpdateTaskStageForUser, userMonsterIdToExpectedHealth);

			boolean successful = false;
			if (legit) {
				successful = writeChangesToDb(userId, curTime,
						userMonsterIdToExpectedHealth, userTaskId,
						isUpdateTaskStageForUser, nuTaskStageId,
						droplessTsfuId, changeDmgMultipier, nuPvpDmgMultiplier);
			}

			if (successful) {
				resBuilder.setStatus(ResponseStatus.SUCCESS);
			}

			UpdateMonsterHealthResponseEvent resEvent = new UpdateMonsterHealthResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);

		} catch (Exception e) {
			log.error(
					"exception in UpdateMonsterHealthController processEvent",
					e);
			//don't let the client hang
			try {
				resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
				UpdateMonsterHealthResponseEvent resEvent = new UpdateMonsterHealthResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in UpdateMonsterHealthController processEvent",
						e);
			}
		} finally {
			getLocker().unlockPlayer(userUuid, this.getClass().getSimpleName());
		}
	}

	/*
	 * Return true if user request is valid; false otherwise and set the
	 * builder status to the appropriate value.
	 * Also, returns the expected health for the user monsters
	 */
	private boolean checkLegit(Builder resBuilder, String userId,
			List<UserMonsterCurrentHealthProto> umchpList,
			boolean isUpdateTaskStageForUser,
			Map<String, Integer> userMonsterIdToExpectedHealth) {

		boolean isUmchpListEmpty = (null == umchpList || umchpList.isEmpty());
		if (isUmchpListEmpty && !isUpdateTaskStageForUser) {
			log.error("client error: no user monsters sent. and is not updating"
					+ " user's current task stage id");
			return false;
		} else if (isUmchpListEmpty && isUpdateTaskStageForUser) {
			log.info("just updating user's current task stage id");
		}

		if (!isUmchpListEmpty) {
			//extract the ids so it's easier to get userMonsters from db
			List<String> userMonsterIds = monsterStuffUtils.getUserMonsterIds(
					umchpList, userMonsterIdToExpectedHealth);
			Map<String, MonsterForUser> userMonsters = getMonsterForUserRetrieveUtils()
					.getSpecificOrAllUserMonstersForUser(userId, userMonsterIds);

			if (null == userMonsters || userMonsters.isEmpty()) {
				log.error("unexpected error: userMonsterIds don't exist. ids="
						+ userMonsterIds);
				return false;
			}

			//see if the user has the equips
			if (userMonsters.size() != umchpList.size()) {
				log.error("unexpected error: mismatch between user equips client sent and "
						+ "what is in the db. clientUserMonsterIds="
						+ userMonsterIds
						+ "\t inDb="
						+ userMonsters
						+ "\t continuing the processing");
			}
		}

		return true;
		//resBuilder.setStatus(UpdateMonsterHealthStatus.SUCCESS);
	}

	private boolean writeChangesToDb(String uId, Timestamp clientTime,
			Map<String, Integer> userMonsterIdToExpectedHealth,
			String userTaskId, boolean isUpdateTaskStageForUser,
			int nuTaskStageId, String droplessTsfuId,
			boolean changeDmgMultipier, float nuPvpDmgMultiplier) {
		//replace existing health for these user monsters with new values 
		if (!userMonsterIdToExpectedHealth.isEmpty()) {
			log.info("updating user's monsters' healths");
			int numUpdated = UpdateUtils.get().updateUserMonstersHealth(
					userMonsterIdToExpectedHealth);
			log.info(String.format("numUpdated=%s", numUpdated));

			//number updated is based on INSERT ... ON DUPLICATE KEY UPDATE
			//so returns 2 if one row was updated, 1 if inserted
			if (numUpdated > 2 * userMonsterIdToExpectedHealth.size()) {
				log.warn("unexpected error: more than user monsters were"
						+ " updated. actual numUpdated=" + numUpdated
						+ "expected: userMonsterIdToExpectedHealth="
						+ userMonsterIdToExpectedHealth);
			}
		}

		if (isUpdateTaskStageForUser) {
			int numUpdated = UpdateUtils.get().updateUserTaskTsId(userTaskId,
					nuTaskStageId);
			log.info(String.format("task for user numUpdated=%s", numUpdated));
		}

		if (droplessTsfuId != null) {
			int numUpdated = UpdateUtils.get()
					.updateTaskStageForUserNoMonsterDrop(droplessTsfuId);
			log.info(String.format("task stage for user numUpdated=%s",
					numUpdated));
		}

		if (changeDmgMultipier) {
			int numUpdated = UpdateUtils.get().updatePvpMonsterDmgMultiplier(
					uId, nuPvpDmgMultiplier);
			log.info(String.format("pvp league for user numUpdated=%s",
					numUpdated));
		}

		return true;
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public MonsterForUserRetrieveUtils2 getMonsterForUserRetrieveUtils() {
		return monsterForUserRetrieveUtils;
	}

	public void setMonsterForUserRetrieveUtils(
			MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils) {
		this.monsterForUserRetrieveUtils = monsterForUserRetrieveUtils;
	}

}
