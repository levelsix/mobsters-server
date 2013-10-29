package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.EndDungeonRequestEvent;
import com.lvl6.events.response.EndDungeonResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.TaskForUser;
import com.lvl6.info.TaskStageForUser;
import com.lvl6.info.TaskStageMonster;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventDungeonProto.EndDungeonRequestProto;
import com.lvl6.proto.EventDungeonProto.EndDungeonResponseProto;
import com.lvl6.proto.EventDungeonProto.EndDungeonResponseProto.Builder;
import com.lvl6.proto.EventDungeonProto.EndDungeonResponseProto.EndDungeonStatus;
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.TaskForUserRetrieveUtils;
import com.lvl6.retrieveutils.TaskStageForUserRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskStageMonsterRetrieveUtils;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.InsertUtils;

@Component @DependsOn("gameServer") public class EndDungeonController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());


  public EndDungeonController() {
    numAllocatedThreads = 4;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new EndDungeonRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_END_DUNGEON_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    EndDungeonRequestProto reqProto = ((EndDungeonRequestEvent)event).getEndDungeonRequestProto();

    //get values sent from the client (the request proto)
    MinimumUserProto senderProto = reqProto.getSender();
    int userId = senderProto.getUserId();
    long userTaskId = reqProto.getUserTaskId();
    boolean userWon = reqProto.getUserWon();
    Timestamp curTime = new Timestamp(reqProto.getClientTime());

    //set some values to send to the client (the response proto)
    EndDungeonResponseProto.Builder resBuilder = EndDungeonResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setStatus(EndDungeonStatus.FAIL_OTHER); //default

    server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    try {
      User aUser = RetrieveUtils.userRetrieveUtils().getUserById(userId);
//      int previousSilver = 0;
//      int previousGold = 0;

      List<TaskForUser> userTaskList = new ArrayList<TaskForUser>();
      TaskForUser ut = null;
      boolean legit = checkLegit(resBuilder, aUser, userId, userTaskId, userTaskList);


      boolean successful = false;
      List<FullUserMonsterProto> protos = new ArrayList<FullUserMonsterProto>();
      if(legit) {
    	  ut = userTaskList.get(0);
//        previousSilver = aUser.getCoins() + aUser.getVaultBalance();
//        previousGold = aUser.getDiamonds();
    	  successful = writeChangesToDb(aUser, userId, ut, userWon, curTime,
    			  protos);
//        writeToUserCurrencyHistory(aUser, money, curTime, previousSilver, previousGold);
      }
      if (successful) {
      	long taskForUserId = ut.getId(); 
      	//the things to delete and store into history
      	List<TaskStageForUser> tsfuList = TaskStageForUserRetrieveUtils
      			.getTaskStagesForUserWithTaskForUserId(taskForUserId);
      	
      	//delete from task_stage_for_user and put into task_stage_history
      	Map<Integer, Integer> monsterIdToNumPieces = new HashMap<Integer, Integer>();
      	recordStageHistory(tsfuList, monsterIdToNumPieces);
      	
      	//update user's monsters
      	String mfusop = ControllerConstants.MFUSOP__END_DUNGEON + taskForUserId;
      	List<FullUserMonsterProto> newOrUpdated = MonsterStuffUtils.
      			updateUserMonsters(userId, monsterIdToNumPieces, mfusop);

    	  setResponseBuilder(resBuilder, newOrUpdated);
      }
      
      EndDungeonResponseEvent resEvent = new EndDungeonResponseEvent(userId);
      resEvent.setTag(event.getTag());
      resEvent.setEndDungeonResponseProto(resBuilder.build());
      server.writeEvent(resEvent);

      UpdateClientUserResponseEvent resEventUpdate = MiscMethods
          .createUpdateClientUserResponseEventAndUpdateLeaderboard(aUser);
      resEventUpdate.setTag(event.getTag());
      server.writeEvent(resEventUpdate);
      
      if (successful) {
      	//update quests
      }
      
    } catch (Exception e) {
      log.error("exception in EndDungeonController processEvent", e);
      //don't let the client hang
      try {
    	  resBuilder.setStatus(EndDungeonStatus.FAIL_OTHER);
    	  EndDungeonResponseEvent resEvent = new EndDungeonResponseEvent(userId);
    	  resEvent.setTag(event.getTag());
    	  resEvent.setEndDungeonResponseProto(resBuilder.build());
    	  server.writeEvent(resEvent);
      } catch (Exception e2) {
    	  log.error("exception2 in EndDungeonController processEvent", e);
      }
    } finally {
      server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    }
  }

  /*
   * Return true if user request is valid; false otherwise and set the
   * builder status to the appropriate value.
   */
  private boolean checkLegit(Builder resBuilder, User u, int userId,
		  long userTaskId, List<TaskForUser> userTaskList) {
    if (null == u) {
      log.error("unexpected error: user is null. user=" + u);
      return false;
    }
    
    TaskForUser ut = TaskForUserRetrieveUtils.getUserTaskForId(userTaskId);
    if (null == ut) {
    	log.error("unexpected error: no user task for id userTaskId=" + userTaskId);
    	return false;
    }
    
    userTaskList.add(ut);
    resBuilder.setStatus(EndDungeonStatus.SUCCESS);
    return true;
  }

  private boolean writeChangesToDb(User u, int uId, TaskForUser ut, boolean userWon,
		  Timestamp clientTime, List<FullUserMonsterProto> protos) {
	  int silverGained = ut.getSilverGained();
	  int expGained = ut.getExpGained();
	  
	  if (userWon) {
		  //insert the equips into user_equip
//		  meteEquips(uId, clientTime, ut, protos);
		  
		  //update user silver and experience
		  if (!updateUser(u, silverGained, expGained, clientTime)) {
			  return false;
		  }
	  }
	  
	  //TODO: MOVE THIS INTO A UTIL METHOD FOR TASK 
	  //delete from user_task and insert it into user_task_history
	  long utId = ut.getId();
	  int tId = ut.getTaskId();
	  int numRevives = ut.getNumRevives();
	  Date startDate = ut.getStartDate();
	  long startMillis = startDate.getTime();
	  Timestamp startTime = new Timestamp(startMillis);
	  int num = InsertUtils.get().insertIntoTaskHistory(utId,uId, tId,
			  expGained, silverGained, numRevives, startTime, clientTime, userWon);
	  if (1 != num) {
		  log.error("unexpected error: error when inserting into user_task_history. " +
		  		"numInserted=" + num + " Attempting to undo shi");
		  updateUser(u, -1* silverGained, -1 * expGained, clientTime);
		  return false;
	  }
	  
	  //DELETE FROM TASK_FOR_USER TABLE
	  num = DeleteUtils.get().deleteTaskForUserWithTaskForUserId(utId); 
	  log.info("num rows deleted from task_for_user table. num=" + num);
	  
	  return true;
  }
  
  private boolean updateUser(User u, int silverGained, int expGained,
		  Timestamp clientTime) {
	  int energyChange = 0;
	  boolean simulateEnergyRefill = false;
	  if (!u.updateRelativeCoinsExpTaskscompleted(silverGained,
	  		expGained, 1, clientTime)) {
		  log.error("problem with updating user stats post-task. silverGained="
				  + silverGained + ", expGained=" + expGained + ", increased" +
				  " tasks completed by 1, energyChange=" + energyChange +
				  ", clientTime=" + clientTime + ", simulateEnergyRefill=" +
				  simulateEnergyRefill + ", user=" + u);
		  return false;
	  }
	  return true;
  }
  //
  private void recordStageHistory(List<TaskStageForUser> tsfuList,
  		Map<Integer, Integer> monsterIdToNumPieces) {
  	//keep track of how many pieces dropped and by which task stage monster
  	Map<Integer, Integer> taskStageMonsterIdToQuantity =
  			new HashMap<Integer, Integer>();
  	
  	
  	//collections to hold values to be saved to the db
  	List<Long> userTaskStageId = new ArrayList<Long>();
  	List<Long> userTaskId = new ArrayList<Long>();
  	List<Integer> stageNum = new ArrayList<Integer>();
  	List<Integer> taskStageMonsterIdList = new ArrayList<Integer>();
  	List<Integer> expGained = new ArrayList<Integer>();
  	List<Integer> silverGained = new ArrayList<Integer>();
  	List<Boolean> monsterPieceDropped = new ArrayList<Boolean>();
  	
  	for (int i = 0; i < tsfuList.size(); i++) {
  		TaskStageForUser tsfu = tsfuList.get(i);
  		userTaskStageId.add(tsfu.getId());
  		userTaskId.add(tsfu.getUserTaskId());
  		stageNum.add(tsfu.getStageNum());
  		
  		int taskStageMonsterId = tsfu.getTaskStageMonsterId();
  		taskStageMonsterIdList.add(taskStageMonsterId);
  		expGained.add(tsfu.getExpGained());
  		silverGained.add(tsfu.getSilverGained());
  		boolean dropped = tsfu.isMonsterPieceDropped();
  		monsterPieceDropped.add(dropped);
  		
  		if (!dropped) {
  			//not going to keep track of non dropped monster pieces
  			continue;
  		}
  		
  		//since monster piece dropped, update our current stats on monster pieces
  		if (taskStageMonsterIdToQuantity.containsKey(taskStageMonsterId)) {
  			//saw this task stage monster id before, increment quantity
  			int quantity = 1 + taskStageMonsterIdToQuantity.get(taskStageMonsterId);
  			taskStageMonsterIdToQuantity.put(taskStageMonsterId, quantity);
  			
  		} else {
  			//haven't seen this task stage monster id yet, so start off at 1
  			taskStageMonsterIdToQuantity.put(taskStageMonsterId, 1);
  		}
  	}
  	
  	int num = InsertUtils.get().insertIntoTaskStageHistory(userTaskStageId,
  			userTaskId, stageNum, taskStageMonsterIdList, expGained, silverGained,
  			monsterPieceDropped);
  	log.info("num task stage history rows inserted: num=" + num +
  			"taskStageForUser=" + tsfuList);
  	
  	//DELETE FROM TASK_STAGE_FOR_USER
  	num = DeleteUtils.get().deleteTaskStagesForUserWithIds(userTaskStageId);
  	log.info("num task stage for user rows deleted: num=" + num);
  	
  	//retrieve those task stage monsters. aggregate the quantities by monster id
  	//assume different task stage monsters can be the same monster
  	Collection<Integer> taskStageMonsterIds = taskStageMonsterIdToQuantity.keySet();
  	Map<Integer, TaskStageMonster> monstersThatDropped = TaskStageMonsterRetrieveUtils
  			.getTaskStageMonstersForIds(taskStageMonsterIds);
  	
  	for (int taskStageMonsterId : taskStageMonsterIds) {
  		TaskStageMonster monsterThatDropped = monstersThatDropped.get(taskStageMonsterId);
  		int monsterId = monsterThatDropped.getMonsterId();
  		int numPiecesDroppedForMonster = taskStageMonsterIdToQuantity.get(taskStageMonsterId); 
  		
  		//aggregate pieces based on monsterId, since assuming different task
  		//stage monsters can be the same monster
  		if (monsterIdToNumPieces.containsKey(monsterId)) {
  			int newAmount = numPiecesDroppedForMonster + monsterIdToNumPieces.get(monsterId);
  			monsterIdToNumPieces.put(monsterId, newAmount);
  			
  		} else {
  			//first time seeing this monster, store existing quantity
  			monsterIdToNumPieces.put(monsterId, numPiecesDroppedForMonster);
  		}
  	}
  }
  
  
  private void setResponseBuilder(Builder resBuilder,
		  List<FullUserMonsterProto> protos) {
	  resBuilder.setStatus(EndDungeonStatus.SUCCESS);
	  
	  if (!protos.isEmpty()) {
	  	resBuilder.addAllUpdatedOrNew(protos);
	  }
  }
  
  private void writeToUserCurrencyHistory(User aUser, Map<String, Integer> money, Timestamp curTime,
      int previousCash, int previousGems) {
    Map<String, Integer> previousGemsCash = new HashMap<String, Integer>();
    Map<String, String> reasonsForChanges = new HashMap<String, String>();
    String reasonForChange = ControllerConstants.UCHRFC__BOSS_ACTION;
    String gems = MiscMethods.gems;
    String cash = MiscMethods.cash;

    previousGemsCash.put(gems, previousGems);
    previousGemsCash.put(cash, previousCash);
    reasonsForChanges.put(gems, reasonForChange);
    reasonsForChanges.put(cash, reasonForChange);

    MiscMethods.writeToUserCurrencyOneUserGoldAndOrSilver(aUser, curTime, money, 
        previousGemsCash, reasonsForChanges);

  }
}
