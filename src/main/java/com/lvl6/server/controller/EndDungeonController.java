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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.EndDungeonRequestEvent;
import com.lvl6.events.response.EndDungeonResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.TaskForUserOngoing;
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
import com.lvl6.proto.UserProto.MinimumUserProtoWithMaxResources;
import com.lvl6.retrieveutils.TaskForUserOngoingRetrieveUtils;
import com.lvl6.retrieveutils.TaskStageForUserRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskStageMonsterRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.InsertUtils;

@Component @DependsOn("gameServer") public class EndDungeonController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  @Autowired
  protected Locker locker;

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
    log.info("reqProto=" + reqProto);
    
    //get values sent from the client (the request proto)
    MinimumUserProtoWithMaxResources senderResourcesProto = reqProto.getSender();
    MinimumUserProto senderProto = senderResourcesProto.getMinUserProto();
    int userId = senderProto.getUserId();
    long userTaskId = reqProto.getUserTaskId();
    boolean userWon = reqProto.getUserWon();
    Date currentDate = new Date(reqProto.getClientTime());
    Timestamp curTime = new Timestamp(reqProto.getClientTime());
    boolean firstTimeUserWonTask = reqProto.getFirstTimeUserWonTask();
    int maxCash = senderResourcesProto.getMaxCash();
    int maxOil = senderResourcesProto.getMaxOil();

    //set some values to send to the client (the response proto)
    EndDungeonResponseProto.Builder resBuilder = EndDungeonResponseProto.newBuilder();
    resBuilder.setSender(senderResourcesProto);
    resBuilder.setUserWon(userWon);
    resBuilder.setStatus(EndDungeonStatus.FAIL_OTHER); //default

    getLocker().lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    try {
      User aUser = RetrieveUtils.userRetrieveUtils().getUserById(userId);
      int previousCash = 0;
      int previousOil = 0;

      TaskForUserOngoing ut = TaskForUserOngoingRetrieveUtils.getUserTaskForId(userTaskId);
      boolean legit = checkLegit(resBuilder, aUser, userId, userTaskId, ut);


      boolean successful = false;
      Map<String, Integer> money = new HashMap<String, Integer>();
      if(legit) {
        previousCash = aUser.getCash();
        previousOil = aUser.getOil();
    	  successful = writeChangesToDb(aUser, userId, ut, userWon, curTime,
    			  money, maxCash, maxOil);
    	  
    	  resBuilder.setTaskId(ut.getTaskId());
      }
      if (successful) {
      	long taskForUserId = ut.getId(); 
      	//the things to delete and store into history
      	List<TaskStageForUser> tsfuList = TaskStageForUserRetrieveUtils
      			.getTaskStagesForUserWithTaskForUserId(taskForUserId);
      	
      	//delete from task_stage_for_user and put into task_stage_history
      	Map<Integer, Integer> monsterIdToNumPieces = new HashMap<Integer, Integer>();
      	//TODO: record  (items(?))
//      	Map<Integer, Integer> itemIdToQuantity = new HashMap<Integer, Integer>();
      	recordStageHistory(tsfuList, monsterIdToNumPieces);
      	
      	
      	if (userWon) {
      		log.info("user won dungeon, awarding the monsters and items");
      		//update user's monsters
      		StringBuilder mfusopB = new StringBuilder();
      		mfusopB.append(ControllerConstants.MFUSOP__END_DUNGEON);
      		mfusopB.append(" ");
      		mfusopB.append(taskForUserId);
      		String mfusop = mfusopB.toString();
      		List<FullUserMonsterProto> newOrUpdated = MonsterStuffUtils.
      				updateUserMonsters(userId, monsterIdToNumPieces, mfusop, currentDate);
      		setResponseBuilder(resBuilder, newOrUpdated);
      		
//      		//MAYBE NEED TO SEND THESE TO THE CLIENT?
//      		Map<Integer, ItemForUser> itemIdsToItems = updateUserItems(tsfuList, userId);
      		
      	}
      	
      }
      
      EndDungeonResponseEvent resEvent = new EndDungeonResponseEvent(userId);
      resEvent.setTag(event.getTag());
      resEvent.setEndDungeonResponseProto(resBuilder.build());
      server.writeEvent(resEvent);

      if (successful) {
    	  //null PvpLeagueFromUser means will pull from hazelcast instead
      	UpdateClientUserResponseEvent resEventUpdate = MiscMethods
      			.createUpdateClientUserResponseEventAndUpdateLeaderboard(aUser, null);
      	resEventUpdate.setTag(event.getTag());
      	server.writeEvent(resEventUpdate);
      	int taskId = ut.getTaskId();
      	writeToUserCurrencyHistory(aUser, curTime, userTaskId, taskId,
      			previousCash, previousOil, money);
      	writeToTaskForUserCompleted(userId, taskId, userWon, firstTimeUserWonTask, curTime);
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
      getLocker().unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    }
  }

  /*
   * Return true if user request is valid; false otherwise and set the
   * builder status to the appropriate value.
   */
  private boolean checkLegit(Builder resBuilder, User u, int userId,
		  long userTaskId, TaskForUserOngoing ut) {
    if (null == u) {
      log.error("unexpected error: user is null. user=" + u);
      return false;
    }
    
    if (null == ut) {
    	log.error("unexpected error: no user task for id userTaskId=" + userTaskId);
    	return false;
    }
    
    resBuilder.setStatus(EndDungeonStatus.SUCCESS);
    return true;
  }

  private boolean writeChangesToDb(User u, int uId, TaskForUserOngoing ut,
		  boolean userWon, Timestamp clientTime, Map<String, Integer> money,
		  int maxCash, int maxOil) {
	  int cashGained = ut.getCashGained();
	  int expGained = ut.getExpGained();
	  int oilGained = ut.getOilGained();
	  
	  int curCash = Math.min(u.getCash(), maxCash); //in case user's cash is more than maxCash
		int maxCashUserCanGain = maxCash - curCash;
		cashGained = Math.min(cashGained, maxCashUserCanGain);
		
		int curOil = Math.min(u.getOil(), maxOil);
		int maxOilUserCanGain = maxOil - curOil;
		oilGained = Math.min(oilGained, maxOilUserCanGain);
		
	  log.info("user before currency change. " + u);
	  if (userWon) {
	  	log.info("user WON DUNGEON!!!!!!!!. ");
		  //update user cash and experience
		  if (!updateUser(u, expGained, cashGained, oilGained, clientTime)) {
			  return false;
		  } else {
		  	if (0 != cashGained) {
		  		money.put(MiscMethods.cash, cashGained);
		  	}
		  	if (0 != oilGained) {
		  		money.put(MiscMethods.oil, oilGained);
		  	}
		  }
	  }
	  log.info("user after currency change. " + u);
	  //TODO: MOVE THIS INTO A UTIL METHOD FOR TASK 
	  //delete from user_task and insert it into user_task_history
	  long utId = ut.getId();
	  int tId = ut.getTaskId();
	  int numRevives = ut.getNumRevives();
	  Date startDate = ut.getStartDate();
	  long startMillis = startDate.getTime();
	  Timestamp startTime = new Timestamp(startMillis);
	  boolean cancelled = false;
	  int tsId = ut.getTaskStageId(); 
	  int num = InsertUtils.get().insertIntoTaskHistory(utId, uId, tId,
			  expGained, cashGained, oilGained, numRevives, startTime,
			  clientTime, userWon, cancelled, tsId);
	  if (1 != num) {
		  log.error("unexpected error: error when inserting into user_task_history. " +
		  		"numInserted=" + num + " Attempting to undo shi");
		  updateUser(u, -1 * expGained, -1 * cashGained,  -1 * oilGained, clientTime);
		  return false;
	  }
	  
	  //DELETE FROM TASK_FOR_USER TABLE
	  num = DeleteUtils.get().deleteTaskForUserOngoingWithTaskForUserId(utId); 
	  log.info("num rows deleted from task_for_user table. num=" + num);
	  
	  return true;
  }
  
  private boolean updateUser(User u, int expGained, int cashGained, int oilGained,
		  Timestamp clientTime) {
	  int energyChange = 0;
	  if (!u.updateRelativeCashOilExpTasksCompleted(expGained, cashGained, oilGained, 1,
	  		clientTime)) {
		  log.error("problem with updating user stats post-task. expGained=" + expGained +
		  		", cashGained=" + cashGained + ", oilGained=" + oilGained + ", increased" +
				  " tasks completed by 1, energyChange=" + energyChange +
				  ", clientTime=" + clientTime + ", user=" + u);
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
  	List<String> monsterTypes = new ArrayList<String>();
  	List<Integer> expGained = new ArrayList<Integer>();
  	List<Integer> cashGained = new ArrayList<Integer>();
  	List<Integer> oilGained = new ArrayList<Integer>();
  	List<Boolean> monsterPieceDropped = new ArrayList<Boolean>();
  	List<Integer> itemIdDropped = new ArrayList<Integer>();
  	
  	for (int i = 0; i < tsfuList.size(); i++) {
  		TaskStageForUser tsfu = tsfuList.get(i);
  		userTaskStageId.add(tsfu.getId());
  		userTaskId.add(tsfu.getUserTaskId());
  		stageNum.add(tsfu.getStageNum());
  		
  		int taskStageMonsterId = tsfu.getTaskStageMonsterId();
  		taskStageMonsterIdList.add(taskStageMonsterId);
  		monsterTypes.add(tsfu.getMonsterType());
  		expGained.add(tsfu.getExpGained());
  		cashGained.add(tsfu.getCashGained());
  		oilGained.add(tsfu.getOilGained());
  		boolean dropped = tsfu.isMonsterPieceDropped();
  		monsterPieceDropped.add(dropped);
  		itemIdDropped.add(tsfu.getItemIdDropped());
  		
  		if (!dropped) {
  			//not going to keep track of non dropped monster pieces
  			continue;
  		}
  		
  		//since monster piece dropped, update our current stats on monster pieces
  		//this was done under the assumption that one task stage could have
  		//more than one task stage monster (otheriwse only the else case would execute)
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
  			userTaskId, stageNum, taskStageMonsterIdList, monsterTypes, expGained,
  			cashGained, oilGained, monsterPieceDropped, itemIdDropped);
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
  
  //not using this method because how many items the user has is kept track through
  //quest progress
  /*
   private Map<Integer, ItemForUser> updateUserItems(List<TaskStageForUser> tsfuList,
  		int userId) {
  	Map<Integer, Integer> itemIdsToQuantities = getItemsDropped(tsfuList);
		
		//retrieve these specific items for the user, so as to update them 
  	Collection<Integer> itemIds = itemIdsToQuantities.keySet();
  	
  	Map<Integer, ItemForUser> itemIdToUserItem = ItemForUserRetrieveUtils
  			.getSpecificOrAllUserItems(userId, itemIds);
  	log.info("items user won: " + itemIdsToQuantities);
  	log.info("existing items before modification: " + itemIdToUserItem);
  	//update how many items the user has now
  	
  	for (Integer itemId : itemIds) {
  		//get cur amount of items

  		//check base case
  		if (!itemIdToUserItem.containsKey(itemId)) {
  			//first time user is getting this item
  			ItemForUser ifu = new ItemForUser(userId, itemId, 0);
  			itemIdToUserItem.put(itemId, ifu);
  		}
  		
  		ItemForUser ifu = itemIdToUserItem.get(itemId);
  		int curAmount = ifu.getQuantity();
  		
  		//update it
  		int delta = itemIdsToQuantities.get(itemId);
  		curAmount += delta;
  		ifu.setQuantity(curAmount);
  	}
  	
  	
  	int numUpdated = UpdateUtils.get().updateUserItems(userId, itemIdToUserItem);
  	log.info("existing items after modification: " + itemIdToUserItem + "\t numUpdated=" +
  			numUpdated);
  	
  	
  	return itemIdToUserItem;
  }
  
  //go through list of task stage for user, aggregate all the item ids with non zero
  //quantities
  private Map<Integer, Integer> getItemsDropped(List<TaskStageForUser> tsfuList) {
  	Map<Integer, Integer> itemIdsToQuantities = new HashMap<Integer, Integer>();
  	
  	for (TaskStageForUser tsfu : tsfuList) {
  		//if item dropped, add it in with the others
  		int itemIdDropped = tsfu.getItemIdDropped();
  		
  		if (itemIdDropped <= 0) {
  			//item didn't drop
  			continue;
  		}
  		
  		int quantity = 0;
  		
  		//if item dropped before, get how much dropped
  		if (itemIdsToQuantities.containsKey(itemIdDropped)) {
  			quantity = itemIdsToQuantities.get(itemIdDropped);
  		}
  		//update
  		quantity++;
  		itemIdsToQuantities.put(itemIdDropped, quantity);
  	}
  	
  	return itemIdsToQuantities;
  }
  */
  
  private void writeToUserCurrencyHistory(User aUser, Timestamp curTime,
		  long userTaskId, int taskId, int previousCash, int previousOil,
		  Map<String, Integer> money) {
	  if (money.isEmpty()) {
		  return;
	  }
	  
  	StringBuilder sb = new StringBuilder();
  	sb.append("userTask=");
  	sb.append(userTaskId);
  	sb.append(" taskId=");
  	sb.append(taskId);
  	String cash = MiscMethods.cash;
  	String oil = MiscMethods.oil;
  	String reasonForChange = ControllerConstants.UCHRFC__END_TASK;
  	
  	int userId = aUser.getId();
    Map<String, Integer> previousCurrencies = new HashMap<String, Integer>();
    Map<String, Integer> currentCurrencies = new HashMap<String, Integer>();
    Map<String, String> reasonsForChanges = new HashMap<String, String>();
    Map<String, String> detailsMap = new HashMap<String, String>();

    if (money.containsKey(cash)) {
    	previousCurrencies.put(cash, previousCash);
    }
    if (money.containsKey(oil)) {
    	previousCurrencies.put(oil, previousOil);
    }

    currentCurrencies.put(cash, aUser.getCash());
    currentCurrencies.put(oil, aUser.getOil());
    reasonsForChanges.put(cash, reasonForChange);
    reasonsForChanges.put(oil, reasonForChange);
    detailsMap.put(cash, sb.toString());
    detailsMap.put(oil, sb.toString());
    MiscMethods.writeToUserCurrencyOneUser(userId, curTime, money, previousCurrencies,
    		currentCurrencies, reasonsForChanges, detailsMap);

  }
  
  private void writeToTaskForUserCompleted(int userId, int taskId, 
  		boolean userWon, boolean firstTimeUserWonTask, Timestamp now) {
  	if (userWon && firstTimeUserWonTask) {
  		int numInserted = InsertUtils.get()
  				.insertIntoTaskForUserCompleted(userId, taskId, now);
  		
  		log.info("numInserted into task_for_user_completed: " + numInserted);
  	}
  }

  public Locker getLocker() {
	  return locker;
  }

  public void setLocker(Locker locker) {
	  this.locker = locker;
  }

}
