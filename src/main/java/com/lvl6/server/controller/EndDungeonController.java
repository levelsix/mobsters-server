package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.EndDungeonRequestEvent;
import com.lvl6.events.response.EndDungeonResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.ItemForUser;
import com.lvl6.info.Task;
import com.lvl6.info.TaskForUserOngoing;
import com.lvl6.info.TaskMapElement;
import com.lvl6.info.TaskStageForUser;
import com.lvl6.info.TaskStageMonster;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventDungeonProto.EndDungeonRequestProto;
import com.lvl6.proto.EventDungeonProto.EndDungeonResponseProto;
import com.lvl6.proto.EventDungeonProto.EndDungeonResponseProto.Builder;
import com.lvl6.proto.EventDungeonProto.EndDungeonResponseProto.EndDungeonStatus;
import com.lvl6.proto.ItemsProto.UserItemProto;
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.proto.UserProto.MinimumUserProtoWithMaxResources;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.TaskForUserCompletedRetrieveUtils;
import com.lvl6.retrieveutils.TaskForUserCompletedRetrieveUtils.UserTaskCompleted;
import com.lvl6.retrieveutils.TaskForUserOngoingRetrieveUtils2;
import com.lvl6.retrieveutils.TaskStageForUserRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.TaskMapElementRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskStageMonsterRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.InsertUtils;
import com.lvl6.utils.utilmethods.StringUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component @DependsOn("gameServer") public class EndDungeonController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  @Autowired
  protected Locker locker;
  
  @Autowired
  protected UserRetrieveUtils2 userRetrieveUtil;

  @Autowired
  protected ItemForUserRetrieveUtil itemForUserRetrieveUtil;

  @Autowired
  protected TaskForUserOngoingRetrieveUtils2 taskForUserOngoingRetrieveUtil;
  
  @Autowired
  protected TaskForUserCompletedRetrieveUtils taskForUserCompletedRetrieveUtil;
  
  @Autowired
  protected TaskStageForUserRetrieveUtils2 taskStageForUserRetrieveUtil;
  
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
    log.info(String.format("reqProto=%s", reqProto));
    
    //get values sent from the client (the request proto)
    MinimumUserProtoWithMaxResources senderResourcesProto = reqProto.getSender();
    MinimumUserProto senderProto = senderResourcesProto.getMinUserProto();
    String userId = senderProto.getUserUuid();
    String userTaskId = reqProto.getUserTaskUuid();
    boolean userWon = reqProto.getUserWon();
    Date currentDate = new Date(reqProto.getClientTime());
    Timestamp curTime = new Timestamp(reqProto.getClientTime());
    boolean firstTimeUserWonTask = reqProto.getFirstTimeUserWonTask();
    int maxCash = senderResourcesProto.getMaxCash();
    int maxOil = senderResourcesProto.getMaxOil();
    
    Set<String> droplessTsfuIds = new HashSet<String>();
    if (null != reqProto.getDroplessTsfuUuidsList()) {
    		droplessTsfuIds.addAll(reqProto.getDroplessTsfuUuidsList());
    }
    //set some values to send to the client (the response proto)
    EndDungeonResponseProto.Builder resBuilder = EndDungeonResponseProto.newBuilder();
    resBuilder.setSender(senderResourcesProto);
    resBuilder.setUserWon(userWon);
    resBuilder.setStatus(EndDungeonStatus.FAIL_OTHER); //default

    UUID userUuid = null;
	boolean invalidUuids = true;
	
	try {
		userUuid = UUID.fromString(userId);
		StringUtils.convertToUUID(droplessTsfuIds);
		invalidUuids = false;
	} catch (Exception e) {
		log.error(String.format(
			"UUID error. incorrect userId=%s",
			userId), e);
	}
	
	//UUID checks
	if (invalidUuids) {
		resBuilder.setStatus(EndDungeonStatus.FAIL_OTHER);
		EndDungeonResponseEvent resEvent = new EndDungeonResponseEvent(userId);
		resEvent.setTag(event.getTag());
		resEvent.setEndDungeonResponseProto(resBuilder.build());
		server.writeEvent(resEvent);
    	return;
    }
    
    getLocker().lockPlayer(userUuid, this.getClass().getSimpleName());
    try {
      User aUser = userRetrieveUtil.getUserById(userId);
      int previousCash = 0;
      int previousOil = 0;

      TaskForUserOngoing ut = taskForUserOngoingRetrieveUtil.getUserTaskForId(userTaskId);
      boolean legit = checkLegit(resBuilder, aUser, userId, userTaskId, ut);


      boolean successful = false;
      Map<String, Integer> money = new HashMap<String, Integer>();
      int taskId = 0;
      TaskMapElement tme = null;
      int itemId = 0;
      UserTaskCompleted newUtc = new UserTaskCompleted();
      UserTaskCompleted oldUtc = null;
      if(legit) {
    	  taskId = ut.getTaskId();
    	  resBuilder.setTaskId(taskId);
    	  
    	  tme = TaskMapElementRetrieveUtils
    		  .getTaskMapElementForTaskId(taskId);
    	  oldUtc = taskForUserCompletedRetrieveUtil
    		  .getCompletedTaskForUser(userId, taskId);
    	  
    	  //award the item only once
    	  if (firstTimeUserWonTask && null != tme && null == oldUtc) {
    		  itemId = tme.getItemDropId();
    	  }

    	  previousCash = aUser.getCash();
    	  previousOil = aUser.getOil();
    	  successful = writeChangesToDb(aUser, userId, ut, userWon, curTime,
    		  money, maxCash, maxOil, firstTimeUserWonTask, tme, oldUtc,
    		  newUtc);

      }
      if (successful) {
      	String taskForUserId = ut.getId(); 
      	//the things to delete and store into history
      	List<TaskStageForUser> tsfuList = taskStageForUserRetrieveUtil
      			.getTaskStagesForUserWithTaskForUserId(taskForUserId);
      	
      	//delete from task_stage_for_user and put into task_stage_history,
      	//keep track of the monsters the user gets in the next two maps
      	Map<Integer, Integer> monsterIdToNumPieces = new HashMap<Integer, Integer>();
      	Map<Integer, Map<Integer, Integer>> monsterIdToLvlToQuantity =
      		new HashMap<Integer, Map<Integer, Integer>>();
      	//TODO: record  (items(?))
//      	Map<Integer, Integer> itemIdToQuantity = new HashMap<Integer, Integer>();
      	recordStageHistory(tsfuList, droplessTsfuIds,
      		monsterIdToNumPieces, monsterIdToLvlToQuantity);
      	
      	if (userWon) {
      		log.info("user won dungeon, awarding the monsters and items");
      		//update user's monsters
      		StringBuilder mfusopB = new StringBuilder();
      		mfusopB.append(ControllerConstants.MFUSOP__END_DUNGEON);
      		mfusopB.append(" ");
      		mfusopB.append(taskForUserId);
      		String mfusop = mfusopB.toString();
      		List<FullUserMonsterProto> newOrUpdated = MonsterStuffUtils.
      				updateUserMonsters(userId, monsterIdToNumPieces,
      					monsterIdToLvlToQuantity, mfusop, currentDate);
      		
      		awardOneTimeItem(resBuilder, userId, itemId, taskId, tme);
      		
      		setResponseBuilder(resBuilder, newOrUpdated, newUtc);
      		
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
      			.createUpdateClientUserResponseEventAndUpdateLeaderboard(aUser, null, null);
      	resEventUpdate.setTag(event.getTag());
      	server.writeEvent(resEventUpdate);
      	writeToUserCurrencyHistory(aUser, curTime, userTaskId, taskId,
      			previousCash, previousOil, money);
      	writeToTaskForUserCompleted(userWon, firstTimeUserWonTask,
      		curTime, newUtc, oldUtc);
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
      getLocker().unlockPlayer(userUuid, this.getClass().getSimpleName());
    }
  }

  /*
   * Return true if user request is valid; false otherwise and set the
   * builder status to the appropriate value.
   */
  private boolean checkLegit(Builder resBuilder, User u, String userId,
	  String userTaskId, TaskForUserOngoing ut) {
    if (null == u) {
      log.error(String.format(
    	  "unexpected error: user is null. user=%s", u));
      return false;
    }
    
    if (null == ut) {
    	log.error(String.format(
    		"unexpected error: no user task for id userTaskId=%s", userTaskId));
    	return false;
    }
    
    resBuilder.setStatus(EndDungeonStatus.SUCCESS);
    return true;
  }

  //newUtc will be filled out
  private boolean writeChangesToDb(User u, String uId, TaskForUserOngoing ut,
		  boolean userWon, Timestamp clientTime, Map<String, Integer> money,
		  int maxCash, int maxOil, boolean firstTimeUserWonTask, TaskMapElement tme,
		  UserTaskCompleted oldUtc, UserTaskCompleted newUtc)
  {
	  int taskId = ut.getTaskId();
	  newUtc.setUserId(uId);
	  newUtc.setTaskId(taskId);
	  
	  List<Integer> cashGainedContainer = new ArrayList<Integer>();
	  List<Integer> oilGainedContainer = new ArrayList<Integer>();
	  int expGained = ut.getExpGained();
	  
	  int remainingCash = 0;
	  int remainingOil = 0;
	  
	  if (firstTimeUserWonTask && null != tme) {
		  //first time user completed task, TaskMapElement has extra rewards
		  Task t = TaskRetrieveUtils.getTaskForTaskId(taskId);
		  expGained += t.getExpReward();
		  remainingCash = tme.getCashReward();
		  remainingOil = tme.getOilReward();
	  } else if (!firstTimeUserWonTask && null != tme && null != oldUtc) {
		  //user completed task, but TaskMapElement might have leftover rewards
		  remainingCash = oldUtc.getUnclaimedCash();
		  remainingOil = oldUtc.getUnclaimedOil();
	  }
	  
	  calculateResourcesGained(u, ut, maxCash, maxOil, remainingCash,
		  remainingOil, newUtc, cashGainedContainer, oilGainedContainer);
	  
	  int cashGained = cashGainedContainer.get(0);
	  int oilGained = oilGainedContainer.get(0);

	  log.info("user before currency change. {}, newUtc={}, oldUtc={}, cashGained={}, oilGained={}",
		  new Object[] { u, newUtc, oldUtc, cashGained, oilGained } );
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
	  log.info(String.format("user after currency change. %s", u));
	  //TODO: MOVE THIS INTO A UTIL METHOD FOR TASK 
	  //delete from user_task and insert it into user_task_history
	  String utId = ut.getId();
	  int numRevives = ut.getNumRevives();
	  Date startDate = ut.getStartDate();
	  long startMillis = startDate.getTime();
	  Timestamp startTime = new Timestamp(startMillis);
	  boolean cancelled = false;
	  int tsId = ut.getTaskStageId(); 
	  int num = InsertUtils.get().insertIntoTaskHistory(utId, uId, taskId,
			  expGained, cashGained, oilGained, numRevives, startTime,
			  clientTime, userWon, cancelled, tsId);
	  if (1 != num) {
		  log.error(String.format(
			  "error inserting into user_task_history. numInserted=%s. Attempting to undo shi",
			  num));
		  updateUser(u, -1 * expGained, -1 * cashGained,  -1 * oilGained, clientTime);
		  return false;
	  }
	  
	  //DELETE FROM TASK_FOR_USER TABLE
	  num = DeleteUtils.get().deleteTaskForUserOngoingWithTaskForUserId(utId); 
	  log.info(String.format(
		  "num rows deleted from task_for_user table. num=%s", num));
	  
	  return true;
  }

  private void calculateResourcesGained(
	  User u,
	  TaskForUserOngoing ut,
	  int maxCash,
	  int maxOil,
	  int remainingCash,
	  int remainingOil,
	  UserTaskCompleted utc,
	  List<Integer> cashGainedContainer,
	  List<Integer> oilGainedContainer )
  {
	  List<Integer> unclaimedResourceContainer = new ArrayList<Integer>();
	  
	  //unclaimedCash is populated after this call
	  int cashGained = calcResourceGained(MiscMethods.CASH,
		  u.getCash(), ut.getCashGained(), maxCash, remainingCash,
		  unclaimedResourceContainer);
	  
	  utc.setUnclaimedCash(
		  unclaimedResourceContainer.get(0));
	  //could reset unclaimedResourceContainer... 
	  
	  //unclaimedOil is populated after this call
	  int oilGained = calcResourceGained(MiscMethods.OIL,
		  u.getOil(), ut.getOilGained(), maxOil, remainingOil,
		  unclaimedResourceContainer);
	  utc.setUnclaimedOil(
		  unclaimedResourceContainer.get(1));

	  cashGainedContainer.add(cashGained);
	  oilGainedContainer.add(oilGained);
  }

  private int calcResourceGained(
	  String resource,
	  int currentResourceAmt,
	  int resourceGained,
	  int maxResourceAmt,
	  int additionalResources,
	  List<Integer> unclaimedResourceContainer )
  {
	  int cappedResourceGained = MiscMethods.capResourceGain(
		  currentResourceAmt, resourceGained, maxResourceAmt);
	  if (cappedResourceGained < resourceGained) {
		  //this means the user collected resources beyond storage capacity
		  //so no need to continue calculating
		  unclaimedResourceContainer.add(additionalResources);
//		  String prefix = "resources gained from task ";
//		  log.info(
//			  "{} (not including stored resources) overflow{} user storage.",
//			  prefix);
//		  log.info("{} currentAmt={} capacity={}, resourceGained={}, additional={}",
//			  new Object[] { prefix, currentResourceAmt, maxResourceAmt,
//			  resourceGained, additionalResources }
//		  );
		  return cappedResourceGained;
	  }
	  
	  if (additionalResources <= 0) {
		  //this means there is no more resource stored in this task
		  //so no need to continue calculating
//		  log.info("no more resources. type={}, current={}, additional={}",
//			  new Object[] { resource, currentResourceAmt, additionalResources } );
		  unclaimedResourceContainer.add(0);
		  return cappedResourceGained;
	  }
	  
	  int resourceGained2 = resourceGained + additionalResources;
	  cappedResourceGained = MiscMethods.capResourceGain(
		  currentResourceAmt, resourceGained2, maxResourceAmt);
	  
	  if (cappedResourceGained < resourceGained2) {
		  //calculate amount of resource that is beyond storage capacity
		  int resourceOverflow = resourceGained2 - cappedResourceGained;
		  unclaimedResourceContainer.add(resourceOverflow);
		  
//		  String prefix = String.format(
//			  "task resources overflow user %s storage.",
//			  resource); 
//		  log.info("{} currentAmt={} capacity={}, resourceGained={}, additional={}",
//			  new Object[] { prefix, currentResourceAmt, maxResourceAmt,
//			  resourceGained, additionalResources }
//		  );
		  return cappedResourceGained;
	  } else {
//		  String prefix = String.format(
//			  "task resources do not overflow user %s storage.",
//			  resource); 
//		  log.info("{} currentAmt={} capacity={}, resourceGained={}, additional={}",
//			  new Object[] { prefix, currentResourceAmt, maxResourceAmt,
//			  resourceGained, additionalResources }
//		  );
		  unclaimedResourceContainer.add(0);
		  return cappedResourceGained;
	  }
  }

  private boolean updateUser(User u, int expGained, int cashGained, int oilGained,
	  Timestamp clientTime)
  {
	  int energyChange = 0;
	  if (!u.updateRelativeCashOilExpTasksCompleted(expGained, cashGained, oilGained, 1,
		  clientTime)) {
		  String preface = "problem updating user stats.";
		  String midface = "increased tasks completed by 1,";
		  log.error(String.format(
			  "%s expGained=%s, cashGained=%s, oilGained=%s, %s energyChange=%s, clientTime=%s, user=%s",
			  preface, expGained, cashGained, oilGained, midface, energyChange, clientTime, u));
		  return false;
	  }
	  return true;
  }
  //
  private void recordStageHistory(List<TaskStageForUser> tsfuList,
	  	Set<String> droplessTsfuIds,
  		Map<Integer, Integer> monsterIdToNumPieces,
  		Map<Integer, Map<Integer, Integer>> monsterIdToLvlToQuantity)
  {
  	//keep track of how many pieces dropped and by which task stage monster
  	Map<Integer, Integer> tsmIdToQuantity =
  			new HashMap<Integer, Integer>();
  	
  	
  	//collections to hold values to be saved to the db
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
  	
  	for (int i = 0; i < tsfuList.size(); i++) {
  		TaskStageForUser tsfu = tsfuList.get(i);
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
  		if (droplessTsfuIds.contains(tsfu.getId())) {
  			dropped = false;
  		}
  		
  		monsterPieceDropped.add(dropped);
  		itemIdDropped.add(tsfu.getItemIdDropped());
  		
  		TaskStageMonster tsm = TaskStageMonsterRetrieveUtils.getTaskStageMonsterForId(tsmId);
  		monsterIdDrops.add(
  			tsm.getMonsterIdDrop());
  		monsterDropLvls.add(
  			tsm.getMonsterDropLvl());
  		attackedFirstList.add(tsfu.isAttackedFirst());
  		
  		if (!dropped) {
  			//not going to keep track of non dropped monster pieces
  			//since user is not going to get it
  			continue;
  		}
  		
  		//since monster piece dropped, update our current stats on monster pieces
  		//this was done under the assumption that one task stage could have
  		//more than one task stage monster (otheriwse only the else case would execute)
  		if (tsmIdToQuantity.containsKey(tsmId)) {
  			//saw this task stage monster id before, increment quantity
  			int quantity = 1 + tsmIdToQuantity.get(tsmId);
  			tsmIdToQuantity.put(tsmId, quantity);
  			
  		} else {
  			//haven't seen this task stage monster id yet, so start off at 1
  			tsmIdToQuantity.put(tsmId, 1);
  		}
  	}
  	
  	int num = InsertUtils.get().insertIntoTaskStageHistory(userTaskStageId,
  			userTaskId, stageNum, tsmIdList, monsterTypes, expGained,
  			cashGained, oilGained, monsterPieceDropped, itemIdDropped,
  			monsterIdDrops, monsterDropLvls, attackedFirstList);
  	log.info(String.format(
  		"num task stage history rows inserted: num=%s, taskStageForUser=%s",
  		num, tsfuList));
  	
  	//DELETE FROM TASK_STAGE_FOR_USER
  	num = DeleteUtils.get().deleteTaskStagesForUserWithIds(userTaskStageId);
  	log.info(String.format("num task stage for user rows deleted: num=%s", num));
  	
  	//retrieve those task stage monsters. aggregate the quantities by monster id
  	//assume different task stage monsters can be the same monster
  	Collection<Integer> tsmIds = tsmIdToQuantity.keySet();
  	Map<Integer, TaskStageMonster> monstersThatDropped = TaskStageMonsterRetrieveUtils
  			.getTaskStageMonstersForIds(tsmIds);
  	
  	for (int tsmId : tsmIds) {
  		TaskStageMonster monsterThatDropped = monstersThatDropped.get(tsmId);
  		//int monsterId = monsterThatDropped.getMonsterId();
  		//task stage monster can drop something other than itself
  		int monsterId = monsterThatDropped.getMonsterIdDrop();
  		int monsterDropLvl = monsterThatDropped.getMonsterDropLvl();
  		
  		//if monster that drop has level higher than zero, it is a complete monster
  		if (monsterDropLvl > 0) {
  			//base case initialization when inserting never before seen monsterId
  			if (!monsterIdToLvlToQuantity.containsKey(monsterId)) {
  				monsterIdToLvlToQuantity.put(
  					monsterId, new HashMap<Integer, Integer>());
  			}
  			Map<Integer, Integer> lvlToQuantity =
  				monsterIdToLvlToQuantity.get(monsterId);
  			
  			//base case initialization when inserting never before seen level for monsterId
  			if (!lvlToQuantity.containsKey(monsterDropLvl)) {
  				lvlToQuantity.put(monsterDropLvl, 0);
  			}
  			int newAmount = 1 + lvlToQuantity.get(monsterDropLvl); 
  			lvlToQuantity.put(monsterDropLvl, newAmount);
  			
  			
  			continue;
  		}
  		
  		int numPiecesDroppedForMonster = tsmIdToQuantity.get(tsmId); 
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
  
  private void awardOneTimeItem(
	  Builder resBuilder, String userId, int itemId, int taskId,
	  TaskMapElement tme)
  {
	  log.info(String.format(
		  "awarding item: itemId=%s, taskId=%s, TaskMapElement=%s",
		  itemId, taskId, tme));
	  if (itemId <= 0) {
		  log.info("NOT!!!");
		  return;
	  }
	  
	  int quantity = 1;
	  int numInserted = InsertUtils.get()
		  .insertIntoUpdateUserItem(userId, itemId, quantity);
	  log.info(String.format(
		  "numInserted into userItem: %s",
		  numInserted));
	  if (numInserted > 0) {
		  //send updated quantity not just quantity of 1
		  List<ItemForUser> ifuList = (itemForUserRetrieveUtil
			  .getSpecificOrAllItemForUser(
				  userId,
				  Collections.singleton(itemId)));
		  
		  if (ifuList.size() > 0) {
		      ItemForUser ifu = ifuList.get(0);
	          UserItemProto uip = CreateInfoProtoUtils.createUserItemProtoFromUserItem(ifu);
	          resBuilder.setUserItem(uip);
//	        resBuilder.setUserItem(CreateInfoProtoUtils.createUserItemProto(userId, itemId,
//	            quantity));
	          resBuilder.setTaskMapSectionName(tme.getSectionName());
		  } else {
		      log.error (String.format ("unable to retrieve item for itemId=%s, userId=%s", itemId, userId));
		  }
	  } else {
		  log.error(String.format(
			  "unable to award item specified in TaskMapElement=%s",
			  tme));
	  }

  }
  
  private void setResponseBuilder(Builder resBuilder,
		  List<FullUserMonsterProto> protos, UserTaskCompleted utc)
  {
	  resBuilder.setStatus(EndDungeonStatus.SUCCESS);
	  
	  if (!protos.isEmpty()) {
	  	resBuilder.addAllUpdatedOrNew(protos);
	  }

	  resBuilder.setUtcp(
		  CreateInfoProtoUtils.createUserTaskCompletedProto(utc));
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
	  String userTaskId, int taskId, int previousCash, int previousOil,
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
  	
  	String userId = aUser.getId();
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
  
  private void writeToTaskForUserCompleted( 
  		boolean userWon, boolean firstTimeUserWonTask, Timestamp now,
  		UserTaskCompleted newUtc, UserTaskCompleted oldUtc)
  {
	  if (!userWon) {
		  return;
	  }
	  if (firstTimeUserWonTask) {
		  int numInserted = InsertUtils.get()
			  .insertIntoTaskForUserCompleted(newUtc, now);

		  log.info(String.format(
			  "numInserted into task_for_user_completed: %s", numInserted));
		  return;
	  }

	  int previousUnclaimedCash = 0;
	  int previousUnclaimedOil = 0;

	  if (null != oldUtc) {
		  //just a precaution
		  previousUnclaimedCash = oldUtc.getUnclaimedCash();
		  previousUnclaimedOil = oldUtc.getUnclaimedOil();
	  }
	  if (0 == previousUnclaimedCash && 0 == previousUnclaimedOil) {
		  //no need to update since user depleted the task of its resources
		  return;
	  }

	  //persist newUtc to the db
	  int numUpdated = UpdateUtils.get().updateTaskForUserCompleted(newUtc);
	  log.info("numUpdated task_for_user_completed: {}", numUpdated);
  }

  public Locker getLocker() {
	  return locker;
  }

  public void setLocker(Locker locker) {
	  this.locker = locker;
  }

  public UserRetrieveUtils2 getUserRetrieveUtil()
  {
	  return userRetrieveUtil;
  }

  public void setUserRetrieveUtil( UserRetrieveUtils2 userRetrieveUtil )
  {
	  this.userRetrieveUtil = userRetrieveUtil;
  }

  public ItemForUserRetrieveUtil getItemForUserRetrieveUtil()
  {
	  return itemForUserRetrieveUtil;
  }

  public void setItemForUserRetrieveUtil( ItemForUserRetrieveUtil itemForUserRetrieveUtil )
  {
	  this.itemForUserRetrieveUtil = itemForUserRetrieveUtil;
  }

  public TaskForUserOngoingRetrieveUtils2 getTaskForUserOngoingRetrieveUtil()
  {
	  return taskForUserOngoingRetrieveUtil;
  }

  public void setTaskForUserOngoingRetrieveUtil(
	  TaskForUserOngoingRetrieveUtils2 taskForUserOngoingRetrieveUtil )
  {
	  this.taskForUserOngoingRetrieveUtil = taskForUserOngoingRetrieveUtil;
  }

  public TaskForUserCompletedRetrieveUtils getTaskForUserCompletedRetrieveUtil()
  {
	  return taskForUserCompletedRetrieveUtil;
  }

  public void setTaskForUserCompletedRetrieveUtil(
	  TaskForUserCompletedRetrieveUtils taskForUserCompletedRetrieveUtil )
  {
	  this.taskForUserCompletedRetrieveUtil = taskForUserCompletedRetrieveUtil;
  }

  public TaskStageForUserRetrieveUtils2 getTaskStageForUserRetrieveUtil()
  {
	  return taskStageForUserRetrieveUtil;
  }

  public void setTaskStageForUserRetrieveUtil(
	  TaskStageForUserRetrieveUtils2 taskStageForUserRetrieveUtil )
  {
	  this.taskStageForUserRetrieveUtil = taskStageForUserRetrieveUtil;
  }

}
