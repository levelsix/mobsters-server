package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.protobuf.ByteString;
import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.EndPvpBattleRequestEvent;
import com.lvl6.events.response.EndPvpBattleResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.Clan;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.PvpBattleHistoryDao;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.UserCurrencyHistoryDao;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.PvpBattleCountForUserPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.PvpBattleHistoryPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureForUserPojo;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.BattleProto.PvpHistoryProto;
import com.lvl6.proto.EventPvpProto.EndPvpBattleRequestProto;
import com.lvl6.proto.EventPvpProto.EndPvpBattleResponseProto;
import com.lvl6.proto.EventPvpProto.StructStolen;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.proto.UserProto.MinimumUserProtoWithMaxResources;
import com.lvl6.pvp.HazelcastPvpUtil;
import com.lvl6.pvp.PvpBattleOutcome;
import com.lvl6.pvp.PvpUser;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2;
import com.lvl6.retrieveutils.PvpBattleForUserRetrieveUtils2;
import com.lvl6.retrieveutils.PvpBattleHistoryRetrieveUtil2;
import com.lvl6.retrieveutils.PvpLeagueForUserRetrieveUtil2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.daos.PvpBattleCountForUserDao2;
import com.lvl6.retrieveutils.daos.StructureForUserDao2;
import com.lvl6.retrieveutils.rarechange.MonsterLevelInfoRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.PvpLeagueRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ServerToggleRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.EndPvpBattleAction;
import com.lvl6.server.controller.utils.HistoryUtils;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.server.controller.utils.PvpBattleCountUtils;
import com.lvl6.server.controller.utils.ResourceUtil;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.TimeUtils;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

@Component

public class EndPvpBattleController extends EventController {

	private static Logger log = LoggerFactory.getLogger(EndPvpBattleController.class);

	@Autowired
	protected HazelcastPvpUtil hazelcastPvpUtil;

	@Autowired
	protected Locker locker;

	@Autowired
	protected TimeUtils timeUtil;

	@Autowired
	protected ResourceUtil resourceUtil;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	protected PvpBattleForUserRetrieveUtils2 pvpBattleForUserRetrieveUtil;

	@Autowired
	protected PvpLeagueForUserRetrieveUtil2 pvpLeagueForUserRetrieveUtil;

	@Autowired
	protected PvpBattleHistoryRetrieveUtil2 pvpBattleHistoryRetrieveUtil2;

	@Autowired
	protected ClanRetrieveUtils2 clanRetrieveUtil;

	@Autowired
	protected MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtil;

	@Autowired
	protected MonsterStuffUtils monsterStuffUtils;

	@Autowired
	protected PvpLeagueRetrieveUtils pvpLeagueRetrieveUtils;

	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;

	@Autowired
	protected ServerToggleRetrieveUtils serverToggleRetrieveUtil;

	@Autowired
	protected MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtil;

	@Autowired
	protected MiscMethods miscMethods;

	@Autowired
	protected InsertUtil insertUtil;

	@Autowired
	protected UpdateUtil updateUtil;

	@Autowired
	protected TimeUtils timeUtils;

	@Autowired
	protected StructureForUserDao2 sfuDao;

	@Autowired
	protected HistoryUtils historyUtils;

	@Autowired
	protected PvpBattleHistoryDao pbhDao;

	@Autowired
	protected UserCurrencyHistoryDao uchDao;
	
	@Autowired
	protected PvpBattleCountForUserDao2 pbcfuDao;

	@Autowired
	protected PvpBattleCountUtils pbcUtils;



	public EndPvpBattleController() {

	}

