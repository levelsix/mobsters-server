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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.SubmitMonsterEnhancementRequestEvent;
import com.lvl6.events.response.SubmitMonsterEnhancementResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.MonsterEnhancingForUser;
import com.lvl6.info.MonsterEvolvingForUser;
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
import com.lvl6.proto.UserProto.MinimumUserProtoWithMaxResources;
import com.lvl6.retrieveutils.MonsterEnhancingForUserRetrieveUtils;
import com.lvl6.retrieveutils.MonsterEvolvingForUserRetrieveUtils;
import com.lvl6.retrieveutils.MonsterHealingForUserRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component @DependsOn("gameServer") public class SubmitMonsterEnhancementController extends EventController {
	
	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	@Autowired
	protected Locker locker;

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
		
		log.info("reqProto=" + reqProto);

		//get data client sent
		MinimumUserProtoWithMaxResources senderResourcesProto = reqProto.getSender();
		MinimumUserProto senderProto = senderResourcesProto.getMinUserProto();
//		List<UserEnhancementItemProto> ueipDelete = reqProto.getUeipDeleteList();
//		List<UserEnhancementItemProto> ueipUpdated = reqProto.getUeipUpdateList();
		List<UserEnhancementItemProto> ueipNew = reqProto.getUeipNewList();
		int userId = senderProto.getUserUuid();
		//positive value, need to convert to negative when updating user
		int gemsSpent = reqProto.getGemsSpent();
		//positive means refund, negative means charge user
		int oilChange = reqProto.getOilChange();
		Timestamp clientTime = new Timestamp((new Date()).getTime());
		int maxOil= senderResourcesProto.getMaxOil();

//		Map<Long, UserEnhancementItemProto> deleteMap = MonsterStuffUtils.
//				convertIntoUserMonsterIdToUeipProtoMap(ueipDelete);
//		Map<Long, UserEnhancementItemProto> updateMap = MonsterStuffUtils.
//				convertIntoUserMonsterIdToUeipProtoMap(ueipUpdated);
		Map<Long, UserEnhancementItemProto> newMap = MonsterStuffUtils.
				convertIntoUserMonsterIdToUeipProtoMap(ueipNew);
		
		
		//set some values to send to the client (the response proto)
		SubmitMonsterEnhancementResponseProto.Builder resBuilder = SubmitMonsterEnhancementResponseProto.newBuilder();
		resBuilder.setSender(senderResourcesProto);
		resBuilder.setStatus(SubmitMonsterEnhancementStatus.FAIL_OTHER);

		getLocker().lockPlayer(userId, getClass().getSimpleName());
		try {
			int previousOil = 0;
			int previousGems = 0;
			//get whatever we need from the database
			User aUser = RetrieveUtils.userRetrieveUtils().getUserById(userId);
			Map<Long, MonsterEnhancingForUser> alreadyEnhancing =
						MonsterEnhancingForUserRetrieveUtils.getMonstersForUser(userId);
			Map<Long, MonsterHealingForUser> alreadyHealing =
    			MonsterHealingForUserRetrieveUtils.getMonstersForUser(userId);
			MonsterEvolvingForUser evolution = MonsterEvolvingForUserRetrieveUtils
					.getEvolutionForUser(userId);
    	
			//retrieve the new monsters that will be used in enhancing, and
			//the ones being updated
			Set<Long> newAndUpdatedIds = new HashSet<Long>();
			newAndUpdatedIds.addAll(newMap.keySet());
//			newAndUpdatedIds.addAll(updateMap.keySet());
			Map<Long, MonsterForUser> existingUserMonsters = RetrieveUtils
    			.monsterForUserRetrieveUtils().getSpecificOrAllUserMonstersForUser(userId, newAndUpdatedIds);

			boolean legitMonster = checkLegit(resBuilder, aUser, userId, existingUserMonsters, 
					alreadyEnhancing, alreadyHealing, newMap, evolution,
					gemsSpent, oilChange);

			boolean successful = false;
			Map<String, Integer> money = new HashMap<String, Integer>();
			
			if (legitMonster) {
				previousOil = aUser.getOil();
				previousGems = aUser.getGems();
				successful = writeChangesToDB(aUser, userId, gemsSpent, oilChange,
//						deleteMap, updateMap,
					newMap, money, maxOil);
			}
		
			if (successful) {
				resBuilder.setStatus(SubmitMonsterEnhancementStatus.SUCCESS);
			}

			SubmitMonsterEnhancementResponseEvent resEvent = new SubmitMonsterEnhancementResponseEvent(senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setSubmitMonsterEnhancementResponseProto(resBuilder.build());  
			server.writeEvent(resEvent);

			if (successful) {
				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = MiscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(aUser, null);
				resEventUpdate.setTag(event.getTag());
				server.writeEvent(resEventUpdate);

				writeToUserCurrencyHistory(aUser, clientTime, money, previousOil, previousGems,
//						deleteMap, updateMap,
						newMap);
			}

		} catch (Exception e) {
			log.error("exception in EnhanceMonster processEvent", e);
		} finally {
			getLocker().unlockPlayer(userId, getClass().getSimpleName());   
		}
	}

	 /*
  * Return true if user request is valid; false otherwise and set the
  * 	builder status to the appropriate value. delete, update, new maps
  * 	MIGHT BE MODIFIED.
  * 
  * For the most part, will always return success. Why?
  * (Will return fail if user does not have enough funds.) 
  * Answer: For the map(s)
  * 
  * delete (DEPRECATED) - The monsters to be removed from enhancing will/should
  * 	only be the ones the user already has in enhancing.
  * update (DEPRECATED) - The monsters to be updated in enhancing will/should already exist
  * new - brand new monsters
  * 
  * Note: If any of the monsters have "restricted" property set to true,
  * 	then said monster can only be the base monster. 
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
//			Map<Long, UserEnhancementItemProto> deleteMap,
//			Map<Long, UserEnhancementItemProto> updateMap,
			Map<Long, UserEnhancementItemProto> newMap, MonsterEvolvingForUser evolution,
			int gemsSpent, int oilChange) {
		if (null == u ) {
//			log.error(String.format(
//				"user is null. userId=%s, deleteMap=%s, updateMap=%s, newMap=%s",
//				userId, deleteMap, updateMap, newMap));
			log.error(String.format(
				"user is null. userId=%s, newMap=%s",
				userId, newMap));
			return false;
		}
		
		if ( null != alreadyEnhancing && !alreadyEnhancing.isEmpty() ) {
			log.error(String.format(
				"user already has monsters enhancing=%s, user=%s",
				alreadyEnhancing, u));
			return false;
		}
		
		//retain only the userMonsters in deleteMap and updateMap that are in enhancing
		boolean keepThingsInDomain = true;
		boolean keepThingsNotInDomain = false;
//		Set<Long> alreadyEnhancingIds = alreadyEnhancing.keySet();
//		if (null != deleteMap && !deleteMap.isEmpty()) {
//			MonsterStuffUtils.retainValidMonsters(alreadyEnhancingIds, deleteMap,
//					keepThingsInDomain, keepThingsNotInDomain);
//		}
//
//		if (null != updateMap && !updateMap.isEmpty()) {
//			MonsterStuffUtils.retainValidMonsters(alreadyEnhancingIds, updateMap,
//					keepThingsInDomain, keepThingsNotInDomain);
//		}

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
			
			//retain only the userMonsters in newMap that are not in evolutions
			Set<Long> idsInEvolutions = MonsterStuffUtils.getUserMonsterIdsUsedInEvolution(
					evolution, null);
			MonsterStuffUtils.retainValidMonsters(idsInEvolutions, newMap,
					keepThingsInDomain, keepThingsNotInDomain);
			
		}
		
		// If any of the monsters have "restricted" property set to true,
		// then said monster can only be the base monster.
		Set<Long> restrictedUserMonsterIds = restrictedUserMonsters(existingUserMonsters);
		for (Long restrictedUserMonsterId : restrictedUserMonsterIds) {
			
			UserEnhancementItemProto ueip = null;
			/*if (updateMap.containsKey(restrictedUserMonsterId)) {
				ueip = updateMap.get(restrictedUserMonsterId);
				
			} else
			*/
			if (newMap.containsKey(restrictedUserMonsterId)) {
				ueip = newMap.get(restrictedUserMonsterId);
			}
			
			if (null == ueip) {
				continue;
			}
			
			//monster is restricted, better be a base monster
			if (ueip.getEnhancingCost() > 0 || ueip.getExpectedStartTimeMillis() > 0) {
//				String msg = String.format(
//					"user is using restricted monster in enhancing (not as base monster): %s. userMonsters= %s, updateMap=%s, newMap=%s",
//					ueip, existingUserMonsters, updateMap, newMap );
				String msg = String.format(
					"user is using restricted monster in enhancing (not as base monster): %s. userMonsters= %s, newMap=%s",
					ueip, existingUserMonsters, newMap );
				log.error(msg);
				return false;
			}
		}
		
		

		//CHECK MONEY
