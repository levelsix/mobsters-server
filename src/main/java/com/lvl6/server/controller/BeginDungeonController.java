package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
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
import com.lvl6.info.MonsterReward;
import com.lvl6.info.Task;
import com.lvl6.info.TaskStage;
import com.lvl6.info.User;
import com.lvl6.info.UserTask;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventProto.BeginDungeonRequestProto;
import com.lvl6.proto.EventProto.BeginDungeonResponseProto;
import com.lvl6.proto.EventProto.BeginDungeonResponseProto.BeginDungeonStatus;
import com.lvl6.proto.EventProto.BeginDungeonResponseProto.Builder;
import com.lvl6.proto.InfoProto.MinimumUserProto;
import com.lvl6.proto.InfoProto.TaskStageProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.retrieveutils.UserTaskRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterRewardRetrieveUtils;
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
    
    //copy pasted from PickLockBoxController.java Pick()
//    if (!MiscMethods.checkClientTimeAroundApproximateNow(curTime)) {
//      log.error("client time too apart of server time. client time =" + curTime + ", servertime~="
//          + new Date());
//      resBuilder.setStatus(BeginDungeonStatus.FAIL_CLIENT_TOO_APART_FROM_SERVER_TIME);
//      return false;
//    } 
    if(!userHasSufficientStamergy(u, aTask)) {
      log.error("user error: use does not have enough stamergy for task" +
          "user stamergy=" + u.getEnergy() + "\t task=" + aTask);
      resBuilder.setStatus(BeginDungeonStatus.FAIL_INSUFFICIENT_STAMERGY);
      return false;
    }

    //In order to attack boss, user needs to rank up a city. When ranking
    //up a city, a user_boss entry should be created/updated.
    //Ergo entry in user_bosses should exist when this controller executes.
    UserTask aUserTask = UserTaskRetrieveUtils.getUserTaskForUserId(userId);
    if(null != aUserTask) {
      log.error("unexpected error: user has existing task when beginning another. " +
      		"No task should exist. user=" + u + "\t task=" + aTask + "\t userTask=" + aUserTask);
      //TODO: DELETE TASK AND PUT IT INTO USER TASK HISTORY
    }
    
    tsMap.putAll(ts);
    resBuilder.setStatus(BeginDungeonStatus.SUCCESS);
    return true;
  }

  /* 
   * Return true if user has energy >= to energy cost to attack boss
   */
  private boolean userHasSufficientStamergy(User u, Task t) {
    int stamergyCost = t.getEnergyCost();
    int userEnergy = u.getEnergy();
    
    boolean enoughStamergy = userEnergy >= stamergyCost;
    return enoughStamergy;
  }
  
  private boolean writeChangesToDb(User u, int uId, Task t, int tId,
		  Map<Integer, TaskStage> tsMap, Timestamp clientTime, List<Long> utIdList,
		  Map<Integer, TaskStageProto> stageNumsToProtos) {
	  
	  //local vars storing eventual db data
	  Map<Integer, Integer> stageNumsToSilvers = new HashMap<Integer, Integer>();
	  Map<Integer, Integer> stageNumsToExps = new HashMap<Integer, Integer>();
	  Map<Integer, Integer> stageNumsToEquipIds= new HashMap<Integer, Integer>();
	  
	  //calculate the SINGLE monster the user fights in each stage
	  Map<Integer, TaskStageProto> stageNumsToProtosTemp = generateStage(
			  tsMap, stageNumsToSilvers, stageNumsToExps, stageNumsToEquipIds);
	  //calculate the exp that the user could gain for this task
	  int expGained = MiscMethods.sumMap(stageNumsToExps);
	  //calculate the silver that the user could gain for this task
	  int silverGained = MiscMethods.sumMap(stageNumsToSilvers);
	  
	  //charge the user's stamergy
	  boolean simulateEnergyRefill =
			  (u.getEnergy() == u.getEnergyMax());
	  int energyChange = t.getEnergyCost() * -1;
	  if (!u.updateRelativeCoinsExpTaskscompletedEnergySimulateenergyrefill(
			  0, 0, 0, energyChange, simulateEnergyRefill, clientTime)) {
		  log.error("problem with updating user stats post-task. silverGained="
				  + 0 + ", expGained=" + 0 + ", increased tasks completed by 0," +
				  " energyChange=" + energyChange + ", clientTime=" + clientTime +
				  ", simulateEnergyRefill=" + simulateEnergyRefill + ", user=" + u);
		  return false;
	  }

	  //record into user_task stage table	  
	  int stageNums = tsMap.size();
	  long userTaskId = InsertUtils.get().insertIntoUserTask(uId, tId,
			  stageNumsToEquipIds, stageNumsToExps, stageNumsToSilvers,
			  expGained, silverGained, clientTime, stageNums);
	  
	  //send stuff back up to caller
	  utIdList.add(userTaskId);
	  stageNumsToProtos.putAll(stageNumsToProtosTemp);
	  return true;
  }
  
  //stage can have multiple monsters; stage has a drop rate (but is useless for now); 
  //for each stage do the following
  //1) generate random number (rn1), 
  //2) if it indicates gem dropped select one of the monsters fairly
  //3) then for that monster generate random number (rn2), 
  //4) go through rewards tied to that monster accumulating the drop rates
  //5) select current reward when accumulated drop rate exceeds random number
  //generated (rn2)
  //1) select monster at random
  //1a) determine if monster drops equip
  //2) create MonsterProto
  //3) create TaskStageProto with the MonsterProto
  private Map<Integer, TaskStageProto> generateStage (
  		Map<Integer, TaskStage> tsMap, Map<Integer, Integer> stageNumsToSilvers,
  		Map<Integer, Integer> stageNumsToExps,
		  Map<Integer, Integer> stageNumsToEquipIds) {
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
		  int monsterId = fairlyPickMonster(monsterIds, rand);
		  
		  //randomly select a reward, IF ANY, that this monster can drop;
		  int equipId = selectMonsterEquipReward(monsterId, rand);
		  
		  //don't do these two lines if we want to allow user to see more than
		  //one monster
		  monsterIds.clear();
		  monsterIds.add(monsterId);
		  /*Code below is done such that if more than one monster is generated
		    above, then user has potential to get the silver and exp from all
		    the monsters including the one above.*/
		  //determine how much exp and silver the user gets
		  Set<Integer> uniqMonsterIds = new HashSet<Integer>(monsterIds);
		  Map<Integer, Monster> monsterIdsToMonsters =
				  MonsterRetrieveUtils.getMonstersForMonsterIds(uniqMonsterIds);
		  
		  int expGained = calculateExpGained(monsterIds, monsterIdsToMonsters);
		  List<Integer> individualSilvers = new ArrayList<Integer>();
		  int silverGained = calculateSilverGained(monsterIds, monsterIdsToMonsters,
				  individualSilvers);
		  
		  //create the proto
		  Map<Integer, Integer> monsterIdsToEquipIds = new HashMap<Integer, Integer>();
		  monsterIdsToEquipIds.put(monsterId, equipId);
		  //if stage has 2 of monster1, only one monster1 drops an equip
		  boolean allowDuplicateMonsterToDropEquip = false;
		  TaskStageProto tsp = CreateInfoProtoUtils.createTaskStageProto(tsId,
				  ts, monsterIds, monsterIdsToMonsters, monsterIdsToEquipIds,
				  individualSilvers, allowDuplicateMonsterToDropEquip);
		  
		  //update the protos to return to parent function
		  stageNumsToProtos.put(stageNum, tsp);
		  stageNumsToSilvers.put(stageNum, silverGained);
		  stageNumsToExps.put(stageNum, expGained);
		  stageNumsToEquipIds.put(stageNum, equipId);
	  }
	  return stageNumsToProtos;
  }
  
  private int fairlyPickMonster(List<Integer> monsterIds, Random rand) {
	  int randInt = rand.nextInt(monsterIds.size());
	  
	  int luckyId = monsterIds.get(randInt);
	  return luckyId;
  }
  
  //for a monster, choose the reward to give (equipId)
  //assumption: sum of all the reward drop rates is AT MOST 1
  private int selectMonsterEquipReward(Integer monsterId, Random rand) {
	  int equipId = ControllerConstants.NOT_SET;
	  
	  double randDouble = rand.nextDouble();
	  double probabilitySoFar = 0.0d;

	  //get rewards this monster can drop
	  List<MonsterReward> rewards = MonsterRewardRetrieveUtils.
			  getMonsterRewardForMonsterId(monsterId);
	  
	  //choose a reward, if any
	  for (MonsterReward mr: rewards) {
		  probabilitySoFar += mr.getDropRate();
		  
		  if (randDouble < probabilitySoFar) {
			  equipId = mr.getEquipId();
			  break;
		  }
	  }
	  
	  return equipId;
  }
  
  private int calculateExpGained(List<Integer> monsterIds,
		  Map<Integer, Monster> monsterIdsToMonsters) {
	  int totalExp = 0;
	  for (int monsterId : monsterIds) {
		  Monster m = monsterIdsToMonsters.get(monsterId);
		  totalExp += m.getExpDrop();
	  }
	  return totalExp;
  }
  
  private int calculateSilverGained(List<Integer> monsterIds,
		  Map<Integer, Monster> monsterIdsToMonsters, List<Integer> individualSilvers) {
	  int totalSilver = 0;
	  for (int i = 0; i < monsterIds.size(); i++) {
		  int monsterId = monsterIds.get(i);
		  Monster m = monsterIdsToMonsters.get(monsterId);
		  int silverDrop = m.getSilverDrop(); 
		  totalSilver += silverDrop;
		  
		  //store for the caller's use
		  individualSilvers.add(silverDrop);
	  }
	  return totalSilver;
  }
  
  private void setResponseBuilder(Builder resBuilder, List<Long> userTaskIdList,
		  Map<Integer, TaskStageProto> stageNumsToProtos) {
	  //stuff to send to the client
	  long userTaskId = userTaskIdList.get(0);
	  resBuilder.setUserTaskId(userTaskId);
	  
	  for (int i = 0; i < stageNumsToProtos.size(); i++) {
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
