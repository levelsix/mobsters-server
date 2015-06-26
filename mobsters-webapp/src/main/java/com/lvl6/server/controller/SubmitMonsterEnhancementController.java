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
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.SubmitMonsterEnhancementRequestEvent;
import com.lvl6.events.response.AchievementProgressResponseEvent;
import com.lvl6.events.response.EvolutionFinishedResponseEvent;
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
import com.lvl6.proto.MonsterStuffProto.UserEnhancementItemProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.proto.UserProto.MinimumUserProtoWithMaxResources;
import com.lvl6.retrieveutils.MonsterEnhancingForUserRetrieveUtils2;
import com.lvl6.retrieveutils.MonsterEvolvingForUserRetrieveUtils2;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2;
import com.lvl6.retrieveutils.MonsterHealingForUserRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.TimeUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component

public class SubmitMonsterEnhancementController extends EventController {

	private static Logger log = LoggerFactory.getLogger(SubmitMonsterEnhancementController.class);

	@Autowired
	protected Locker locker;

	@Autowired
	protected MiscMethods miscMethods;
	
	@Autowired
	protected MonsterEnhancingForUserRetrieveUtils2 monsterEnhancingForUserRetrieveUtils;
	@Autowired
	protected MonsterHealingForUserRetrieveUtils2 monsterHealingForUserRetrieveUtils;
	@Autowired
	protected MonsterEvolvingForUserRetrieveUtils2 monsterEvolvingForUserRetrieveUtils;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtils;
	
	@Autowired
	protected MonsterStuffUtils monsterStuffUtils;
	
	@Autowired
	protected TimeUtils timeUtils;

	@Autowired
	protected MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils;

	public SubmitMonsterEnhancementController() {
		
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
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		SubmitMonsterEnhancementRequestProto reqProto = ((SubmitMonsterEnhancementRequestEvent) event)
				.getSubmitMonsterEnhancementRequestProto();

		log.info(String.format("reqProto=%s", reqProto));

		//get data client sent
		MinimumUserProtoWithMaxResources senderResourcesProto = reqProto
				.getSender();
		MinimumUserProto senderProto = senderResourcesProto.getMinUserProto();
		//		List<UserEnhancementItemProto> ueipDelete = reqProto.getUeipDeleteList();
		//		List<UserEnhancementItemProto> ueipUpdated = reqProto.getUeipUpdateList();
		List<UserEnhancementItemProto> ueipNew = reqProto.getUeipNewList();
		String userId = senderProto.getUserUuid();
		//positive value, need to convert to negative when updating user
		int gemsSpent = reqProto.getGemsSpent();
		//positive means refund, negative means charge user
		int oilChange = reqProto.getOilChange();
		Timestamp clientTime = new Timestamp((new Date()).getTime());
		int maxOil = senderResourcesProto.getMaxOil();
		Date clientTimeDate = new Date(reqProto.getClientTime());
		
		Map<String, UserEnhancementItemProto> newMap = monsterStuffUtils
				.convertIntoUserMonsterIdToUeipProtoMap(ueipNew);

		//set some values to send to the client (the response proto)
		SubmitMonsterEnhancementResponseProto.Builder resBuilder = SubmitMonsterEnhancementResponseProto
				.newBuilder();
		resBuilder.setSender(senderResourcesProto);
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
		
		if(reqProto.getClientTime() == 0) {
			resBuilder.setStatus(ResponseStatus.FAIL_CLIENT_TIME_NOT_SENT);
			log.error("clientTime not sent");
			SubmitMonsterEnhancementResponseEvent resEvent = new SubmitMonsterEnhancementResponseEvent(senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}
		
		if(timeUtils.numMinutesDifference(clientTimeDate, new Date()) > 
		ControllerConstants.CLIENT_TIME_MINUTES_CONSTANT_CHECK) {
			resBuilder.setStatus(ResponseStatus.FAIL_TIME_OUT_OF_SYNC);
			log.error("time is out of sync > 2 hrs for userId {}", senderProto.getUserUuid());
			SubmitMonsterEnhancementResponseEvent resEvent = new SubmitMonsterEnhancementResponseEvent(senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		UUID userUuid = null;
		boolean invalidUuids = true;
		try {
			userUuid = UUID.fromString(userId);

			if (ueipNew != null) {
				for (UserEnhancementItemProto ueip : ueipNew) {
					UUID.fromString(ueip.getUserMonsterUuid());
				}
			}

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
					"UUID error. incorrect userId=%s, ueipNew=%s", userId,
					ueipNew), e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			SubmitMonsterEnhancementResponseEvent resEvent = new SubmitMonsterEnhancementResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder
					.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		locker.lockPlayer(userUuid, getClass().getSimpleName());
		try {
			int previousOil = 0;
			int previousGems = 0;
			//get whatever we need from the database
			User aUser = getUserRetrieveUtils().getUserById(userId);
			Map<String, MonsterEnhancingForUser> alreadyEnhancing = getMonsterEnhancingForUserRetrieveUtils()
					.getMonstersForUser(userId);
			Map<String, MonsterHealingForUser> alreadyHealing = getMonsterHealingForUserRetrieveUtils()
					.getMonstersForUser(userId);
			MonsterEvolvingForUser evolution = getMonsterEvolvingForUserRetrieveUtils()
					.getEvolutionForUser(userId);

			//retrieve the new monsters that will be used in enhancing, and
			//the ones being updated
			Set<String> newAndUpdatedIds = new HashSet<String>();
			newAndUpdatedIds.addAll(newMap.keySet());
			//			newAndUpdatedIds.addAll(updateMap.keySet());
			Map<String, MonsterForUser> existingUserMonsters = getMonsterForUserRetrieveUtils()
					.getSpecificOrAllUserMonstersForUser(userId,
							newAndUpdatedIds);

			boolean legitMonster = checkLegit(resBuilder, aUser, userId,
					existingUserMonsters, alreadyEnhancing, alreadyHealing,
					newMap, evolution, gemsSpent, oilChange);

			boolean successful = false;
			Map<String, Integer> money = new HashMap<String, Integer>();

			if (legitMonster) {
				previousOil = aUser.getOil();
				previousGems = aUser.getGems();
				successful = writeChangesToDB(aUser, userId, gemsSpent,
						oilChange,
						//						deleteMap, updateMap,
						newMap, money, maxOil);
			}

			if (successful) {
				resBuilder.setStatus(ResponseStatus.SUCCESS);
			}

			SubmitMonsterEnhancementResponseEvent resEvent = new SubmitMonsterEnhancementResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder
					.build());
			responses.normalResponseEvents().add(resEvent);

			if (successful) {
				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = miscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								aUser, null, null);
				resEventUpdate.setTag(event.getTag());
				responses.normalResponseEvents().add(resEventUpdate);

				writeToUserCurrencyHistory(aUser, clientTime, money,
						previousOil, previousGems,
						//						deleteMap, updateMap,
						newMap);
			}

		} catch (Exception e) {
			log.error("exception in EnhanceMonster processEvent", e);
			try {
				resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
				SubmitMonsterEnhancementResponseEvent resEvent = new SubmitMonsterEnhancementResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder
						.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in SubmitMonsterEnhancementController processEvent",
						e);
			}
		} finally {
			locker.unlockPlayer(userUuid, getClass().getSimpleName());
		}
	}