//		if (!hasEnoughGems(resBuilder, u, gemsSpent, oilChange, deleteMap, updateMap, newMap)) {
		if (!hasEnoughGems(resBuilder, u, gemsSpent, oilChange, newMap)) {
			return false;
		}

//		if (!hasEnoughOil(resBuilder, u, gemsSpent, oilChange, deleteMap, updateMap, newMap)) {
		if (!hasEnoughOil(resBuilder, u, gemsSpent, oilChange, newMap)) {
			return false;
		}

		return true;
	}
	
	private Set<Long> restrictedUserMonsters(Map<Long, MonsterForUser> existingUserMonsters) {
		
		Set<Long> restrictedUserMonsterIds = new HashSet<Long>();
		for ( MonsterForUser mfu : existingUserMonsters.values() ) {
			if (mfu.isRestricted()) {
				restrictedUserMonsterIds.add(mfu.getId());
			}
		}
		
		return restrictedUserMonsterIds;
	}
 
	private boolean hasEnoughGems(Builder resBuilder, User u, int gemsSpent,
			int oilChange,
//			Map<Long, UserEnhancementItemProto> deleteMap,
//			Map<Long, UserEnhancementItemProto> updateMap,
			Map<Long, UserEnhancementItemProto> newMap) {
		int userGems = u.getGems();
		//if user's aggregate gems is < cost, don't allow transaction
		if (userGems < gemsSpent) {
			log.error(String.format(
				"insufficient gems. userGems=%s, gemsSpent=%s, newMap=%s, oilChange=%s, user=%s",
				userGems, gemsSpent, newMap, oilChange, u));
			resBuilder.setStatus(SubmitMonsterEnhancementStatus.FAIL_INSUFFICIENT_GEMS);
			return false;
		}
		return true;
	}

	private boolean hasEnoughOil(Builder resBuilder, User u, int oilChange,
			int gemsSpent,
//			Map<Long, UserEnhancementItemProto> deleteMap,
//			Map<Long, UserEnhancementItemProto> updateMap,
			Map<Long, UserEnhancementItemProto> newMap) {
		int userOil = u.getOil(); 
		//positive 'cashChange' means refund, negative means charge user
		int cost = -1 * oilChange;
		
		//if user not spending gems and is just spending cash, check if he has enough
		if (0 == gemsSpent && userOil < cost) {
			log.error(String.format(
				"insufficient oil. userOil=%s, cost=%s, newMap=%s, user=%s",
				userOil, cost, newMap, u));
			resBuilder.setStatus(SubmitMonsterEnhancementStatus.FAIL_INSUFFICIENT_OIL);
			return false;
		}
		return true;
	}

	private boolean writeChangesToDB(User user, int uId, int gemsSpent,
		int oilChange,
//		Map<Long, UserEnhancementItemProto> protoDeleteMap,
//		Map<Long, UserEnhancementItemProto> protoUpdateMap,
		Map<Long, UserEnhancementItemProto> protoNewMap,
		Map<String, Integer> money, int maxOil)
	{

		//CHARGE THE USER
		int cashChange = 0;
		int gemChange = -1 * gemsSpent;

		//if user is getting oil back, make sure it doesn't exceed his limit
		if (oilChange > 0) {
			int curOil = Math.min(user.getOil(), maxOil); //in case user's oil is more than maxOil.
			int maxOilUserCanGain = maxOil - curOil;
			oilChange = Math.min(oilChange, maxOilUserCanGain);
		}
		
		if (0 != oilChange || 0 != gemChange) {
			
//			log.info("oilChange=" + oilChange + "\t gemChange=" + gemChange);
			int numChange = user.updateRelativeCashAndOilAndGems(cashChange, oilChange, gemChange); 
			if (1 != numChange) {
				log.warn(String.format(
					"problem with updating user stats: gemChange=%s, oilChange=%s, user=%s",
					gemChange, oilChange, user));
			} else {
				//everything went well
				if (0 != oilChange) {
					money.put(MiscMethods.oil, oilChange);
				}
				if (0 != gemsSpent) {
					money.put(MiscMethods.gems, gemChange);
				}
			}
		}
//		log.info(String.format("deleteMap=%s", protoDeleteMap));
//		log.info(String.format("updateMap=%s", protoUpdateMap));
//		log.info(String.format("newMap=%s", protoNewMap));
		
		
		int num = 0;
//		//delete everything left in the map, if there are any
//		if (!protoDeleteMap.isEmpty()) {
//			List<Long> deleteIds = new ArrayList<Long>(protoDeleteMap.keySet());
//			num = DeleteUtils.get().deleteMonsterEnhancingForUser(uId, deleteIds);
//			log.info("deleted monster enhancing rows. numDeleted=" + num +
//					"\t protoDeleteMap=" + protoDeleteMap);
//		}
		
//		//convert protos to java counterparts
//		List<MonsterEnhancingForUser> updateMap = MonsterStuffUtils.convertToMonsterEnhancingForUser(
//	  		uId, protoUpdateMap);
//		log.info(String.format("updateMap=%s", updateMap));
//		
		List<MonsterEnhancingForUser> newMap = MonsterStuffUtils.convertToMonsterEnhancingForUser(
			uId, protoNewMap);
		log.info(String.format("newMap=%s", newMap));

		List<MonsterEnhancingForUser> updateAndNew = new ArrayList<MonsterEnhancingForUser>();
//	  updateAndNew.addAll(updateMap);
		updateAndNew.addAll(newMap);
		//update everything in enhancing table that is in update and new map
		if (null != updateAndNew && !updateAndNew.isEmpty()) {
			num = UpdateUtils.get().updateUserMonsterEnhancing(uId, updateAndNew);
			log.info(String.format(
				"updated monster enhancing rows. numInserted=%s", num));
		}
		
		//for the new monsters, ensure that the monsters are "unequipped"
	  if (null != protoNewMap && !protoNewMap.isEmpty()) {
	  	//for the new monsters, set the teamSlotNum to 0
	  	int size = protoNewMap.size();
	  	List<Long> userMonsterIdList = new ArrayList<Long>(protoNewMap.keySet());
	  	List<Integer> teamSlotNumList = Collections.nCopies(size, 0);
	  	num = UpdateUtils.get().updateNullifyUserMonstersTeamSlotNum(userMonsterIdList, teamSlotNumList);
	  	log.info(String.format(
	  		"updated user monster rows. numUpdated=%s", num));
	  }
	  
		return true;
	}

  
	public void writeToUserCurrencyHistory(User aUser, Timestamp date,
		Map<String, Integer> currencyChange, int previousOil, int previousGems,
//		Map<Long, UserEnhancementItemProto> protoDeleteMap,
//		Map<Long, UserEnhancementItemProto> protoUpdateMap,
		Map<Long, UserEnhancementItemProto> protoNewMap)
	{

		StringBuilder detailsSb = new StringBuilder();
		
		//maybe shouldn't keep track...oh well, more info hopefully is better than none
//		if (null != protoDeleteMap && !protoDeleteMap.isEmpty()) {
//			detailsSb.append("deleteIds=");
//			for (UserEnhancementItemProto ueip : protoDeleteMap.values()) {
//				long id = ueip.getUserMonsterId();
//				detailsSb.append(id);
//				detailsSb.append(" ");
//			}
//		}
//		if (null != protoUpdateMap && !protoUpdateMap.isEmpty()) {
//			detailsSb.append("updateIds=");
//			for (UserEnhancementItemProto ueip : protoUpdateMap.values()) {
//				long id = ueip.getUserMonsterId();
//				detailsSb.append(id);
//				detailsSb.append(" ");
//			}
//		}
		if (null != protoNewMap && !protoNewMap.isEmpty()) {
			detailsSb.append("newIds=");
			for (UserEnhancementItemProto ueip : protoNewMap.values()) {
				long id = ueip.getUserMonsterId();
				detailsSb.append(id);
				detailsSb.append(" ");
			}
		}
		
		int userId = aUser.getId();
		Map<String, Integer> previousCurrency = new HashMap<String, Integer>();
		Map<String, Integer> currentCurrency = new HashMap<String, Integer>();
		Map<String, String> reasonsForChanges = new HashMap<String, String>();
		Map<String, String> detailsMap = new HashMap<String, String>();
		String reason = ControllerConstants.UCHRFC__ENHANCING;
		String oil = MiscMethods.oil;
		String gems = MiscMethods.gems;
		
		previousCurrency.put(oil, previousOil);
		previousCurrency.put(gems, previousGems);
		currentCurrency.put(oil, aUser.getOil());
		currentCurrency.put(gems, aUser.getGems());
		
		reasonsForChanges.put(oil, reason);
		reasonsForChanges.put(gems, reason);
		detailsMap.put(oil, detailsSb.toString());
		detailsMap.put(gems, detailsSb.toString());
		
		MiscMethods.writeToUserCurrencyOneUser(userId, date, currencyChange,
				previousCurrency, currentCurrency, reasonsForChanges,
				detailsMap);
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

}
