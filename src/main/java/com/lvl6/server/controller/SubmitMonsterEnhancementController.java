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
import com.lvl6.events.request.SubmitMonsterEnhancementRequestEvent;
import com.lvl6.events.response.SubmitMonsterEnhancementResponseEvent;
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

		MinimumUserProto senderProto = reqProto.getSender();
		List<UserEnhancementItemProto> ueipDelete = reqProto.getUeipDeleteList();
		List<UserEnhancementItemProto> ueipUpdated = reqProto.getUeipUpdateList();
		List<UserEnhancementItemProto> ueipNew = reqProto.getUeipNewList();
		int userId = senderProto.getUserId();

		Map<Long, UserEnhancementItemProto> deleteMap = MonsterStuffUtils.
				convertIntoUserMonsterIdToUeipProtoMap(ueipDelete);
		Map<Long, UserEnhancementItemProto> updateMap = MonsterStuffUtils.
				convertIntoUserMonsterIdToUeipProtoMap(ueipUpdated);
		Map<Long, UserEnhancementItemProto> newMap = MonsterStuffUtils.
				convertIntoUserMonsterIdToUeipProtoMap(ueipNew);
		
		
		//set some values to send to the client (the response proto)
		SubmitMonsterEnhancementResponseProto.Builder resBuilder = SubmitMonsterEnhancementResponseProto.newBuilder();
		resBuilder.setSender(senderProto);

		server.lockPlayer(senderProto.getUserId(), getClass().getSimpleName());
		try {
			//int previousSilver = 0;
			//int previousGold = 0;
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
    			.monsterForUserRetrieveUtils().getSpecificUserMonstersForUser(userId, newIds);

			boolean legitMonster = checkLegit(resBuilder, aUser, userId, existingUserMonsters, 
					alreadyEnhancing, alreadyHealing, deleteMap, updateMap, newMap);

			boolean successful = false;
//			Map<String, Integer> money = new HashMap<String, Integer>();

			if (legitMonster) {
//				previousSilver = user.getCoins();
				successful = writeChangesToDB(aUser, userId, deleteMap, updateMap, newMap);
			}
		
			if (successful) {
			}

			SubmitMonsterEnhancementResponseEvent resEvent = new SubmitMonsterEnhancementResponseEvent(senderProto.getUserId());
			resEvent.setTag(event.getTag());
			resEvent.setSubmitMonsterEnhancementResponseProto(resBuilder.build());  
			server.writeEvent(resEvent);

			if (successful) {
//				UpdateClientUserResponseEvent resEventUpdate = MiscMethods.createUpdateClientUserResponseEventAndUpdateLeaderboard(user);
//				resEventUpdate.setTag(event.getTag());
//				server.writeEvent(resEventUpdate);

//				writeToUserCurrencyHistory(user, clientTime, money, previousSilver);
//				writeIntoMonsterEnhancementHistory(enhancingUserMonster, feederUserMonsters, false, clientTime);		      
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
 		Map<Long, UserEnhancementItemProto> newMap) {
   if (null == u ) {
     log.error("unexpected error: user is null. user=" + u + "\t deleteMap="+ deleteMap +
     		"\t updateMap=" + updateMap + "\t newMap=" + newMap);
     return false;
   }
   
   //retain only the userMonsters in deleteMap and updateMap, the client sent, that are in enhancing
   boolean keepThingsInDomain = true;
   boolean keepThingsNotInDomain = false;
   Set<Long> alreadyEnhancingIds = alreadyEnhancing.keySet();
   MonsterStuffUtils.retainValidMonsters(alreadyEnhancingIds, deleteMap,
  		 keepThingsInDomain, keepThingsNotInDomain);
   MonsterStuffUtils.retainValidMonsters(alreadyEnhancingIds, updateMap,
  		 keepThingsInDomain, keepThingsNotInDomain);
   
   //retain only the userMonsters in newMap, the client sent, that are in the db
   Set<Long> existingIds = existingUserMonsters.keySet();
   MonsterStuffUtils.retainValidMonsters(existingIds, newMap,
  		 keepThingsInDomain, keepThingsNotInDomain);
   
   //retain only the userMonsters in newMap, the client sent, that are not in healing
   keepThingsInDomain = false;
   keepThingsNotInDomain = true;
   Set<Long> alreadyHealingIds = alreadyHealing.keySet();
   MonsterStuffUtils.retainValidMonsters(alreadyHealingIds, newMap,
  		 keepThingsInDomain, keepThingsNotInDomain);
   
   
   //TODO: CHECK MONEY
   	
   resBuilder.setStatus(SubmitMonsterEnhancementStatus.SUCCESS);
   return true;
 }

	private boolean writeChangesToDB(User user, int uId,
			Map<Long, UserEnhancementItemProto> protoDeleteMap,
		  Map<Long, UserEnhancementItemProto> protoUpdateMap,
		  Map<Long, UserEnhancementItemProto> protoNewMap) {

		//TODO: CHARGE THE USER
//		if (!user.updateRelativeDiamondsCoinsExperienceNaive(goldChange, silverChange, 0)) {
//			log.error("problem with updating user stats: diamondChange=" + goldChange
//					+ ", coinChange=" + silverChange + ", user is " + user);
//		} else {
//			//everything went well
//			if (0 != silverChange) {
//				money.put(MiscMethods.silver, silverChange);
//			}
//		}
		
		int num = 0;
		//delete everything left in the map, if there are any
		if (!protoDeleteMap.isEmpty()) {
			List<Long> deleteIds = new ArrayList<Long>(protoDeleteMap.keySet());
			num = DeleteUtils.get().deleteMonsterEnhancingForUser(uId, deleteIds);
			log.info("deleted monster enhancing rows. numDeleted=" + num +
					"\t protoDeleteMap=" + protoDeleteMap);
		}
		
		
		//convert protos to java cou
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
		
		//unequip all the monsters that are in the newMap
//		List<UserMonster> userMonsters = new ArrayList<UserMonster>(feederUserMonsters);
//		for (UserMonster ue : userMonsters) {
//			if (!MiscMethods.unequipUserMonsterIfMonsterped(user, ue)) {
//				resBuilder.setStatus(EnhanceMonsterStatus.OTHER_FAIL);
//				log.error("problem with unequipping user equip" + ue.getId());
//				return false;
//			}
//		}

		
		return true;
	}

  
	public void writeToUserCurrencyHistory(User aUser, Timestamp date, Map<String, Integer> money,
			int previousSilver) {
		Map<String, Integer> previousGoldSilver = new HashMap<String, Integer>();
		Map<String, String> reasonsForChanges = new HashMap<String, String>();
		String reasonForChange = ControllerConstants.UCHRFC__ENHANCING;
		String silver = MiscMethods.silver;

		previousGoldSilver.put(silver, previousSilver);
		reasonsForChanges.put(silver, reasonForChange);

		MiscMethods.writeToUserCurrencyOneUserGoldAndOrSilver(aUser, date, money, previousGoldSilver, reasonsForChanges);
	}

//	private void writeIntoMonsterEnhancementHistory(UserMonster equipUnderEnhancement, List<UserMonster> feederUserMonsters,
//			boolean speedUp, Timestamp clientTime) {
//		long equipEnhancementId = equipUnderEnhancement.getId();
//		int userId = equipUnderEnhancement.getUserId();
//		int equipId = equipUnderEnhancement.getMonsterId();
//		int equipLevel = equipUnderEnhancement.getLevel();
//		int previousEnhancementPercentage = equipUnderEnhancement.getEnhancementPercentage();
//
//		int currentEnhancementPercentage = MiscMethods.calculateEnhancementForMonster(equipUnderEnhancement,
//				feederUserMonsters)+previousEnhancementPercentage;
//
//		Timestamp timeOfEnhancement = clientTime;
//
//
//		int numInserted = InsertUtils.get().insertIntoMonsterEnhancementHistory(equipEnhancementId,
//				userId, equipId, equipLevel, currentEnhancementPercentage, previousEnhancementPercentage,
//				timeOfEnhancement);
//
//		if(1 != numInserted) {
//			log.error("could not record equip enhancing collection. equipUnderEnhancement="
//					+ equipUnderEnhancement + ", speedUp=" + speedUp + ", clientTime=" + clientTime);
//		}
//	}

}