	@Override
	public RequestEvent createRequestEvent() {
		return new EndPvpBattleRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_END_PVP_BATTLE_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		EndPvpBattleRequestProto reqProto = ((EndPvpBattleRequestEvent) event)
				.getEndPvpBattleRequestProto();
		log.info("reqProto={}", reqProto);

		//get values sent from the client (the request proto)
		MinimumUserProtoWithMaxResources senderProtoMaxResources = reqProto
				.getSender();
		MinimumUserProto senderProto = senderProtoMaxResources
				.getMinUserProto();
		String attackerId = senderProto.getUserUuid();
		String defenderId = reqProto.getDefenderUuid();
		boolean attackerAttacked = reqProto.getUserAttacked();
		boolean attackerWon = reqProto.getUserWon();
		int oilStolenFromStorage = reqProto.getOilStolenFromStorage(); //non negative
		int cashStolenFromStorage = reqProto.getCashStolenFromStorage(); // non negative
		int oilStolenFromGenerators = reqProto.getOilStolenFromGenerator();
		int cashStolenFromGenerators = reqProto.getCashStolenFromGenerator();
		float nuPvpDmgMultiplier = reqProto.getNuPvpDmgMultiplier();
		List<Integer> monsterDropIds = reqProto.getMonsterDropIdsList();
		int attackerMaxOil = senderProtoMaxResources.getMaxOil();
		int attackerMaxCash = senderProtoMaxResources.getMaxCash();
		Timestamp curTime = new Timestamp(reqProto.getClientTime());
		Date curDate = new Date(curTime.getTime());
		ByteString replay = reqProto.getReplay();
		List<StructStolen> listOfGenerators = reqProto.getStructStolenList();

		if (!attackerWon && (oilStolenFromStorage + oilStolenFromGenerators) != 0) {
			log.error("oilStolen should be 0 since attacker lost!\t client sent oilFromStorage"
					+ " {} and oilFromGenerator {}", oilStolenFromStorage, oilStolenFromGenerators);
			oilStolenFromStorage = 0;
			oilStolenFromGenerators = 0;
		}
		if (!attackerWon && (cashStolenFromStorage + cashStolenFromGenerators) != 0) {
			log.error("cashStolen should be 0 since attacker lost!\t client sent cashFromStorage"
					+ "{} and cashFromGenerator{}", cashStolenFromStorage, cashStolenFromGenerators);
			cashStolenFromStorage = 0;
			cashStolenFromGenerators = 0;
		}

		//set some values to send to the client (the response proto)
		EndPvpBattleResponseProto.Builder resBuilder = EndPvpBattleResponseProto
				.newBuilder();
		resBuilder.setSender(senderProtoMaxResources);
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER); //default
		resBuilder.setAttackerAttacked(attackerAttacked);
		resBuilder.setAttackerWon(attackerWon);
		EndPvpBattleResponseEvent resEvent = new EndPvpBattleResponseEvent(
				attackerId);
		resEvent.setTag(event.getTag());
		
		if(reqProto.getClientTime() == 0) {
			resBuilder.setStatus(ResponseStatus.FAIL_CLIENT_TIME_NOT_SENT);
			log.error("clientTime not sent");
			resEvent.setResponseProto(resBuilder.build());
			resEvent.setTag(event.getTag());
			responses.normalResponseEvents().add(resEvent);
			return;
		}
		
		if(timeUtils.numMinutesDifference(new Date(reqProto.getClientTime()), new Date()) > 
		ControllerConstants.CLIENT_TIME_MINUTES_CONSTANT_CHECK) {
			resBuilder.setStatus(ResponseStatus.FAIL_TIME_OUT_OF_SYNC);
			log.error("time is out of sync > 2 hrs for userId {}", senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			resEvent.setTag(event.getTag());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		boolean invalidUuids = true;
		UUID attackerUuid = null;
		UUID defenderUuid = null;
		try {
			attackerUuid = UUID.fromString(attackerId);
			if (null != defenderId && !defenderId.isEmpty()) {
				defenderUuid = UUID.fromString(defenderId);
			} else {
				defenderId = "";
			}
			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
					"UUID error. incorrect attackerId=%s, defenderId=%s",
					attackerId, defenderId), e);
			invalidUuids = true;
		}

