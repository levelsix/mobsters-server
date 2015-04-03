package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
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
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.HealMonsterRequestEvent;
import com.lvl6.events.response.HealMonsterResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.MonsterEnhancingForUser;
import com.lvl6.info.MonsterEvolvingForUser;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.MonsterHealingForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventMonsterProto.HealMonsterRequestProto;
import com.lvl6.proto.EventMonsterProto.HealMonsterResponseProto;
import com.lvl6.proto.EventMonsterProto.HealMonsterResponseProto.Builder;
import com.lvl6.proto.EventMonsterProto.HealMonsterResponseProto.HealMonsterStatus;
import com.lvl6.proto.MonsterStuffProto.UserMonsterCurrentHealthProto;
import com.lvl6.proto.MonsterStuffProto.UserMonsterHealingProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.proto.UserProto.MinimumUserProtoWithMaxResources;
import com.lvl6.retrieveutils.MonsterEnhancingForUserRetrieveUtils2;
import com.lvl6.retrieveutils.MonsterEvolvingForUserRetrieveUtils2;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2;
import com.lvl6.retrieveutils.MonsterHealingForUserRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component
@DependsOn("gameServer")
public class HealMonsterController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	protected Locker locker;

	@Autowired
	protected MonsterEnhancingForUserRetrieveUtils2 monsterEnhancingForUserRetrieveUtils;
	@Autowired
	protected MonsterHealingForUserRetrieveUtils2 monsterHealingForUserRetrieveUtils;
	@Autowired
	protected MonsterEvolvingForUserRetrieveUtils2 monsterEvolvingForUserRetrieveUtils;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtils;

	@Autowired
	protected MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils;
	
	@Autowired
	protected MonsterStuffUtils monsterStuffUtils;

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
		HealMonsterRequestProto reqProto = ((HealMonsterRequestEvent) event)
				.getHealMonsterRequestProto();
		log.info(String.format("reqProto=%s", reqProto));

		//get values sent from the client (the request proto)
		MinimumUserProtoWithMaxResources senderResourcesProto = reqProto
				.getSender();
		MinimumUserProto senderProto = senderResourcesProto.getMinUserProto();
		String userId = senderProto.getUserUuid();
		List<UserMonsterHealingProto> umhDelete = reqProto.getUmhDeleteList();
		List<UserMonsterHealingProto> umhUpdate = reqProto.getUmhUpdateList();
		List<UserMonsterHealingProto> umhNew = reqProto.getUmhNewList();
		//positive means refund, negative means charge user
		int cashChange = reqProto.getCashChange();
		int gemCostForHealing = reqProto.getGemCostForHealing();

		boolean isSpeedup = reqProto.getIsSpeedup();
		int gemsForSpeedup = reqProto.getGemsForSpeedup();
		List<UserMonsterCurrentHealthProto> umchpList = reqProto.getUmchpList();
		Map<String, Integer> userMonsterIdToExpectedHealth = new HashMap<String, Integer>();
		List<String> userMonsterIds = monsterStuffUtils.getUserMonsterIds(
				umchpList, userMonsterIdToExpectedHealth);

		int gemCost = gemCostForHealing + gemsForSpeedup;//reqProto.getTotalGemCost();
		Timestamp curTime = new Timestamp((new Date()).getTime());
		int maxCash = senderResourcesProto.getMaxCash();

		Map<String, UserMonsterHealingProto> deleteMap = monsterStuffUtils
				.convertIntoUserMonsterIdToUmhpProtoMap(umhDelete);
		Map<String, UserMonsterHealingProto> updateMap = monsterStuffUtils
				.convertIntoUserMonsterIdToUmhpProtoMap(umhUpdate);
		Map<String, UserMonsterHealingProto> newMap = monsterStuffUtils
				.convertIntoUserMonsterIdToUmhpProtoMap(umhNew);

		log.info(String
				.format("umchpList=%s, userMonsterIdToExpectedHealth=%s, userMonsterIds=%s",
						umchpList, userMonsterIdToExpectedHealth,
						userMonsterIds));

		//set some values to send to the client (the response proto)
		HealMonsterResponseProto.Builder resBuilder = HealMonsterResponseProto
				.newBuilder();
		resBuilder.setSender(senderResourcesProto);
		resBuilder.setStatus(HealMonsterStatus.FAIL_OTHER); //default

		UUID userUuid = null;
		UUID userMonsterUuid = null;
		boolean invalidUuids = true;
		try {
			userUuid = UUID.fromString(userId);

			if (userMonsterIds != null) {
				for (String userMonsterId : userMonsterIds) {
					userMonsterUuid = UUID.fromString(userMonsterId);
				}
			}

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
					"UUID error. incorrect userId=%s, userMonsterIds=%s",
					userId, userMonsterIds), e);
			invalidUuids = true;
		}

		getLocker().lockPlayer(userUuid, this.getClass().getSimpleName());
		try {
			int previousCash = 0;
			int previousGems = 0;
			//get whatever we need from the database
			User aUser = getUserRetrieveUtils().getUserById(userId);
			Map<String, MonsterHealingForUser> alreadyHealing = getMonsterHealingForUserRetrieveUtils()
					.getMonstersForUser(userId);
			Map<String, MonsterEnhancingForUser> alreadyEnhancing = getMonsterEnhancingForUserRetrieveUtils()
					.getMonstersForUser(userId);
			MonsterEvolvingForUser evolution = getMonsterEvolvingForUserRetrieveUtils()
					.getEvolutionForUser(userId);

			//retrieve only the new monsters that will be healed
			Map<String, MonsterForUser> existingUserMonsters = new HashMap<String, MonsterForUser>();
			if (null != newMap && !newMap.isEmpty()) {
				Set<String> newIds = new HashSet<String>();
				newIds.addAll(newMap.keySet());
				existingUserMonsters = getMonsterForUserRetrieveUtils()
						.getSpecificOrAllUserMonstersForUser(userId, newIds);
			}

			boolean legit = checkLegit(resBuilder, aUser, userId, cashChange,
					gemCost, existingUserMonsters, alreadyHealing,
					alreadyEnhancing, deleteMap, updateMap, newMap,
					userMonsterIds, evolution);

			boolean successful = false;
			//first two maps are for different heal monster and speed up heal monster
			Map<String, Integer> money = new HashMap<String, Integer>();
			Map<String, Integer> moneyForHealSpeedup = new HashMap<String, Integer>();
			//this map is both combined
			Map<String, Integer> changeMap = new HashMap<String, Integer>();
			if (legit) {
				previousCash = aUser.getCash();
				previousGems = aUser.getGems();

				//from HealMonsterWaitTimeCompleteController
				userMonsterIdToExpectedHealth = getValidEntries(userMonsterIds,
						userMonsterIdToExpectedHealth);
				successful = writeChangesToDb(aUser, userId, cashChange,
						gemCost, deleteMap, updateMap, newMap,
						gemCostForHealing, money, userMonsterIds,
						userMonsterIdToExpectedHealth, isSpeedup,
						gemsForSpeedup, moneyForHealSpeedup, changeMap, maxCash);
			}

			if (successful) {
				resBuilder.setStatus(HealMonsterStatus.SUCCESS);
			}

			HealMonsterResponseEvent resEvent = new HealMonsterResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setHealMonsterResponseProto(resBuilder.build());
			server.writeEvent(resEvent);

			if (successful) {
				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = MiscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								aUser, null, null);
				resEventUpdate.setTag(event.getTag());
				server.writeEvent(resEventUpdate);
				//TODO: WRITE TO monster healing HISTORY
				writeToUserCurrencyHistory(aUser, changeMap, money, curTime,
						previousCash, previousGems, deleteMap, updateMap,
						newMap, moneyForHealSpeedup);
			}
		} catch (Exception e) {
			log.error("exception in HealMonsterController processEvent", e);
			//don't let the client hang
			try {
				resBuilder.setStatus(HealMonsterStatus.FAIL_OTHER);
				HealMonsterResponseEvent resEvent = new HealMonsterResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setHealMonsterResponseProto(resBuilder.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in HealMonsterController processEvent", e);
			}
		} finally {
			getLocker().unlockPlayer(userUuid, this.getClass().getSimpleName());
		}
	}

	/*
	 * Return true if user request is valid; false otherwise and set the
	 * builder status to the appropriate value. delete, update, new maps
	 * MIGHT BE MODIFIED.
	 * 
	 * from HealMonsterWaitTimeComplete controller logic
	 * @healedUp MIGHT ALSO BE MODIFIED.
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
	 * Ex. If user wants to delete a monster, 'A', that isn't healing, along with some
	 * monsters already healing, 'B', i.e. wants to delete (A, B), then only the valid
	 * monster(s), 'B', will be deleted. Same logic with update and new. 
	 * 
	 */
	private boolean checkLegit(Builder resBuilder, User u, String userId,
			int cashChange, int gemCost,
			Map<String, MonsterForUser> existingUserMonsters,
			Map<String, MonsterHealingForUser> alreadyHealing,
			Map<String, MonsterEnhancingForUser> alreadyEnhancing,
			Map<String, UserMonsterHealingProto> deleteMap,
			Map<String, UserMonsterHealingProto> updateMap,
			Map<String, UserMonsterHealingProto> newMap, List<String> healedUp,
			MonsterEvolvingForUser evolution) {
		if (null == u) {
			log.error("unexpected error: user is null. user=" + u
					+ "\t deleteMap=" + deleteMap + "\t updateMap=" + updateMap
					+ "\t newMap=" + newMap);
			return false;
		}

		//CHECK MONEY
		int userGems = u.getGems();
		if (gemCost > userGems) {
			log.error("user error: user does not have enough gems. userGems="
					+ userGems + "\t gemCost=" + gemCost + "\t user=" + u);
			resBuilder.setStatus(HealMonsterStatus.FAIL_INSUFFICIENT_FUNDS);
			return false;
		}

		// scenario can be user has insufficient cash but has enough
		// gems to cover the difference
		int userCash = u.getCash();
		//since negative cashChange means charge, then negative of that is
		//the cost. If cashChange is positive, meaning refund, user will always
		//have more than a negative amount
		int cashCost = -1 * cashChange;
		if (gemCost == 0 && cashCost > userCash) {
			//user doesn't have enough cash and is not paying gems.

			log.error("user error: user has too little cash and not using gems. userCash="
					+ userCash + "\t cashCost=" + cashCost + "\t user=" + u);
			resBuilder.setStatus(HealMonsterStatus.FAIL_INSUFFICIENT_FUNDS);
			return false;
		}
		//if user has insufficient cash but gems is nonzero, take it on full faith
		//client calculated things correctly

		//retain only the userMonsters, the client sent, that are in healing
		boolean keepThingsInDomain = true;
		boolean keepThingsNotInDomain = false;
		Set<String> alreadyHealingIds = alreadyHealing.keySet();
		monsterStuffUtils.retainValidMonsters(alreadyHealingIds, deleteMap,
				keepThingsInDomain, keepThingsNotInDomain);
		monsterStuffUtils.retainValidMonsters(alreadyHealingIds, updateMap,
				keepThingsInDomain, keepThingsNotInDomain);

		//retain only the userMonsters, the client sent, that are in the db
		Set<String> existingIds = existingUserMonsters.keySet();
		monsterStuffUtils.retainValidMonsters(existingIds, newMap,
				keepThingsInDomain, keepThingsNotInDomain);

		//retain only the userMonsters, the client sent, that are not in enhancing
		keepThingsInDomain = false;
		keepThingsNotInDomain = true;
		Set<String> alreadyEnhancingIds = alreadyEnhancing.keySet();
		monsterStuffUtils.retainValidMonsters(alreadyEnhancingIds, newMap,
				keepThingsInDomain, keepThingsNotInDomain);

		//retain only the userMonsters, the client sent, that are not in evolutions
		Set<String> idsInEvolutions = monsterStuffUtils
				.getUserMonsterIdsUsedInEvolution(evolution, null);
		monsterStuffUtils.retainValidMonsters(idsInEvolutions, newMap,
				keepThingsInDomain, keepThingsNotInDomain);

		//FROM HealMonsterWaitTimeComplete CONTROLLER
		//modify healedUp to contain only those that exist
		monsterStuffUtils.retainValidMonsterIds(alreadyHealingIds, healedUp);

		return true;
	}

	//only the entries in the map that have their key in validIds will be kept  
	//idsToValues contains keys that are in validIds and some that aren't
	private Map<String, Integer> getValidEntries(List<String> validIds,
			Map<String, Integer> idsToValues) {

		Map<String, Integer> returnMap = new HashMap<String, Integer>();

		for (String id : validIds) {
			int value = idsToValues.get(id);
			returnMap.put(id, value);
		}
		return returnMap;
	}

	private boolean writeChangesToDb(User u, String uId, int cashChange,
			int gemCost, Map<String, UserMonsterHealingProto> protoDeleteMap,
			Map<String, UserMonsterHealingProto> protoUpdateMap,
			Map<String, UserMonsterHealingProto> protoNewMap,
			int gemCostForHealing, Map<String, Integer> moneyForHealing,
			List<String> userMonsterIds,
			Map<String, Integer> userMonsterIdsToHealths, boolean isSpeedup,
			int gemsForSpeedup, Map<String, Integer> moneyForSpeedup,
			Map<String, Integer> changeMap, int maxCash) {

		log.info(String.format("cashChange=%s", cashChange));
		log.info(String.format("gemCost=%s", gemCost));
		log.info(String.format("deleteMap=%s", protoDeleteMap));
		log.info(String.format("updateMap=%s", protoUpdateMap));
		log.info(String.format("newMap=%s", protoNewMap));
		log.info(String.format("gemCostForHealing=%s", gemCostForHealing));
		log.info(String.format("isSpeedup=%s", isSpeedup));
		log.info(String.format("gemsForSpedup%s", gemsForSpeedup));

		int oilChange = 0;
		int gemChange = -1 * gemCost;

		//if user is getting cash back, make sure it doesn't exceed his limit
		if (cashChange > 0) {
			int curCash = Math.min(u.getCash(), maxCash); //in case user's cash is more than maxCash.
			log.info(String.format("curCash=%s", curCash));
			int maxCashUserCanGain = maxCash - curCash;
			log.info(String.format("maxCashUserCanGain=%s", maxCashUserCanGain));
			cashChange = Math.min(cashChange, maxCashUserCanGain);
		}

		//if checks are here because the changes are 0 if the HealMonsterWaitTimeComplete
		//feature part of this controller is being processed or if user reached max resources
		if (0 != cashChange || 0 != gemChange) {

			//CHARGE THE USER
			log.info(String
					.format("user before funds change. u=%s, cashChange=%s, oilChange=%s, gemChange=%s",
							u, cashChange, oilChange, gemChange));

			int num = u.updateRelativeCashAndOilAndGems(cashChange, oilChange,
					gemChange);
			log.info(String.format("user after funds change. u=%s", u));
			if (num != 1) {
				String preface = "problem with updating user's funds.";
				log.error(String.format(
						"%s cashChange=%s, gemCost=%s, user=%s, numUpdated=%s",
						preface, cashChange, gemCost, u, num));
				return false;
			} else {
				//things went ok
				if (0 != cashChange) {
					moneyForHealing.put(MiscMethods.cash, cashChange);
					changeMap.put(MiscMethods.cash, cashChange);
				}
				if (0 != gemCostForHealing) {
					moneyForHealing.put(MiscMethods.gems, gemCostForHealing);
				}
				if (0 != gemsForSpeedup) {
					moneyForSpeedup.put(MiscMethods.gems, gemsForSpeedup);
				}
				if (0 != gemCost) {
					changeMap.put(MiscMethods.gems, gemChange);
				}

			}
		}

		//delete the selected monsters from  the healing table, if there are
		//any to delete
		if (!protoDeleteMap.isEmpty()) {
			List<String> deleteIds = new ArrayList<String>(
					protoDeleteMap.keySet());
			int num = DeleteUtils.get().deleteMonsterHealingForUser(uId,
					deleteIds);
			log.info(String
					.format("deleted monster healing rows. numDeleted=%s \t protoDeleteMap=%s",
							num, protoDeleteMap));
		}

		//convert protos to java counterparts
		List<MonsterHealingForUser> updateList = monsterStuffUtils
				.convertToMonsterHealingForUser(uId, protoUpdateMap);
		List<MonsterHealingForUser> newList = monsterStuffUtils
				.convertToMonsterHealingForUser(uId, protoNewMap);

		List<MonsterHealingForUser> updateAndNew = new ArrayList<MonsterHealingForUser>();
		updateAndNew.addAll(updateList);
		updateAndNew.addAll(newList);

		log.info(String.format("updated and new monsters for healing: %s",
				updateAndNew));

		//client could have deleted one item from two item queue, or added at least one item
		if (!updateAndNew.isEmpty()) {
			//update and insert the new monsters
			int num = UpdateUtils.get().updateUserMonsterHealing(uId,
					updateAndNew);
			log.info(String
					.format("updated monster healing rows. numUpdated/inserted=%s",
							num));
		}

		//don't unequip the monsters
		//	  //for the new monsters, ensure that the monsters are "unequipped"
		//	  if (!protoNewMap.isEmpty()) {
		//	  	//for the new monsters, set the teamSlotNum to 0
		//	  	int size = protoNewMap.size();
		//	  	List<Long> userMonsterIdList = new ArrayList<Long>(protoNewMap.keySet());
		//	  	List<Integer> teamSlotNumList = Collections.nCopies(size, 0);
		//	  	num = UpdateUtils.get().updateNullifyUserMonstersTeamSlotNum(userMonsterIdList, teamSlotNumList);
		//	  	log.info("updated user monster rows. numUpdated=" + num);
		//	  }

		//LOGIC FROM HealMonsterWaitTimeCompleteController
		if (null != userMonsterIdsToHealths
				&& !userMonsterIdsToHealths.isEmpty()) {
			//HEAL THE MONSTER
			int num = UpdateUtils.get().updateUserMonstersHealth(
					userMonsterIdsToHealths);
			log.info(String.format("num updated=%s", num));
		}
		//should always execute, but who knows...
		if (null != userMonsterIds && !userMonsterIds.isEmpty()) {
			//delete the selected monsters from  the healing table
			int num = DeleteUtils.get().deleteMonsterHealingForUser(uId,
					userMonsterIds);
			log.info(String
					.format("deleted monster healing rows. numDeleted=%s \t userMonsterIds=%s",
							num, userMonsterIds));
		}
		return true;
	}

	private void writeToUserCurrencyHistory(User aUser,
			Map<String, Integer> changeMap,
			Map<String, Integer> moneyForHealing, Timestamp curTime,
			int previousCash, int previousGems,
			Map<String, UserMonsterHealingProto> deleteMap,
			Map<String, UserMonsterHealingProto> updateMap,
			Map<String, UserMonsterHealingProto> newMap,
			Map<String, Integer> moneyForHealSpeedup) {

		String userId = aUser.getId();
		Map<String, Integer> previousCurrencies = new HashMap<String, Integer>();
		Map<String, Integer> currentCurrencies = new HashMap<String, Integer>();
		Map<String, String> reasonsForChanges = new HashMap<String, String>();
		Map<String, String> details = new HashMap<String, String>();

		StringBuilder reasonForChange = new StringBuilder();
		reasonForChange
				.append(ControllerConstants.UCHRFC__HEAL_MONSTER_OR_SPED_UP_HEALING);
		String gems = MiscMethods.gems;
		String cash = MiscMethods.cash;

		StringBuilder detailSb = new StringBuilder();
		if (!moneyForHealing.isEmpty()) {
			detailSb.append("heal monster info: ");
		}
		if (moneyForHealing.containsKey(gems)) {
			detailSb.append("gemChange=");
			detailSb.append(moneyForHealing.get(gems));
			detailSb.append(" ");
		}
		if (moneyForHealing.containsKey(cash)) {
			detailSb.append("cashChange=");
			detailSb.append(moneyForHealing.get(cash));
			detailSb.append(" ");
		}
		//could just individually add in the ids or something else, but eh, lazy
		//not really necessary to record ids, but maybe more info is better
		if (null != deleteMap && !deleteMap.isEmpty()) {
			detailSb.append("deleted=");
			detailSb.append(deleteMap.keySet());
			detailSb.append(" ");
		}
		if (null != updateMap && !updateMap.isEmpty()) {
			detailSb.append("updated=");
			detailSb.append(updateMap.keySet());
			detailSb.append(" ");
		}
		if (null != newMap && !newMap.isEmpty()) {
			detailSb.append("new=");
			detailSb.append(newMap.keySet());
			detailSb.append(" ");
		}

		if (!moneyForHealSpeedup.isEmpty()) {
			detailSb.append("sped up healing info: ");
		}
		if (moneyForHealSpeedup.containsKey(gems)) {
			detailSb.append("gemChange= ");
			detailSb.append(moneyForHealSpeedup.get(gems));
		}

		previousCurrencies.put(gems, previousGems);
		previousCurrencies.put(cash, previousCash);
		currentCurrencies.put(gems, aUser.getGems());
		currentCurrencies.put(cash, aUser.getCash());
		reasonsForChanges.put(gems, reasonForChange.toString());
		reasonsForChanges.put(cash, reasonForChange.toString());
		details.put(gems, detailSb.toString());
		details.put(cash, detailSb.toString());

		MiscMethods.writeToUserCurrencyOneUser(userId, curTime, changeMap,
				previousCurrencies, currentCurrencies, reasonsForChanges,
				details);

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
