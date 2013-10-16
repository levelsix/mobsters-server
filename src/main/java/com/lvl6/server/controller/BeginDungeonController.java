package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
import com.lvl6.info.Task;
import com.lvl6.info.TaskStage;
import com.lvl6.info.User;
import com.lvl6.info.UserTask;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventDungeonProto.BeginDungeonRequestProto;
import com.lvl6.proto.EventDungeonProto.BeginDungeonResponseProto;
import com.lvl6.proto.EventDungeonProto.BeginDungeonResponseProto.BeginDungeonStatus;
import com.lvl6.proto.EventDungeonProto.BeginDungeonResponseProto.Builder;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.proto.TaskProto.TaskStageProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.retrieveutils.TaskForUserRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskStageMonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskStageRetrieveUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.InsertUtils;

@Component @DependsOn("gameServer") public class BeginDungeonController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());


  public BeginDungeonController() {
    numAllocatedThreads = 4;
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

    //get values sent from the client (the request proto)
    MinimumUserProto senderProto = reqProto.getSender();
    int userId = senderProto.getUserId();
    Timestamp curTime = new Timestamp(reqProto.getClientTime());
    int taskId = reqProto.getTaskId();

    //set some values to send to the client (the response proto)
    BeginDungeonResponseProto.Builder resBuilder = BeginDungeonResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setStatus(BeginDungeonStatus.FAIL_OTHER); //default

    server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    try {
      User aUser = RetrieveUtils.userRetrieveUtils().getUserById(userId);
      Task aTask = TaskRetrieveUtils.getTaskForTaskId(taskId);
//      int previousSilver = 0;
//      int previousGold = 0;

      //userBossList should be populated if successful
      Map<Integer, TaskStage> tsMap = new HashMap<Integer, TaskStage>();
      boolean legit = checkLegit(resBuilder, aUser, userId, aTask,
    		  taskId, tsMap);

      List<Long> userTaskIdList = new ArrayList<Long>();
      Map<Integer, TaskStageProto> stageNumsToProtos = new HashMap<Integer, TaskStageProto>();
      boolean successful = false;
      if(legit) {
    	  
//        previousSilver = aUser.getCoins() + aUser.getVaultBalance();
//        previousGold = aUser.getDiamonds();
      	//determine the specifics for each stage (stored in stageNumsToProtos)
      	//then record specifics in db
    	  successful = writeChangesToDb(aUser, userId, aTask, taskId, tsMap, curTime,
    			  userTaskIdList, stageNumsToProtos);
//        writeToUserCurrencyHistory(aUser, money, curTime, previousSilver, previousGold);
      }
      if (successful) {
    	  setResponseBuilder(resBuilder, userTaskIdList, stageNumsToProtos);
      }
      
      BeginDungeonResponseEvent resEvent = new BeginDungeonResponseEvent(userId);
      resEvent.setTag(event.getTag());
      resEvent.setBeginDungeonResponseProto(resBuilder.build());
      server.writeEvent(resEvent);

      UpdateClientUserResponseEvent resEventUpdate = MiscMethods
          .createUpdateClientUserResponseEventAndUpdateLeaderboard(aUser);
      resEventUpdate.setTag(event.getTag());
      server.writeEvent(resEventUpdate);
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
		  int taskId, Map<Integer, TaskStage> tsMap) {
    if (null == u || null == aTask) {
      log.error("unexpected error: user or task is null. user=" + u + "\t task="+ aTask);
      return false;
    }
    
    Map<Integer, TaskStage> ts = TaskStageRetrieveUtils.getTaskStagesForTaskId(taskId);
    if (null == ts) {
    	log.error("unexpected error: task has no taskStages. task=" + aTask);
    	return false;
    }
    
    UserTask aUserTask = TaskForUserRetrieveUtils.getUserTaskForUserId(userId);
    if(null != aUserTask) {
      log.error("unexpected error: user has existing task when beginning another. " +
      		"No task should exist. user=" + u + "\t task=" + aTask + "\t userTask=" + aUserTask);
      //TODO: DELETE TASK AND PUT IT INTO USER TASK HISTORY
    }
    
    tsMap.putAll(ts);
    resBuilder.setStatus(BeginDungeonStatus.SUCCESS);
    return true;
  }
  
  private boolean writeChangesToDb(User u, int uId, Task t, int tId,
		  Map<Integer, TaskStage> tsMap, Timestamp clientTime, List<Long> utIdList,
		  Map<Integer, TaskStageProto> stageNumsToProtos) {
	  
	  //local vars storing eventual db data (accounting for multiple monsters in stage)
	  Map<Integer, List<Integer>> stageNumsToSilvers = new HashMap<Integer, List<Integer>>();
	  Map<Integer, List<Integer>> stageNumsToExps = new HashMap<Integer, List<Integer>>();
	  Map<Integer, List<Boolean>> stageNumsToPuzzlePiecesDropped = new HashMap<Integer, List<Boolean>>();
	  Map<Integer, List<Integer>> stageNumsToMonsterIds = new HashMap<Integer, List<Integer>>();
	  
	  //calculate the SINGLE monster the user fights in each stage
	  Map<Integer, TaskStageProto> stageNumsToProtosTemp = generateStage(
			  tsMap, stageNumsToSilvers, stageNumsToExps,
			  stageNumsToPuzzlePiecesDropped, stageNumsToMonsterIds);
	  
	  //calculate the exp that the user could gain for this task
	  int expGained = MiscMethods.sumListsInMap(stageNumsToExps);
	  //calculate the silver that the user could gain for this task
	  int silverGained = MiscMethods.sumListsInMap(stageNumsToSilvers);
	  
	  
	  if (!u.updateRelativeCoinsExpTaskscompleted(0, 0, 0, clientTime)) {
		  log.error("problem with updating user stats post-task. silverGained="
				  + 0 + ", expGained=" + 0 + ", increased tasks completed by 0," +
				  ", clientTime=" + clientTime + ", user=" + u);
		  return false;
	  }

	  //record into user_task table	  
	  long userTaskId = InsertUtils.get().insertIntoUserTaskReturnId(uId, tId,
			  expGained, silverGained, clientTime);
	  
	  //record into user_task stage table
	  recordStages(userTaskId, stageNumsToSilvers, stageNumsToExps,
	  		stageNumsToPuzzlePiecesDropped, stageNumsToMonsterIds);
	  
	  //send stuff back up to caller
	  utIdList.add(userTaskId);
	  stageNumsToProtos.putAll(stageNumsToProtosTemp);
	  return true;
  }
  
  //stage can have multiple monsters; stage has a drop rate (but is useless for now); 
  //for each stage do the following
  //1) select monster at random
  //1a) determine if monster drops a puzzle piece
  //2) create MonsterProto
  //3) create TaskStageProto with the MonsterProto
  private Map<Integer, TaskStageProto> generateStage (
  		Map<Integer, TaskStage> tsMap, Map<Integer, List<Integer>> stageNumsToSilvers,
  		Map<Integer, List<Integer>> stageNumsToExps,
		  Map<Integer, List<Boolean>> stageNumsToPuzzlePieceDropped,
		  Map<Integer, List<Integer>> stageNumsToMonsterIds) {
	  Map<Integer, TaskStageProto> stageNumsToProtos = new HashMap<Integer, TaskStageProto>();
	  Random rand = new Random();
	  
	  //for each stage, calculate the monster(s) the user will face and
	  //reward(s) that might be given if the user wins
	  for (int tsId : tsMap.keySet()) {
		  TaskStage ts = tsMap.get(tsId);
		  int stageNum = ts.getStageNum();
		  
		  //select one monster, at random. This is the ONE monster for this stage
		  List<Integer> monsterIds = 
				  TaskStageMonsterRetrieveUtils.getMonsterIdsForTaskStageId(tsId);
		  int quantity = 1; //change value to increase monsters spawned
		  List<Integer> spawnedMonsterIds = fairlyPickMonsters(monsterIds, rand, quantity);
		  
		  
		  /*Code below is done such that if more than one monster is generated
		    above, then user has potential to get the silver and exp from all
		    the monsters including the one above.*/
		  
		  //randomly select a reward, IF ANY, that this monster can drop;
		  List<Boolean> puzzlePiecesDropped = generatePuzzlePieces(spawnedMonsterIds);
		  
		  //determine how much exp and silver the user gets
		  Set<Integer> uniqMonsterIds = new HashSet<Integer>(monsterIds);
		  Map<Integer, Monster> monsterIdsToMonsters =
				  MonsterRetrieveUtils.getMonstersForMonsterIds(uniqMonsterIds);
		  
		  List<Integer> individualExps = calculateExpGained(monsterIds, monsterIdsToMonsters);
		  List<Integer> individualSilvers =  calculateSilverGained(monsterIds, monsterIdsToMonsters);
		  
		  //create the proto
		  TaskStageProto tsp = CreateInfoProtoUtils.createTaskStageProto(tsId,
				  ts, monsterIds, monsterIdsToMonsters, puzzlePiecesDropped,
				  individualSilvers);
		  
		  //update the protos to return to parent function
		  stageNumsToProtos.put(stageNum, tsp);
		  stageNumsToSilvers.put(stageNum, individualSilvers);
		  stageNumsToExps.put(stageNum, individualExps);
		  stageNumsToPuzzlePieceDropped.put(stageNum, puzzlePiecesDropped);
		  stageNumsToMonsterIds.put(stageNum, spawnedMonsterIds);
	  }
	  
	  return stageNumsToProtos;
  }
  
  private List<Integer> fairlyPickMonsters(List<Integer> monsterIds,
  		Random rand, int quantity) {
  	//efficiency check. If only n monsters and want n, return input.
  	if (monsterIds.size() == quantity) {
  		return monsterIds;
  	}
  	
  	//return value
  	List<Integer> selectedMonsterIds = new ArrayList<Integer>();
  	
  	List<Integer> copyMonsterIds = new ArrayList<Integer>(monsterIds);
  	for (int i = 0; i < quantity; i++) {
  		//select random index
  		int randInt = rand.nextInt(copyMonsterIds.size());
  		int luckyId = copyMonsterIds.get(randInt);
  		
  		//remove the selected id
  		copyMonsterIds.remove(randInt);
  		selectedMonsterIds.add(luckyId);
  		
  	}
	  return selectedMonsterIds;
  }
  
  //for a monster, choose the reward to give (monster puzzle piece)
  private List<Boolean> generatePuzzlePieces(List<Integer> monsterIds) {
  	List<Boolean> piecesDropped = new ArrayList<Boolean>();
  	
  	Map<Integer, Monster> monsterIdsToMonsters =
  			MonsterRetrieveUtils.getMonstersForMonsterIds(monsterIds);
  	
  	//ostensibly and explicitly preserve ordering in monsterIds
  	for (int i = 0; i < monsterIds.size(); i++) {
  		boolean pieceDropped = false;
  		
  		int id = monsterIds.get(i);
  		//to protect against a nonexistent monster, thus null pointer
  		if (monsterIdsToMonsters.containsKey(id)) {
  			Monster m = monsterIdsToMonsters.get(id);
  			pieceDropped = m.didPuzzlePieceDrop();
  		}
  		piecesDropped.add(pieceDropped);
  	}
  	
  	return piecesDropped;
  }
  
  private List<Integer> calculateExpGained(List<Integer> monsterIds,
		  Map<Integer, Monster> monsterIdsToMonsters) {
  	
  	List<Integer> individualExps = new ArrayList<Integer>();
  	
	  for (int monsterId : monsterIds) {
		  Monster m = monsterIdsToMonsters.get(monsterId);
		  int expReward = m.getExpReward();
		  individualExps.add(expReward);
	  }
	  return individualExps;
  }
  
  private List<Integer> calculateSilverGained(List<Integer> monsterIds,
		  Map<Integer, Monster> monsterIdsToMonsters) {
  	
  	List<Integer> individualSilvers = new ArrayList<Integer>();
  	
	  for (int i = 0; i < monsterIds.size(); i++) {
		  int monsterId = monsterIds.get(i);
		  Monster m = monsterIdsToMonsters.get(monsterId);
		  int silverDrop = m.getSilverDrop(); 
		  individualSilvers.add(silverDrop);
	  }
	  return individualSilvers;
  }
  
  private void recordStages(long userTaskId, Map<Integer, List<Integer>> stageNumsToSilvers,
  		Map<Integer, List<Integer>> stageNumsToExps,
  		Map<Integer, List<Boolean>> stageNumsToPuzzlePiecesDropped,
  		Map<Integer, List<Integer>> stageNumsToMonsterIds) {
  	Set<Integer> stageNums = stageNumsToExps.keySet();
	  List<Integer> stageNumList = new ArrayList<Integer>(stageNums);
	  Collections.sort(stageNumList);
	  int size = stageNumList.size();
	  
	  
	  List<Long> userTaskIds = Collections.nCopies(size, userTaskId);
	  //loop through the individual stages, saving each to the db.
	  for (int i = 0; i < size; i++) {
	  	int stageNum = stageNumList.get(i);
	  	List<Integer> repeatedStageNum = Collections.nCopies(size, stageNum);
	  	List<Integer> monsterIds = stageNumsToMonsterIds.get(stageNum);
	  	List<Integer> expsGained = stageNumsToExps.get(stageNum);
	  	List<Integer> silverGained = stageNumsToSilvers.get(stageNum);
	  	List<Boolean> monsterPiecesDropped = stageNumsToPuzzlePiecesDropped.get(stageNum);
	  	
	  	InsertUtils.get().insertIntoUserTaskStage(userTaskIds, repeatedStageNum,
	  			monsterIds, expsGained, silverGained, monsterPiecesDropped);
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
  
  private void writeToUserCurrencyHistory(User aUser, Map<String, Integer> money, Timestamp curTime,
      int previousSilver, int previousGold) {
    Map<String, Integer> previousGoldSilver = new HashMap<String, Integer>();
    Map<String, String> reasonsForChanges = new HashMap<String, String>();
    String reasonForChange = ControllerConstants.UCHRFC__BOSS_ACTION;
    String gold = MiscMethods.gold;
    String silver = MiscMethods.silver;

    previousGoldSilver.put(gold, previousGold);
    previousGoldSilver.put(silver, previousSilver);
    reasonsForChanges.put(gold, reasonForChange);
    reasonsForChanges.put(silver, reasonForChange);

    MiscMethods.writeToUserCurrencyOneUserGoldAndOrSilver(aUser, curTime, money, 
        previousGoldSilver, reasonsForChanges);

  }
}