		if (invalidUuids) {
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER); //default
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		boolean gotLock = false;
		try {
			//NEED TO LOCK BOTH PLAYERS, well need to lock defender because defender can be online,
			//Lock attacker because someone might be attacking him while attacker is attacking defender?
			if (null != defenderUuid) {
				gotLock = locker.lockPlayers(defenderUuid, attackerUuid,
						this.getClass().getSimpleName());
				log.info("locked defender and attacker");
			} else {
				//ONLY ATTACKER IF DEFENDER IS FAKE
				gotLock = locker.lockPlayer(attackerUuid,
						this.getClass().getSimpleName());
				log.info("locked attacker");
			}

			EndPvpBattleAction epba = new EndPvpBattleAction(attackerId,
					defenderId, attackerAttacked, attackerWon, oilStolenFromStorage,
					cashStolenFromStorage, nuPvpDmgMultiplier, monsterDropIds,
					attackerMaxOil, attackerMaxCash, reqProto.getClientTime(),
					curDate, curTime, replay, resourceUtil, userRetrieveUtil,
					pvpBattleForUserRetrieveUtil,
					pvpLeagueForUserRetrieveUtil, clanRetrieveUtil,
					monsterForUserRetrieveUtil, monsterStuffUtils,
					pvpLeagueRetrieveUtils, createInfoProtoUtils,
					serverToggleRetrieveUtil, monsterLevelInfoRetrieveUtil,
					miscMethods, hazelcastPvpUtil, timeUtil, insertUtil, updateUtil,
					listOfGenerators, oilStolenFromGenerators, cashStolenFromGenerators,
					sfuDao, historyUtils, pbhDao, uchDao, pbcfuDao, timeUtils,
					pbcUtils);

			epba.execute(resBuilder);

			Map<String, Map<String, Integer>> changeMap = new HashMap<String, Map<String, Integer>>();
			Map<String, Map<String, Integer>> previousCurrencyMap = new HashMap<String, Map<String, Integer>>();

			PvpBattleHistoryPojo battleJustEnded = epba.getPbh();

			if (ResponseStatus.SUCCESS.equals(resBuilder.getStatus())) {
				List<PvpHistoryProto> historyProtoList = null;
				if (null != battleJustEnded) {
					//Note: no protos for fake defenders are created
					historyProtoList = createInfoProtoUtils
							.createAttackedOthersPvpHistoryProto(attackerId,
									epba.getIdToUser(),
									Collections.singletonList(battleJustEnded));
				}
				if (null != historyProtoList && !historyProtoList.isEmpty()) {
					PvpHistoryProto attackedOtherHistory = historyProtoList
							.get(0);
					log.info("attackedOtherHistory {}", attackedOtherHistory);
					resBuilder.setBattleThatJustEnded(attackedOtherHistory);
				}
				List<StructureForUserPojo> updateList = epba.getUpdateList();
				if(updateList != null && !updateList.isEmpty()) {
					resBuilder.addAllUpdatedUserStructs(createInfoProtoUtils
							.createStructStolenFromGenerators(updateList));
				}
			}

			//respond to the attacker
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);

			//TODO: NEED TO REFACTOR CONSTRUCTING THE PvpBattleHistoryProto
			if (ResponseStatus.SUCCESS.equals(resBuilder.getStatus())) {
				User attacker = epba.getAttacker();
				User defender = epba.getDefender();
				//respond to the defender
				if (null != epba.getDefender()) {
					if (null != battleJustEnded) {
						PvpHistoryProto php = createPvpProto(attacker,
								defender, curDate, battleJustEnded,
								epba.getCashStolenFromStorage(),
								epba.getCashStolenFromGenerators(),
								epba.getOilStolenFromStorage(),
								epba.getOilStolenFromGenerators());
						log.info("gotAttackedHistory {}", php);
						resBuilder.setBattleThatJustEnded(php);
					}

					EndPvpBattleResponseEvent resEventDefender = new EndPvpBattleResponseEvent(
							defenderId);
					resEventDefender.setTag(0);
					resEventDefender.setResponseProto(resBuilder
							.build());
					responses.apnsResponseEvents().add(resEventDefender);
				}
				//regardless of whether the attacker won, his elo will change
				UpdateClientUserResponseEvent resEventUpdate = miscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								attacker, epba.getAttackerPlfu(), null);
				resEventUpdate.setTag(event.getTag());
				responses.normalResponseEvents().add(resEventUpdate);

