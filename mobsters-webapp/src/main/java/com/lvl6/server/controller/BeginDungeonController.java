package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.BeginDungeonRequestEvent;
import com.lvl6.events.response.BeginDungeonResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventDungeonProto.BeginDungeonRequestProto;
import com.lvl6.proto.EventDungeonProto.BeginDungeonResponseProto;
import com.lvl6.proto.EventDungeonProto.BeginDungeonResponseProto.Builder;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.TaskProto.TaskStageProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.TaskForUserOngoingRetrieveUtils2;
import com.lvl6.retrieveutils.TaskStageForUserRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.QuestJobMonsterItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.QuestJobRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskStageMonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskStageRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.BeginDungeonAction;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.spring.AppContext;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.TimeUtils;
import com.lvl6.utils.utilmethods.InsertUtil;

@Component

public class BeginDungeonController extends EventController {

	private static Logger log = LoggerFactory.getLogger(BeginDungeonController.class);

	@Autowired
	protected Locker locker;

	@Autowired
	protected MiscMethods miscMethods;

	@Autowired
	CreateInfoProtoUtils createInfoProtoUtils;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	protected TaskForUserOngoingRetrieveUtils2 taskForUserOngoingRetrieveUtils;

	@Autowired
	protected TaskStageForUserRetrieveUtils2 taskStageForUserRetrieveUtils;

	@Autowired
	protected MonsterStuffUtils monsterStuffUtils;

	@Autowired
	protected TaskStageMonsterRetrieveUtils taskStageMonsterRetrieveUtils;

	@Autowired
	protected QuestJobMonsterItemRetrieveUtils questJobMonsterItemRetrieveUtils;

	@Autowired
	protected QuestJobRetrieveUtils questJobRetrieveUtils;

	@Autowired
	protected TaskRetrieveUtils taskRetrieveUtils;

	@Autowired
	protected TaskStageRetrieveUtils taskStageRetrieveUtils;

	@Autowired
	protected TimeUtils timeUtils;

//	@Autowired
//	protected HistoryUtils historyUtil;
//
//	@Autowired
//	protected TaskStageHistoryDao tshDao;

	@Autowired
	protected InsertUtil insertUtil;

	public BeginDungeonController() {

	}

	@Override
	public RequestEvent createRequestEvent() {
		return new BeginDungeonRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_BEGIN_DUNGEON_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		BeginDungeonRequestProto reqProto = ((BeginDungeonRequestEvent) event)
				.getBeginDungeonRequestProto();
		log.info("reqProto={}", reqProto);

		//get values sent from the client (the request proto)
		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		Timestamp curTime = new Timestamp(reqProto.getClientTime());
		int taskId = reqProto.getTaskId();

		//if is event, start the cool down timer in event_persistent_for_user
		boolean isEvent = reqProto.getIsEvent();
		int eventId = reqProto.getPersistentEventId();
		int gemsSpent = reqProto.getGemsSpent();

		//active quests a user has, this is to allow monsters to drop something
		//other than a piece of themselves (quest_monster_item)
		List<Integer> questIds = reqProto.getQuestIdsList();

		//used for element tutorial, client sets what enemy monster element should appear
		//and only that one guy should appear (quest tasks should have only one stage in db)
//		Element elem = reqProto.getElem();
		// if not set, then go select monsters at random
//		boolean forceEnemyElem = reqProto.getForceEnemyElem();

//		boolean alreadyCompletedMiniTutorialTask = reqProto
//				.getAlreadyCompletedMiniTutorialTask();

		//set some values to send to the client (the response proto)
		BeginDungeonResponseProto.Builder resBuilder = BeginDungeonResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setTaskId(taskId);
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER); //default

