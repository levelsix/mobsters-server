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
import com.lvl6.events.request.HealMonsterWaitTimeCompleteRequestEvent;
import com.lvl6.events.response.HealMonsterWaitTimeCompleteResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.MonsterHealingForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventMonsterProto.HealMonsterWaitTimeCompleteRequestProto;
import com.lvl6.proto.EventMonsterProto.HealMonsterWaitTimeCompleteResponseProto;
import com.lvl6.proto.EventMonsterProto.HealMonsterWaitTimeCompleteResponseProto.Builder;
import com.lvl6.proto.EventMonsterProto.HealMonsterWaitTimeCompleteResponseProto.HealMonsterWaitTimeCompleteStatus;
import com.lvl6.proto.MonsterStuffProto.UserMonsterCurrentHealthProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.MonsterHealingForUserRetrieveUtils;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component @DependsOn("gameServer") public class HealMonsterWaitTimeCompleteController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());


  public HealMonsterWaitTimeCompleteController() {
    numAllocatedThreads = 4;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new HealMonsterWaitTimeCompleteRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_HEAL_MONSTER_WAIT_TIME_COMPLETE_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    HealMonsterWaitTimeCompleteRequestProto reqProto = ((HealMonsterWaitTimeCompleteRequestEvent)event).getHealMonsterWaitTimeCompleteRequestProto();

    //get values sent from the client (the request proto)
    MinimumUserProto senderProto = reqProto.getSender();
    int userId = senderProto.getUserId();
    boolean isSpeedUp = reqProto.getIsSpeedup();
    List<UserMonsterCurrentHealthProto> umchpList = reqProto.getUmchpList();
    Map<Long, Integer> userMonsterIdToExpectedHealth = new HashMap<Long, Integer>();
    List<Long> userMonsterIds = MonsterStuffUtils.getUserMonsterIds(umchpList, userMonsterIdToExpectedHealth);
    int gemsForSpeedUp = reqProto.getGemsForSpeedup();

    //set some values to send to the client (the response proto)
    HealMonsterWaitTimeCompleteResponseProto.Builder resBuilder = HealMonsterWaitTimeCompleteResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setStatus(HealMonsterWaitTimeCompleteStatus.FAIL_OTHER); //default

    server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    try {
      //int previousSilver = 0;
      //int previousGold = 0;
    	//get whatever we need from the database
    	User aUser = RetrieveUtils.userRetrieveUtils().getUserById(userId);
    	Map<Long, MonsterHealingForUser> alreadyHealing =
    			MonsterHealingForUserRetrieveUtils.getMonstersForUser(userId);
    	
      boolean legit = checkLegit(resBuilder, aUser, userId, alreadyHealing,
      		userMonsterIds, isSpeedUp, gemsForSpeedUp);

      boolean successful = false;
      if(legit) {
//        previousSilver = aUser.getCoins();
//        previousGold = aUser.getDiamonds();
    	  successful = writeChangesToDb(aUser, userId, userMonsterIds, userMonsterIdToExpectedHealth,
    	  		isSpeedUp, gemsForSpeedUp);
//        writeToUserCurrencyHistory(aUser, money, curTime, previousSilver, previousGold);
      }
      if (successful) {
    	  setResponseBuilder(resBuilder);
      }
      
      HealMonsterWaitTimeCompleteResponseEvent resEvent = new HealMonsterWaitTimeCompleteResponseEvent(userId);
      resEvent.setTag(event.getTag());
      resEvent.setHealMonsterWaitTimeCompleteResponseProto(resBuilder.build());
      server.writeEvent(resEvent);

      UpdateClientUserResponseEvent resEventUpdate = MiscMethods
          .createUpdateClientUserResponseEventAndUpdateLeaderboard(aUser);
      resEventUpdate.setTag(event.getTag());
      server.writeEvent(resEventUpdate);
    } catch (Exception e) {
      log.error("exception in HealMonsterWaitTimeCompleteController processEvent", e);
      //don't let the client hang
      try {
    	  resBuilder.setStatus(HealMonsterWaitTimeCompleteStatus.FAIL_OTHER);
    	  HealMonsterWaitTimeCompleteResponseEvent resEvent = new HealMonsterWaitTimeCompleteResponseEvent(userId);
    	  resEvent.setTag(event.getTag());
    	  resEvent.setHealMonsterWaitTimeCompleteResponseProto(resBuilder.build());
    	  server.writeEvent(resEvent);
      } catch (Exception e2) {
    	  log.error("exception2 in HealMonsterWaitTimeCompleteController processEvent", e);
      }
    } finally {
      server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    }
  }
  
  /**
   * Return true if user request is valid; false otherwise and set the
   * builder status to the appropriate value. @healedUp
   * MIGHT BE MODIFIED.
   * 
   * Will return fail if user does not have enough funds. 
   * For the most part, will always return success. Why?
   * Answer: For @healedUp, the monsters the client thinks completed healing,
   * only existing/valid ids will be taken off the healing queue.
   * 
   * Ex. Queue is (a,b,c,d). If user says monster (a,b,e) finished healing, 
   * only the valid monsters (a,b) will be removed from the queue.
   * 
   * @param resBuilder
   * @param u
   * @param userId
   * @param alreadyHealing - the monsters that are in the healing queue
   * @param healedUp - userMonsterIds the user thinks has finished healing
   * @param speedUUp
   * @param gemsForSpeedUp
   * @return
   */
  private boolean checkLegit(Builder resBuilder, User u, int userId,
  		Map<Long, MonsterHealingForUser> alreadyHealing, List<Long> healedUp,
  		boolean speedUp, int gemsForSpeedUp) {
    if (null == u || null == healedUp || healedUp.isEmpty()) {
      log.error("unexpected error: user or idList is null. user=" + u +
      		"\t healedUp="+ healedUp + "\t speedUp=" + speedUp);
      return false;
    }
    log.info("alreadyHealing=" + alreadyHealing);
    
    Set<Long> alreadyHealingIds = alreadyHealing.keySet();
    retainValidMonsters(alreadyHealingIds, healedUp);
    
    //TODO: CHECK MONEY and CHECK SPEEDUP
    if (speedUp) {
    	
    }
    //TODO:update monster's healths
    	
    resBuilder.setStatus(HealMonsterWaitTimeCompleteStatus.SUCCESS);
    return true;
  }
  
  /*
   * selected monsters (the second argument) might be modified
   */
  private void retainValidMonsters(Set<Long> existing, List<Long> ids) {
//  	ids.add(123456789L);
//  	log.info("existing=" + existing + "\t ids=" + ids);
  	
  	List<Long> copyIds = new ArrayList<Long>(ids);
  	// remove the invalid ids from ids client sent 
  	// (modifying argument so calling function doesn't have to do it)
  	ids.retainAll(existing);
  	
  	if (copyIds.size() != ids.size()) {
  		//client asked for invalid ids
  		log.warn("client asked for some invalid ids. asked for ids=" + copyIds + 
  				"\t existingIds=" + existing + "\t remainingIds after purge =" + ids);
  	}
  }
  
  private boolean writeChangesToDb(User u, int uId, List<Long> userMonsterIds,
  		Map<Long, Integer> userMonsterIdsToHealths, boolean isSpeedUp, int gemsForSpeedUp) {


  	//TODO: CHARGE THE USER
//	  if (!u.updateRelativeCoinsExpTaskscompleted(0, 0, 0, clientTime)) {
//		  log.error("problem with updating user stats post-task. silverGained="
//				  + 0 + ", expGained=" + 0 + ", increased tasks completed by 0," +
//				  ", clientTime=" + clientTime + ", user=" + u);
//		  return false;
//	  }
  	
  	//TODO: HEAL THE MONSTER
  	List<Integer> currentHealths = new ArrayList<Integer>();
  	int num = UpdateUtils.get().updateUserMonstersHealth(userMonsterIds,
  			currentHealths, userMonsterIdsToHealths);
  	log.info("num updated=" + num);

	  //delete the selected monsters from  the healing table
	  num = DeleteUtils.get().deleteMonsterHealingForUser(
	  		uId, userMonsterIds);
	  log.info("deleted monster healing rows. numDeleted=" + num +
	  		"\t userMonsterIds=" + userMonsterIds);
	  
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
