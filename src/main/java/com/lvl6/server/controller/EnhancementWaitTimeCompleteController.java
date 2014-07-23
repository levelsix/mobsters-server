package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.EnhancementWaitTimeCompleteRequestEvent;
import com.lvl6.events.response.EnhancementWaitTimeCompleteResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.MonsterEnhancingForUser;
import com.lvl6.info.MonsterForUser;
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
import com.lvl6.server.Locker;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component @DependsOn("gameServer") public class EnhancementWaitTimeCompleteController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  @Autowired
  protected Locker locker;

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

    log.info("reqProto=" + reqProto);
    
    //get values sent from the client (the request proto)
    MinimumUserProto senderProto = reqProto.getSender();
    int userId = senderProto.getUserId();
    boolean isSpeedUp = reqProto.getIsSpeedup();
    int gemsForSpeedUp = reqProto.getGemsForSpeedup();
    UserMonsterCurrentExpProto umcep = reqProto.getUmcep();
    //user monster ids that will be deleted from monster enhancing for user table
    List<Long> userMonsterIdsThatFinished = reqProto.getUserMonsterIdsList();
    userMonsterIdsThatFinished = new ArrayList<Long>(userMonsterIdsThatFinished);
    Timestamp curTime = new Timestamp((new Date()).getTime());

    //set some values to send to the client (the response proto)
    EnhancementWaitTimeCompleteResponseProto.Builder resBuilder = EnhancementWaitTimeCompleteResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setStatus(EnhancementWaitTimeCompleteStatus.FAIL_OTHER); //default

    getLocker().lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    try {
      int previousGems = 0;
      List<Long> userMonsterIds = new ArrayList<Long>();
      userMonsterIds.add(umcep.getUserMonsterId()); //monster being enhanced
      userMonsterIds.addAll(userMonsterIdsThatFinished);
      
    	//get whatever we need from the database
    	User aUser = RetrieveUtils.userRetrieveUtils().getUserById(userId);
    	Map<Long, MonsterEnhancingForUser> inEnhancing =
    			MonsterEnhancingForUserRetrieveUtils.getMonstersForUser(userId);
    	Map<Long, MonsterForUser> idsToUserMonsters = RetrieveUtils
    			.monsterForUserRetrieveUtils()
    			.getSpecificOrAllUserMonstersForUser(userId, userMonsterIds);
    	
    	//do check to make sure one monster has a null start time
      boolean legit = checkLegit(resBuilder, aUser, userId, idsToUserMonsters,
      		inEnhancing, umcep, userMonsterIdsThatFinished, isSpeedUp, gemsForSpeedUp);

      Map<String, Integer> money = new HashMap<String, Integer>();
      boolean successful = false;
      if(legit) {
        previousGems = aUser.getGems();
    	  successful = writeChangesToDb(aUser, userId, curTime, inEnhancing,
    	  		umcep, userMonsterIdsThatFinished, isSpeedUp, gemsForSpeedUp, money);
      }
      if (successful) {
    	  setResponseBuilder(resBuilder);
      }
      
      EnhancementWaitTimeCompleteResponseEvent resEvent = new EnhancementWaitTimeCompleteResponseEvent(userId);
      resEvent.setTag(event.getTag());
      resEvent.setEnhancementWaitTimeCompleteResponseProto(resBuilder.build());
      server.writeEvent(resEvent);

      if (successful) {
      	//tell the client to update user because user's funds most likely changed
    	  //null PvpLeagueFromUser means will pull from hazelcast instead
      	UpdateClientUserResponseEvent resEventUpdate = MiscMethods
      			.createUpdateClientUserResponseEventAndUpdateLeaderboard(aUser, null);
      	resEventUpdate.setTag(event.getTag());
      	server.writeEvent(resEventUpdate);
      	
      	writeChangesToHistory(userId, inEnhancing, userMonsterIdsThatFinished);
      	writeToUserCurrencyHistory(aUser, curTime, umcep.getUserMonsterId(), money, previousGems);
      }
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
      getLocker().unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
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
   * @param idsToUserMonsters - the monsters the user has
   * @param inEnhancing - the monsters that are in the enhancing queue
   * @param umcep - the base monster that is updated from using up some of the feeders
   * @param usedUpUserMonsterIds - userMonsterIds the user thinks has finished being enhanced
   * @param speedUUp
   * @param gemsForSpeedUp
   * @return
   */
  private boolean checkLegit(Builder resBuilder, User u, int userId,
  		Map<Long, MonsterForUser> idsToUserMonsters,
  		Map<Long, MonsterEnhancingForUser> inEnhancing, UserMonsterCurrentExpProto umcep,
  		List<Long> usedUpMonsterIds, boolean speedup, int gemsForSpeedup) {
  	
    if (null == u || null == umcep || usedUpMonsterIds.isEmpty()) {
      log.error("unexpected error: user or idList is null. user=" + u +
      		"\t umcep="+ umcep + "usedUpMonsterIds=" + usedUpMonsterIds +
      		"\t speedup=" + speedup + "\t gemsForSpeedup=" + gemsForSpeedup);
      return false;
    }
    log.info("inEnhancing=" + inEnhancing);
    long userMonsterIdBeingEnhanced = umcep.getUserMonsterId();
    
    //make sure that the user monster ids that will be deleted will only be
    //the ids that exist in enhancing table
    Set<Long> inEnhancingIds = inEnhancing.keySet();
    MonsterStuffUtils.retainValidMonsterIds(inEnhancingIds, usedUpMonsterIds);
    
    //check to make sure the base monsterId is in enhancing
    if (!inEnhancingIds.contains(userMonsterIdBeingEnhanced)) {
    	log.error("client did not send updated base monster specifying what new exp and lvl are");
    	return false;
    }
    
    /* NOT SURE IF THESE ARE  NECESSARY, SO DOING IT ANYWAY*/
    //check to make sure the monster being enhanced is part of the
    //user's monsters
    if (!idsToUserMonsters.containsKey(userMonsterIdBeingEnhanced)) {
    	log.error("monster being enhanced doesn't exist!. userMonsterIdBeingEnhanced=" + 
    			userMonsterIdBeingEnhanced + "\t deleteIds=" + usedUpMonsterIds +
    			"\t inEnhancing=" + inEnhancing + "\t gemsForSpeedup=" + gemsForSpeedup +
    			"\t speedup=" + speedup);
    	return false;
    }
    
    //retain only the valid monster for user ids that will be deleted
    Set<Long> existingIds = idsToUserMonsters.keySet();
    MonsterStuffUtils.retainValidMonsterIds(existingIds, usedUpMonsterIds);
    
    
    //CHECK MONEY and CHECK SPEEDUP
    if (speedup) {
    	int userGems = u.getGems();
    	
    	if (userGems < gemsForSpeedup) {
    		log.error("user does not have enough gems to speed up enhancing.userGems=" +
    				userGems + "\t cost=" + gemsForSpeedup + "\t umcep=" + umcep +
    				"\t inEnhancing=" + inEnhancing + "\t deleteIds=" + usedUpMonsterIds);
    		resBuilder.setStatus(EnhancementWaitTimeCompleteStatus.FAIL_INSUFFICIENT_FUNDS);
    		return false;
    	}
    }
    	
    resBuilder.setStatus(EnhancementWaitTimeCompleteStatus.SUCCESS);
    return true;
  }
  
  private boolean writeChangesToDb(User u, int uId, Timestamp clientTime,
  		Map<Long, MonsterEnhancingForUser> inEnhancing, UserMonsterCurrentExpProto umcep,
  		List<Long> userMonsterIds, boolean isSpeedup, int gemsForSpeedup,
  		Map<String, Integer> money) {


  	if (isSpeedup) {
  		//CHARGE THE USER
  		int gemCost = -1 * gemsForSpeedup;
  		if (!u.updateRelativeGemsNaive(gemCost)) {
  			log.error("problem with updating user gems. gemsForSpeedup=" + gemsForSpeedup +
  					", clientTime=" + clientTime + ", baseMonster" + umcep +  ", clientTime=" +
  					clientTime + ", userMonsterIdsToDelete=" + userMonsterIds + ", user=" + u);
  			return false;
  		} else {
  			if (0 != gemCost) {
  				money.put(MiscMethods.gems, gemCost);
  			}
  		}
  	}
  	
  	long userMonsterIdBeingEnhanced = umcep.getUserMonsterId();
  	int newExp = umcep.getExpectedExperience();
  	int newLvl = umcep.getExpectedLevel();
  	int newHp = umcep.getExpectedHp();
  	
  	//GIVE THE MONSTER EXP
  	int num = UpdateUtils.get().updateUserMonsterExpAndLvl(userMonsterIdBeingEnhanced,
  			newExp, newLvl, newHp);
  	log.info("num updated=" + num);

	  return true;
  }
  
  private void setResponseBuilder(Builder resBuilder) {
  }
  
  private void writeChangesToHistory(int uId,
  		Map<Long, MonsterEnhancingForUser> inEnhancing,
  		List<Long> userMonsterIds) {
  	
  	//TODO: keep track of the userMonsters that are deleted
  	
  	
  	//TODO: keep track of the monsters that were enhancing
  	
  	//delete the selected monsters from  the enhancing table
	  int num = DeleteUtils.get().deleteMonsterEnhancingForUser(
	  		uId, userMonsterIds);
	  log.info("deleted monster healing rows. numDeleted=" + num +
	  		"\t userMonsterIds=" + userMonsterIds + "\t inEnhancing=" + inEnhancing);

	  
	  //delete the userMonsterIds from the monster_for_user table, but don't delete
	  //the monster user is enhancing
	  num = DeleteUtils.get().deleteMonstersForUser(userMonsterIds);
	  log.info("defeated monster_for_user rows. numDeleted=" + num + "\t inEnhancing=" +
	  		inEnhancing);
	  
  }
  
  private void writeToUserCurrencyHistory(User aUser, Timestamp curTime,
		  long userMonsterId, Map<String, Integer> money, int previousGems) {
	  if (money.isEmpty()) {
		  return;
	  }
	  String gems = MiscMethods.gems;
	  String reasonForChange = ControllerConstants.UCHRFC__SPED_UP_ENHANCING;
	  
	  int userId = aUser.getId();
	  Map<String, Integer> previousCurrencies = new HashMap<String, Integer>();
	  Map<String, Integer> currentCurrencies = new HashMap<String, Integer>();
	  Map<String, String> reasonsForChanges = new HashMap<String, String>();
	  Map<String, String> detailsMap = new HashMap<String, String>();

	  previousCurrencies.put(gems, previousGems);
	  currentCurrencies.put(gems, aUser.getGems());
	  reasonsForChanges.put(gems, reasonForChange);
	  detailsMap.put(gems, " userMonsterId=" + userMonsterId); 
	  MiscMethods.writeToUserCurrencyOneUser(userId, curTime, money,
			  previousCurrencies, currentCurrencies, reasonsForChanges,
			  detailsMap);

  }

  public Locker getLocker() {
	  return locker;
  }

  public void setLocker(Locker locker) {
	  this.locker = locker;
  }

}
