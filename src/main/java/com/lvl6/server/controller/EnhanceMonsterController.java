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
import com.lvl6.events.request.EnhanceMonsterRequestEvent;
import com.lvl6.events.response.EnhanceMonsterResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.MonsterEnhancingForUser;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventMonsterProto.EnhanceMonsterRequestProto;
import com.lvl6.proto.EventMonsterProto.EnhanceMonsterResponseProto;
import com.lvl6.proto.EventMonsterProto.EnhanceMonsterResponseProto.Builder;
import com.lvl6.proto.EventMonsterProto.EnhanceMonsterResponseProto.EnhanceMonsterStatus;
import com.lvl6.proto.MonsterStuffProto.UserEnhancementItemProto;
import com.lvl6.proto.MonsterStuffProto.UserEnhancementProto;
import com.lvl6.proto.MonsterStuffProto.UserMonsterCurrentExpProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.server.Locker;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component @DependsOn("gameServer") public class EnhanceMonsterController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  @Autowired
  protected Locker locker;

  public EnhanceMonsterController() {
    numAllocatedThreads = 4;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new EnhanceMonsterRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_ENHANCE_MONSTER_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    EnhanceMonsterRequestProto reqProto = ((EnhanceMonsterRequestEvent)event).getEnhanceMonsterRequestProto();

    log.info(
    	String.format("reqProto=%s", reqProto));
    
    //get values sent from the client (the request proto)
    MinimumUserProto senderProto = reqProto.getSender();
    int userId = senderProto.getUserId();
    UserEnhancementProto uep = reqProto.getUep();
    UserMonsterCurrentExpProto result = reqProto.getEnhancingResult();
    int gemsSpent = reqProto.getGemsSpent();
    int oilChange = reqProto.getOilChange();
    
    //user monster ids that will be deleted from monster enhancing for user table
    Timestamp curTime = new Timestamp((new Date()).getTime());

    //set some values to send to the client (the response proto)
    EnhanceMonsterResponseProto.Builder resBuilder = EnhanceMonsterResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setStatus(EnhanceMonsterStatus.FAIL_OTHER); //default

    getLocker().lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    try {
    	int previousGems = 0;

    	//get whatever we need from the database
    	User aUser = RetrieveUtils.userRetrieveUtils().getUserById(userId);
    	Map<Long, MonsterForUser> baseAndFeeders = new HashMap<Long, MonsterForUser>();

    	//do check to make sure one monster has a null start time
    	boolean legit = checkLegit(resBuilder, aUser, userId, uep,
    		result, gemsSpent, oilChange, baseAndFeeders);

    	Map<String, Integer> money = null;
    	boolean successful = false;
    	if(legit) {
    		money = new HashMap<String, Integer>();
    		previousGems = aUser.getGems();
    		successful = writeChangesToDb(aUser, userId, curTime, uep,
    			result, baseAndFeeders, gemsSpent, oilChange, money);
    	}
    	if (successful) {
    		setResponseBuilder(resBuilder);
    	}

    	EnhanceMonsterResponseEvent resEvent = new EnhanceMonsterResponseEvent(userId);
    	resEvent.setTag(event.getTag());
    	resEvent.setEnhanceMonsterResponseProto(resBuilder.build());
    	server.writeEvent(resEvent);

    	if (successful) {
    		//tell the client to update user because user's funds most likely changed
    		//null PvpLeagueFromUser means will pull from hazelcast instead
    		UpdateClientUserResponseEvent resEventUpdate = MiscMethods
    			.createUpdateClientUserResponseEventAndUpdateLeaderboard(aUser, null);
    		resEventUpdate.setTag(event.getTag());
    		server.writeEvent(resEventUpdate);

//    		writeChangesToHistory(userId, inEnhancing, userMonsterIdsThatFinished);
    		writeToUserCurrencyHistory(aUser, curTime, result.getUserMonsterId(), money, previousGems);
    	}
    } catch (Exception e) {
    	log.error("exception in EnhanceMonsterController processEvent", e);
    	//don't let the client hang
    	try {
    		resBuilder.setStatus(EnhanceMonsterStatus.FAIL_OTHER);
    		EnhanceMonsterResponseEvent resEvent = new EnhanceMonsterResponseEvent(userId);
    		resEvent.setTag(event.getTag());
    		resEvent.setEnhanceMonsterResponseProto(resBuilder.build());
    		server.writeEvent(resEvent);
    	} catch (Exception e2) {
    		log.error("exception2 in EnhanceMonsterController processEvent", e);
    	}
    } finally {
    	getLocker().unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    }
  }
  
  /**
   * Return true if user request is valid; false otherwise and set the
   * builder status to the appropriate value.
   */
  private boolean checkLegit(Builder resBuilder, User u, int userId,
  		UserEnhancementProto uep , UserMonsterCurrentExpProto umcep,
  		int gems, int oil, Map<Long, MonsterForUser> userMonsters) {
  	
    if (null == u || null == uep || null == uep.getBaseMonster() ||
    	uep.getFeedersCount() <= 0 || null == umcep) {
      log.error(String.format(
    	  "user or baseMonster or feeders or result is null. user=%s, baseMonster=%s, feeders=%s, result=%s",
      		u, uep.getBaseMonster(), uep.getFeedersList(), umcep));
      return false;
    }
    
    List<Long> userMonsterIds = getUserMonsterIds(uep);
    Map<Long, MonsterForUser> baseAndFeeders = RetrieveUtils
    	.monsterForUserRetrieveUtils().getSpecificUserMonsters(userMonsterIds);
    Set<Long> existingIds = baseAndFeeders.keySet();
    
    if (!existingIds.containsAll(userMonsterIds)) {
    	log.error(String.format(
    		"some monsters don't exist. uep=%s, baseAndFeeders=%s",
    		uep, baseAndFeeders));
    	return false;
    }
    
    //CHECK MONEY and CHECK SPEEDUP
    int userGems = u.getGems();

    if (userGems < gems) {
    	log.error(String.format(
    		"user does not have enough gems. userGems=%s, cost=%s" 
    		, userGems, gems));
    	resBuilder.setStatus(EnhanceMonsterStatus.FAIL_INSUFFICIENT_GEMS);
    	return false;
    }
    
    int userOil = u.getOil();
    if (userOil < oil || userOil < -1*oil) {
    	log.error(String.format(
    		"user does not have enough oil. userOil=%s, oilChange=%s",
    		userOil, oil));
    }
	
    //pass back MonsterForUser back up to caller
    userMonsters.putAll(baseAndFeeders);
    return true;
  }
  

  private List<Long> getUserMonsterIds(UserEnhancementProto uep)
  {
	  List<Long> userMonsterIds = new ArrayList<Long>();
	  UserEnhancementItemProto base = uep.getBaseMonster();
	  userMonsterIds.add(base.getUserMonsterId());
	  
	  for (UserEnhancementItemProto feeder: uep.getFeedersList()) {
		  userMonsterIds.add(feeder.getUserMonsterId());
	  }
	  return userMonsterIds;
  }
  
  private boolean writeChangesToDb(User u, int uId, Timestamp clientTime,
  		UserEnhancementProto uep, UserMonsterCurrentExpProto result,
  		Map<Long, MonsterForUser> baseAndFeeders, int gemsSpent, int oilChange,
  		Map<String, Integer> money) {

	  //CHARGE THE USER
	  int gemCost = -1 * Math.abs(gemsSpent);
	  oilChange = -1 * Math.abs(oilChange);
	  if (1 != u.updateRelativeCashAndOilAndGems(0, oilChange, gemCost)) {
		  log.error(
			  String.format(
				  "problem with updating user gems and oil. gems=%s, oil=%s",
				  gemCost, oilChange));
		  return false;
	  } else {
		  if (0 != gemCost) {
			  money.put(MiscMethods.gems, gemCost);
		  }
		  if (0 != oilChange) {
			  money.put(MiscMethods.oil, oilChange);
		  }
	  }
	  
	  long userMonsterIdBeingEnhanced = result.getUserMonsterId();
	  int newExp = result.getExpectedExperience();
	  int newLvl = result.getExpectedLevel();
	  int newHp = result.getExpectedHp();

	  //GIVE THE MONSTER EXP
	  int num = UpdateUtils.get().updateUserMonsterExpAndLvl(userMonsterIdBeingEnhanced,
		  newExp, newLvl, newHp);
	  log.info(
		  String.format("num updated=%s", num));

	  baseAndFeeders.remove(userMonsterIdBeingEnhanced);
	  num = DeleteUtils.get().deleteMonstersForUser(
		  new ArrayList<Long>(baseAndFeeders.keySet()));
	  log.info(
		  String.format("deleted=%s", num));
	  
	  return true;
  }
  
  private void setResponseBuilder(Builder resBuilder) {
	  resBuilder.setStatus(EnhanceMonsterStatus.SUCCESS);
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
