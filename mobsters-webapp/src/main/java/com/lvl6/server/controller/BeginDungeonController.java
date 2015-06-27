package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.BeginDungeonRequestEvent;
import com.lvl6.events.response.AchievementProgressResponseEvent;
import com.lvl6.events.response.BeginDungeonResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.QuestJobMonsterItem;
import com.lvl6.info.Task;
import com.lvl6.info.TaskForUserOngoing;
import com.lvl6.info.TaskStage;
import com.lvl6.info.TaskStageForUser;
import com.lvl6.info.TaskStageMonster;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventDungeonProto.BeginDungeonRequestProto;
import com.lvl6.proto.EventDungeonProto.BeginDungeonResponseProto;
import com.lvl6.proto.EventDungeonProto.BeginDungeonResponseProto.Builder;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.SharedEnumConfigProto.Element;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.TaskProto.TaskStageProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.TaskForUserOngoingRetrieveUtils2;
import com.lvl6.retrieveutils.TaskStageForUserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.QuestJobMonsterItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.QuestJobRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskStageMonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskStageRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.TimeUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.InsertUtils;

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
		log.info(String.format("reqProto=%s", reqProto));

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
		Element elem = reqProto.getElem();
		// if not set, then go select monsters at random
		boolean forceEnemyElem = reqProto.getForceEnemyElem();

		boolean alreadyCompletedMiniTutorialTask = reqProto
				.getAlreadyCompletedMiniTutorialTask();

		//set some values to send to the client (the response proto)
		BeginDungeonResponseProto.Builder resBuilder = BeginDungeonResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setTaskId(taskId);
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER); //default
		
		if(reqProto.getClientTime() == 0) {
			resBuilder.setStatus(ResponseStatus.FAIL_CLIENT_TIME_NOT_SENT);
			log.error("clientTime not sent");
			BeginDungeonResponseEvent resEvent = new BeginDungeonResponseEvent(senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			resEvent.setTag(event.getTag());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		if(timeUtils.numMinutesDifference(new Date(reqProto.getClientTime()), new Date()) > 
		ControllerConstants.CLIENT_TIME_MINUTES_CONSTANT_CHECK) {
			resBuilder.setStatus(ResponseStatus.FAIL_TIME_OUT_OF_SYNC);
			log.error("time is out of sync > 2 hrs for userId {}", senderProto.getUserUuid());
			BeginDungeonResponseEvent resEvent = 
					new BeginDungeonResponseEvent(senderProto.getUserUuid());
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

		getLocker().lockPlayer(userUuid, this.getClass().getSimpleName());
		try {
			User aUser = RetrieveUtils.userRetrieveUtils().getUserById(userId);
			Task aTask = taskRetrieveUtils.getTaskForTaskId(taskId);

			Map<Integer, TaskStage> tsMap = new HashMap<Integer, TaskStage>();
			boolean legit = checkLegit(resBuilder, aUser, userId, aTask,
					taskId, tsMap, isEvent, eventId, gemsSpent);

			List<String> userTaskIdList = new ArrayList<String>();
			Map<Integer, TaskStageProto> stageNumsToProtos = new HashMap<Integer, TaskStageProto>();
			Map<String, Integer> currencyChange = new HashMap<String, Integer>();
			Map<String, Integer> previousCurrency = new HashMap<String, Integer>();
			boolean successful = false;
			if (legit) {

				//determine the specifics for each stage (stored in stageNumsToProtos)
				//then record specifics in db
				successful = writeChangesToDb(aUser, userId, aTask, taskId,
						tsMap, curTime, isEvent, eventId, gemsSpent,
						userTaskIdList, stageNumsToProtos, questIds, elem,
						forceEnemyElem, alreadyCompletedMiniTutorialTask,
						currencyChange, previousCurrency);
			}
			if (successful) {
				setResponseBuilder(resBuilder, userTaskIdList,
						stageNumsToProtos);
			}
			log.info(String.format("successful=%s", successful));
			//      log.info("resBuilder=" + resBuilder);
			log.info(String.format("resBuilder=%s", resBuilder.build()));

			BeginDungeonResponseEvent resEvent = new BeginDungeonResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);

			if (successful && 0 != gemsSpent) {
				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = miscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								aUser, null, null);
				resEventUpdate.setTag(event.getTag());
				responses.normalResponseEvents().add(resEventUpdate);

				writeToUserCurrencyHistory(userId, aUser, eventId, taskId,
						curTime, currencyChange, previousCurrency);
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
			getLocker().unlockPlayer(userUuid, this.getClass().getSimpleName());
		}
	}

	/*
	 * Return true if user request is valid; false otherwise and set the
	 * builder status to the appropriate value.
	 */
	private boolean checkLegit(Builder resBuilder, User u, String userId,
			Task aTask, int taskId, Map<Integer, TaskStage> tsMap,
			boolean isEvent, int eventId, int gemsSpent) {
		if (null == u || null == aTask) {
			log.error(String.format("user or task is null. user=%s, task=%s",
					u, aTask));
			return false;
		}

		Map<Integer, TaskStage> ts = taskStageRetrieveUtils
				.getTaskStagesForTaskId(taskId);
		if (null == ts) {
			log.error(String.format("task has no taskStages. task=%s" + aTask));
			return false;
		}

		TaskForUserOngoing aTaskForUser = taskForUserOngoingRetrieveUtils
				.getUserTaskForUserId(userId);
		if (null != aTaskForUser) {
			log.warn("(will continue processing, but) user has existing task when"
					+ " beginning another. No task should exist. user="
					+ u
					+ "\t task=" + aTask + "\t userTask=" + aTaskForUser);
			//DELETE TASK AND PUT IT INTO TASK HISTORY
			String userTaskId = aTaskForUser.getId();
			deleteExistingTaskForUser(userTaskId, aTaskForUser);
			//DELETE FROM TASK STAGE FOR USER AND PUT IT INTO TASK STAGE HISTORY
			deleteExistingTaskStagesForUser(userTaskId);
		}

		//TODO: if event, maybe somehow check if user has enough gems to reset event
		//right now just relying on client
		//    if (isEvent) {
		//    	if (eventId > 0) {
		//
		//    	} else {
		//    		log.error("isEvent set to true but eventId not positive " + "\t eventId=" +
		//    				eventId + "\t gemsSpent=" + gemsSpent);
		//    		return false;
		//    	}
		//    }

		tsMap.putAll(ts);
		resBuilder.setStatus(ResponseStatus.SUCCESS);
		return true;
	}

	//TODO: MOVE THESE METHODS INTO A UTILS FOR TASK
	private void deleteExistingTaskForUser(String taskForUserId,
			TaskForUserOngoing aTaskForUser) {
		//  	DeleteUtils.get().deleteTaskForUserOngoingWithTaskForUserId(taskForUserId);
		String userId = aTaskForUser.getUserId();
		int taskId = aTaskForUser.getTaskId();
		int expGained = aTaskForUser.getExpGained();
		int cashGained = aTaskForUser.getCashGained();
		int oilGained = aTaskForUser.getOilGained();
		int numRevives = aTaskForUser.getNumRevives();
		Date aDate = aTaskForUser.getStartDate(); //shouldn't null
		Timestamp startTime = null;
		if (null != aDate) {
			startTime = new Timestamp(aDate.getTime());
		}
		Timestamp endTime = null;
		boolean userWon = false;
		boolean cancelled = true;
		int taskStageId = aTaskForUser.getTaskStageId();

		int num = DeleteUtils.get().deleteTaskForUserOngoingWithTaskForUserId(
				taskForUserId);
		log.warn(String
				.format("deleted existing task_for_user. taskForUser=%s, (should be 1) numDeleted=%s",
						aTaskForUser, num));
		//meh, fogedaboudit
		num = InsertUtils.get().insertIntoTaskHistory(taskForUserId, userId,
				taskId, expGained, cashGained, oilGained, numRevives,
				startTime, endTime, userWon, cancelled, taskStageId);
		log.warn(String
				.format("inserted into task_history. taskForUser=%s, (should be 1) numInserted=%s",
						aTaskForUser, num));
	}

	private void deleteExistingTaskStagesForUser(String taskForUserId) {
		List<TaskStageForUser> taskStages = taskStageForUserRetrieveUtils
				.getTaskStagesForUserWithTaskForUserId(taskForUserId);

		List<String> userTaskStageId = new ArrayList<String>();
		List<String> userTaskId = new ArrayList<String>();
		List<Integer> stageNum = new ArrayList<Integer>();
		List<Integer> tsmIdList = new ArrayList<Integer>();
		List<String> monsterTypes = new ArrayList<String>();
		List<Integer> expGained = new ArrayList<Integer>();
		List<Integer> cashGained = new ArrayList<Integer>();
		List<Integer> oilGained = new ArrayList<Integer>();
		List<Boolean> monsterPieceDropped = new ArrayList<Boolean>();
		List<Integer> itemIdDropped = new ArrayList<Integer>();
		List<Integer> monsterIdDrops = new ArrayList<Integer>();
		List<Integer> monsterDropLvls = new ArrayList<Integer>();
		List<Boolean> attackedFirstList = new ArrayList<Boolean>();

		for (int i = 0; i < taskStages.size(); i++) {
			TaskStageForUser tsfu = taskStages.get(i);
			userTaskStageId.add(tsfu.getId());
			userTaskId.add(tsfu.getUserTaskId());
			stageNum.add(tsfu.getStageNum());

			int tsmId = tsfu.getTaskStageMonsterId();
			tsmIdList.add(tsmId);
			monsterTypes.add(tsfu.getMonsterType());
			expGained.add(tsfu.getExpGained());
			cashGained.add(tsfu.getCashGained());
			oilGained.add(tsfu.getOilGained());
			boolean dropped = tsfu.isMonsterPieceDropped();
			monsterPieceDropped.add(dropped);
			itemIdDropped.add(tsfu.getItemIdDropped());

			TaskStageMonster tsm = taskStageMonsterRetrieveUtils
					.getTaskStageMonsterForId(tsmId);
			monsterIdDrops.add(tsm.getMonsterIdDrop());
			monsterDropLvls.add(tsm.getMonsterDropLvl());
			attackedFirstList.add(tsfu.isAttackedFirst());
		}

		int num = DeleteUtils.get().deleteTaskStagesForUserWithIds(
				userTaskStageId);
		log.warn(String.format(
				"num task stage history rows deleted: %s, taskStageForUser=%s",
				num, taskStages));

		num = InsertUtils.get().insertIntoTaskStageHistory(userTaskStageId,
				userTaskId, stageNum, tsmIdList, monsterTypes, expGained,
				cashGained, oilGained, monsterPieceDropped, itemIdDropped,
				monsterIdDrops, monsterDropLvls, attackedFirstList);
		log.warn(String
				.format("num task stage history rows inserted: %s, taskStageForUser=%s",
						num, taskStages));
	}

	private boolean writeChangesToDb(User u, String uId, Task t, int tId,
			Map<Integer, TaskStage> tsMap, Timestamp clientTime,
			boolean isEvent, int eventId, int gemsSpent, List<String> utIdList,
			Map<Integer, TaskStageProto> stageNumsToProtos,
			List<Integer> questIds, Element elem, boolean forceEnemyElem,
			boolean alreadyCompletedMiniTutorialTask,
			Map<String, Integer> currencyChange,
			Map<String, Integer> previousCurrency) {
		boolean success = false;

		//return values from creating the user task stages
		List<Integer> expList = new ArrayList<Integer>();
		List<Integer> cashList = new ArrayList<Integer>();
		List<Integer> oilList = new ArrayList<Integer>();
		Map<Integer, List<TaskStageForUser>> stageNumToStages = generateUserTaskStages(
				uId, tId, questIds, tsMap, expList, cashList, oilList);

		int expGained = expList.get(0);
		int cashGained = cashList.get(0);
		int oilGained = oilList.get(0);

		//record into user_task table
		int tsId = taskStageRetrieveUtils.getFirstTaskStageIdForTaskId(tId);
		String userTaskId = InsertUtils.get().insertIntoUserTaskReturnId(uId,
				tId, expGained, cashGained, oilGained, clientTime, tsId);

		//record into user_task stage table

		//send stuff back up to caller
		utIdList.add(userTaskId);
		recordStages(userTaskId, tId, stageNumToStages, stageNumsToProtos);

		success = true;
		log.debug("stageNumsToProtosTemp={}", stageNumsToProtos);

		//start the cool down timer if for event
		if (isEvent) {
			int numInserted = InsertUtils.get()
					.insertIntoUpdateEventPersistentForUser(uId, eventId,
							clientTime);
			log.info(String
					.format("started cool down timer for (eventId, userId): (%s,%s), numInserted=%s",
							uId, eventId, numInserted));
			previousCurrency.put(miscMethods.gems, u.getGems());
			if (0 != gemsSpent) {
				int gemChange = -1 * gemsSpent;
				success = updateUser(u, gemChange);
				log.info(String
						.format("upgraded user gems to reset event cool down timer? Answer: %s",
								success));
			}
			if (success) {
				currencyChange.put(miscMethods.gems, u.getGems());
			}
		}

		return success;
	}

	private boolean updateUser(User u, int gemChange) {
		if (!u.updateRelativeGemsNaive(gemChange, 0)) {
			log.error(String
					.format("problem updating user gems to delete event cool down timer. gemChange=%s, user=%s",
							gemChange, u));
			return false;
		}
		return true;
	}

	private Map<Integer, List<TaskStageForUser>> generateUserTaskStages(
			String userId, int taskId, List<Integer> questIds,
			Map<Integer, TaskStage> tsMap, List<Integer> expGained,
			List<Integer> cashGained, List<Integer> oilGained) {
		//return value
		int expSum = 0;
		int cashSum = 0;
		int oilSum = 0;
		Map<Integer, List<TaskStageForUser>> stageNumToUserTaskStages = new HashMap<Integer, List<TaskStageForUser>>();

		Random rand = ControllerConstants.RAND;
		//quest monster items are dropped based on QUEST JOB IDS not quest ids
		List<Integer> questJobIds = questJobRetrieveUtils
				.getQuestJobIdsForQuestIds(questIds);

		//for each stage, calculate the monster(s) the user will face and
		//reward(s) that might be given if the user wins
		for (int tsId : tsMap.keySet()) {

			//calculate the monster(s) the user will face for this stage
			//at the moment only one monster will be generated
			List<TaskStageMonster> spawnedTaskStageMonsters = new ArrayList<TaskStageMonster>();
			generateSpawnedMonsters(tsId, 1, rand, spawnedTaskStageMonsters);

			log.info(String.format("monster(s) spawned=%s",
					spawnedTaskStageMonsters));
			/*Code below is done such that if more than one monster is generated
			  above, then user has potential to get the cash and exp from all
			  the monsters including the one above.*/

			TaskStage ts = tsMap.get(tsId);
			int stageNum = ts.getStageNum();
			boolean userFirst = ts.isAttackerAlwaysHitsFirst();
			List<Integer> stageExpReward = new ArrayList<Integer>();
			List<Integer> stageCashReward = new ArrayList<Integer>();
			List<Integer> stageOilReward = new ArrayList<Integer>();
			List<TaskStageForUser> userTaskStages = createUserTaskStagesFromMonsters(
					stageNum, userFirst, spawnedTaskStageMonsters, questJobIds,
					stageExpReward, stageCashReward, stageOilReward);
			stageNumToUserTaskStages.put(stageNum, userTaskStages);
		}

		expGained.add(expSum);
		cashGained.add(cashSum);
		oilGained.add(oilSum);
		return stageNumToUserTaskStages;
	}

	public void generateSpawnedMonsters(int tsId, int quantity, Random rand,
			List<TaskStageMonster> spawnedTaskStageMonsters) {

		//select one monster, at random. This is the ONE monster for this stage
		List<TaskStageMonster> possibleMonsters = taskStageMonsterRetrieveUtils
				.getMonstersForTaskStageId(tsId);
		List<TaskStageMonster> copyTaskStageMonsters = new ArrayList<TaskStageMonster>(
				possibleMonsters);

		int size = copyTaskStageMonsters.size();
		int quantityWanted = quantity;
		//sum up chance to appear, and need to normalize all the probabilities
		float sumOfProbabilities = monsterStuffUtils
				.sumProbabilities(copyTaskStageMonsters);

		for (int i = 0; i < size; i++) {
			if (quantityWanted == 0) {
				//since we selected all the monsters we wanted, exit
				break;
			}
			if (quantityWanted < 0) {
				log.error("selecting some amount of monsters out of n possible monsters failed.");
				break;
			}
			//since we want k more monsters and we have k left, take them all
			int numLeft = size - i;
			if (quantityWanted == numLeft) {
				List<TaskStageMonster> leftOvers = copyTaskStageMonsters
						.subList(i, size);
				spawnedTaskStageMonsters.addAll(leftOvers);
				break;
			}

			//seeing if current monster is selected
			TaskStageMonster tsmSoFar = copyTaskStageMonsters.get(i);
			float chanceToAppear = tsmSoFar.getChanceToAppear();

			if (monsterSelected(rand, tsmSoFar, chanceToAppear,
					sumOfProbabilities)) {
				spawnedTaskStageMonsters.add(tsmSoFar);
				quantityWanted -= 1;
			}
			//selecting without replacement so this guy's probability needs to go
			sumOfProbabilities -= chanceToAppear;
		}
	}

	private boolean monsterSelected(Random rand, TaskStageMonster tsm,
			float chanceToAppear, float sumOfProbabilities) {
		float randFloat = rand.nextFloat();

		float normalizedProb = chanceToAppear / sumOfProbabilities;
		if (normalizedProb > randFloat) {
			//random number generated falls within this monster's probability window
			return true;
		}
		return false;
	}

	private List<TaskStageForUser> createUserTaskStagesFromMonsters(
			int stageNum, boolean attackedFirst,
			List<TaskStageMonster> spawnedTaskStageMonsters,
			List<Integer> questJobIds, List<Integer> stageExpReward,
			List<Integer> stageCashReward, List<Integer> stageOilReward) {
		//return values
		List<TaskStageForUser> tsfuList = new ArrayList<TaskStageForUser>();
		int expReward = 0;
		int cashReward = 0;
		int oilReward = 0;

		//create as many TaskStageForUsers as there are TaskStageMonsters and
		//aggregate the exp and cash
		for (TaskStageMonster tsm : spawnedTaskStageMonsters) {
			TaskStageForUser tsfu = createTaskStageFromMonster(stageNum, tsm,
					questJobIds);
			tsfu.setAttackedFirst(attackedFirst);

			expReward += tsfu.getExpGained();
			cashReward += tsfu.getCashGained();
			oilReward += tsfu.getOilGained();
			tsfuList.add(tsfu);
		}

		stageExpReward.add(expReward);
		stageCashReward.add(cashReward);
		stageOilReward.add(oilReward);

		return tsfuList;
	}

	private TaskStageForUser createTaskStageFromMonster(int stageNum,
			TaskStageMonster tsm, List<Integer> questJobIds) {
		TaskStageForUser tsfu = new TaskStageForUser();

		tsfu.setStageNum(stageNum);
		tsfu.setTaskStageMonsterId(tsm.getId());
		tsfu.setMonsterType(tsm.getMonsterType());
		tsfu.setExpGained(tsm.getExpReward());
		tsfu.setCashGained(tsm.getCashDrop());
		tsfu.setOilGained(tsm.getOilDrop());

		//determine the item that this monster drops, if any, (-1 means no item drop)
		int itemId = generateQuestJobMonsterItem(questJobIds, tsm);
		tsfu.setItemIdDropped(itemId);

		if (itemId > 0) {
			log.info(String
					.format("not generating monster piece since monster dropped item=%s, tsm=%s",
							itemId, tsm));
			tsfu.setMonsterPieceDropped(false);
		} else {
			tsfu.setMonsterPieceDropped(tsm.didPuzzlePieceDrop());
		}

		return tsfu;
	}

	//save these task stage for user objects; convert into protos and store into
	//stageNumsToProtos. one TaskStageForUser represents one monster in the current stage
	private void recordStages(String userTaskId, int taskId,
			Map<Integer, List<TaskStageForUser>> stageNumToStages,
			Map<Integer, TaskStageProto> stageNumsToProtos) {
		List<Integer> stageNumList = new ArrayList<Integer>(
				stageNumToStages.keySet());
		Collections.sort(stageNumList);

		//loop through the individual stages, aggregate them
		//to later be saved to the db
		List<TaskStageForUser> allTsfu = new ArrayList<TaskStageForUser>();
		for (Integer stageNum : stageNumList) {

			//aggregate to later be saved to db
			List<TaskStageForUser> tsfuList = stageNumToStages.get(stageNum);

			//don't forget to set the taskForUserId
			for (TaskStageForUser tsfu : tsfuList) {
				tsfu.setUserTaskId(userTaskId);
			}

			allTsfu.addAll(tsfuList);
		}

		log.info(String.format("userTaskStages=%s", allTsfu));

		//save to db
		List<String> tsfuIds = InsertUtils.get().insertIntoUserTaskStage(
				allTsfu);

		if (null == tsfuIds || tsfuIds.size() != allTsfu.size()) {
			log.error("wtf, dbconnections failed to return ids when creating TaskStageForUsers");
			allTsfu = taskStageForUserRetrieveUtils
					.getTaskStagesForUserWithTaskForUserId(userTaskId);

			stageNumToStages.clear();
			for (TaskStageForUser tsfu : allTsfu) {
				int stageNum = tsfu.getStageNum();

				if (!stageNumToStages.containsKey(stageNum)) {
					stageNumToStages.put(stageNum,
							new ArrayList<TaskStageForUser>());
				}
				List<TaskStageForUser> tsfuList = stageNumToStages
						.get(stageNum);
				tsfuList.add(tsfu);
			}

		} else {
			//set the tsfuIds
			for (int index = 0; index < tsfuIds.size(); index++) {
				TaskStageForUser tsfu = allTsfu.get(index);
				String tsfuId = tsfuIds.get(index);
				tsfu.setId(tsfuId);
			}
		}

		//create the proto
		for (Integer stageNum : stageNumList) {
			List<TaskStageForUser> tsfu = stageNumToStages.get(stageNum);
			TaskStageProto tsp = createInfoProtoUtils.createTaskStageProto(
					taskId, stageNum, tsfu);

			//return to sender
			stageNumsToProtos.put(stageNum, tsp);
		}

	}

	//see if quest job id and monster id have an item. if yes, see if it drops.
	//If it drops return the item id.
	//default return -1
	private int generateQuestJobMonsterItem(List<Integer> questJobIds,
			TaskStageMonster tsm) {

		int monsterId = tsm.getMonsterId();
		for (int questJobId : questJobIds) {

			QuestJobMonsterItem qjmi = questJobMonsterItemRetrieveUtils
					.getItemForQuestJobAndMonsterId(questJobId, monsterId);
			log.info(String.format("QuestJobMonsterItem=%s", qjmi));

			if (null == qjmi) {
				continue;
			}
			log.info("item might drop");

			//roll to see if item should drop
			if (!qjmi.didItemDrop()) {
				log.info(String.format(
						"task stage monster didn't drop item. tsm=%s", tsm));
				continue;
			}
			//since quest job and monster have item associated with it and the
			//item "dropped"
			//return item dropped

			int itemId = qjmi.getItemId();
			log.info(String.format("item dropped=%s", itemId));
			return itemId;
		}

		log.info(String.format(
				"no quest job ids sent by client. questJobIds=%s", questJobIds));
		//no item
		return -1;
	}

	private void setResponseBuilder(Builder resBuilder,
			List<String> userTaskIdList,
			Map<Integer, TaskStageProto> stageNumsToProtos) {
		//stuff to send to the client
		String userTaskId = userTaskIdList.get(0);
		resBuilder.setUserTaskUuid(userTaskId);

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

	private void writeToUserCurrencyHistory(String userId, User user,
			int eventId, int taskId, Timestamp curTime,
			Map<String, Integer> currencyChange,
			Map<String, Integer> previousCurrency) {
		if (currencyChange.isEmpty()) {
			return;
		}

		String reason = ControllerConstants.UCHRFC__END_PERSISTENT_EVENT_COOLDOWN;
		StringBuilder detailsSb = new StringBuilder();
		detailsSb.append("eventId=");
		detailsSb.append(eventId);
		detailsSb.append(", taskId=");
		detailsSb.append(taskId);
		String details = detailsSb.toString();

		Map<String, Integer> currentCurrency = new HashMap<String, Integer>();
		Map<String, String> reasonsForChanges = new HashMap<String, String>();
		Map<String, String> detailsMap = new HashMap<String, String>();
		String gems = miscMethods.gems;

		//	  if (currencyChange.containsKey(gems)) {
		currentCurrency.put(gems, user.getGems());
		reasonsForChanges.put(gems, reason);
		detailsMap.put(gems, details);
		//	  }

		miscMethods.writeToUserCurrencyOneUser(userId, curTime, currencyChange,
				previousCurrency, currentCurrency, reasonsForChanges,
				detailsMap);
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
