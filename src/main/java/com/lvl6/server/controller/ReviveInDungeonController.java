package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.ReviveInDungeonRequestEvent;
import com.lvl6.events.response.ReviveInDungeonResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.Task;
import com.lvl6.info.User;
import com.lvl6.info.UserTask;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventProto.ReviveInDungeonRequestProto;
import com.lvl6.proto.EventProto.ReviveInDungeonResponseProto;
import com.lvl6.proto.EventProto.ReviveInDungeonResponseProto.Builder;
import com.lvl6.proto.EventProto.ReviveInDungeonResponseProto.ReviveInDungeonStatus;
import com.lvl6.proto.InfoProto.FullUserEquipProto;
import com.lvl6.proto.InfoProto.MinimumUserProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.retrieveutils.UserTaskRetrieveUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.InsertUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component @DependsOn("gameServer") public class ReviveInDungeonController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());


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

    //set some values to send to the client (the response proto)
    ReviveInDungeonResponseProto.Builder resBuilder = ReviveInDungeonResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setStatus(ReviveInDungeonStatus.FAIL_OTHER); //default

    server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    try {
      User aUser = RetrieveUtils.userRetrieveUtils().getUserById(userId);
//      int previousSilver = 0;
//      int previousGold = 0;

      List<UserTask> userTaskList = new ArrayList<UserTask>();
      boolean legit = checkLegit(resBuilder, aUser, userId, userTaskId, userTaskList);


      boolean successful = false;
      if(legit) {
    	  UserTask ut = userTaskList.get(0);
//        previousSilver = aUser.getCoins() + aUser.getVaultBalance();
//        previousGold = aUser.getDiamonds();
    	  successful = writeChangesToDb(aUser, userId, ut, userTaskId, curTime);
//        writeToUserCurrencyHistory(aUser, money, curTime, previousSilver, previousGold);
      }
      if (successful) {
    	  resBuilder.setStatus(ReviveInDungeonStatus.SUCCESS);
      }
      
      ReviveInDungeonResponseEvent resEvent = new ReviveInDungeonResponseEvent(userId);
      resEvent.setTag(event.getTag());
      resEvent.setReviveInDungeonResponseProto(resBuilder.build());
      server.writeEvent(resEvent);

      UpdateClientUserResponseEvent resEventUpdate = MiscMethods
          .createUpdateClientUserResponseEventAndUpdateLeaderboard(aUser);
      resEventUpdate.setTag(event.getTag());
      server.writeEvent(resEventUpdate);
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
      server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    }
  }

  /*
   * Return true if user request is valid; false otherwise and set the
   * builder status to the appropriate value.
   */
  private boolean checkLegit(Builder resBuilder, User u, int userId,
		  long userTaskId, List<UserTask> userTaskList) {
    if (null == u) {
      log.error("unexpected error: user is null. user=" + u);
      return false;
    }
    
    //make sure user task exists
    UserTask ut = UserTaskRetrieveUtils.getUserTaskForId(userTaskId);
    if (null == ut) {
    	log.error("unexpected error: no user task for id userTaskId=" + userTaskId);
    	return false;
    }
    
    //make sure user has enough diamonds/gold?
    int userDiamonds = u.getDiamonds();
    int cost = ControllerConstants.TASK_ACTION__REVIVE_COST;
    if (cost > userDiamonds) {
    	log.error("user error: user does not have enough diamonds to revive. " +
    			"cost=" + cost + "\t userDiamonds=" + userDiamonds);
    	resBuilder.setStatus(ReviveInDungeonStatus.FAIL_INSUFFICIENT_FUNDS);
    	return false;
    }
    
    userTaskList.add(ut);
    resBuilder.setStatus(ReviveInDungeonStatus.SUCCESS);
    return true;
  }

  private boolean writeChangesToDb(User u, int uId, UserTask ut, long userTaskId, 
		  Timestamp clientTime) {
	  
	  //update user diamonds
	  int diamondChange = ControllerConstants.TASK_ACTION__REVIVE_COST;
	  if (!updateUser(u, diamondChange)) {
		  log.error("unexpected error: could not decrement user's gold by " +
				  diamondChange);
		  //update num revives for user task
		  return false;
	  }
	  
	  int numRevives = ut.getNumRevives() + 1;
	  int numUpdated = UpdateUtils.get().incrementUserTaskNumRevives(userTaskId, numRevives); 
	  if (1 != numUpdated) {
		  log.error("unexpected error: user_task not updated correctly. Attempting " +
				  "to give back diamonds. userTaskId=" + userTaskId + "\t numUpdated=" +
				  numUpdated + "\t user=" + u);
		  //undo gold charge
		  if (!updateUser(u, diamondChange)) {
			  log.error("unexpected error: could not change user's gold by " +
					  diamondChange);
		  }
		  return false;
	  }
	  return true;
  }
  
  private boolean updateUser(User u, int diamondChange) {
	  if (!u.updateRelativeDiamondsNaive(diamondChange)) {
		  log.error("unexpected error: problem with updating user diamonds for reviving. diamondChange=" +
				  diamondChange + "user=" + u);
		  return false;
	  }
	  return true;
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
