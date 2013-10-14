package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
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
import com.lvl6.info.User;
import com.lvl6.info.UserTask;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventDungeonProto.EndDungeonRequestProto;
import com.lvl6.proto.EventDungeonProto.EndDungeonResponseProto;
import com.lvl6.proto.EventDungeonProto.EndDungeonResponseProto.Builder;
import com.lvl6.proto.EventDungeonProto.EndDungeonResponseProto.EndDungeonStatus;
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.UserTaskRetrieveUtils;
import com.lvl6.utils.RetrieveUtils;
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

      List<UserTask> userTaskList = new ArrayList<UserTask>();
      boolean legit = checkLegit(resBuilder, aUser, userId, userTaskId, userTaskList);


      boolean successful = false;
      List<FullUserMonsterProto> protos = new ArrayList<FullUserMonsterProto>();
      if(legit) {
    	  UserTask ut = userTaskList.get(0);
//        previousSilver = aUser.getCoins() + aUser.getVaultBalance();
//        previousGold = aUser.getDiamonds();
    	  successful = writeChangesToDb(aUser, userId, ut, userWon, curTime,
    			  protos);
//        writeToUserCurrencyHistory(aUser, money, curTime, previousSilver, previousGold);
      }
      if (successful) {
    	  setResponseBuilder(resBuilder, protos);
      }
      
      EndDungeonResponseEvent resEvent = new EndDungeonResponseEvent(userId);
      resEvent.setTag(event.getTag());
      resEvent.setEndDungeonResponseProto(resBuilder.build());
      server.writeEvent(resEvent);

      UpdateClientUserResponseEvent resEventUpdate = MiscMethods
          .createUpdateClientUserResponseEventAndUpdateLeaderboard(aUser);
      resEventUpdate.setTag(event.getTag());
      server.writeEvent(resEventUpdate);
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
		  long userTaskId, List<UserTask> userTaskList) {
    if (null == u) {
      log.error("unexpected error: user is null. user=" + u);
      return false;
    }
    
    UserTask ut = UserTaskRetrieveUtils.getUserTaskForId(userTaskId);
    if (null == ut) {
    	log.error("unexpected error: no user task for id userTaskId=" + userTaskId);
    	return false;
    }
    
    userTaskList.add(ut);
    resBuilder.setStatus(EndDungeonStatus.SUCCESS);
    return true;
  }

  private boolean writeChangesToDb(User u, int uId, UserTask ut, boolean userWon,
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
	  
	  //delete from user_task and insert it into user_task_history
	  long utId = ut.getId();
	  int tId = ut.getTaskId();
	  int numRevives = ut.getNumRevives();
	  Date startDate = ut.getStartDate();
	  long startMillis = startDate.getTime();
	  Timestamp startTime = new Timestamp(startMillis);
	  int numInserted = InsertUtils.get().insertIntoUserTaskHistory(utId,uId, tId,
			  expGained, silverGained, numRevives, startTime, clientTime, userWon);
	  if (1 != numInserted) {
		  log.error("unexpected error: error when inserting into user_task_history. " +
		  		"numInserted=" + numInserted + " Attempting to undo shi");
		  updateUser(u, -1* silverGained, -1 * expGained, clientTime);
		  return false;
	  }
	  return true;
  }
  
  //fill up 'protos' with the equips the user gets after writing to db
  /*private void meteEquips(int uId, Timestamp now, UserTask ut,
		  List<FullUserEquipProto> protos) {
	  List<Integer> equipIds = ut.getMonsterRewardEquipIds();
	  
	  //arguments for db call
	  int amount = equipIds.size();
	  int forgeLevel = ControllerConstants.DEFAULT_USER_EQUIP_LEVEL;
	  int enhancementLevel = ControllerConstants.DEFAULT_USER_EQUIP_ENHANCEMENT_PERCENT;
	  List<Integer> levels = new ArrayList<Integer>(Collections.nCopies(amount, forgeLevel));
	  List<Integer> enhancement = new ArrayList<Integer>(Collections.nCopies(amount, enhancementLevel));
	  String reason = ControllerConstants.UER__TASK_ACTION;

	  //give user the equips
	  List<Long> userEquipIds = InsertUtils.get().insertUserEquips(uId,
			  equipIds, levels, enhancement, now, reason);
	  
	  //construct the protos, now that the userEquipIds are known
	  for (int i = 0; i < equipIds.size(); i++) {
		  long userEquipId = userEquipIds.get(i);
		  int equipId = equipIds.get(i);
		  
		  FullUserEquipProto fuep = CreateInfoProtoUtils.createFullUserEquipProto(
				  userEquipId, uId, equipId, forgeLevel, enhancementLevel);
		  protos.add(fuep);
	  }
  }*/
  
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
  
  private void setResponseBuilder(Builder resBuilder,
		  List<FullUserMonsterProto> protos) {
	  resBuilder.setStatus(EndDungeonStatus.SUCCESS);
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
