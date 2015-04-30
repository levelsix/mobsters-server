//package com.lvl6.server.controller;
//
//import java.sql.Timestamp;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.DependsOn;
//import org.springframework.stereotype.Component;
//
//import com.lvl6.events.RequestEvent;
//import com.lvl6.events.request.HealMonsterWaitTimeCompleteRequestEvent;
//import com.lvl6.events.response.HealMonsterWaitTimeCompleteResponseEvent;
//import com.lvl6.events.response.UpdateClientUserResponseEvent;
//import com.lvl6.info.MonsterHealingForUser;
//import com.lvl6.info.User;
//import com.lvl6.misc.MiscMethods;
//import com.lvl6.proto.EventMonsterProto.HealMonsterWaitTimeCompleteRequestProto;
//import com.lvl6.proto.EventMonsterProto.HealMonsterWaitTimeCompleteResponseProto;
//import com.lvl6.proto.EventMonsterProto.HealMonsterWaitTimeCompleteResponseProto.Builder;
//import com.lvl6.proto.EventMonsterProto.HealMonsterWaitTimeCompleteResponseProto.HealMonsterWaitTimeCompleteStatus;
//import com.lvl6.proto.MonsterStuffProto.UserMonsterCurrentHealthProto;
//import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
//import com.lvl6.proto.UserProto.MinimumUserProto;
//import com.lvl6.retrieveutils.MonsterHealingForUserRetrieveUtils;
//import com.lvl6.server.controller.utils.MonsterStuffUtils;
//import com.lvl6.utils.RetrieveUtils;
//import com.lvl6.utils.utilmethods.DeleteUtils;
//import com.lvl6.utils.utilmethods.UpdateUtils;
//
//@Component  public class HealMonsterWaitTimeCompleteController extends EventController {
//
//  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
//
//
//  public HealMonsterWaitTimeCompleteController() {
//    
//  }
//
//  @Override
//  public RequestEvent createRequestEvent() {
//    return new HealMonsterWaitTimeCompleteRequestEvent();
//  }
//
//  @Override
//  public EventProtocolRequest getEventType() {
//    return EventProtocolRequest.C_HEAL_MONSTER_WAIT_TIME_COMPLETE_EVENT;
//  }
//
//  @Override
//  public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
//    HealMonsterWaitTimeCompleteRequestProto reqProto = ((HealMonsterWaitTimeCompleteRequestEvent)event).getHealMonsterWaitTimeCompleteRequestProto();
//
//    //get values sent from the client (the request proto)
//    MinimumUserProto senderProto = reqProto.getSender();
//    int userId = senderProto.getUserUuid();
//    boolean isSpeedUp = reqProto.getIsSpeedup();
//    List<UserMonsterCurrentHealthProto> umchpList = reqProto.getUmchpList();
//    Map<Long, Integer> userMonsterIdToExpectedHealth = new HashMap<Long, Integer>();
//    List<Long> userMonsterIds = MonsterStuffUtils.getUserMonsterIds(umchpList, userMonsterIdToExpectedHealth);
//    int gemsForSpeedUp = reqProto.getGemsForSpeedup();
//    Timestamp curTime = new Timestamp((new Date()).getTime());
//    
//    log.info("umchpList=" + umchpList + "\t userMonsterIdToExpectedHealth" +
//    		userMonsterIdToExpectedHealth + "\t userMonsterIds=" + userMonsterIds);
//
//    //set some values to send to the client (the response proto)
//    HealMonsterWaitTimeCompleteResponseProto.Builder resBuilder = HealMonsterWaitTimeCompleteResponseProto.newBuilder();
//    resBuilder.setSender(senderProto);
//    resBuilder.setStatus(HealMonsterWaitTimeCompleteStatus.FAIL_OTHER); //default
//
//    locker.lockPlayer(UUID.fromString(senderProto.getUserUuid()), this.getClass().getSimpleName());
//    try {
//      int previousGems = 0;
//    	//get whatever we need from the database
//    	User aUser = RetrieveUtils.userRetrieveUtils().getUserById(userId);
//    	Map<Long, MonsterHealingForUser> alreadyHealing =
//    			MonsterHealingForUserRetrieveUtils.getMonstersForUser(userId);
//    	
//      boolean legit = checkLegit(resBuilder, aUser, userId, alreadyHealing,
//      		userMonsterIds, isSpeedUp, gemsForSpeedUp);
//
//      boolean successful = false;
//      Map<String, Integer> money = new HashMap<String, Integer>();
//      if(legit) {
//        previousGems = aUser.getGems();
//        userMonsterIdToExpectedHealth = getValidEntries(userMonsterIds, userMonsterIdToExpectedHealth);
//    	  successful = writeChangesToDb(aUser, userId, userMonsterIds,
//    	  		userMonsterIdToExpectedHealth, isSpeedUp, gemsForSpeedUp);
//      }
//      
//      HealMonsterWaitTimeCompleteResponseEvent resEvent = new HealMonsterWaitTimeCompleteResponseEvent(userId);
//      resEvent.setTag(event.getTag());
//      resEvent.setHealMonsterWaitTimeCompleteResponseProto(resBuilder.build());
//      responses.normalResponseEvents().add(resEvent);
//
//      if (successful) {
//      	//since user's money most likely changed, tell client to update user
//      	UpdateClientUserResponseEvent resEventUpdate = MiscMethods
//      			.createUpdateClientUserResponseEventAndUpdateLeaderboard(aUser);
//      	resEventUpdate.setTag(event.getTag());
//      	responses.normalResponseEvents().add(resEventUpdate);
//      	
//      	//TODO: WRITE TO HISTORY
//      	writeToUserCurrencyHistory(aUser, money, curTime, previousGems);
//      }
//    } catch (Exception e) {
//      log.error("exception in HealMonsterWaitTimeCompleteController processEvent", e);
//      //don't let the client hang
//      try {
//    	  resBuilder.setStatus(HealMonsterWaitTimeCompleteStatus.FAIL_OTHER);
//    	  HealMonsterWaitTimeCompleteResponseEvent resEvent = new HealMonsterWaitTimeCompleteResponseEvent(userId);
//    	  resEvent.setTag(event.getTag());
//    	  resEvent.setHealMonsterWaitTimeCompleteResponseProto(resBuilder.build());
//    	  responses.normalResponseEvents().add(resEvent);
//      } catch (Exception e2) {
//    	  log.error("exception2 in HealMonsterWaitTimeCompleteController processEvent", e);
//      }
//    } finally {
//      locker.unlockPlayer(UUID.fromString(senderProto.getUserUuid()), this.getClass().getSimpleName());
//    }
//  }
//  
//  /**
//   * Return true if user request is valid; false otherwise and set the
//   * builder status to the appropriate value. @healedUp
//   * MIGHT BE MODIFIED.
//   * 
//   * Will return fail if user does not have enough funds. 
//   * For the most part, will always return success. Why?
//   * Answer: For @healedUp, the monsters the client thinks completed healing,
//   * only existing/valid ids will be taken off the healing queue.
//   * 
//   * Ex. Queue is (a,b,c,d). If user says monster (a,b,e) finished healing, 
//   * only the valid monsters (a,b) will be removed from the queue.
//   * 
//   * @param resBuilder
//   * @param u
//   * @param userId
//   * @param alreadyHealing - the monsters that are in the healing queue
//   * @param healedUp - userMonsterIds the user thinks has finished healing
//   * @param speedUUp
//   * @param gemsForSpeedUp
//   * @return
//   */
//  private boolean checkLegit(Builder resBuilder, User u, int userId,
//  		Map<Long, MonsterHealingForUser> alreadyHealing, List<Long> healedUp,
//  		boolean speedup, int gemsForSpeedup) {
//    if (null == u || null == healedUp || healedUp.isEmpty()) {
//      log.error("unexpected error: user or idList is null. user=" + u +
//      		"\t healedUp="+ healedUp + "\t speedUp=" + speedup);
//      return false;
//    }
//    log.info("alreadyHealing=" + alreadyHealing);
//    
//    //modify healedUp to contain only those that exist
//    Set<Long> alreadyHealingIds = alreadyHealing.keySet();
//    MonsterStuffUtils.retainValidMonsterIds(alreadyHealingIds, healedUp);
//    
//    //CHECK MONEY and CHECK SPEEDUP
//    if (speedup) {
//    	int userGems = u.getGems();
//    	if (userGems < gemsForSpeedup) {
//    		log.error("user error: user sped up healing but has too little gems userGems=" +
//    				userGems + "\t gemsForSpeedup=" + gemsForSpeedup);
//    		resBuilder.setStatus(HealMonsterWaitTimeCompleteStatus.FAIL_INSUFFICIENT_FUNDS);
//    		return false;
//    	}
//    }
//    //TODO:update monster's healths
//    	
//    resBuilder.setStatus(HealMonsterWaitTimeCompleteStatus.SUCCESS);
//    return true;
//  }
//  
//  //only the entries in the map that have their key in validIds will be kept  
//  private Map<Long, Integer> getValidEntries(List<Long> validIds, 
//  		Map<Long, Integer> idsToValues) {
//  	
//  	Map<Long, Integer> returnMap = new HashMap<Long, Integer>();
//  	 
//  	for(long id : validIds) {
//  		int value = idsToValues.get(id);
//  		returnMap.put(id, value);
//  	}
//  	return returnMap;
//  }
//  
//  private boolean writeChangesToDb(User u, int uId, List<Long> userMonsterIds,
//  		Map<Long, Integer> userMonsterIdsToHealths, boolean isSpeedup, int gemsForSpeedup) {
//
//  	if (isSpeedup) {
//  		
//  	//CHARGE THE USER
//  		if (!u.updateRelativeDiamondsNaive(gemsForSpeedup)) {
//  			log.error("could not update user gems. gemsForSpeedup=" + gemsForSpeedup +
//  					"user=" + u + "\t userMonsterIdsToHealths=" + userMonsterIdsToHealths);
//  			return false;
//  		}
//  	}
//  	
//  	//HEAL THE MONSTER
//  	int num = UpdateUtils.get().updateUserMonstersHealth(userMonsterIdsToHealths);
//  	log.info("num updated=" + num);
//
//  	//should always execute, but who knows...
//  	if (null != userMonsterIds && !userMonsterIds.isEmpty()) {
//  		//delete the selected monsters from  the healing table
//  		num = DeleteUtils.get().deleteMonsterHealingForUser(
//  				uId, userMonsterIds);
//  		log.info("deleted monster healing rows. numDeleted=" + num +
//  				"\t userMonsterIds=" + userMonsterIds);
//  	}
//	  return true;
//  }
//  
//  //TODO: FIX THIS
//  private void writeToUserCurrencyHistory(User aUser, Map<String, Integer> money,
//  		Timestamp curTime, int previousGems) {
////  	if (null == money || money.isEmpty()) {
////  		return;
////  	}
////    Map<String, Integer> previousGemsCash = new HashMap<String, Integer>();
////    Map<String, String> reasonsForChanges = new HashMap<String, String>();
////    String reasonForChange = ControllerConstants.UCHRFC__BOSS_ACTION;
////    String gems = MiscMethods.gems;
////
////    previousGemsCash.put(gems, previousGems);
////    reasonsForChanges.put(gems, reasonForChange);
////
////    MiscMethods.writeToUserCurrencyOneUserGemsAndOrCash(aUser, curTime, money, 
////        previousGemsCash, reasonsForChanges);
//
//  }
//}
