package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.EnhancementWaitTimeCompleteRequestEvent;
import com.lvl6.events.response.EnhancementWaitTimeCompleteResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.MonsterEnhancingForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventMonsterProto.EnhancementWaitTimeCompleteRequestProto;
import com.lvl6.proto.EventMonsterProto.EnhancementWaitTimeCompleteResponseProto;
import com.lvl6.proto.EventMonsterProto.EnhancementWaitTimeCompleteResponseProto.Builder;
import com.lvl6.proto.EventMonsterProto.EnhancementWaitTimeCompleteResponseProto.EnhancementWaitTimeCompleteStatus;
import com.lvl6.proto.MonsterStuffProto.UserMonsterCurrentExpProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.MonsterEnhancingForUserRetrieveUtils;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component @DependsOn("gameServer") public class EnhancementWaitTimeCompleteController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());


  public EnhancementWaitTimeCompleteController() {
    numAllocatedThreads = 4;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new EnhancementWaitTimeCompleteRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_ENHANCEMENT_WAIT_TIME_COMPLETE_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    EnhancementWaitTimeCompleteRequestProto reqProto = ((EnhancementWaitTimeCompleteRequestEvent)event).getEnhancementWaitTimeCompleteRequestProto();

    //get values sent from the client (the request proto)
    MinimumUserProto senderProto = reqProto.getSender();
    int userId = senderProto.getUserId();
    boolean isSpeedUp = reqProto.getIsSpeedup();
    int gemsForSpeedUp = reqProto.getGemsForSpeedup();
    UserMonsterCurrentExpProto umcep = reqProto.getUmcep();
    //user monster ids that will be deleted from monster enhancing for user table
    List<Long> userMonsterIdsThatFinished = reqProto.getUserMonsterIdsList();
    userMonsterIdsThatFinished = new ArrayList<Long>(userMonsterIdsThatFinished);

    //set some values to send to the client (the response proto)
    EnhancementWaitTimeCompleteResponseProto.Builder resBuilder = EnhancementWaitTimeCompleteResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setStatus(EnhancementWaitTimeCompleteStatus.FAIL_OTHER); //default

    server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    try {
      //int previousSilver = 0;
      //int previousGold = 0;
    	//get whatever we need from the database
    	User aUser = RetrieveUtils.userRetrieveUtils().getUserById(userId);
    	Map<Long, MonsterEnhancingForUser> alreadyEnhancing =
    			MonsterEnhancingForUserRetrieveUtils.getMonstersForUser(userId);
    	
    	//do check to make sure one monster has a null start time
      boolean legit = checkLegit(resBuilder, aUser, userId, alreadyEnhancing,
      		umcep, userMonsterIdsThatFinished, isSpeedUp, gemsForSpeedUp);

      boolean successful = false;
      if(legit) {
//        previousSilver = aUser.getCoins();
//        previousGold = aUser.getDiamonds();
    	  successful = writeChangesToDb(aUser, userId, alreadyEnhancing, umcep,
    	  		userMonsterIdsThatFinished, isSpeedUp, gemsForSpeedUp);
//        writeToUserCurrencyHistory(aUser, money, curTime, previousSilver, previousGold);
      }
      if (successful) {
    	  setResponseBuilder(resBuilder);
      }
      
      EnhancementWaitTimeCompleteResponseEvent resEvent = new EnhancementWaitTimeCompleteResponseEvent(userId);
      resEvent.setTag(event.getTag());
      resEvent.setEnhancementWaitTimeCompleteResponseProto(resBuilder.build());
      server.writeEvent(resEvent);

      UpdateClientUserResponseEvent resEventUpdate = MiscMethods
          .createUpdateClientUserResponseEventAndUpdateLeaderboard(aUser);
      resEventUpdate.setTag(event.getTag());
      server.writeEvent(resEventUpdate);
    } catch (Exception e) {
      log.error("exception in EnhancementWaitTimeCompleteController processEvent", e);
      //don't let the client hang
      try {
    	  resBuilder.setStatus(EnhancementWaitTimeCompleteStatus.FAIL_OTHER);
    	  EnhancementWaitTimeCompleteResponseEvent resEvent = new EnhancementWaitTimeCompleteResponseEvent(userId);
    	  resEvent.setTag(event.getTag());
    	  resEvent.setEnhancementWaitTimeCompleteResponseProto(resBuilder.build());
    	  server.writeEvent(resEvent);
      } catch (Exception e2) {
    	  log.error("exception2 in EnhancementWaitTimeCompleteController processEvent", e);
      }
    } finally {
      server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    }
  }
  
  /**
   * Return true if user request is valid; false otherwise and set the
   * builder status to the appropriate value.
   * 
   * Will return fail if user does not have enough funds. 
   * For the most part, will always return success. Why?
   * Answer: For @healedUp, the monsters the client thinks completed healing,
   * only existing/valid ids will be taken off the healing queue.
   * 
   * Ex. Queue is (a,b,c,d). a is the base monster, b,c,d are the feeders.
   * If user says monster (b, e) finished enhancing a, 
   * only the valid monsters (b) will be removed from the queue, leaving (a,c,d)
   * 
   * @param resBuilder
   * @param u
   * @param userId
   * @param alreadyEnhancing - the monsters that are in the enhancing queue
   * @param umcep - the base monster that is updated from using up some of the feeders
   * @param usedUpUserMonsterIds - userMonsterIds the user thinks has finished being enhanced
   * @param speedUUp
   * @param gemsForSpeedUp
   * @return
   */
  private boolean checkLegit(Builder resBuilder, User u, int userId,
  		Map<Long, MonsterEnhancingForUser> alreadyEnhancing, UserMonsterCurrentExpProto umcep,
  		List<Long> usedUpMonsterIds, boolean speedUp, int gemsForSpeedUp) {
  	
    if (null == u || null == umcep || usedUpMonsterIds.isEmpty()) {
      log.error("unexpected error: user or idList is null. user=" + u +
      		"\t umcep="+ umcep + "usedUpMonsterIds=" + usedUpMonsterIds +
      		"\t speedUp=" + speedUp + "\t gemsForSpeedUp=" + gemsForSpeedUp);
      return false;
    }
    log.info("alreadyEnhancing=" + alreadyEnhancing);
    
    Set<Long> alreadyEnhancingIds = alreadyEnhancing.keySet();
    MonsterStuffUtils.retainValidMonsterIds(alreadyEnhancingIds, usedUpMonsterIds);
    
    //check to make sure the base monsterId is in enhancing
    if (!alreadyEnhancingIds.contains(umcep.getUserMonsterId())) {
    	log.error("client did not send updated base monster specifying what new exp and lvl are");
    	return false;
    }
    
    //TODO: CHECK MONEY and CHECK SPEEDUP
    if (speedUp) {
    	
    }
    	
    resBuilder.setStatus(EnhancementWaitTimeCompleteStatus.SUCCESS);
    return true;
  }
  
  private boolean writeChangesToDb(User u, int uId,
  		Map<Long, MonsterEnhancingForUser> alreadyEnhancing, UserMonsterCurrentExpProto umcep,
  		List<Long> userMonsterIds, boolean isSpeedUp, int gemsForSpeedUp) {


  	//TODO: CHARGE THE USER
//	  if (!u.updateRelativeCoinsExpTaskscompleted(0, 0, 0, clientTime)) {
//		  log.error("problem with updating user stats post-task. silverGained="
//				  + 0 + ", expGained=" + 0 + ", increased tasks completed by 0," +
//				  ", clientTime=" + clientTime + ", user=" + u);
//		  return false;
//	  }
  	
  	long userMonsterIdBeingEnhanced = umcep.getUserMonsterId();
  	int newExp = umcep.getExpectedExperience();
  	int newLvl = umcep.getExpectedLevel();
  	
  	//TODO: GIVE THE MONSTER EXP
  	int num = UpdateUtils.get().updateUserMonsterExpAndLvl(userMonsterIdBeingEnhanced,
  			newExp, newLvl);
  	log.info("num updated=" + num);

	  //delete the selected monsters from  the healing table
	  num = DeleteUtils.get().deleteMonsterEnhancingForUser(
	  		uId, userMonsterIds);
	  log.info("deleted monster healing rows. numDeleted=" + num +
	  		"\t userMonsterIds=" + userMonsterIds);
	  
	  //delete the userMonsterIds from the monster_for_user table, but don't delete
	  //the monster user is enhancing
	  num = DeleteUtils.get().deleteMonstersForUser(userMonsterIds);
	  log.info("defeated monster_for_user rows. numDeleted=" + num);
	  
	  return true;
  }
  
  private void setResponseBuilder(Builder resBuilder) {
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