				//defender's elo and resources changed only if attacker won, and defender is real
				if (attackerWon && null != defender) {
					UpdateClientUserResponseEvent resEventUpdateDefender = miscMethods
							.createUpdateClientUserResponseEventAndUpdateLeaderboard(
									defender, epba.getDefenderPlfu(), null);
					resEventUpdateDefender.setTag(0);
					responses.normalResponseEvents().add(resEventUpdateDefender);
				}

//				if (attackerWon) {
//					//TRACK CURRENCY HISTORY, resource changes only if attacker won
//					writeToUserCurrencyHistory(attackerId, attacker,
//							defenderId, defender, attackerWon, curTime,
//							changeMap, previousCurrencyMap);
//				}
			}

		} catch (Exception e) {
			log.error("exception in EndPvpBattleController processEvent", e);
			//don't let the client hang
			try {
				resEvent.setResponseProto(resBuilder.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in EndPvpBattleController processEvent",
						e);
			}

		} finally {
			if (gotLock) {
				if (null != defenderUuid) {
					locker.unlockPlayers(defenderUuid, attackerUuid,
							this.getClass().getSimpleName());
					log.info("unlocked defender and attacker");
				} else {
					locker.unlockPlayer(attackerUuid,
							this.getClass().getSimpleName());
					log.info("unlocked attacker");
				}
			}
		}
	}

