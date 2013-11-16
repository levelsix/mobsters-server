package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import com.lvl6.events.request.SubmitMonsterEnhancementRequestEvent;
import com.lvl6.events.response.SubmitMonsterEnhancementResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.MonsterEnhancingForUser;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.MonsterHealingForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventMonsterProto.SubmitMonsterEnhancementRequestProto;
import com.lvl6.proto.EventMonsterProto.SubmitMonsterEnhancementResponseProto;
import com.lvl6.proto.EventMonsterProto.SubmitMonsterEnhancementResponseProto.Builder;
import com.lvl6.proto.EventMonsterProto.SubmitMonsterEnhancementResponseProto.SubmitMonsterEnhancementStatus;
import com.lvl6.proto.MonsterStuffProto.UserEnhancementItemProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.MonsterEnhancingForUserRetrieveUtils;
import com.lvl6.retrieveutils.MonsterHealingForUserRetrieveUtils;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component @DependsOn("gameServer") public class SubmitMonsterEnhancementController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	public SubmitMonsterEnhancementController() {
		numAllocatedThreads = 3;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new SubmitMonsterEnhancementRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_SUBMIT_MONSTER_ENHANCEMENT_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		SubmitMonsterEnhancementRequestProto reqProto = ((SubmitMonsterEnhancementRequestEvent)event)
				.getSubmitMonsterEnhancementRequestProto();

		//get data client sent
		MinimumUserProto senderProto = reqProto.getSender();
		List<UserEnhancementItemProto> ueipDelete = reqProto.getUeipDeleteList();
		List<UserEnhancementItemProto> ueipUpdated = reqProto.getUeipUpdateList();
		List<UserEnhancementItemProto> ueipNew = reqProto.getUeipNewList();
		int userId = senderProto.getUserId();
		int gemsSpent = reqProto.getGemsSpent();
		//positive means refund, negative means charge user
		int cashChange = reqProto.getCashChange();
		Timestamp clientTime = new Timestamp((new Date()).getTime());

		Map<Long, UserEnhancementItemProto> deleteMap = MonsterStuffUtils.
				convertIntoUserMonsterIdToUeipProtoMap(ueipDelete);
		Map<Long, UserEnhancementItemProto> updateMap = MonsterStuffUtils.
				convertIntoUserMonsterIdToUeipProtoMap(ueipUpdated);
		Map<Long, UserEnhancementItemProto> newMap = MonsterStuffUtils.
				convertIntoUserMonsterIdToUeipProtoMap(ueipNew);
		
		
		//set some values to send to the client (the response proto)
		SubmitMonsterEnhancementResponseProto.Builder resBuilder = SubmitMonsterEnhancementResponseProto.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(SubmitMonsterEnhancementStatus.FAIL_OTHER);

		server.lockPlayer(senderProto.getUserId(), getClass().getSimpleName());
		try {
			int previousCash = 0;
			int previousGems = 0;
			//get whatever we need from the database
			User aUser = RetrieveUtils.userRetrieveUtils().getUserById(userId);
			Map<Long, MonsterEnhancingForUser> alreadyEnhancing =
						MonsterEnhancingForUserRetrieveUtils.getMonstersForUser(userId);
			Map<Long, MonsterHealingForUser> alreadyHealing =
    			MonsterHealingForUserRetrieveUtils.getMonstersForUser(userId);
    	
			//retrieve only the new monsters that will be used in enhancing
			Set<Long> newIds = new HashSet<Long>();
			newIds.addAll(newMap.keySet());
    	Map<Long, MonsterForUser> existingUserMonsters = RetrieveUtils
    			.monsterForUserRetrieveUtils().getSpecificOrAllUserMonstersForUser(userId, newIds);

			boolean legitMonster = checkLegit(resBuilder, aUser, userId, existingUserMonsters, 
					alreadyEnhancing, alreadyHealing, deleteMap, updateMap, newMap,
					gemsSpent, cashChange);

			boolean successful = false;
			Map<String, Integer> money = new HashMap<String, Integer>();
			
			if (legitMonster) {
				previousCash = aUser.getCash();
				previousGems = aUser.getGems();
				successful = writeChangesToDB(aUser, userId, gemsSpent, cashChange,
						deleteMap, updateMap, newMap, money);
			}
		
			if (successful) {
				resBuilder.setStatus(SubmitMonsterEnhancementStatus.SUCCESS);
			}

			SubmitMonsterEnhancementResponseEvent resEvent = new SubmitMonsterEnhancementResponseEvent(senderProto.getUserId());
			resEvent.setTag(event.getTag());
			resEvent.setSubmitMonsterEnhancementResponseProto(resBuilder.build());  
			server.writeEvent(resEvent);

			if (successful) {
				UpdateClientUserResponseEvent resEventUpdate = MiscMethods.createUpdateClientUserResponseEventAndUpdateLeaderboard(aUser);
				resEventUpdate.setTag(event.getTag());
				server.writeEvent(resEventUpdate);

				writeToUserCurrencyHistory(aUser, clientTime, money, previousCash, previousGems,
						deleteMap, updateMap, newMap);
			}

		} catch (Exception e) {
			log.error("exception in EnhanceMonster processEvent", e);
		} finally {
			server.unlockPlayer(senderProto.getUserId(), getClass().getSimpleName());   
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
  * delete - The monsters to be removed from enhancing will only be the ones
  * the user already has in enhancing.
  * update - Same logic as above.
  * new - Same as above.
  * 
  * Ex. If user wants to delete a monster (A) that isn't enhancing, along with some
  * monsters already enhancing (B), only the valid monsters (B) will be deleted.
  * Same logic with update and new. 
  * 
  */
	private boolean checkLegit(Builder resBuilder, User u, int userId,
			Map<Long, MonsterForUser> existingUserMonsters,
			Map<Long, MonsterEnhancingForUser> alreadyEnhancing,
			Map<Long, MonsterHealingForUser> alreadyHealing,
			Map<Long, UserEnhancementItemProto> deleteMap,
			Map<Long, UserEnhancementItemProto> updateMap,
			Map<Long, UserEnhancementItemProto> newMap, int gemsSpent, int cashChange) {
		if (null == u ) {
			log.error("unexpected error: user is null. user=" + u + "\t deleteMap="+ deleteMap +
					"\t updateMap=" + updateMap + "\t newMap=" + newMap);
			return false;
		}
		//NOTE: RETAIN CASES ONLY FILTER THINGS, AND NOT CAUSE THIS REQUEST TO FAIL
		//retain only the userMonsters in deleteMap and updateMap that are in enhancing
		boolean keepThingsInDomain = true;
		boolean keepThingsNotInDomain = false;
		Set<Long> alreadyEnhancingIds = alreadyEnhancing.keySet();
		if (null != deleteMap && !deleteMap.isEmpty()) {
			MonsterStuffUtils.retainValidMonsters(alreadyEnhancingIds, deleteMap,
					keepThingsInDomain, keepThingsNotInDomain);
		}

		if (null != updateMap && !updateMap.isEmpty()) {
			MonsterStuffUtils.retainValidMonsters(alreadyEnhancingIds, updateMap,
					keepThingsInDomain, keepThingsNotInDomain);
		}

		if (null != newMap && !newMap.isEmpty()) {
			//retain only the userMonsters in newMap that are in the db
			Set<Long> existingIds = existingUserMonsters.keySet();
			MonsterStuffUtils.retainValidMonsters(existingIds, newMap,
					keepThingsInDomain, keepThingsNotInDomain);

			//retain only the userMonsters in newMap that are not in healing
			keepThingsInDomain = false;
			keepThingsNotInDomain = true;
			Set<Long> alreadyHealingIds = alreadyHealing.keySet();
			MonsterStuffUtils.retainValidMonsters(alreadyHealingIds, newMap,
					keepThingsInDomain, keepThingsNotInDomain);
		}

		//CHECK MONEY
		if (!hasEnoughGems(resBuilder, u, gemsSpent, cashChange, deleteMap, updateMap, newMap)) {
			return false;
		}

		if (!hasEnoughCash(resBuilder, u, cashChange, deleteMap, updateMap, newMap)) {
			return false;
		}

		return true;
	}
 
	private boolean hasEnoughGems(Builder resBuilder, User u, int gemsSpent,
			int cashChange, Map<Long, UserEnhancementItemProto> deleteMap,
			Map<Long, UserEnhancementItemProto> updateMap,
			Map<Long, UserEnhancementItemProto> newMap) {
		int userGems = u.getGems();
		//if user's aggregate gems is < cost, don't allow transaction
		if (userGems < gemsSpent) {
			log.error("user error: user does not have enough gems. userGems=" + userGems +
					"\t gemsSpent=" + gemsSpent + "\t deleteMap=" + deleteMap + "\t newMap=" +
					newMap + "\t updateMap=" + updateMap + "\t cashChange=" + cashChange +
					"\t user=" + u);
			resBuilder.setStatus(SubmitMonsterEnhancementStatus.FAIL_INSUFFICIENT_GEMS);
			return false;
		}
		return true;
	}

	private boolean hasEnoughCash(Builder resBuilder, User u, int cashChange,
			Map<Long, UserEnhancementItemProto> deleteMap,
			Map<Long, UserEnhancementItemProto> updateMap,
			Map<Long, UserEnhancementItemProto> newMap) {
		int userCash = u.getCash(); 
		//positive 'cashChange' means refund, negative means charge user
		int cost = -1 * cashChange;
		if (userCash < cost) {
			log.error("user error: user does not have enough cash. userCash=" + userCash +
					"\t cost=" + cost + "\t deleteMap=" + deleteMap + "\t newMap=" +
					newMap + "\t updateMap=" + updateMap + "\t user=" + u);
			resBuilder.setStatus(SubmitMonsterEnhancementStatus.FAIL_INSUFFICIENT_CASH);
			return false;
		}
		return true;
	}

	private boolean writeChangesToDB(User user, int uId, int gemsSpent,
			int cashChange, Map<Long, UserEnhancementItemProto> protoDeleteMap,
		  Map<Long, UserEnhancementItemProto> protoUpdateMap,
		  Map<Long, UserEnhancementItemProto> protoNewMap,
		  Map<String, Integer> money) {

		//CHARGE THE USER
		int gemChange = -1 * gemsSpent;
		int numChange = user.updateRelativeCoinsAndDiamonds(cashChange, gemChange); 
		if (1 != numChange) {
			log.warn("problem with updating user stats: gemChange=" + gemChange
					+ ", cashChange=" + cashChange + ", user is " + user);
		} else {
			//everything went well
			if (0 != cashChange) {
				money.put(MiscMethods.cash, cashChange);
			}
			if (0 != gemsSpent) {
				money.put(MiscMethods.gems, gemChange);
			}
		}
		
		int num = 0;
		//delete everything left in the map, if there are any
		if (!protoDeleteMap.isEmpty()) {
			List<Long> deleteIds = new ArrayList<Long>(protoDeleteMap.keySet());
			num = DeleteUtils.get().deleteMonsterEnhancingForUser(uId, deleteIds);
			log.info("deleted monster enhancing rows. numDeleted=" + num +
					"\t protoDeleteMap=" + protoDeleteMap);
		}
		
		//convert protos to java counterparts
		List<MonsterEnhancingForUser> updateMap = MonsterStuffUtils.convertToMonsterEnhancingForUser(
	  		uId, protoUpdateMap);
		log.info("updateMap=" + updateMap);
		
	  List<MonsterEnhancingForUser> newMap = MonsterStuffUtils.convertToMonsterEnhancingForUser(
	  		uId, protoNewMap);
	  log.info("newMap=" + newMap);
	  
	  List<MonsterEnhancingForUser> updateAndNew = new ArrayList<MonsterEnhancingForUser>();
	  updateAndNew.addAll(updateMap);
	  updateAndNew.addAll(newMap);
		//update everything in enhancing table that is in update and new map
		if (null != updateAndNew && !updateAndNew.isEmpty()) {
			num = UpdateUtils.get().updateUserMonsterEnhancing(uId, updateAndNew);
			log.info("updated monster enhancing rows. numUpdated/inserted=" + num);
		}
		
		//for the new monsters, ensure that the monsters are "unequipped"
	  if (null != protoNewMap && !protoNewMap.isEmpty()) {
	  	//for the new monsters, set the teamSlotNum to 0
	  	int size = protoNewMap.size();
	  	List<Long> userMonsterIdList = new ArrayList<Long>(protoNewMap.keySet());
	  	List<Integer> teamSlotNumList = Collections.nCopies(size, 0);
	  	num = UpdateUtils.get().updateNullifyUserMonstersTeamSlotNum(userMonsterIdList, teamSlotNumList);
	  	log.info("updated user monster rows. numUpdated=" + num);
	  }
	  
		return true;
	}

  
	public void writeToUserCurrencyHistory(User aUser, Timestamp date,
			Map<String, Integer> money, int previousCash, int previousGems,
			Map<Long, UserEnhancementItemProto> protoDeleteMap,
		  Map<Long, UserEnhancementItemProto> protoUpdateMap,
		  Map<Long, UserEnhancementItemProto> protoNewMap) {
		
		Map<String, Integer> previousGemsCash = new HashMap<String, Integer>();
		Map<String, String> reasonsForChanges = new HashMap<String, String>();
		StringBuffer reasonForChange = new StringBuffer();
		reasonForChange.append(ControllerConstants.UCHRFC__ENHANCING);
		
		String cash = MiscMethods.cash;
		String gems = MiscMethods.gems;
		previousGemsCash.put(cash, previousCash);
		previousGemsCash.put(gems, previousGems);
		
		//maybe shouldn't keep track...oh well, more info hopefully is better than none
		if (null != protoDeleteMap && !protoDeleteMap.isEmpty()) {
			reasonForChange.append("deleteIds=");
			for (UserEnhancementItemProto ueip : protoDeleteMap.values()) {
				long id = ueip.getUserMonsterId();
				reasonForChange.append(id);
				reasonForChange.append(" ");
			}
		}
		if (null != protoUpdateMap && !protoUpdateMap.isEmpty()) {
			reasonForChange.append("updateIds=");
			for (UserEnhancementItemProto ueip : protoUpdateMap.values()) {
				long id = ueip.getUserMonsterId();
				reasonForChange.append(id);
				reasonForChange.append(" ");
			}
		}
		if (null != protoNewMap && !protoNewMap.isEmpty()) {
			reasonForChange.append("newIds=");
			for (UserEnhancementItemProto ueip : protoNewMap.values()) {
				long id = ueip.getUserMonsterId();
				reasonForChange.append(id);
				reasonForChange.append(" ");
			}
		}
		
		reasonsForChanges.put(cash, reasonForChange.toString());
		reasonsForChanges.put(gems, reasonForChange.toString());
		//TODO: FIX THIS
//		MiscMethods.writeToUserCurrencyOneUserGemsAndOrCash(aUser, date, money, previousGemsCash, reasonsForChanges);
	}

}
