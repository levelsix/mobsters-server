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
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.BeginDungeonRequestEvent;
import com.lvl6.events.response.BeginDungeonResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.Monster;
import com.lvl6.info.QuestMonsterItem;
import com.lvl6.info.Task;
import com.lvl6.info.TaskForUserOngoing;
import com.lvl6.info.TaskStage;
import com.lvl6.info.TaskStageForUser;
import com.lvl6.info.TaskStageMonster;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.proto.EventDungeonProto.BeginDungeonRequestProto;
import com.lvl6.proto.EventDungeonProto.BeginDungeonResponseProto;
import com.lvl6.proto.EventDungeonProto.BeginDungeonResponseProto.BeginDungeonStatus;
import com.lvl6.proto.EventDungeonProto.BeginDungeonResponseProto.Builder;
import com.lvl6.proto.MonsterStuffProto.MonsterProto.MonsterElement;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.TaskProto.TaskStageProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.TaskForUserOngoingRetrieveUtils;
import com.lvl6.retrieveutils.TaskStageForUserRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.QuestMonsterItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskStageMonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskStageRetrieveUtils;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.InsertUtils;

@Component @DependsOn("gameServer") public class BeginDungeonController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());


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
//    log.info("reqProto=" + reqProto);

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
    MonsterElement elem = reqProto.getElem();
    // if not set, then go select monsters at random
    boolean forceEnemyElem = reqProto.getForceEnemyElem();
    
    //set some values to send to the client (the response proto)
    BeginDungeonResponseProto.Builder resBuilder = BeginDungeonResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setTaskId(taskId);
    resBuilder.setStatus(BeginDungeonStatus.FAIL_OTHER); //default

    server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    try {
      User aUser = RetrieveUtils.userRetrieveUtils().getUserById(userId);
      Task aTask = TaskRetrieveUtils.getTaskForTaskId(taskId);

      Map<Integer, TaskStage> tsMap = new HashMap<Integer, TaskStage>();
      boolean legit = checkLegit(resBuilder, aUser, userId, aTask, taskId, tsMap,
      		isEvent, eventId, gemsSpent);

      List<Long> userTaskIdList = new ArrayList<Long>();
      Map<Integer, TaskStageProto> stageNumsToProtos = new HashMap<Integer, TaskStageProto>();
      boolean successful = false;
      if(legit) {
    	  
      	//determine the specifics for each stage (stored in stageNumsToProtos)
      	//then record specifics in db
    	  successful = writeChangesToDb(aUser, userId, aTask, taskId, tsMap, curTime,
    			  isEvent, eventId, gemsSpent, userTaskIdList, stageNumsToProtos, questIds,
    			  elem, forceEnemyElem);
      }
      if (successful) {
    	  setResponseBuilder(resBuilder, userTaskIdList, stageNumsToProtos);
      }
      
      BeginDungeonResponseEvent resEvent = new BeginDungeonResponseEvent(userId);
      resEvent.setTag(event.getTag());
      resEvent.setBeginDungeonResponseProto(resBuilder.build());
      server.writeEvent(resEvent);

      if (0 != gemsSpent) {
      	UpdateClientUserResponseEvent resEventUpdate = MiscMethods
      			.createUpdateClientUserResponseEventAndUpdateLeaderboard(aUser);
      	resEventUpdate.setTag(event.getTag());
      	server.writeEvent(resEventUpdate);
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
      server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
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
    //right now just relying on user
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
  	int numRevives = aTaskForUser.getNumRevives();
  	Date aDate = aTaskForUser.getStartDate(); //shouldn't null
  	Timestamp startTime = null;
  	if (null != aDate) {
  		startTime = new Timestamp(aDate.getTime());
  	}
  	Timestamp endTime = null;
  	boolean userWon = false;
  	boolean cancelled = true;
  	
  	int num = DeleteUtils.get().deleteTaskForUserOngoingWithTaskForUserId(taskForUserId);
  	log.warn("deleted existing task_for_user. taskForUser=" + aTaskForUser +
  			"\t (should be 1) numDeleted=" + num);
  	//meh, fogedaboudit 
    num = InsertUtils.get().insertIntoTaskHistory(taskForUserId, userId, taskId,
    		expGained, cashGained, numRevives, startTime, endTime, userWon, cancelled);
    log.warn("inserted into task_history. taskForUser=" + aTaskForUser +
  			"\t (should be 1) numInserted=" + num);
  }
  
  private void deleteExistingTaskStagesForUser(long taskForUserId) {
  	List<TaskStageForUser> taskStages = TaskStageForUserRetrieveUtils
  			.getTaskStagesForUserWithTaskForUserId(taskForUserId);

  	List<Long> userTaskStageId = new ArrayList<Long>();
  	List<Long> userTaskId = new ArrayList<Long>();
  	List<Integer> stageNum = new ArrayList<Integer>();
  	List<Integer> taskStageMonsterIdList = new ArrayList<Integer>();
  	List<String> monsterTypes = new ArrayList<String>();
  	List<Integer> expGained = new ArrayList<Integer>();
  	List<Integer> cashGained = new ArrayList<Integer>();
  	List<Boolean> monsterPieceDropped = new ArrayList<Boolean>();
  	List<Integer> itemIdDropped = new ArrayList<Integer>();

  	for (int i = 0; i < taskStages.size(); i++) {
  		TaskStageForUser tsfu = taskStages.get(i);
  		userTaskStageId.add(tsfu.getId());
  		userTaskId.add(tsfu.getUserTaskId());
  		stageNum.add(tsfu.getStageNum());

  		int monsterId = tsfu.getTaskStageMonsterId();
  		taskStageMonsterIdList.add(monsterId);
  		monsterTypes.add(tsfu.getMonsterType());
  		expGained.add(tsfu.getExpGained());
  		cashGained.add(tsfu.getCashGained());
  		boolean dropped = tsfu.isMonsterPieceDropped();
  		monsterPieceDropped.add(dropped);
  		itemIdDropped.add(tsfu.getItemIdDropped());
  		
  	}
  	
  	int num = DeleteUtils.get().deleteTaskStagesForUserWithIds(userTaskStageId);
  	log.warn("num task stage history rows deleted: num=" + num +
  			"taskStageForUser=" + taskStages);

  	num = InsertUtils.get().insertIntoTaskStageHistory(userTaskStageId,
  			userTaskId, stageNum, taskStageMonsterIdList, monsterTypes, expGained,
  			cashGained, monsterPieceDropped, itemIdDropped);
  	log.warn("num task stage history rows inserted: num=" + num +
  			"taskStageForUser=" + taskStages);
  }
  
  private boolean writeChangesToDb(User u, int uId, Task t, int tId,
		  Map<Integer, TaskStage> tsMap, Timestamp clientTime, boolean isEvent, int eventId,
		  int gemsSpent, List<Long> utIdList, Map<Integer, TaskStageProto> stageNumsToProtos,
		  List<Integer> questIds, MonsterElement elem, boolean forceEnemyElem) {
	  
	  //local vars storing eventual db data (accounting for multiple monsters in stage)
	  Map<Integer, List<Integer>> stageNumsToSilvers = new HashMap<Integer, List<Integer>>();
	  Map<Integer, List<Integer>> stageNumsToExps = new HashMap<Integer, List<Integer>>();
	  Map<Integer, List<Boolean>> stageNumsToPuzzlePiecesDropped = new HashMap<Integer, List<Boolean>>();
	  Map<Integer, List<TaskStageMonster>> stageNumsToTaskStageMonsters = new HashMap<Integer, List<TaskStageMonster>>();
	  Map<Integer, Map<Integer, List<Integer>>> stageNumsToTaskStageMonsterIdToItemId = 
	  		new HashMap<Integer, Map<Integer, List<Integer>>>();
	  
	  //calculate the SINGLE monster the user fights in each stage
	  Map<Integer, TaskStageProto> stageNumsToProtosTemp = generateStage(questIds,
			  tsMap, stageNumsToSilvers, stageNumsToExps, stageNumsToPuzzlePiecesDropped,
			  stageNumsToTaskStageMonsters, stageNumsToTaskStageMonsterIdToItemId,
			  elem, forceEnemyElem);
	  
	  //calculate the exp that the user could gain for this task
	  int expGained = MiscMethods.sumListsInMap(stageNumsToExps);
	  //calculate the cash that the user could gain for this task
	  int cashGained = MiscMethods.sumListsInMap(stageNumsToSilvers);
	  
	  //don't know why this is used
//	  if (!u.updateRelativeCoinsExpTaskscompleted(0, 0, 0, clientTime)) {
//		  log.error("problem with updating user stats post-task. cashGained="
//				  + 0 + ", expGained=" + 0 + ", increased tasks completed by 0," +
//				  ", clientTime=" + clientTime + ", user=" + u);
//		  return false;
//	  }

	  //record into user_task table	  
	  long userTaskId = InsertUtils.get().insertIntoUserTaskReturnId(uId, tId,
			  expGained, cashGained, clientTime);
	  
	  //record into user_task stage table
	  recordStages(userTaskId, stageNumsToSilvers, stageNumsToExps,
	  		stageNumsToPuzzlePiecesDropped, stageNumsToTaskStageMonsters,
	  		stageNumsToTaskStageMonsterIdToItemId);
	  
	  //send stuff back up to caller
	  utIdList.add(userTaskId);
	  stageNumsToProtos.putAll(stageNumsToProtosTemp);
	  
	  //start the cool down timer if for event
	  if (isEvent) {
	  	int numInserted = InsertUtils.get().insertIntoUpdateEventPersistentForUser(uId, eventId, clientTime);
	  	log.info("started cool down timer for (eventId, userId): " + uId + "," + eventId +
	  			"\t numInserted=" + numInserted);

	  	if (0 != gemsSpent) {
	  		int gemChange = -1 * gemsSpent;
	  		boolean success = updateUser(u, gemChange);
	  		log.info("successfully upgraded user gems to reset event cool down timer? Answer:" + success);
	  	}
	  	
	  }
	  
	  return true;
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
  		Map<Integer, TaskStage> tsMap, Map<Integer, List<Integer>> stageNumsToSilvers,
  		Map<Integer, List<Integer>> stageNumsToExps,
		  Map<Integer, List<Boolean>> stageNumsToPuzzlePieceDropped,
		  Map<Integer, List<TaskStageMonster>> stageNumsToTaskStageMonsters,
		  Map<Integer, Map<Integer, List<Integer>>> stageNumsToTaskStageMonsterIdToItemId,
		  MonsterElement elem, boolean forceEnemyElem) {
	  Map<Integer, TaskStageProto> stageNumsToProtos = new HashMap<Integer, TaskStageProto>();
	  Random rand = new Random();
	  
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
		  Map<Integer, List<Integer>> taskStageMonsterIdToItemId = new HashMap<Integer, List<Integer>>();
		  generateItems(spawnedTaskStageMonsters, questIds, taskStageMonsterIdToItemId);
		  
		  //randomly select a reward, IF ANY, that this monster can drop;
		  //if an item drops puzzle piece does not drop.
		  List<Boolean> puzzlePiecesDropped = generatePuzzlePieces(spawnedTaskStageMonsters,
		  		taskStageMonsterIdToItemId);
		  
		  List<Integer> individualExps = calculateExpGained(spawnedTaskStageMonsters);
		  List<Integer> individualSilvers =  calculateSilverGained(spawnedTaskStageMonsters);
		  
		  
		  //create the proto
		  TaskStageProto tsp = CreateInfoProtoUtils.createTaskStageProto(tsId,
				  ts, spawnedTaskStageMonsters, puzzlePiecesDropped, individualSilvers,
				  taskStageMonsterIdToItemId);
		  
		  log.info("task stage proto=" + tsp); 
		  log.info("after tsp taskStageMonsterIdToItemId=" + taskStageMonsterIdToItemId);
		  
		  //NOTE, all the sizes are equal:
		  //individualSilvers.size() == individualExps.size() == puzzlePiecesDropped.size()
		  // == spawnedTaskStageMonsters.size()
		  //update the protos to return to parent function
		  stageNumsToProtos.put(stageNum, tsp);
		  stageNumsToSilvers.put(stageNum, individualSilvers);
		  stageNumsToExps.put(stageNum, individualExps);
		  stageNumsToPuzzlePieceDropped.put(stageNum, puzzlePiecesDropped);
		  stageNumsToTaskStageMonsters.put(stageNum, spawnedTaskStageMonsters);
		  stageNumsToTaskStageMonsterIdToItemId.put(stageNum, taskStageMonsterIdToItemId);
	  }
	  
	  return stageNumsToProtos;
  }
  
  private void selectElementMonster(List<TaskStageMonster> taskStageMonsters,
  		MonsterElement elem, List<TaskStageMonster> spawnedTaskStageMonsters) {
  	
  	for (TaskStageMonster tsm : taskStageMonsters) {
  		
  		int monsterId = tsm.getMonsterId();
  		MonsterElement mon = MonsterRetrieveUtils.getMonsterElementForMonsterId(monsterId);
  		
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
  		List<Integer> questIds, Map<Integer, List<Integer>> taskStageMonsterIdToItemId) {
  	
  	//no quest ids means no items (empty map)
  	if (null == questIds || questIds.isEmpty()) {
  		return; 
  	}
  	
  	for (int index = 0; index < taskStageMonsters.size(); index++) {
  		TaskStageMonster tsm = taskStageMonsters.get(index);
  		
  		//determine the item that this monster drops, if any, (-1 means no item drop)
  		int itemId = generateQuestMonsterItem(questIds, tsm);
  		int tsmId = tsm.getId();
  		
  		//hacky way of accounting for multiple identical task stage monsters that
  		//can drop one item
  		if (!taskStageMonsterIdToItemId.containsKey(tsmId)) {
  			taskStageMonsterIdToItemId.put(tsmId, new ArrayList<Integer>());
  		}
  		
  		List<Integer> itemIds = taskStageMonsterIdToItemId.get(tsmId);
  		itemIds.add(itemId);
  	}
  	log.info("generate items taskStageMonsterIdToItemId=" + taskStageMonsterIdToItemId);
  }

  //see if quest id and monster id have an item. if yes, see if it drops. If it drops
  //return the item id. 
  //default return -1
  private int generateQuestMonsterItem(List<Integer> questIds, TaskStageMonster tsm) {
  	
  	int monsterId = tsm.getMonsterId();
  	for (int questId : questIds) {
  		
  		QuestMonsterItem qmi = QuestMonsterItemRetrieveUtils
  				.getItemForQuestAndMonsterId(questId, monsterId);
  		log.info("quest monster item =" + qmi);
  		
  		if (null == qmi) {
  			continue;
  		}
  		log.info("item might drop");
  		
  	  //roll to see if item should drop
  		if (!qmi.didItemDrop()) {
  			log.info("task stage monster didn't drop item. tsm=" + tsm);
  			continue;
  		}
  		//since quest and monster have item associated with it and the item "dropped"
  		//return this
  		
  		int itemId = qmi.getItemId();
  		log.info("item dropped=" + itemId);
  		return itemId;
  	}
  	
  	log.info("no quest ids");
  	//no item
  	return -1;
  }
  
  //for a monster, choose the reward to give (monster puzzle piece)
  private List<Boolean> generatePuzzlePieces(List<TaskStageMonster> taskStageMonsters,
  		Map<Integer, List<Integer>> taskStageMonsterIdToItemId) {
  	List<Boolean> piecesDropped = new ArrayList<Boolean>();
  	
  	//ostensibly and explicitly preserve ordering in monsterIds
  	for (TaskStageMonster tsm : taskStageMonsters) {
  		int tsmId = tsm.getId();
  		
  		if (taskStageMonsterIdToItemId.containsKey(tsmId)) {
  			log.info("not generating monster piece since monster dropped item(s). tsmId=" + tsmId);
  			log.info("item(s)=" + taskStageMonsterIdToItemId.get(tsmId));
  			piecesDropped.add(false);
  			continue;
  		}
  		
  		boolean pieceDropped = tsm.didPuzzlePieceDrop();
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
  
  private List<Integer> calculateSilverGained(List<TaskStageMonster> taskStageMonsters) {
  	
  	List<Integer> individualSilvers = new ArrayList<Integer>();
  	
	  for (TaskStageMonster tsm : taskStageMonsters) {
		  int cashDrop = tsm.getCashDrop(); 
		  individualSilvers.add(cashDrop);
	  }
	  return individualSilvers;
  }
  
  private void recordStages(long userTaskId, Map<Integer, List<Integer>> stageNumsToSilvers,
  		Map<Integer, List<Integer>> stageNumsToExps,
  		Map<Integer, List<Boolean>> stageNumsToPuzzlePiecesDropped,
  		Map<Integer, List<TaskStageMonster>> stageNumsToTaskStageMonsters,
  		Map<Integer, Map<Integer, List<Integer>>> stageNumsToTaskStageMonsterIdToItemId) {
  	
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
	  	List<Integer> cashGained = stageNumsToSilvers.get(stageNum);
	  	List<Boolean> monsterPiecesDropped = stageNumsToPuzzlePiecesDropped.get(stageNum);
	  	Map<Integer, List<Integer>> taskStageMonsterIdToItemId = 
	  			stageNumsToTaskStageMonsterIdToItemId.get(stageNum);
	  	
	  	int numStageRows = taskStageMonsters.size();
	  	List<Long> userTaskIds = Collections.nCopies(numStageRows, userTaskId);
	  	List<Integer> repeatedStageNum = Collections.nCopies(numStageRows, stageNum);
	  	
	  	List<Integer> taskStageMonsterIds = new ArrayList<Integer>();
	  	List<String> monsterTypes = new ArrayList<String>();
	  	for (TaskStageMonster tsm : taskStageMonsters) {
        taskStageMonsterIds.add(tsm.getId());
        monsterTypes.add(tsm.getMonsterType().name());
      }
	  	
	  	int num = InsertUtils.get().insertIntoUserTaskStage(userTaskIds,
	  			repeatedStageNum, taskStageMonsterIds, monsterTypes, expsGained,
	  			cashGained, monsterPiecesDropped, taskStageMonsterIdToItemId);
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
  
}