	/*
	 * Return true if user request is valid; false otherwise and set the
	 * 	builder status to the appropriate value.
	 * 
	 * Will return fail if user does not have enough funds, or if any
	 * of the monsters are currently engaged in something else, such as
	 * healing, enhancing 
	 * 
	 * Note: If any of the monsters have "restricted" property set to true,
	 * 	then said monster can only be the base monster. 
	 * 
	 * Ex. If user wants to delete a monster (A) that isn't enhancing, along with some
	 * monsters already enhancing (B), only the valid monsters (B) will be deleted.
	 * Same logic with update and new. 
	 * 
	 */
	private boolean checkLegit(Builder resBuilder, User u, String userId,
			Map<String, MonsterForUser> existingUserMonsters,
			Map<String, MonsterEnhancingForUser> alreadyEnhancing,
			Map<String, MonsterHealingForUser> alreadyHealing,
			Map<String, UserEnhancementItemProto> newMap,
			MonsterEvolvingForUser evolution, int gemsSpent, int oilChange) {
		if (null == u) {
			log.error("user is null. userId={}, newMap={}", userId, newMap);
			return false;
		}

		if (null != alreadyEnhancing && !alreadyEnhancing.isEmpty()) {
			log.error("user already has monsters enhancing={}, user={}",
					alreadyEnhancing, u);
			resBuilder
					.setStatus(ResponseStatus.FAIL_MONSTER_IN_ENHANCING);
			return false;
		}

		if (null != newMap && !newMap.isEmpty()) {
			Set<String> newMapIds = newMap.keySet();

			Set<String> existingIds = existingUserMonsters.keySet();
			if (!existingIds.containsAll(newMapIds)) {
				log.error("some monsters not in the db. inDb={}, newMap={}",
						existingUserMonsters, newMap);
				resBuilder
						.setStatus(ResponseStatus.FAIL_MONSTER_NONEXISTENT);
				return false;
			}

			Set<String> alreadyHealingIds = alreadyHealing.keySet();
			if (!Collections.disjoint(newMapIds, alreadyHealingIds)) {
				log.error("some monsters are healing. healing={}, newMap={}",
						alreadyHealing, newMap);
				resBuilder
						.setStatus(ResponseStatus.FAIL_MONSTER_ALREADY_IN_HEALING);
				return false;
			}

			//retain only the userMonsters in newMap that are not in evolutions
			Set<String> idsInEvolutions = monsterStuffUtils
					.getUserMonsterIdsUsedInEvolution(evolution, null);
			if (!Collections.disjoint(idsInEvolutions, newMapIds)) {
				log.error("some monsters are evolving. evolving={}, newMap={}",
						evolution, newMap);
				resBuilder
						.setStatus(ResponseStatus.FAIL_MONSTER_IN_EVOLUTION);
				return false;
			}

		}

		// If any of the monsters have "restricted" property set to true,
		// then said monster can only be the base monster.
		Set<String> restrictedUserMonsterIds = restrictedUserMonsters(existingUserMonsters);
		for (String restrictedUserMonsterId : restrictedUserMonsterIds) {

			UserEnhancementItemProto ueip = null;
			if (newMap.containsKey(restrictedUserMonsterId)) {
				ueip = newMap.get(restrictedUserMonsterId);
			}

			if (null == ueip) {
				continue;
			}

			//monster is restricted, better be a base monster
			if (ueip.getEnhancingCost() > 0
					|| ueip.getExpectedStartTimeMillis() > 0) {
				String preface = "restricted not-base-monster in enhancing:";
				log.error("{} ueip={}. userMonsters= {}, newMap={}",
						new Object[] { preface, ueip, existingUserMonsters,
								newMap });
				resBuilder
						.setStatus(ResponseStatus.FAIL_MONSTER_RESTRICTED);
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

	private Set<String> restrictedUserMonsters(
			Map<String, MonsterForUser> existingUserMonsters) {

		Set<String> restrictedUserMonsterIds = new HashSet<String>();
		for (MonsterForUser mfu : existingUserMonsters.values()) {
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
			Map<String, UserEnhancementItemProto> newMap) {
		int userGems = u.getGems();
		//if user's aggregate gems is < cost, don't allow transaction
		if (userGems < gemsSpent) {
			log.error(String
					.format("insufficient gems. userGems=%s, gemsSpent=%s, newMap=%s, oilChange=%s, user=%s",
							userGems, gemsSpent, newMap, oilChange, u));
			resBuilder
					.setStatus(ResponseStatus.FAIL_INSUFFICIENT_GEMS);
			return false;
		}
		return true;
	}

	private boolean hasEnoughOil(Builder resBuilder, User u, int oilChange,
			int gemsSpent,
			//			Map<Long, UserEnhancementItemProto> deleteMap,
			//			Map<Long, UserEnhancementItemProto> updateMap,
			Map<String, UserEnhancementItemProto> newMap) {
		int userOil = u.getOil();
		//positive 'cashChange' means refund, negative means charge user
		int cost = -1 * oilChange;

		//if user not spending gems and is just spending cash, check if he has enough
		if (0 == gemsSpent && userOil < cost) {
			log.error(String
					.format("insufficient oil. userOil=%s, cost=%s, newMap=%s, user=%s",
							userOil, cost, newMap, u));
			resBuilder
					.setStatus(ResponseStatus.FAIL_INSUFFICIENT_OIL);
			return false;
		}
		return true;
	}

	private boolean writeChangesToDB(User user, String uId,
			int gemsSpent,
			int oilChange,
			//		Map<Long, UserEnhancementItemProto> protoDeleteMap,
			//		Map<Long, UserEnhancementItemProto> protoUpdateMap,
			Map<String, UserEnhancementItemProto> protoNewMap,
			Map<String, Integer> money, int maxOil) {

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
			int numChange = user.updateRelativeCashAndOilAndGems(cashChange,
					oilChange, gemChange, 0);
			if (1 != numChange) {
				log.warn(String
						.format("problem with updating user stats: gemChange=%s, oilChange=%s, user=%s",
								gemChange, oilChange, user));
			} else {
				//everything went well
				if (0 != oilChange) {
					money.put(miscMethods.oil, oilChange);
				}
				if (0 != gemsSpent) {
					money.put(miscMethods.gems, gemChange);
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
		List<MonsterEnhancingForUser> newMap = monsterStuffUtils
				.convertToMonsterEnhancingForUser(uId, protoNewMap);
		log.info(String.format("newMap=%s", newMap));

		List<MonsterEnhancingForUser> updateAndNew = new ArrayList<MonsterEnhancingForUser>();
		//	  updateAndNew.addAll(updateMap);
		updateAndNew.addAll(newMap);
		//update everything in enhancing table that is in update and new map
		if (null != updateAndNew && !updateAndNew.isEmpty()) {
			num = UpdateUtils.get().updateUserMonsterEnhancing(uId,
					updateAndNew);
			log.info(String.format(
					"updated monster enhancing rows. numInserted=%s", num));
		}

		//		//for the new monsters, ensure that the monsters are "unequipped"
		//		if (null != protoNewMap && !protoNewMap.isEmpty()) {
		//			//for the new monsters, set the teamSlotNum to 0
		//			int size = protoNewMap.size();
		//			List<String> userMonsterIdList = new ArrayList<String>(protoNewMap.keySet());
		//			List<Integer> teamSlotNumList = Collections.nCopies(size, 0);
		//			num = UpdateUtils.get().updateNullifyUserMonstersTeamSlotNum(userMonsterIdList, teamSlotNumList);
		//			log.info(String.format(
		//				"updated user monster rows. numUpdated=%s", num));
		//		}

		return true;
	}

	public void writeToUserCurrencyHistory(User aUser, Timestamp date,
			Map<String, Integer> currencyChange, int previousOil,
			int previousGems,
			//		Map<Long, UserEnhancementItemProto> protoDeleteMap,
			//		Map<Long, UserEnhancementItemProto> protoUpdateMap,
			Map<String, UserEnhancementItemProto> protoNewMap) {

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
				String id = ueip.getUserMonsterUuid();
				detailsSb.append(id);
				detailsSb.append(" ");
			}
		}

		String userId = aUser.getId();
		Map<String, Integer> previousCurrency = new HashMap<String, Integer>();
		Map<String, Integer> currentCurrency = new HashMap<String, Integer>();
		Map<String, String> reasonsForChanges = new HashMap<String, String>();
		Map<String, String> detailsMap = new HashMap<String, String>();
		String reason = ControllerConstants.UCHRFC__ENHANCING;
		String oil = miscMethods.oil;
		String gems = miscMethods.gems;

		previousCurrency.put(oil, previousOil);
		previousCurrency.put(gems, previousGems);
		currentCurrency.put(oil, aUser.getOil());
		currentCurrency.put(gems, aUser.getGems());

		reasonsForChanges.put(oil, reason);
		reasonsForChanges.put(gems, reason);
		detailsMap.put(oil, detailsSb.toString());
		detailsMap.put(gems, detailsSb.toString());

		miscMethods.writeToUserCurrencyOneUser(userId, date, currencyChange,
				previousCurrency, currentCurrency, reasonsForChanges,
				detailsMap);
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public MonsterEnhancingForUserRetrieveUtils2 getMonsterEnhancingForUserRetrieveUtils() {
		return monsterEnhancingForUserRetrieveUtils;
	}

	public void setMonsterEnhancingForUserRetrieveUtils(
			MonsterEnhancingForUserRetrieveUtils2 monsterEnhancingForUserRetrieveUtils) {
		this.monsterEnhancingForUserRetrieveUtils = monsterEnhancingForUserRetrieveUtils;
	}

	public MonsterHealingForUserRetrieveUtils2 getMonsterHealingForUserRetrieveUtils() {
		return monsterHealingForUserRetrieveUtils;
	}

	public void setMonsterHealingForUserRetrieveUtils(
			MonsterHealingForUserRetrieveUtils2 monsterHealingForUserRetrieveUtils) {
		this.monsterHealingForUserRetrieveUtils = monsterHealingForUserRetrieveUtils;
	}

	public MonsterEvolvingForUserRetrieveUtils2 getMonsterEvolvingForUserRetrieveUtils() {
		return monsterEvolvingForUserRetrieveUtils;
	}

	public void setMonsterEvolvingForUserRetrieveUtils(
			MonsterEvolvingForUserRetrieveUtils2 monsterEvolvingForUserRetrieveUtils) {
		this.monsterEvolvingForUserRetrieveUtils = monsterEvolvingForUserRetrieveUtils;
	}

	public UserRetrieveUtils2 getUserRetrieveUtils() {
		return userRetrieveUtils;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtils) {
		this.userRetrieveUtils = userRetrieveUtils;
	}

	public MonsterForUserRetrieveUtils2 getMonsterForUserRetrieveUtils() {
		return monsterForUserRetrieveUtils;
	}

	public void setMonsterForUserRetrieveUtils(
			MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils) {
		this.monsterForUserRetrieveUtils = monsterForUserRetrieveUtils;
	}
}
