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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.BeginDungeonRequestEvent;
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
import com.lvl6.proto.EventDungeonProto.BeginDungeonResponseProto.BeginDungeonStatus;
import com.lvl6.proto.EventDungeonProto.BeginDungeonResponseProto.Builder;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.SharedEnumConfigProto.Element;
import com.lvl6.proto.TaskProto.TaskStageProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.TaskForUserCompletedRetrieveUtils;
import com.lvl6.retrieveutils.TaskForUserOngoingRetrieveUtils;
import com.lvl6.retrieveutils.TaskStageForUserRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.QuestJobMonsterItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.QuestJobRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskStageMonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskStageRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.InsertUtils;

@Component @DependsOn("gameServer") public class BeginDungeonController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  @Autowired
  protected Locker locker;

  public BeginDungeonController() {
    numAllocatedThreads = 8;
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
  protected void processRequestEvent(RequestEvent event) throws Exception {
    BeginDungeonRequestProto reqProto = ((BeginDungeonRequestEvent)event).getBeginDungeonRequestProto();
    log.info("reqProto=" + reqProto);

    //get values sent from the client (the request proto)
    MinimumUserProto senderProto = reqProto.getSender();
    int userId = senderProto.getUserId();
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
    
    boolean alreadyCompletedMiniTutorialTask = reqProto.getAlreadyCompletedMiniTutorialTask();
    
    //set some values to send to the client (the response proto)
    BeginDungeonResponseProto.Builder resBuilder = BeginDungeonResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setTaskId(taskId);
    resBuilder.setStatus(BeginDungeonStatus.FAIL_OTHER); //default

    getLocker().lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    try {
      User aUser = RetrieveUtils.userRetrieveUtils().getUserById(userId);
      Task aTask = TaskRetrieveUtils.getTaskForTaskId(taskId);

      Map<Integer, TaskStage> tsMap = new HashMap<Integer, TaskStage>();
      boolean legit = checkLegit(resBuilder, aUser, userId, aTask, taskId, tsMap,
      		isEvent, eventId, gemsSpent);

      List<Long> userTaskIdList = new ArrayList<Long>();
      Map<Integer, TaskStageProto> stageNumsToProtos = new HashMap<Integer, TaskStageProto>();
      Map<String, Integer> currencyChange = new HashMap<String, Integer>();
      Map<String, Integer> previousCurrency = new HashMap<String, Integer>();
      boolean successful = false;
      if(legit) {
    	  
      	//determine the specifics for each stage (stored in stageNumsToProtos)
      	//then record specifics in db
    	  successful = writeChangesToDb(aUser, userId, aTask, taskId, tsMap,
    			  curTime, isEvent, eventId, gemsSpent, userTaskIdList,
    			  stageNumsToProtos, questIds, elem, forceEnemyElem,
    			  alreadyCompletedMiniTutorialTask,
    			  currencyChange, previousCurrency);
      }
      if (successful) {
    	  setResponseBuilder(resBuilder, userTaskIdList, stageNumsToProtos);
      }
      log.info("successful=" + successful);
      log.info("resBuilder=" + resBuilder);
      log.info("resBuilder=" + resBuilder.build());
      
      BeginDungeonResponseEvent resEvent = new BeginDungeonResponseEvent(userId);
      resEvent.setTag(event.getTag());
      resEvent.setBeginDungeonResponseProto(resBuilder.build());
      server.writeEvent(resEvent);

      if (successful && 0 != gemsSpent) {
    	  //null PvpLeagueFromUser means will pull from hazelcast instead
      	UpdateClientUserResponseEvent resEventUpdate = MiscMethods
      			.createUpdateClientUserResponseEventAndUpdateLeaderboard(aUser, null);
      	resEventUpdate.setTag(event.getTag());
      	server.writeEvent(resEventUpdate);
      	
      	writeToUserCurrencyHistory(userId, aUser, eventId, taskId, curTime,
      			currencyChange, previousCurrency);
      }
    } catch (Exception e) {
      log.error("exception in BeginDungeonController processEvent", e);
      //don't let the client hang
      try {
    	  resBuilder.setStatus(BeginDungeonStatus.FAIL_OTHER);
    	  BeginDungeonResponseEvent resEvent = new BeginDungeonResponseEvent(userId);
    	  resEvent.setTag(event.getTag());
    	  resEvent.setBeginDungeonResponseProto(resBuilder.build());
    	  server.writeEvent(resEvent);
      } catch (Exception e2) {
    	  log.error("exception2 in BeginDungeonController processEvent", e);
      }
    } finally {
      getLocker().unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    }
  }

  /*
   * Return true if user request is valid; false otherwise and set the
   * builder status to the appropriate value.
   */
  private boolean checkLegit(Builder resBuilder, User u, int userId, Task aTask,
		  int taskId, Map<Integer, TaskStage> tsMap, boolean isEvent, int eventId,
		  int gemsSpent) {
    if (null == u || null == aTask) {
      log.error("unexpected error: user or task is null. user=" + u + "\t task="+ aTask);
      return false;
    }
    
    Map<Integer, TaskStage> ts = TaskStageRetrieveUtils.getTaskStagesForTaskId(taskId);
    if (null == ts) {
    	log.error("unexpected error: task has no taskStages. task=" + aTask);
    	return false;
    }
    
    TaskForUserOngoing aTaskForUser = TaskForUserOngoingRetrieveUtils.getUserTaskForUserId(userId);
    if(null != aTaskForUser) {
      log.warn("(will continue processing, but) user has existing task when" +
      		" beginning another. No task should exist. user=" + u + "\t task=" +
      		aTask + "\t userTask=" + aTaskForUser);
      //DELETE TASK AND PUT IT INTO TASK HISTORY
      long userTaskId = aTaskForUser.getId();
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
    resBuilder.setStatus(BeginDungeonStatus.SUCCESS);
    return true;
  }
  
  //TODO: MOVE THESE METHODS INTO A UTILS FOR TASK
  private void deleteExistingTaskForUser(long taskForUserId, TaskForUserOngoing aTaskForUser) {
//  	DeleteUtils.get().deleteTaskForUserOngoingWithTaskForUserId(taskForUserId);
  	int userId = aTaskForUser.getUserId();
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
  	
  	int num = DeleteUtils.get().deleteTaskForUserOngoingWithTaskForUserId(taskForUserId);
  	log.warn("deleted existing task_for_user. taskForUser=" + aTaskForUser +
  			"\t (should be 1) numDeleted=" + num);
  	//meh, fogedaboudit 
    num = InsertUtils.get().insertIntoTaskHistory(taskForUserId, userId,
    		taskId, expGained, cashGained, oilGained, numRevives, startTime,
    		endTime, userWon, cancelled, taskStageId);
    log.warn("inserted into task_history. taskForUser=" + aTaskForUser +
  			"\t (should be 1) numInserted=" + num);
  }
  
  private void deleteExistingTaskStagesForUser(long taskForUserId) {
  	List<TaskStageForUser> taskStages = TaskStageForUserRetrieveUtils
  			.getTaskStagesForUserWithTaskForUserId(taskForUserId);

  	List<Long> userTaskStageId = new ArrayList<Long>();
  	List<Long> userTaskId = new ArrayList<Long>();
  	List<Integer> stageNum = new ArrayList<Integer>();
  	List<Integer> tsmIdList = new ArrayList<Integer>();
  	List<String> monsterTypes = new ArrayList<String>();
  	List<Integer> expGained = new ArrayList<Integer>();
  	List<Integer> cashGained = new ArrayList<Integer>();
  	List<Integer> oilGained = new ArrayList<Integer>();
  	List<Boolean> monsterPieceDropped = new ArrayList<Boolean>();
  	List<Integer> itemIdDropped = new ArrayList<Integer>();
  	List<Integer> monsterIdDrops = new ArrayList<Integer>();
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
  		monsterIdDrops.add(
  			TaskStageMonsterRetrieveUtils.getMonsterIdDropForId(
  				tsmId));
  	}
  	
  	int num = DeleteUtils.get().deleteTaskStagesForUserWithIds(userTaskStageId);
  	log.warn("num task stage history rows deleted: num=" + num +
  			"taskStageForUser=" + taskStages);

  	num = InsertUtils.get().insertIntoTaskStageHistory(userTaskStageId,
  			userTaskId, stageNum, tsmIdList, monsterTypes, expGained,
  			cashGained, oilGained, monsterPieceDropped, itemIdDropped, monsterIdDrops);
  	log.warn("num task stage history rows inserted: num=" + num +
  			"taskStageForUser=" + taskStages);
  }
  
  private boolean writeChangesToDb(User u, int uId, Task t, int tId,
		  Map<Integer, TaskStage> tsMap, Timestamp clientTime, boolean isEvent,
		  int eventId, int gemsSpent, List<Long> utIdList,
		  Map<Integer, TaskStageProto> stageNumsToProtos,
		  List<Integer> questIds, Element elem,
		  boolean forceEnemyElem,
		  boolean alreadyCompletedMiniTutorialTask,
		  Map<String, Integer> currencyChange,
		  Map<String, Integer> previousCurrency) {
	  boolean success = false;
	  
	  //local vars storing eventual db data (accounting for multiple monsters in stage)
  	Map<Integer, List<Integer>> stageNumsToExps = new HashMap<Integer, List<Integer>>();
  	Map<Integer, List<Integer>> stageNumsToCash = new HashMap<Integer, List<Integer>>();
  	Map<Integer, List<Integer>> stageNumsToOil = new HashMap<Integer, List<Integer>>();
	  Map<Integer, List<Boolean>> stageNumsToPuzzlePiecesDropped = new HashMap<Integer, List<Boolean>>();
	  Map<Integer, List<TaskStageMonster>> stageNumsToTaskStageMonsters = new HashMap<Integer, List<TaskStageMonster>>();
	  Map<Integer, Map<Integer, Integer>> stageNumsToTsmIdToItemId = 
	  		new HashMap<Integer, Map<Integer, Integer>>();
	  
	  //calculate the SINGLE monster the user fights in each stage
	  Map<Integer, TaskStageProto> stageNumsToProtosTemp = generateStage(questIds,
			  tsMap, stageNumsToExps, stageNumsToCash, stageNumsToOil,
			  stageNumsToPuzzlePiecesDropped, stageNumsToTaskStageMonsters,
			  stageNumsToTsmIdToItemId, elem, forceEnemyElem,
			  uId, tId, alreadyCompletedMiniTutorialTask);
	  
	  //calculate the exp that the user could gain for this task
	  int expGained = MiscMethods.sumListsInMap(stageNumsToExps);
	  //calculate the cash that the user could gain for this task
	  int cashGained = MiscMethods.sumListsInMap(stageNumsToCash);
	  int oilGained = MiscMethods.sumListsInMap(stageNumsToOil);

	  //record into user_task table	  
	  int tsId = TaskStageRetrieveUtils.getFirstTaskStageIdForTaskId(tId);
	  long userTaskId = InsertUtils.get().insertIntoUserTaskReturnId(uId, tId,
			  expGained, cashGained, oilGained, clientTime, tsId);
	  
	  //record into user_task stage table
	  recordStages(userTaskId, stageNumsToExps, stageNumsToCash, stageNumsToOil,
	  		stageNumsToPuzzlePiecesDropped, stageNumsToTaskStageMonsters,
	  		stageNumsToTsmIdToItemId);
	  
	  //send stuff back up to caller
	  utIdList.add(userTaskId);
	  stageNumsToProtos.putAll(stageNumsToProtosTemp);
	  
	  success = true;
	  log.info("stageNumsToProtosTemp=" + stageNumsToProtosTemp);
	  log.info("stageNumsToProtos=" + stageNumsToProtos);
	  
	  
	  //start the cool down timer if for event
	  if (isEvent) {
	  	int numInserted = InsertUtils.get().insertIntoUpdateEventPersistentForUser(uId, eventId, clientTime);
	  	log.info("started cool down timer for (eventId, userId): " + uId + "," + eventId +
	  			"\t numInserted=" + numInserted);
	  	previousCurrency.put(MiscMethods.gems, u.getGems());
	  	if (0 != gemsSpent) {
	  		int gemChange = -1 * gemsSpent;
	  		success = updateUser(u, gemChange);
	  		log.info("successfully upgraded user gems to reset event cool down timer? Answer:" + success);
	  	}
	  	if (success) {
	  		currencyChange.put(MiscMethods.gems, u.getGems());
	  	}
	  }
	  
	  return success;
  }
  
  private boolean updateUser(User u, int gemChange) {
	  if (!u.updateRelativeGemsNaive(gemChange)) {
		  log.error("unexpected error: problem with updating user gems to delete event cool down timer. gemChange=" +
				  gemChange + "user=" + u);
		  return false;
	  }
	  return true;
  }
  
  //stage can have multiple monsters; stage has a drop rate (but is useless for now); 
  //for each stage do the following
  //1) select monster at random
  //1a) determine if monster drops a puzzle piece
  //2) create MonsterProto
  //3) create TaskStageProto with the MonsterProto
  private Map<Integer, TaskStageProto> generateStage(List<Integer> questIds,
  		Map<Integer, TaskStage> tsMap, Map<Integer, List<Integer>> stageNumsToExps,
  		Map<Integer, List<Integer>> stageNumsToCash,
  		Map<Integer, List<Integer>> stageNumsToOil,
  		Map<Integer, List<Boolean>> stageNumsToPuzzlePieceDropped,
  		Map<Integer, List<TaskStageMonster>> stageNumsToTaskStageMonsters,
  		Map<Integer, Map<Integer, Integer>> stageNumsToTsmIdToItemId,
  		Element elem, boolean forceEnemyElem, int userId, int taskId,
  		boolean alreadyCompletedMiniTutorialTask) {
	  Map<Integer, TaskStageProto> stageNumsToProtos = new HashMap<Integer, TaskStageProto>();
	  Random rand = new Random();
	  
	  //quest monster items are dropped based on QUEST JOB IDS not quest ids
	  List<Integer> questJobIds = QuestJobRetrieveUtils
			  .getQuestJobIdsForQuestIds(questIds);
	  //for each stage, calculate the monster(s) the user will face and
	  //reward(s) that might be given if the user wins
	  for (int tsId : tsMap.keySet()) {
		  TaskStage ts = tsMap.get(tsId);
		  int stageNum = ts.getStageNum();
		  
		  //select one monster, at random. This is the ONE monster for this stage
		  List<TaskStageMonster> taskStageMonsters = 
				  TaskStageMonsterRetrieveUtils.getMonstersForTaskStageId(tsId);
		  
		  List<TaskStageMonster> spawnedTaskStageMonsters = new ArrayList<TaskStageMonster>();
		  if (forceEnemyElem) {
		  	 selectElementMonster(taskStageMonsters, elem, spawnedTaskStageMonsters);
		  }
		  
		  if (spawnedTaskStageMonsters.isEmpty()) {
		  	int quantity = 1; //change value to ensure monster spawned
		  	selectMonsters(taskStageMonsters, rand, quantity, spawnedTaskStageMonsters);
		  }
		  log.info("monster(s) spawned=" + spawnedTaskStageMonsters);
		  
		  /*Code below is done such that if more than one monster is generated
		    above, then user has potential to get the cash and exp from all
		    the monsters including the one above.*/
		  
		  //if no questIds, then map returned is empty
		  //task stage monster id (not task stage monster monster id) can only represent 1
		  //monster, so no need to worry about dup monsters
		  Map<Integer, Integer> tsmIdToItemId = new HashMap<Integer, Integer>();
		  //if mini tutorial, then there is no item drop, just guaranteed monster drop
		  if (taskId != ControllerConstants.MINI_TUTORIAL__GUARANTEED_MONSTER_DROP_TASK_ID) {
			  generateItems(spawnedTaskStageMonsters, questJobIds, tsmIdToItemId);
		  }
		  
		  //randomly select a reward, IF ANY, that this monster can drop;
		  //if an item drops puzzle piece does not drop.
		  List<Boolean> puzzlePiecesDropped = generatePuzzlePieces(spawnedTaskStageMonsters,
		  		tsmIdToItemId, userId, taskId, alreadyCompletedMiniTutorialTask);
		  
		  List<Integer> individualExps = calculateExpGained(spawnedTaskStageMonsters);
		  List<Integer> individualCash = calculateCashGained(spawnedTaskStageMonsters);
		  List<Integer> individualOil = calculateOilGained(spawnedTaskStageMonsters);
		  
		  //create the proto
		  TaskStageProto tsp = CreateInfoProtoUtils.createTaskStageProto(tsId,
				  ts, spawnedTaskStageMonsters, puzzlePiecesDropped, individualCash,
				  individualOil, tsmIdToItemId);
		  
		  log.info("task stage proto=" + tsp); 
		  log.info("after tsp taskStageMonsterIdToItemId=" + tsmIdToItemId);
		  log.info("exp, cash, oil. exp=" + individualExps + "\t cash=" + individualCash +
		  		"\t oil=" + individualOil);
		  //NOTE, all the sizes are equal:
		  //individualSilvers.size() == individualExps.size() == puzzlePiecesDropped.size()
		  // == spawnedTaskStageMonsters.size()
		  //update the protos to return to parent function
		  stageNumsToProtos.put(stageNum, tsp);
		  stageNumsToExps.put(stageNum, individualExps);
		  stageNumsToCash.put(stageNum, individualCash);
		  stageNumsToOil.put(stageNum, individualOil);
		  stageNumsToPuzzlePieceDropped.put(stageNum, puzzlePiecesDropped);
		  stageNumsToTaskStageMonsters.put(stageNum, spawnedTaskStageMonsters);
		  stageNumsToTsmIdToItemId.put(stageNum, tsmIdToItemId);
	  }
	  
	  return stageNumsToProtos;
  }
  
  private void selectElementMonster(List<TaskStageMonster> taskStageMonsters,
  		Element elem, List<TaskStageMonster> spawnedTaskStageMonsters) {
  	
  	for (TaskStageMonster tsm : taskStageMonsters) {
  		
  		int monsterId = tsm.getMonsterId();
  		Element mon = MonsterStuffUtils.getMonsterElementForMonsterId(monsterId);
  		
  		if (!elem.equals(mon)) {
  			continue;
  		}
  		log.info("found forced element!! forcedElement=" + elem + "\t monster=" + tsm);
  		//found element!!!!
  		spawnedTaskStageMonsters.add(tsm);
  		return;
  	}
  }
  
  /*picking without replacement*/
  private void selectMonsters(List<TaskStageMonster> taskStageMonsters,
  		Random rand, int quantity, List<TaskStageMonster> spawnedTaskStageMonsters) {
  	
  	int size = taskStageMonsters.size();
  	int quantityWanted = quantity;
  	//sum up chance to appear, and need to normalize all the probabilities
  	float sumOfProbabilities = MonsterStuffUtils.sumProbabilities(taskStageMonsters);
  	
  	List<TaskStageMonster> copyTaskStageMonsters = new ArrayList<TaskStageMonster>(taskStageMonsters);
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
  			List<TaskStageMonster> leftOvers = taskStageMonsters.subList(i, size);
  			spawnedTaskStageMonsters.addAll(leftOvers);
  			break;
  		}
  		
  		TaskStageMonster tsmSoFar = copyTaskStageMonsters.get(i);
  		float chanceToAppear = tsmSoFar.getChanceToAppear();
  		float randFloat = rand.nextFloat();
  		
  		float normalizedProb = chanceToAppear/sumOfProbabilities;
  		if (normalizedProb > randFloat) {
  			//random number generated falls within this monster's probability window
  			spawnedTaskStageMonsters.add(tsmSoFar);
  			quantityWanted -= 1;
  		}
  		//selecting without replacement so this guy's probability needs to go
  		sumOfProbabilities -= chanceToAppear;
  	}
  	
  }

  //if an item drops puzzle piece does not drop.
  private void generateItems(List<TaskStageMonster> taskStageMonsters,
  		List<Integer> questJobIds, Map<Integer, Integer> tsmIdToItemId) {
  	
  	//no quest ids means no items (empty map)
  	if (null == questJobIds || questJobIds.isEmpty()) {
  		return; 
  	}
  	
  	for (int index = 0; index < taskStageMonsters.size(); index++) {
  		TaskStageMonster tsm = taskStageMonsters.get(index);
  		
  		//determine the item that this monster drops, if any, (-1 means no item drop)
  		int itemId = generateQuestJobMonsterItem(questJobIds, tsm);
  		int tsmId = tsm.getId();
  		
  		//hacky way of accounting for multiple identical task stage monsters that
  		//can drop one item
  		if (tsmIdToItemId.containsKey(tsmId)) {
  			
  		}
  		//task stage monster id (not task stage monster monster id) can only represent 1
  		//monster
  		tsmIdToItemId.put(tsmId, itemId);
  	}
  	log.info("generate items tsmIdToItemId=" + tsmIdToItemId);
  }

  //see if quest job id and monster id have an item. if yes, see if it drops.
  //If it drops return the item id. 
  //default return -1
  private int generateQuestJobMonsterItem(List<Integer> questJobIds,
		  TaskStageMonster tsm) {
  	
  	int monsterId = tsm.getMonsterId();
  	for (int questJobId : questJobIds) {
  		
  		QuestJobMonsterItem qjmi = QuestJobMonsterItemRetrieveUtils
  				.getItemForQuestJobAndMonsterId(questJobId, monsterId);
  		log.info("QuestJobMonsterItem =" + qjmi);
  		
  		if (null == qjmi) {
  			continue;
  		}
  		log.info("item might drop");
  		
  		//roll to see if item should drop
  		if (!qjmi.didItemDrop()) {
  			log.info("task stage monster didn't drop item. tsm=" + tsm);
  			continue;
  		}
  		//since quest job and monster have item associated with it and the
  		//item "dropped"
  		//return item dropped
  		
  		int itemId = qjmi.getItemId();
  		log.info("item dropped=" + itemId);
  		return itemId;
  	}
  	
  	log.info("no quest job ids sent by client. questJobIds=" + questJobIds);
  	//no item
  	return -1;
  }
  
  //for a monster, choose the reward to give (monster puzzle piece)
  private List<Boolean> generatePuzzlePieces(List<TaskStageMonster> taskStageMonsters,
  		Map<Integer, Integer> tsmIdToItemId, int userId, int taskId,
  		boolean alreadyCompletedMiniTutorialTask) {
  	List<Boolean> piecesDropped = new ArrayList<Boolean>();
  	
  	//ostensibly and explicitly preserve ordering in monsterIds
  	for (TaskStageMonster tsm : taskStageMonsters) {
  		int tsmId = tsm.getId();
  		
  		//I think tsmIdToItemId will always contain entry for tsmId...
  		if (tsmIdToItemId.containsKey(tsmId) && tsmIdToItemId.get(tsmId) > 0) {
  			log.info("not generating monster piece since monster dropped item(s). tsmId=" + tsmId);
  			log.info("item(s)=" + tsmIdToItemId.get(tsmId) + "\t tsm=" + tsm);
  			piecesDropped.add(false);
  			continue;
  		}
  		
  		// When user completes a mini tutorial for the first time
  		// and the mini tutorial guarantees a monster drop, then
  		// the user will only get that monster once. Subsequent
  		// completions of said mini tutorial yield no monster drop.
  		// alreadyCompletedMiniTutorialTask is used to limit number
  		// of db queries the server makes to determine
  		// whether or not the user completed the task
  		if (taskId != ControllerConstants.MINI_TUTORIAL__GUARANTEED_MONSTER_DROP_TASK_ID) {
  			boolean pieceDropped = tsm.didPuzzlePieceDrop();
  			piecesDropped.add(pieceDropped);
  			continue;
  		}

  		boolean pieceDropped = false;
  		if (!alreadyCompletedMiniTutorialTask) {
  			//most likely first time user completed mini tutorial
  			log.info("User completed mini tutorial for first time"
  				+ "...maybe, guaranteeing monster drop if true! taskId="
  				+ taskId + ", taskStageMonsters=" + taskStageMonsters);
  			List<Integer> completedTaskIds = TaskForUserCompletedRetrieveUtils
  				.getAllTaskIdsForUser(userId);
  			if (!completedTaskIds.contains(taskId)) {
  				pieceDropped = true;
  			}
  		}

  		piecesDropped.add(pieceDropped);
  	}
  	
  	return piecesDropped;
  }
  
  private List<Integer> calculateExpGained(List<TaskStageMonster> taskStageMonsters) {
  	
  	List<Integer> individualExps = new ArrayList<Integer>();
  	
	  for (TaskStageMonster tsm : taskStageMonsters) {
		  int expReward = tsm.getExpReward();
		  individualExps.add(expReward);
	  }
	  return individualExps;
  }
  
  private List<Integer> calculateCashGained(List<TaskStageMonster> taskStageMonsters) {
  	
  	List<Integer> individualCash = new ArrayList<Integer>();
  	
	  for (TaskStageMonster tsm : taskStageMonsters) {
		  int cashDrop = tsm.getCashDrop(); 
		  individualCash.add(cashDrop);
	  }
	  return individualCash;
  }
  
  private List<Integer> calculateOilGained(List<TaskStageMonster> taskStageMonsters) {
  	List<Integer> individualOil = new ArrayList<Integer>();
  	
  	for (TaskStageMonster tsm : taskStageMonsters) {
  		int oilDrop = tsm.getOilDrop();
  		individualOil.add(oilDrop);
  	}
  	return individualOil;
  }

  private void recordStages(long userTaskId, Map<Integer, List<Integer>> stageNumsToExps,
  		Map<Integer, List<Integer>> stageNumsToCash,
  		Map<Integer, List<Integer>> stageNumsToOil,
  		Map<Integer, List<Boolean>> stageNumsToPuzzlePiecesDropped,
  		Map<Integer, List<TaskStageMonster>> stageNumsToTaskStageMonsters,
  		Map<Integer, Map<Integer, Integer>> stageNumsToTsmIdToItemId) {

  	Set<Integer> stageNums = stageNumsToExps.keySet();
	  List<Integer> stageNumList = new ArrayList<Integer>(stageNums);
	  Collections.sort(stageNumList);
	  int size = stageNumList.size();
	  
//	  log.info("inserting task_stage_for_user: userTaskId="
//	  		+ userTaskId + "\t stageNumsToSilvers=" + stageNumsToSilvers
//	  		+ "\t stageNumsToExps=" + stageNumsToExps
//	  		+ "\t stageNumsToPuzzlePiecesDropped=" + stageNumsToPuzzlePiecesDropped
//	  		+ "\t stageNumsToMonsterIds=" + stageNumsToMonsterIds);
	  
	  
	  //loop through the individual stages, saving each to the db.
	  for (int i = 0; i < size; i++) {
	  	int stageNum = stageNumList.get(i);
	  	List<TaskStageMonster> taskStageMonsters = stageNumsToTaskStageMonsters.get(stageNum);
	  	List<Integer> expsGained = stageNumsToExps.get(stageNum);
	  	List<Integer> cashGained = stageNumsToCash.get(stageNum);
	  	List<Integer> oilGained = stageNumsToOil.get(stageNum);
	  	List<Boolean> monsterPiecesDropped = stageNumsToPuzzlePiecesDropped.get(stageNum);
	  	Map<Integer, Integer> tsmIdToItemId = stageNumsToTsmIdToItemId.get(stageNum);
	  	
	  	int numStageRows = taskStageMonsters.size();
	  	List<Long> userTaskIds = Collections.nCopies(numStageRows, userTaskId);
	  	List<Integer> repeatedStageNum = Collections.nCopies(numStageRows, stageNum);
	  	
	  	List<Integer> tsmIds = new ArrayList<Integer>();
	  	List<String> monsterTypes = new ArrayList<String>();
	  	for (TaskStageMonster tsm : taskStageMonsters) {
	  		tsmIds.add(tsm.getId());
	  		monsterTypes.add(tsm.getMonsterType());
	  	}
	  	
	  	int num = InsertUtils.get().insertIntoUserTaskStage(userTaskIds, repeatedStageNum,
	  			tsmIds, monsterTypes, expsGained, cashGained, oilGained, monsterPiecesDropped,
	  			tsmIdToItemId);
	  	log.info("for stageNum=" + stageNum + ", inserted " + num + " rows.");
	  }
  }
  
  private void setResponseBuilder(Builder resBuilder, List<Long> userTaskIdList,
		  Map<Integer, TaskStageProto> stageNumsToProtos) {
	  //stuff to send to the client
	  long userTaskId = userTaskIdList.get(0);
	  resBuilder.setUserTaskId(userTaskId);
	  
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
  
  private void writeToUserCurrencyHistory(int userId, User user, int eventId,
		  int taskId, Timestamp curTime, Map<String, Integer> currencyChange,
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
	  String gems = MiscMethods.gems;

//	  if (currencyChange.containsKey(gems)) {
	  currentCurrency.put(gems, user.getGems());
	  reasonsForChanges.put(gems, reason);
	  detailsMap.put(gems, details);
//	  }

	  MiscMethods.writeToUserCurrencyOneUser(userId, curTime, currencyChange, 
			  previousCurrency, currentCurrency, reasonsForChanges, detailsMap);
  }

  public Locker getLocker() {
	  return locker;
  }

  public void setLocker(Locker locker) {
	  this.locker = locker;
  }
  
}