//	private void writeToUserCurrencyHistory(String attackerId, User attacker,
//			String defenderId, User defender, boolean attackerWon,
//			Timestamp curTime, Map<String, Map<String, Integer>> changeMap,
//			Map<String, Map<String, Integer>> previousCurrencyMap) {
//
//		Map<String, Map<String, Integer>> currentCurrencyMap = new HashMap<String, Map<String, Integer>>();
//		Map<String, Map<String, String>> changeReasonsMap = new HashMap<String, Map<String, String>>();
//		Map<String, Map<String, String>> detailsMap = new HashMap<String, Map<String, String>>();
//		String reasonForChange = ControllerConstants.UCHRFC__PVP_BATTLE;
//		String oil = MiscMethods.oil;
//		String cash = MiscMethods.cash;
//
//		//reasons
//		Map<String, String> reasonMap = new HashMap<String, String>();
//		reasonMap.put(cash, reasonForChange);
//		reasonMap.put(oil, reasonForChange);
//		changeReasonsMap.put(attackerId, reasonMap);
//		changeReasonsMap.put(defenderId, reasonMap);
//
//		//attacker stuff
//		//current currency stuff
//		int attackerCash = attacker.getCash();
//		int attackerOil = attacker.getOil();
//		Map<String, Integer> attackerCurrency = new HashMap<String, Integer>();
//		attackerCurrency.put(cash, attackerCash);
//		attackerCurrency.put(oil, attackerOil);
//		//aggregate currency
//		currentCurrencyMap.put(attackerId, attackerCurrency);
//
//		//details
//		StringBuilder attackerDetailsSb = new StringBuilder();
//		if (attackerWon) {
//			attackerDetailsSb.append("beat ");
//		} else {
//			attackerDetailsSb.append("lost to ");
//		}
//		attackerDetailsSb.append(defenderId);
//		String attackerDetails = attackerDetailsSb.toString();
//		Map<String, String> attackerDetailsMap = new HashMap<String, String>();
//		attackerDetailsMap.put(cash, attackerDetails);
//		attackerDetailsMap.put(oil, attackerDetails);
//		//aggregate details
//		detailsMap.put(attackerId, attackerDetailsMap);
//
//		//defender stuff
//		if (null != defender) {
//			//current currency stuff
//			int defenderCash = defender.getCash();
//			int defenderOil = defender.getOil();
//			Map<String, Integer> defenderCurrency = new HashMap<String, Integer>();
//			defenderCurrency.put(cash, defenderCash);
//			defenderCurrency.put(oil, defenderOil);
//			//aggregate currency
//			currentCurrencyMap.put(defenderId, defenderCurrency);
//
//			//details
//			StringBuilder defenderDetailsSb = new StringBuilder();
//			if (attackerWon) {
//				defenderDetailsSb.append("lost to ");
//			} else {
//				defenderDetailsSb.append("beat ");
//			}
//			defenderDetailsSb.append(attackerId);
//			String defenderDetails = defenderDetailsSb.toString();
//			Map<String, String> defenderDetailsMap = new HashMap<String, String>();
//			defenderDetailsMap.put(cash, defenderDetails);
//			defenderDetailsMap.put(oil, defenderDetails);
//			//aggregate details
//			detailsMap.put(defenderId, defenderDetailsMap);
//
//		}
//
//		List<String> userIds = new ArrayList<String>();
//		userIds.add(attackerId);
//
//		if (null != defender && !defenderId.isEmpty()) {
//			userIds.add(defenderId);
//		}
//
//		miscMethods.writeToUserCurrencyUsers(userIds, curTime, changeMap,
//				previousCurrencyMap, currentCurrencyMap, changeReasonsMap,
//				detailsMap);
//
//	}

	//TODO: CLEAN UP: copied from SetPvpBattleHistoryAction, pasted, and modified
	private PvpHistoryProto createPvpProto(User attacker, User defender,
			Date curDate, PvpBattleHistoryPojo gotAttackedHistory,
			int cashStolenFromStorage, int cashStolenFromGenerators,
			int oilStolenFromStorage, int oilStolenFromGenerators) {
		if (null == defender) {
			return null;
		}

		///need to get the elo
		String attackerId = attacker.getId();
		String defenderId = defender.getId();
		List<String> userIds = new ArrayList<String>();
		userIds.add(attackerId);
		userIds.add(defenderId);

		Map<String, PvpUser> idsToPvpUsers = hazelcastPvpUtil
				.getPvpUsers(userIds);
		PvpUser attackerPu = idsToPvpUsers.get(attackerId);
		PvpUser defenderPu = idsToPvpUsers.get(defenderId);

		int attackerElo = attackerPu.getElo();
		int defenderElo = defenderPu.getElo();

		//hopefully this gets this pvpHistory just created
		List<PvpBattleHistoryPojo> gotAttackedHistoryList = Collections
				.singletonList(gotAttackedHistory);

		log.info("gotAttackedHistoryList {}", gotAttackedHistoryList);

		String attackerClanId = attacker.getClanId();

		//need clan info for attacker
		Map<String, Clan> attackerIdsToClans = new HashMap<String, Clan>();

		if (null != attackerClanId && !attackerClanId.isEmpty()) {
			Clan attackerClan = clanRetrieveUtil.getClanWithId(attackerClanId);
			attackerIdsToClans.put(attackerId, attackerClan);
		}

		Map<String, User> idsToAttackers = Collections.singletonMap(attackerId,
				attacker);

		//need the monsters for the attacker
		Map<String, List<MonsterForUser>> userIdsToUserMonsters = calculateAttackerMonsters(attackerId);
		
		//retrieve battleCounts
		List<String> defenderIds = new ArrayList<String>();
		defenderIds.add(defenderId);
		List<PvpBattleCountForUserPojo> battleCounts = pbcfuDao.getPvpBattleCountBetweenUsers(attackerId, defenderIds);

		//need the drop rates
		Map<String, Map<String, Integer>> userIdToUserMonsterIdToDroppedId = monsterStuffUtils
				.calculatePvpDrops(userIdsToUserMonsters, monsterLevelInfoRetrieveUtil, attacker.getId(), 
						battleCounts);

		//need to calculate the resources defender can steal
		PvpBattleOutcome potentialResult = new PvpBattleOutcome(defender,
				defenderElo, attacker, attackerElo, serverToggleRetrieveUtil, battleCounts);

		Map<String, Integer> attackerIdsToProspectiveCashWinnings = Collections
				.singletonMap(attackerId,
						potentialResult.getUnsignedCashAttackerWins());
		Map<String, Integer> attackerIdsToProspectiveOilWinnings = Collections
				.singletonMap(attackerId,
						potentialResult.getUnsignedOilAttackerWins());
		Map<String, Integer> attackerIdsToCashStolenFromStorage = Collections.
				singletonMap(attackerId, cashStolenFromStorage);
		Map<String, Integer> attackerIdsToCashStolenFromGenerators = Collections.
				singletonMap(attackerId, cashStolenFromGenerators);
		Map<String, Integer> attackerIdsToOilStolenFromStorage = Collections.
				singletonMap(attackerId, oilStolenFromStorage);
		Map<String, Integer> attackerIdsToOilStolenFromGenerators = Collections.
				singletonMap(attackerId, oilStolenFromGenerators);


		//this PvpHistoryProto contains information for a person who
		//is going to attack
		List<PvpHistoryProto> historyProtoList = createInfoProtoUtils
				.createGotAttackedPvpHistoryProto(gotAttackedHistoryList,
						idsToAttackers, attackerIdsToClans,
						userIdsToUserMonsters,
						userIdToUserMonsterIdToDroppedId,
						attackerIdsToProspectiveCashWinnings,
						attackerIdsToProspectiveOilWinnings,
						attackerIdsToCashStolenFromStorage, attackerIdsToCashStolenFromGenerators,
						attackerIdsToOilStolenFromStorage, attackerIdsToOilStolenFromGenerators);

		if (null != historyProtoList && !historyProtoList.isEmpty()) {
			PvpHistoryProto pp = historyProtoList.get(0);
			return pp;
		}

		return null;
	}

	private Map<String, List<MonsterForUser>> calculateAttackerMonsters(
			String attackerId) {
		List<String> attackerIds = Collections.singletonList(attackerId);
		//need monsters for attacker
		Map<String, Map<String, MonsterForUser>> userIdsToMfuIdsToMonsters = monsterForUserRetrieveUtil
				.getCompleteMonstersForUser(attackerIds);

		Map<String, MonsterForUser> attackerIdToMonsters = userIdsToMfuIdsToMonsters
				.get(attackerId);

		//try to select at most 3 monsters for this user
		List<MonsterForUser> attackerMonsters = selectMonstersForUser(attackerIdToMonsters);

		Map<String, List<MonsterForUser>> userIdsToUserMonsters = Collections
				.singletonMap(attackerId, attackerMonsters);
		return userIdsToUserMonsters;
	}

	private List<MonsterForUser> selectMonstersForUser(
			Map<String, MonsterForUser> mfuIdsToMonsters) {

		//get all the monsters the user has on a team (at the moment, max is 3)
		List<MonsterForUser> defenderMonsters = getEquippedMonsters(mfuIdsToMonsters);

		if (defenderMonsters.size() < 3) {
			//need more monsters so select them randomly, fill up "defenderMonsters" list
			getRandomMonsters(mfuIdsToMonsters, defenderMonsters);
		}

		if (defenderMonsters.size() > 3) {
			//only get three monsters
			defenderMonsters = defenderMonsters.subList(0, 3);
		}

		return defenderMonsters;
	}

	private List<MonsterForUser> getEquippedMonsters(
			Map<String, MonsterForUser> userMonsters) {
		List<MonsterForUser> equipped = new ArrayList<MonsterForUser>();

		for (MonsterForUser mfu : userMonsters.values()) {
			if (mfu.getTeamSlotNum() <= 0) {
				//only want equipped monsters
				continue;
			}
			equipped.add(mfu);

		}
		return equipped;
	}

	private void getRandomMonsters(
			Map<String, MonsterForUser> possibleMonsters,
			List<MonsterForUser> defenderMonsters) {

		Map<String, MonsterForUser> possibleMonstersTemp = new HashMap<String, MonsterForUser>(
				possibleMonsters);

		//remove the defender monsters from possibleMonstersTemp, since defenderMonsters
		//were already selected from possibleMonsters
		for (MonsterForUser m : defenderMonsters) {
			String mfuId = m.getId();

			possibleMonstersTemp.remove(mfuId);
		}

		int amountLeftOver = possibleMonstersTemp.size();
		int amountNeeded = 3 - defenderMonsters.size();

		if (amountLeftOver < amountNeeded) {
			defenderMonsters.addAll(possibleMonstersTemp.values());
			return;
		}

		//randomly select enough monsters to total 3
		List<MonsterForUser> mfuList = new ArrayList<MonsterForUser>(
				possibleMonstersTemp.values());
		Random rand = new Random();

		//for each monster gen rand float, and if it "drops" select it
		for (int i = 0; i < mfuList.size(); i++) {

			//eg. need 2, have 3. If first one is not picked, then need 2, have 2.
			float probToBeChosen = amountNeeded / (amountLeftOver - i);
			float randFloat = rand.nextFloat();

			if (randFloat < probToBeChosen) {
				//we have a winner! select this monster
				MonsterForUser mfu = mfuList.get(i);
				defenderMonsters.add(mfu);

				//need to decrement amount needed
				amountNeeded--;
			}

			//stop at three monsters, don't want to get more
			if (defenderMonsters.size() >= 3) {
				break;
			}
		}
	}

	public HazelcastPvpUtil getHazelcastPvpUtil() {
		return hazelcastPvpUtil;
	}

	public void setHazelcastPvpUtil(HazelcastPvpUtil hazelcastPvpUtil) {
		this.hazelcastPvpUtil = hazelcastPvpUtil;
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public TimeUtils getTimeUtil() {
		return timeUtil;
	}

	public void setTimeUtils(TimeUtils timeUtil) {
		this.timeUtil = timeUtil;
	}

	public UserRetrieveUtils2 getUserRetrieveUtil() {
		return userRetrieveUtil;
	}

	public void setUserRetrieveUtil(UserRetrieveUtils2 userRetrieveUtil) {
		this.userRetrieveUtil = userRetrieveUtil;
	}

	public PvpBattleForUserRetrieveUtils2 getPvpBattleForUserRetrieveUtil() {
		return pvpBattleForUserRetrieveUtil;
	}

	public void setPvpBattleForUserRetrieveUtil(
			PvpBattleForUserRetrieveUtils2 pvpBattleForUserRetrieveUtil) {
		this.pvpBattleForUserRetrieveUtil = pvpBattleForUserRetrieveUtil;
	}

	public PvpLeagueForUserRetrieveUtil2 getPvpLeagueForUserRetrieveUtil() {
		return pvpLeagueForUserRetrieveUtil;
	}

	public void setPvpLeagueForUserRetrieveUtil(
			PvpLeagueForUserRetrieveUtil2 pvpLeagueForUserRetrieveUtil) {
		this.pvpLeagueForUserRetrieveUtil = pvpLeagueForUserRetrieveUtil;
	}

	public PvpBattleHistoryRetrieveUtil2 getPvpBattleHistoryRetrieveUtil2() {
		return pvpBattleHistoryRetrieveUtil2;
	}

	public void setPvpBattleHistoryRetrieveUtil2(
			PvpBattleHistoryRetrieveUtil2 pvpBattleHistoryRetrieveUtil2) {
		this.pvpBattleHistoryRetrieveUtil2 = pvpBattleHistoryRetrieveUtil2;
	}

	public ClanRetrieveUtils2 getClanRetrieveUtil() {
		return clanRetrieveUtil;
	}

	public void setClanRetrieveUtil(ClanRetrieveUtils2 clanRetrieveUtil) {
		this.clanRetrieveUtil = clanRetrieveUtil;
	}

	public MonsterForUserRetrieveUtils2 getMonsterForUserRetrieveUtil() {
		return monsterForUserRetrieveUtil;
	}

	public void setMonsterForUserRetrieveUtil(
			MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtil) {
		this.monsterForUserRetrieveUtil = monsterForUserRetrieveUtil;
	}

	public ServerToggleRetrieveUtils getServerToggleRetrieveUtil() {
		return serverToggleRetrieveUtil;
	}

	public void setServerToggleRetrieveUtil(
			ServerToggleRetrieveUtils serverToggleRetrieveUtil) {
		this.serverToggleRetrieveUtil = serverToggleRetrieveUtil;
	}

	public MonsterStuffUtils getMonsterStuffUtils() {
		return monsterStuffUtils;
	}

	public void setMonsterStuffUtils(MonsterStuffUtils monsterStuffUtils) {
		this.monsterStuffUtils = monsterStuffUtils;
	}

	public PvpLeagueRetrieveUtils getPvpLeagueRetrieveUtils() {
		return pvpLeagueRetrieveUtils;
	}

	public void setPvpLeagueRetrieveUtils(
			PvpLeagueRetrieveUtils pvpLeagueRetrieveUtils) {
		this.pvpLeagueRetrieveUtils = pvpLeagueRetrieveUtils;
	}

	public CreateInfoProtoUtils getCreateInfoProtoUtils() {
		return createInfoProtoUtils;
	}

	public void setCreateInfoProtoUtils(CreateInfoProtoUtils createInfoProtoUtils) {
		this.createInfoProtoUtils = createInfoProtoUtils;
	}

	public MiscMethods getMiscMethods() {
		return miscMethods;
	}

	public void setMiscMethods(MiscMethods miscMethods) {
		this.miscMethods = miscMethods;
	}

	public MonsterLevelInfoRetrieveUtils getMonsterLevelInfoRetrieveUtil() {
		return monsterLevelInfoRetrieveUtil;
	}

	public void setMonsterLevelInfoRetrieveUtil(
			MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtil) {
		this.monsterLevelInfoRetrieveUtil = monsterLevelInfoRetrieveUtil;
	}

}