		if(reqProto.getClientTime() == 0) {
			resBuilder.setStatus(ResponseStatus.FAIL_CLIENT_TIME_NOT_SENT);
			log.error("clientTime not sent");
			BeginDungeonResponseEvent resEvent = new BeginDungeonResponseEvent(userId);
			resEvent.setResponseProto(resBuilder.build());
			resEvent.setTag(event.getTag());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		if(timeUtils.numMinutesDifference(new Date(reqProto.getClientTime()), new Date()) >
		ControllerConstants.CLIENT_TIME_MINUTES_CONSTANT_CHECK) {
			resBuilder.setStatus(ResponseStatus.FAIL_TIME_OUT_OF_SYNC);
			log.error("time is out of sync > 2 hrs for userId {}", userId);
			BeginDungeonResponseEvent resEvent =
					new BeginDungeonResponseEvent(userId);
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
			log.error(String.format("UUID error. incorrect userId=%s", userId),
					e);
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			BeginDungeonResponseEvent resEvent = new BeginDungeonResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		boolean gotLock = false;
		try {
			gotLock = locker.lockPlayer(userUuid, this.getClass().getSimpleName());
			BeginDungeonAction bda = AppContext.getBean(BeginDungeonAction.class);
			bda.wire(userId, curTime, taskId, isEvent, eventId, gemsSpent, questIds);
			bda.execute(resBuilder);

			if (ResponseStatus.SUCCESS.equals(resBuilder.getStatus())) {
				setResponseBuilder(resBuilder, bda);
				log.debug("resBuilder={}", resBuilder.build());
			}

			BeginDungeonResponseEvent resEvent = new BeginDungeonResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);

			if (ResponseStatus.SUCCESS.equals(resBuilder.getStatus()) && 0 != gemsSpent) {
				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = miscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								bda.getUser(), null, null);
				resEventUpdate.setTag(event.getTag());
				responses.normalResponseEvents().add(resEventUpdate);
			}

		} catch (Exception e) {
			log.error("exception in BeginDungeonController processEvent", e);
			//don't let the client hang
			try {
				resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
				BeginDungeonResponseEvent resEvent = new BeginDungeonResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in BeginDungeonController processEvent",
						e);
			}
		} finally {
			if (gotLock) {
				locker.unlockPlayer(userUuid, this.getClass().getSimpleName());
			}
		}
	}

	private void setResponseBuilder(
			Builder resBuilder,
			BeginDungeonAction bda) {
		//stuff to send to the client
		String userTaskId = bda.getUserTaskId();
		resBuilder.setUserTaskUuid(userTaskId);

		Map<Integer, TaskStageProto> stageNumsToProtos = bda
				.getStageNumsToProtos();
		//to handle the case if there are gaps in stageNums for a task, we
		//order the available stage numbers. Then we give it all to the client
		//sequentially, just because.
		Set<Integer> stageNums = stageNumsToProtos.keySet();
		List<Integer> stageNumsOrdered = new ArrayList<Integer>(stageNums);
		Collections.sort(stageNumsOrdered);

		for (Integer i : stageNumsOrdered) {
			TaskStageProto tsp = stageNumsToProtos.get(i);
			resBuilder.addTsp(tsp);
		}
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public TaskForUserOngoingRetrieveUtils2 getTaskForUserOngoingRetrieveUtils() {
		return taskForUserOngoingRetrieveUtils;
	}

	public void setTaskForUserOngoingRetrieveUtils(
			TaskForUserOngoingRetrieveUtils2 taskForUserOngoingRetrieveUtils) {
		this.taskForUserOngoingRetrieveUtils = taskForUserOngoingRetrieveUtils;
	}

	public TaskStageForUserRetrieveUtils2 getTaskStageForUserRetrieveUtils() {
		return taskStageForUserRetrieveUtils;
	}

	public void setTaskStageForUserRetrieveUtils(
			TaskStageForUserRetrieveUtils2 taskStageForUserRetrieveUtils) {
		this.taskStageForUserRetrieveUtils = taskStageForUserRetrieveUtils;
	}

}
