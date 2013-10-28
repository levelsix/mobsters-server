package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.HealMonsterRequestEvent;
import com.lvl6.events.response.HealMonsterResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.MonsterEnhancingForUser;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.MonsterHealingForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventMonsterProto.HealMonsterRequestProto;
import com.lvl6.proto.EventMonsterProto.HealMonsterResponseProto;
import com.lvl6.proto.EventMonsterProto.HealMonsterResponseProto.Builder;
import com.lvl6.proto.EventMonsterProto.HealMonsterResponseProto.HealMonsterStatus;
import com.lvl6.proto.MonsterStuffProto.UserMonsterHealingProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.MonsterEnhancingForUserRetrieveUtils;
import com.lvl6.retrieveutils.MonsterHealingForUserRetrieveUtils;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component @DependsOn("gameServer") public class HealMonsterController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());


  public HealMonsterController() {
    numAllocatedThreads = 4;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new HealMonsterRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_HEAL_MONSTER_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    HealMonsterRequestProto reqProto = ((HealMonsterRequestEvent)event).getHealMonsterRequestProto();

    //get values sent from the client (the request proto)
    MinimumUserProto senderProto = reqProto.getSender();
    int userId = senderProto.getUserId();
    List<UserMonsterHealingProto> umhDelete = reqProto.getUmhDeleteList();
    List<UserMonsterHealingProto> umhUpdate = reqProto.getUmhUpdateList();
    List<UserMonsterHealingProto> umhNew = reqProto.getUmhNewList();
    int cashCost = reqProto.getCashCost();
    int gemCost = reqProto.getGemCost();

    Map<Long, UserMonsterHealingProto> deleteMap = MonsterStuffUtils.convertIntoUserMonsterIdToUmhpProtoMap(umhDelete);
    Map<Long, UserMonsterHealingProto> updateMap = MonsterStuffUtils.convertIntoUserMonsterIdToUmhpProtoMap(umhUpdate);
    Map<Long, UserMonsterHealingProto> newMap = MonsterStuffUtils.convertIntoUserMonsterIdToUmhpProtoMap(umhNew); 
    //set some values to send to the client (the response proto)
    HealMonsterResponseProto.Builder resBuilder = HealMonsterResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setStatus(HealMonsterStatus.FAIL_OTHER); //default

    server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    try {
      //int previousSilver = 0;
      //int previousGold = 0;
    	//get whatever we need from the database
    	User aUser = RetrieveUtils.userRetrieveUtils().getUserById(userId);
    	Map<Long, MonsterHealingForUser> alreadyHealing =
    			MonsterHealingForUserRetrieveUtils.getMonstersForUser(userId);
    	Map<Long, MonsterEnhancingForUser> alreadyEnhancing =
					MonsterEnhancingForUserRetrieveUtils.getMonstersForUser(userId);
    	
    	
    	//retrieve only the new monsters that will be healed
    	Set<Long> newIds = new HashSet<Long>();
    	newIds.addAll(newMap.keySet());
    	Map<Long, MonsterForUser> existingUserMonsters = RetrieveUtils
    			.monsterForUserRetrieveUtils().getSpecificUserMonstersForUser(userId, newIds);
    	
      boolean legit = checkLegit(resBuilder, aUser, userId,
      		cashCost, gemCost, existingUserMonsters, alreadyHealing,
      		alreadyEnhancing, deleteMap, updateMap, newMap);

      boolean successful = false;
      if(legit) {
    	  
//        previousSilver = aUser.getCoins();
//        previousGold = aUser.getDiamonds();
    	  successful = writeChangesToDb(aUser, userId, cashCost,
    	  		gemCost, deleteMap, updateMap, newMap);
//        writeToUserCurrencyHistory(aUser, money, curTime, previousSilver, previousGold);
      }
      if (successful) {
//    	  setResponseBuilder(resBuilder, userId);
      }
      
      HealMonsterResponseEvent resEvent = new HealMonsterResponseEvent(userId);
      resEvent.setTag(event.getTag());
      resEvent.setHealMonsterResponseProto(resBuilder.build());
      server.writeEvent(resEvent);

      UpdateClientUserResponseEvent resEventUpdate = MiscMethods
          .createUpdateClientUserResponseEventAndUpdateLeaderboard(aUser);
      resEventUpdate.setTag(event.getTag());
      server.writeEvent(resEventUpdate);
    } catch (Exception e) {
      log.error("exception in HealMonsterController processEvent", e);
      //don't let the client hang
      try {
    	  resBuilder.setStatus(HealMonsterStatus.FAIL_OTHER);
    	  HealMonsterResponseEvent resEvent = new HealMonsterResponseEvent(userId);
    	  resEvent.setTag(event.getTag());
    	  resEvent.setHealMonsterResponseProto(resBuilder.build());
    	  server.writeEvent(resEvent);
      } catch (Exception e2) {
    	  log.error("exception2 in HealMonsterController processEvent", e);
      }
    } finally {
      server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    }
  }
  
  /*
   * Return true if user request is valid; false otherwise and set the
   * builder status to the appropriate value. delete, update, new maps
   * MIGHT BE MODIFIED.
   * 
   * For the most part, will always return success. Why?
   * (Will return fail if user does not have enough funds.) 
   * Answer: For the map
   * 
   * delete - The monsters to be removed from healing will only be the ones
   * the user already has in healing.
   * update - Same logic as above.
   * new - Same as above.
   * 
   * Ex. If user wants to delete a monster (A) that isn't healing, along with some
   * monsters already healing (B), only the valid monsters (B) will be deleted.
   * Same logic with update and new. 
   * 
   */
  private boolean checkLegit(Builder resBuilder, User u, int userId,
  		int cashCost, int gemCost,
  		Map<Long, MonsterForUser> existingUserMonsters,
  		Map<Long, MonsterHealingForUser> alreadyHealing,
  		Map<Long, MonsterEnhancingForUser> alreadyEnhancing,
  		Map<Long, UserMonsterHealingProto> deleteMap,
  		Map<Long, UserMonsterHealingProto> updateMap,
  		Map<Long, UserMonsterHealingProto> newMap) {
    if (null == u ) {
      log.error("unexpected error: user is null. user=" + u + "\t deleteMap="+ deleteMap +
      		"\t updateMap=" + updateMap + "\t newMap=" + newMap);
      return false;
    }
    
    //CHECK MONEY
    int userGems = u.getDiamonds();
    if (gemCost > userGems) {
    	log.error("user error: user does not have enough gems. userGems="
    			+ userGems + "\t gemCost=" + gemCost + "\t user=" + u);
    	resBuilder.setStatus(HealMonsterStatus.FAIL_INSUFFICIENT_FUNDS);
    	return false;
    }
    
    // scenario can be user has insufficient cash/coin but has enough
    // gems/gold to cover the difference
    int userCash = u.getCoins();
    if (cashCost > userCash && gemCost == 0) {
    	//user doesn't have enough cash and is not paying gems.
    	
    	log.error("user error: user does not have enough cash. userCash="
    			+ userCash + "\t cashCost=" + cashCost + "\t user=" + u);
    	resBuilder.setStatus(HealMonsterStatus.FAIL_INSUFFICIENT_FUNDS);
    	return false;
    }
    
    //retain only the userMonsters, the client sent, that are in healing
    boolean keepThingsInDomain = true;
    boolean keepThingsNotInDomain = false;
    Set<Long> alreadyHealingIds = alreadyHealing.keySet();
    MonsterStuffUtils.retainValidMonsters(alreadyHealingIds, deleteMap, keepThingsInDomain, keepThingsNotInDomain);
    MonsterStuffUtils.retainValidMonsters(alreadyHealingIds, updateMap, keepThingsInDomain, keepThingsNotInDomain);
    
    //retain only the userMonsters, the client sent, that are in the db
    Set<Long> existingIds = existingUserMonsters.keySet();
    MonsterStuffUtils.retainValidMonsters(existingIds, newMap, keepThingsInDomain, keepThingsNotInDomain);
    
    //retain only the userMonsters, the client sent, that are not in enhancing
    keepThingsInDomain = false;
    keepThingsNotInDomain = true;
    Set<Long> alreadyEnhancingIds = alreadyEnhancing.keySet();
    MonsterStuffUtils.retainValidMonsters(alreadyEnhancingIds, newMap, keepThingsInDomain, keepThingsNotInDomain);
    
    	
    resBuilder.setStatus(HealMonsterStatus.SUCCESS);
    return true;
  }
  
  private boolean writeChangesToDb(User u, int uId, 
  		int cashCost, int gemCost,
		  Map<Long, UserMonsterHealingProto> protoDeleteMap,
		  Map<Long, UserMonsterHealingProto> protoUpdateMap,
		  Map<Long, UserMonsterHealingProto> protoNewMap) {


  	//CHARGE THE USER
  	log.info("user before funds change. u=" + u);
  	int num = u.updateRelativeCoinsAndDiamonds(cashCost, gemCost);
  	log.info("user after funds change. u=" + u);
	  if (num != 1) {
		  log.error("problem with updating user's funds. cashCost="
				  + cashCost + ", gemCost=" + gemCost + ", user=" + u);
		  return false;
	  }
  	
	  //delete the selected monsters from  the healing table, if there are
  	//any to delete
  	if (!protoDeleteMap.isEmpty()) {
  		List<Long> deleteIds = new ArrayList<Long>(protoDeleteMap.keySet());
  		num = DeleteUtils.get().deleteMonsterHealingForUser(
  				uId, deleteIds);
  		log.info("deleted monster healing rows. numDeleted=" + num +
  				"\t protoDeleteMap=" + protoDeleteMap);
  	}
	  
	  //convert protos to java counterparts
	  List<MonsterHealingForUser> updateMap = MonsterStuffUtils.convertToMonsterHealingForUser(
	  		uId, protoUpdateMap);
	  List<MonsterHealingForUser> newMap = MonsterStuffUtils.convertToMonsterHealingForUser(
	  		uId, protoNewMap);
	  
	  List<MonsterHealingForUser> updateAndNew = new ArrayList<MonsterHealingForUser>();
	  updateAndNew.addAll(updateMap);
	  updateAndNew.addAll(newMap);
	  
	  //client could have deleted one item from two item queue, or added at least one item
	  if (!updateAndNew.isEmpty()) {
	  	//update and insert the new monsters
	  	num = UpdateUtils.get().updateUserMonsterHealing(uId, updateAndNew);
	  	log.info("updated monster healing rows. numUpdated/inserted=" + num);
	  }
	  
	  //for the new monsters, ensure that the monsters are "unequipped"
	  if (!protoNewMap.isEmpty()) {
	  	//for the new monsters, set the teamSlotNum to 0
	  	int size = protoNewMap.size();
	  	List<Long> userMonsterIdList = new ArrayList<Long>(protoNewMap.keySet());
	  	List<Integer> teamSlotNumList = Collections.nCopies(size, 0);
	  	num = UpdateUtils.get().updateNullifyUserMonstersTeamSlotNum(userMonsterIdList, teamSlotNumList);
	  	log.info("updated user monster rows. numUpdated=" + num);
	  }
	  
	  return true;
  }
  
  private void setResponseBuilder(Builder resBuilder, int userId) {
//  	Map<Long, MonsterHealingForUser> alreadyHealing =
//  			MonsterHealingForUserRetrieveUtils.getMonstersForUser(userId);
//  	
//  	for(MonsterHealingForUser mhfu : alreadyHealing.values()) {
//  		UserMonsterHealingProto umhp =
//  				CreateInfoProtoUtils.createUserMonsterHealingProtoFromObj(mhfu);
//  		resBuilder.addUmhp(umhp);
//  	}
//  	
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
