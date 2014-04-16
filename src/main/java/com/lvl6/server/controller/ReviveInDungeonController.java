package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.ReviveInDungeonRequestEvent;
import com.lvl6.events.response.ReviveInDungeonResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.proto.EventDungeonProto.ReviveInDungeonRequestProto;
import com.lvl6.proto.EventDungeonProto.ReviveInDungeonResponseProto;
import com.lvl6.proto.EventDungeonProto.ReviveInDungeonResponseProto.Builder;
import com.lvl6.proto.EventDungeonProto.ReviveInDungeonResponseProto.ReviveInDungeonStatus;
import com.lvl6.proto.MonsterStuffProto.UserMonsterCurrentHealthProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component @DependsOn("gameServer") public class ReviveInDungeonController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  @Autowired
  protected Locker locker;

  public ReviveInDungeonController() {
    numAllocatedThreads = 4;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new ReviveInDungeonRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_REVIVE_IN_DUNGEON_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    ReviveInDungeonRequestProto reqProto = ((ReviveInDungeonRequestEvent)event).getReviveInDungeonRequestProto();

    //get values sent from the client (the request proto)
    MinimumUserProto senderProto = reqProto.getSender();
    int userId = senderProto.getUserId();
    long userTaskId = reqProto.getUserTaskId();
    Timestamp curTime = new Timestamp(reqProto.getClientTime());
    List<UserMonsterCurrentHealthProto> reviveMeProtoList = reqProto.getReviveMeList();
    //positive value, need to convert to negative when updating user
    int gemsSpent = reqProto.getGemsSpent();

    //set some values to send to the client (the response proto)
    ReviveInDungeonResponseProto.Builder resBuilder = ReviveInDungeonResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setStatus(ReviveInDungeonStatus.FAIL_OTHER); //default

    getLocker().lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    try {
      User aUser = RetrieveUtils.userRetrieveUtils().getUserById(userId);
//      int previousGems = 0;

      //will be populated by checkLegit(...);
      Map<Long, Integer> userMonsterIdToExpectedHealth = new HashMap<Long, Integer>();
    	
//      List<TaskForUserOngoing> userTaskList = new ArrayList<TaskForUserOngoing>();
      boolean legit = checkLegit(resBuilder, aUser, userId, gemsSpent, userTaskId,
      		reviveMeProtoList, userMonsterIdToExpectedHealth);//, userTaskList);


      boolean successful = false;
      if(legit) {
//    	  TaskForUserOngoing ut = userTaskList.get(0);
//        previousGems = aUser.getGems();
    	  successful = writeChangesToDb(aUser, userId, userTaskId, gemsSpent, curTime,
    	  		userMonsterIdToExpectedHealth);
//        writeToUserCurrencyHistory(aUser, money, curTime, previousSilver, previousGold);
      }
      if (successful) {
    	  resBuilder.setStatus(ReviveInDungeonStatus.SUCCESS);
      }
      
      ReviveInDungeonResponseEvent resEvent = new ReviveInDungeonResponseEvent(userId);
      resEvent.setTag(event.getTag());
      resEvent.setReviveInDungeonResponseProto(resBuilder.build());
      server.writeEvent(resEvent);

      if (successful) {
    	  //null PvpLeagueFromUser means will pull from hazelcast instead
      	UpdateClientUserResponseEvent resEventUpdate = MiscMethods
      			.createUpdateClientUserResponseEventAndUpdateLeaderboard(aUser, null);
      	resEventUpdate.setTag(event.getTag());
      	server.writeEvent(resEventUpdate);
      }
    } catch (Exception e) {
      log.error("exception in ReviveInDungeonController processEvent", e);
      //don't let the client hang
      try {
    	  resBuilder.setStatus(ReviveInDungeonStatus.FAIL_OTHER);
    	  ReviveInDungeonResponseEvent resEvent = new ReviveInDungeonResponseEvent(userId);
    	  resEvent.setTag(event.getTag());
    	  resEvent.setReviveInDungeonResponseProto(resBuilder.build());
    	  server.writeEvent(resEvent);
      } catch (Exception e2) {
    	  log.error("exception2 in ReviveInDungeonController processEvent", e);
      }
    } finally {
      getLocker().unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    }
  }

  /*
   * Return true if user request is valid; false otherwise and set the
   * builder status to the appropriate value.
   */
  private boolean checkLegit(Builder resBuilder, User u, int userId, int gemsSpent,
		  long userTaskId, List<UserMonsterCurrentHealthProto> reviveMeProtoList,
		  Map<Long, Integer> userMonsterIdToExpectedHealth) {//, List<TaskForUserOngoing> userTaskList) {
    if (null == u) {
      log.error("unexpected error: user is null. user=" + u);
      return false;
    }
//    
//    //make sure user task exists
//    TaskForUserOngoing ut = TaskForUserOngoingRetrieveUtils.getUserTaskForId(userTaskId);
//    if (null == ut) {
//    	log.error("unexpected error: no user task for id userTaskId=" + userTaskId);
//    	return false;
//    }
    
    //extract the ids so it's easier to get userMonsters from db
    List<Long> userMonsterIds = MonsterStuffUtils.getUserMonsterIds(reviveMeProtoList,
    		userMonsterIdToExpectedHealth);
    Map<Long, MonsterForUser> userMonsters = RetrieveUtils.monsterForUserRetrieveUtils()
    		.getSpecificOrAllUserMonstersForUser(userId, userMonsterIds);

    if (null == userMonsters || userMonsters.isEmpty()) {
    	log.error("unexpected error: userMonsterIds don't exist. ids=" + userMonsterIds);
    	return false;
    }

    //see if the user has the equips
    if (userMonsters.size() != reviveMeProtoList.size()) {
    	log.error("unexpected error: mismatch between user equips client sent and " +
    			"what is in the db. clientUserMonsterIds=" + userMonsterIds + "\t inDb=" +
    			userMonsters + "\t continuing the processing");
    }
    
    //make sure user has enough diamonds/gold?
    int userDiamonds = u.getGems();
    int cost = gemsSpent;
    if (cost > userDiamonds) {
    	log.error("user error: user does not have enough diamonds to revive. " +
    			"cost=" + cost + "\t userDiamonds=" + userDiamonds);
    	resBuilder.setStatus(ReviveInDungeonStatus.FAIL_INSUFFICIENT_FUNDS);
    	return false;
    }
    
//    userTaskList.add(ut);
    resBuilder.setStatus(ReviveInDungeonStatus.SUCCESS);
    return true;
  }

  private boolean writeChangesToDb(User u, int uId, long userTaskId, int gemsSpent,
  		Timestamp clientTime, Map<Long, Integer> userMonsterIdToExpectedHealth) {
	  
	  //update user diamonds
	  int gemsChange = -1 * gemsSpent;
	  if (!updateUser(u, gemsChange)) {
		  log.error("unexpected error: could not decrement user's gold by " +
				  gemsChange);
		  //update num revives for user task
		  return false;
	  }
	  
	  int numRevivesDelta = 1;
	  int numUpdated = UpdateUtils.get().incrementUserTaskNumRevives(userTaskId, numRevivesDelta); 
	  if (1 != numUpdated) {
		  log.error("unexpected error: user_task not updated correctly. Attempting " +
				  "to give back diamonds. userTaskId=" + userTaskId + "\t numUpdated=" +
				  numUpdated + "\t user=" + u);
		  //undo gold charge
		  if (!updateUser(u, -1 * gemsChange)) {
			  log.error("unexpected error: could not change back user's gems by " +
					  -1 * gemsChange);
		  }
		  return false;
	  }
	  
	  //HEAL THE USER MONSTERS
	  //replace existing health for these user monsters with new values 
	  numUpdated = UpdateUtils.get()
	  		.updateUserMonstersHealth(userMonsterIdToExpectedHealth);

	  if (numUpdated >= userMonsterIdToExpectedHealth.size()) {
	  	return true;
	  }
	  log.warn("unexpected error: not all user equips were updated. " +
	  		"actual numUpdated=" + numUpdated + "expected: " +
	  		"userMonsterIdToExpectedHealth=" + userMonsterIdToExpectedHealth);
	  
	  return true;
  }
  
  private boolean updateUser(User u, int diamondChange) {
	  if (!u.updateRelativeGemsNaive(diamondChange)) {
		  log.error("unexpected error: problem with updating user diamonds for reviving. diamondChange=" +
				  diamondChange + "user=" + u);
		  return false;
	  }
	  return true;
  }
  
  //TODO: FIX THIS
  private void writeToUserCurrencyHistory(User aUser, Map<String, Integer> money, Timestamp curTime,
      int previousSilver, int previousGold) {
//    Map<String, Integer> previousGoldSilver = new HashMap<String, Integer>();
//    Map<String, String> reasonsForChanges = new HashMap<String, String>();
//    String reasonForChange = ControllerConstants.UCHRFC__BOSS_ACTION;
//    String gems = MiscMethods.gems;
//    String cash = MiscMethods.cash;
//
//    previousGoldSilver.put(gems, previousGold);
//    previousGoldSilver.put(cash, previousSilver);
//    reasonsForChanges.put(gems, reasonForChange);
//    reasonsForChanges.put(cash, reasonForChange);
//
//    MiscMethods.writeToUserCurrencyOneUserGemsAndOrCash(aUser, curTime, money, 
//        previousGoldSilver, reasonsForChanges);

  }

  public Locker getLocker() {
	  return locker;
  }

  public void setLocker(Locker locker) {
	  this.locker = locker;
  }

}
