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
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.EndPvpBattleRequestEvent;
import com.lvl6.events.response.EndPvpBattleResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.Clan;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.PvpBattleForUser;
import com.lvl6.info.PvpBattleHistory;
import com.lvl6.info.PvpLeagueForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.BattleProto.PvpHistoryProto;
import com.lvl6.proto.EventPvpProto.EndPvpBattleRequestProto;
import com.lvl6.proto.EventPvpProto.EndPvpBattleResponseProto;
import com.lvl6.proto.EventPvpProto.EndPvpBattleResponseProto.Builder;
import com.lvl6.proto.EventPvpProto.EndPvpBattleResponseProto.EndPvpBattleStatus;
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
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
import com.lvl6.retrieveutils.rarechange.PvpLeagueRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ServerToggleRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.InsertUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component
@DependsOn("gameServer")
public class EndPvpBattleController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	protected HazelcastPvpUtil hazelcastPvpUtil;

	@Autowired
	protected Locker locker;

	@Autowired
	protected TimeUtils timeUtils;

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
	protected MiscMethods miscMethods;


	@Autowired
	protected ServerToggleRetrieveUtils serverToggleRetrieveUtil;

	public EndPvpBattleController() {
		numAllocatedThreads = 7;
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
	protected void processRequestEvent(RequestEvent event) throws Exception {
		EndPvpBattleRequestProto reqProto = ((EndPvpBattleRequestEvent) event)
				.getEndPvpBattleRequestProto();
		log.info(String.format("reqProto=%s", reqProto));

		//get values sent from the client (the request proto)
		MinimumUserProtoWithMaxResources senderProtoMaxResources = reqProto
				.getSender();
		MinimumUserProto senderProto = senderProtoMaxResources
				.getMinUserProto();
		String attackerId = senderProto.getUserUuid();
		String defenderId = reqProto.getDefenderUuid();
		boolean attackerAttacked = reqProto.getUserAttacked();
		boolean attackerWon = reqProto.getUserWon();
		int oilStolen = reqProto.getOilChange(); //non negative
		int cashStolen = reqProto.getCashChange(); // non negative
		float nuPvpDmgMultiplier = reqProto.getNuPvpDmgMultiplier();

		List<Integer> monsterDropIds = reqProto.getMonsterDropIdsList();

		if (!attackerWon && oilStolen != 0) {
			log.error("client should set oilStolen to be 0 since attacker lost!"
					+ "\t client sent oilStolen=" + oilStolen);
			oilStolen = 0;
		}

		if (!attackerWon && cashStolen != 0) {
			log.error("client should set cashStolen to be 0 since attacker lost!"
					+ "\t client sent cashStolen=" + cashStolen);
			cashStolen = 0;
		}

		int attackerMaxOil = senderProtoMaxResources.getMaxOil();
		int attackerMaxCash = senderProtoMaxResources.getMaxCash();

		Timestamp curTime = new Timestamp(reqProto.getClientTime());
		Date curDate = new Date(curTime.getTime());

		//set some values to send to the client (the response proto)
		EndPvpBattleResponseProto.Builder resBuilder = EndPvpBattleResponseProto
				.newBuilder();
		resBuilder.setSender(senderProtoMaxResources);
		resBuilder.setStatus(EndPvpBattleStatus.FAIL_OTHER); //default
		resBuilder.setAttackerAttacked(attackerAttacked);
		resBuilder.setAttackerWon(attackerWon);

		EndPvpBattleResponseEvent resEvent = new EndPvpBattleResponseEvent(
				attackerId);
		resEvent.setTag(event.getTag());

		List<String> userIds = new ArrayList<String>();
		userIds.add(attackerId);
		userIds.add(defenderId); //doesn't matter if fake i.e. enemyUserId=0

		UUID attackerUuid = null;
		UUID defenderUuid = null;
		try {
			attackerUuid = UUID.fromString(attackerId);
			if (!"".equals(defenderId)) {
				defenderUuid = UUID.fromString(defenderId);
			}
		} catch (Exception e) {
			log.error(String.format(
					"UUID error. incorrect attackerId=%s, defenderId=%s",
					attackerId, defenderId), e);
		}

		if (null == attackerUuid) {
			resBuilder.setStatus(EndPvpBattleStatus.FAIL_OTHER); //default
			resEvent = new EndPvpBattleResponseEvent(attackerId);
			resEvent.setTag(event.getTag());
			resEvent.setEndPvpBattleResponseProto(resBuilder.build());
			server.writeEvent(resEvent);
			return;
		}

		//NEED TO LOCK BOTH PLAYERS, well need to lock defender because defender can be online,
		//Lock attacker because someone might be attacking him while attacker is attacking defender?
		if (null != defenderUuid) {
			getLocker().lockPlayers(defenderUuid, attackerUuid,
					this.getClass().getSimpleName());
			log.info("locked defender and attacker");
		} else {
			//ONLY ATTACKER IF DEFENDER IS FAKE
			getLocker().lockPlayer(attackerUuid,
					this.getClass().getSimpleName());
			log.info("locked attacker");
		}

		try {
			//get whatever from db
			Map<String, User> users = userRetrieveUtil.getUsersByIds(userIds);
			User attacker = users.get(attackerId);
			User defender = users.get(defenderId);
			PvpBattleForUser pvpBattleInfo = pvpBattleForUserRetrieveUtil
					.getPvpBattleForUserForAttacker(attackerId);
			log.info(String.format("pvpBattleInfo=%s", pvpBattleInfo));

			Map<String, PvpLeagueForUser> plfuMap = pvpLeagueForUserRetrieveUtil
					.getUserPvpLeagueForUsers(userIds);
			log.info(String.format("plfuMap=%s", plfuMap));
			//these objects will be updated if not null
			PvpLeagueForUser attackerPlfu = null;
			PvpLeagueForUser defenderPlfu = null;

			if (plfuMap.containsKey(attackerId)) {
				attackerPlfu = plfuMap.get(attackerId);
			}
			//could be fake user so could be null
			if (plfuMap.containsKey(defenderId)) {
				defenderPlfu = plfuMap.get(defenderId);
			}

			boolean legit = checkLegit(resBuilder, attacker, defender,
					pvpBattleInfo, curDate);

			Map<String, Map<String, Integer>> changeMap = new HashMap<String, Map<String, Integer>>();
			Map<String, Map<String, Integer>> previousCurrencyMap = new HashMap<String, Map<String, Integer>>();
			boolean successful = false;
			if (legit) {
				//it is possible that the defender has a shield, most likely via
				//buying it, and less likely locks didn't work, regardless, the
				//user can have a shield
				resBuilder.setStatsBefore(createInfoProtoUtils
						.createUserPvpLeagueProto(attackerId, attackerPlfu,
								null, false));
				//attackerPlfu is modified
				successful = writeChangesToDb(attacker, attackerId,
						attackerPlfu, defender, defenderId, defenderPlfu,
						pvpBattleInfo, oilStolen, cashStolen, curTime, curDate,
						attackerAttacked, attackerWon, nuPvpDmgMultiplier,
						attackerMaxOil, attackerMaxCash, changeMap,
						previousCurrencyMap, monsterDropIds, resBuilder);
				resBuilder.setStatsAfter(createInfoProtoUtils
						.createUserPvpLeagueProto(attackerId, attackerPlfu,
								null, false));
			}

			PvpBattleHistory battleJustEnded = null;
			if (successful) {
				//TODO: construct the history proto instead of db retrieving
				Timestamp ts = new Timestamp(curDate.getTime());
				battleJustEnded = pvpBattleHistoryRetrieveUtil2.getPvpBattle(
						attackerId, defenderId, ts);

				List<PvpHistoryProto> historyProtoList = null;
				if (null != battleJustEnded) {
					//Note: no protos for fake defenders are created
					historyProtoList = createInfoProtoUtils
							.createAttackedOthersPvpHistoryProto(attackerId,
									users,
									Collections.singletonList(battleJustEnded));
				}

				if (null != historyProtoList && !historyProtoList.isEmpty()) {
					PvpHistoryProto attackedOtherHistory = historyProtoList
							.get(0);
					log.info("attackedOtherHistory {}", attackedOtherHistory);
					resBuilder.setBattleThatJustEnded(attackedOtherHistory);
				}

				resBuilder.setStatus(EndPvpBattleStatus.SUCCESS);
			}

			//respond to the attacker
			resEvent.setEndPvpBattleResponseProto(resBuilder.build());
			server.writeEvent(resEvent);

			if (successful) {
				//respond to the defender
				if (null != defender) {
					if (null != battleJustEnded) {
						PvpHistoryProto php = createPvpProto(attacker,
								defender, curDate, battleJustEnded);
						log.info("gotAttackedHistory {}", php);
						resBuilder.setBattleThatJustEnded(php);
					}

					EndPvpBattleResponseEvent resEventDefender = new EndPvpBattleResponseEvent(
							defenderId);
					resEvent.setTag(0);
					resEventDefender.setEndPvpBattleResponseProto(resBuilder
							.build());
					server.writeEvent(resEventDefender);
				}
				//regardless of whether the attacker won, his elo will change
				UpdateClientUserResponseEvent resEventUpdate = miscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								attacker, attackerPlfu, null);
				resEventUpdate.setTag(event.getTag());
				server.writeEvent(resEventUpdate);

				//defender's elo and resources changed only if attacker won, and defender is real
				if (attackerWon && null != defender) {
					UpdateClientUserResponseEvent resEventUpdateDefender = miscMethods
							.createUpdateClientUserResponseEventAndUpdateLeaderboard(
									defender, defenderPlfu, null);
					resEventUpdate.setTag(event.getTag());
					server.writeEvent(resEventUpdateDefender);
				}

				if (attackerWon) {
					//TRACK CURRENCY HISTORY, resource changes only if attacker won
					writeToUserCurrencyHistory(attackerId, attacker,
							defenderId, defender, attackerWon, curTime,
							changeMap, previousCurrencyMap);
				}
			}

		} catch (Exception e) {
			log.error("exception in EndPvpBattleController processEvent", e);
			//don't let the client hang
			try {
				resEvent.setEndPvpBattleResponseProto(resBuilder.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in EndPvpBattleController processEvent",
						e);
			}

		} finally {
			if (null != defenderUuid) {
				getLocker().unlockPlayers(defenderUuid, attackerUuid,
						this.getClass().getSimpleName());
				log.info("unlocked defender and attacker");
			} else {
				getLocker().unlockPlayer(attackerUuid,
						this.getClass().getSimpleName());
				log.info("unlocked attacker");
			}
		}
	}

	/*
	 * Return true if user request is valid; false otherwise and set the
	 * builder status to the appropriate value.
	 */
	private boolean checkLegit(Builder resBuilder, User attacker,
			User defender, PvpBattleForUser pvpInfo, Date curDate) {

		if (null == pvpInfo) {
			log.error("unexpected error: no battle exists for the attacker. attacker="
					+ attacker + "\t defender=" + defender);
			return false;
		}

		//if the battle has finished one hour after the battle started, don't count it
		long nowMillis = curDate.getTime();
		long battleEndTime = pvpInfo.getBattleStartTime().getTime()
				+ ControllerConstants.PVP__MAX_BATTLE_DURATION_MILLIS;
		if (nowMillis > battleEndTime) {
			resBuilder.setStatus(EndPvpBattleStatus.FAIL_BATTLE_TOOK_TOO_LONG);
			log.error("the client took too long to finish a battle. pvpInfo="
					+ pvpInfo + "\t now=" + curDate);
			return false;
		}

		//  	if (0 == defenderUserId || !userAttacked) {
		//  		//if fake user, or user didn't attack just allow this to happen
		//  		return true;
		//  	}

		return true;
	}

	//the attackerPlfu and the defenderPlfu will be modified
	private boolean writeChangesToDb(User attacker, String attackerId,
			PvpLeagueForUser attackerPlfu, User defender, String defenderId,
			PvpLeagueForUser defenderPlfu, PvpBattleForUser pvpBattleInfo,
			int oilStolen, int cashStolen, Timestamp clientTime,
			Date clientDate, boolean attackerAttacked, boolean attackerWon,
			float nuPvpDmgMultiplier, int attackerMaxOil, int attackerMaxCash,
			Map<String, Map<String, Integer>> changeMap,
			Map<String, Map<String, Integer>> previousCurrencyMap,
			List<Integer> monsterDropIds, Builder resBuilder) {

		boolean cancelled = !attackerAttacked;

		if (cancelled) {
			log.info("battle cancelled");
			//this means that the only thing that changes is defenderOpu's inBattleShieldEndTime
			//just change it so its not in the future
			processCancellation(attacker, attackerId, attackerPlfu, defender,
					defenderId, defenderPlfu, pvpBattleInfo, clientTime,
					nuPvpDmgMultiplier, attackerWon, cancelled);

		} else {
			//user attacked so either he won or lost
			int previousCash = attacker.getCash();
			int previousOil = attacker.getOil();

			//TODO: WHEN MAX ELO IS FIGURED OUT, MAKE SURE ELO DOESN'T GO ABOVE THAT
			//these elo change lists are populated by getEloChanges(...)
			List<Integer> attackerEloChangeList = new ArrayList<Integer>();
			List<Integer> defenderEloChangeList = new ArrayList<Integer>();
			getEloChanges(attacker, attackerPlfu, defender, defenderPlfu,
					attackerWon, pvpBattleInfo, attackerEloChangeList,
					defenderEloChangeList);

			int attackerEloChange = attackerEloChangeList.get(0); //already pos or neg
			int defenderEloChange = defenderEloChangeList.get(0); //already pos or neg
			int attackerCashChange = calculateMaxCashChange(attacker,
					attackerMaxCash, cashStolen, attackerWon);
			int attackerOilChange = calculateMaxOilChange(attacker,
					attackerMaxOil, oilStolen, attackerWon);

			PvpLeagueForUser attackerPrevPlfu = new PvpLeagueForUser(
					attackerPlfu);
			//attackerPlfu will be updated
			updateAttacker(attacker, attackerId, attackerPlfu,
					attackerCashChange, attackerOilChange, attackerEloChange,
					nuPvpDmgMultiplier, attackerWon);

			//need to take into account if defender (online and) spent some cash/oil before
			//pvp battle ends, defender will not gain resources by winning
			//made lists so it can hold the values and be shared among methods
			List<Integer> defenderOilChangeList = new ArrayList<Integer>();
			List<Integer> defenderCashChangeList = new ArrayList<Integer>();
			List<Boolean> displayToDefenderList = new ArrayList<Boolean>();

			//could have attacked a fake person
			PvpLeagueForUser defenderPrevPlfu = null;
			if (null != defenderPlfu) {
				defenderPrevPlfu = new PvpLeagueForUser(defenderPlfu);
			}
			//defender could be fake user, in which case no change is made to defender
			//no change could still be made if the defender is already under attack by another
			//and this attacker is the second guy attacking the defender
			updateDefender(attacker, defender, defenderId, defenderPlfu,
					pvpBattleInfo, defenderEloChange, oilStolen, cashStolen,
					clientDate, attackerWon, defenderEloChangeList,
					defenderOilChangeList, defenderCashChangeList,
					displayToDefenderList, changeMap, previousCurrencyMap);

			writePvpBattleHistoryNotcancelled(attackerId, attackerPrevPlfu,
					attackerPlfu, defenderId, defenderPrevPlfu, defenderPlfu,
					pvpBattleInfo, clientTime, attackerWon, attackerCashChange,
					attackerOilChange, attackerEloChange, defenderEloChange,
					defenderOilChangeList, defenderCashChangeList,
					displayToDefenderList, nuPvpDmgMultiplier, cancelled);

			//user currency stuff
			Map<String, Integer> attackerChangeMap = new HashMap<String, Integer>();
			attackerChangeMap.put(miscMethods.cash, attackerCashChange);
			attackerChangeMap.put(miscMethods.oil, attackerOilChange);
			changeMap.put(attackerId, attackerChangeMap);

			Map<String, Integer> attackerPreviousCurrency = new HashMap<String, Integer>();
			attackerPreviousCurrency.put(miscMethods.cash, previousCash);
			attackerPreviousCurrency.put(miscMethods.oil, previousOil);
			previousCurrencyMap.put(attackerId, attackerPreviousCurrency);

			awardMonsters(resBuilder, attackerId, monsterDropIds, clientDate);

		}

		log.info("deleting from PvpBattleForUser");
		//need to delete PvpBattleForUser
		int numDeleted = DeleteUtils.get().deletePvpBattleForUser(attackerId);
		log.info(String.format("numDeleted (should be 1): %s", numDeleted));

		return true;
	}

	private void awardMonsters(Builder resBuilder, String userId,
			List<Integer> monsterIds, Date currentDate) {
		Map<Integer, Integer> monsterIdToNumPieces = new HashMap<Integer, Integer>();

		for (Integer monsterId : monsterIds) {
			if (!monsterIdToNumPieces.containsKey(monsterId)) {
				monsterIdToNumPieces.put(monsterId, 0);
			}
			int nuQuantity = monsterIdToNumPieces.get(monsterId) + 1;
			monsterIdToNumPieces.put(monsterId, nuQuantity);
		}

		String mfusop = ControllerConstants.MFUSOP__PVP;

		List<FullUserMonsterProto> newOrUpdated = monsterStuffUtils
				.updateUserMonsters(userId, monsterIdToNumPieces, null, mfusop,
						currentDate);

		resBuilder.addAllUpdatedOrNew(newOrUpdated);
	}

	private void processCancellation(User attacker, String attackerId,
			PvpLeagueForUser attackerPlfu, User defender, String defenderId,
			PvpLeagueForUser defenderPlfu, PvpBattleForUser pvpBattleInfo,
			Timestamp clientTime, float nuPvpDmgMultiplier,
			boolean attackerWon, boolean cancelled) {

		if (null != defender
				&& defenderPlfu.getInBattleShieldEndTime().getTime() > clientTime
						.getTime()) {
			//since real player and "battle end time" is after now, change it
			//so defender can be attackable again
			defenderPlfu.setInBattleShieldEndTime(defenderPlfu
					.getShieldEndTime());
			PvpUser defenderOpu = new PvpUser(defenderPlfu);
			getHazelcastPvpUtil().replacePvpUser(defenderOpu, defenderId);
			log.info("changed battleEndTime to shieldEndTime. defenderPvpUser="
					+ defenderOpu);
		}
		int attackerEloBefore = attackerPlfu.getElo();
		int attackerPrevLeague = attackerPlfu.getPvpLeagueId();
		int attackerPrevRank = attackerPlfu.getRank();

		int defenderEloBefore = 0;
		int defenderPrevLeague = 0;
		int defenderPrevRank = 0;
		if (null != defenderPlfu) {
			defenderEloBefore = defenderPlfu.getElo();
			defenderPrevLeague = defenderPlfu.getPvpLeagueId();
			defenderPrevRank = defenderPlfu.getRank();
		}

		//since battle cancelled, nothing should have changed
		log.info("writing to pvp history that user cancelled battle");
		writePvpBattleHistory(attackerId, attackerEloBefore, defenderId,
				defenderEloBefore, attackerPrevLeague, attackerPrevLeague,
				defenderPrevLeague, defenderPrevLeague, attackerPrevRank,
				attackerPrevRank, defenderPrevRank, defenderPrevRank,
				clientTime, pvpBattleInfo, 0, 0, 0, 0, 0, 0,
				nuPvpDmgMultiplier, attackerWon, cancelled, false, false);
	}

	//cashChange is non negative number,
	//returns a signed number representing oilChange
	private int calculateMaxCashChange(User user, int maxCash, int cashChange,
			boolean userWon) {
		if (null == user) {
			log.info("calculateMaxCashChange user is null! cashChange=0");
			//this is for fake user
			return 0;
		}
		//if user somehow has more than max cash, first treat user as having max cash,
		//figure out the amount he gains and then subtract, the extra cash he had
		int userCash = user.getCash();
		int amountOverMax = calculateAmountOverMaxResource(user, userCash,
				maxCash, miscMethods.cash);
		log.info(String.format("calculateMaxCashChange amount over max=%s",
				amountOverMax));

		if (userWon) {
			log.info(String.format("calculateMaxCashChange userWon!. user=%s",
					user));
			int curCash = Math.min(user.getCash(), maxCash); //in case user's cash is more than maxOil.
			log.info(String
					.format("calculateMaxCashChange curCash=%s", curCash));
			int maxCashUserCanGain = maxCash - curCash;
			log.info(String.format(
					"calculateMaxCashChange  maxCashUserCanGain=%s",
					maxCashUserCanGain));
			int maxCashChange = Math.min(cashChange, maxCashUserCanGain);
			log.info(String.format("calculateMaxCashChange maxCashChange=%s",
					maxCashChange));

			//IF USER IS ABOVE maxCash, need to drag him down to maxCash
			int actualCashChange = maxCashChange - amountOverMax;
			log.info(String.format(
					"calculateMaxCashChange  actualCashChange=%s",
					actualCashChange));
			return actualCashChange;

		} else {
			log.info(String.format("calculateMaxCashChange userLost!. user=%s",
					user));

			int maxCashUserCanLose = Math.min(user.getCash(), maxCash);
			//always non negative number
			int maxCashChange = Math.min(cashChange, maxCashUserCanLose);

			int actualCashChange = -1 * (amountOverMax + maxCashChange);
			log.info(String.format(
					"calculateMaxCashChange  actualCashChange=%s",
					actualCashChange));
			return actualCashChange;
		}
	}

	//oilChange is positive number,
	//returns a signed number representing oilChange
	private int calculateMaxOilChange(User user, int maxOil, int oilChange,
			boolean userWon) {
		if (null == user) {
			log.info("calculateMaxOilChange user is null! oilChange=0");
			//this is for fake user
			return 0;
		}

		//if user somehow has more than max oil, first treat user as having max oil,
		//figure out the amount he gains and then subtract, the extra oil he had
		int userOil = user.getOil();
		int amountOverMax = calculateAmountOverMaxResource(user, userOil,
				maxOil, miscMethods.oil);
		log.info(String.format("calculateMaxOilChange amount over max=%s",
				amountOverMax));

		if (userWon) {
			log.info(String.format("calculateMaxOilChange userWon!. user=%s",
					user));
			int curOil = Math.min(user.getOil(), maxOil); //in case user's oil is more than maxOil.
			log.info(String.format("calculateAmountOverMaxOil curOil=%s",
					curOil));
			int maxOilUserCanGain = maxOil - curOil;
			log.info(String.format(
					"calculateAmountOverMaxOil  maxOilUserCanGain=%s",
					maxOilUserCanGain));
			int maxOilChange = Math.min(oilChange, maxOilUserCanGain);
			log.info(String.format("calculateAmountOverMaxOil maxOilChange=%s",
					maxOilChange));

			//IF USER IS ABOVE maxOil, need to drag him down to maxOil
			int actualOilChange = maxOilChange - amountOverMax;
			log.info(String.format(
					"calculateAmountOverMaxOil  actualOilChange=%s",
					actualOilChange));
			return actualOilChange;

		} else {
			log.info(String.format(
					"calculateAmountOverMaxOil userLost!. user=%s", user));
			int maxOilUserCanLose = Math.min(user.getOil(), maxOil);
			//always a nonnegative number
			int maxOilChange = Math.min(oilChange, maxOilUserCanLose);

			int actualOilChange = -1 * (amountOverMax + maxOilChange);
			log.info(String.format(
					"calculateAmountOverMaxOil  actualOilChange=%s",
					actualOilChange));
			return actualOilChange;
		}
	}

	private int calculateAmountOverMaxResource(User u, int userResource,
			int maxResource, String resource) {
		log.info(String.format("calculateAmountOverMaxResource resource=%s",
				resource));
		int resourceLoss = 0;
		if (userResource > maxResource) {
			//		  if (u.isAdmin()) {
			//			  log.info("alright for user to have more than maxResource." +
			//					  " user is admin. user=" + u + "\t maxResource=" + maxResource);
			//		  } else {
			log.info("wtf!!!!! user has more than max cash! user=" + u
					+ "\t cutting him down to maxResource=" + maxResource);
			resourceLoss = userResource - maxResource;
			//		  }
		}
		return resourceLoss;
	}

	//calculate how much elo changes for the attacker and defender
	//based on whether the attacker won, capping min elo at 0 for attacker & defender
	private void getEloChanges(User attacker, PvpLeagueForUser attackerPlfu,
			User defender, PvpLeagueForUser defenderPlfu, boolean attackerWon,
			PvpBattleForUser pvpBattleInfo,
			List<Integer> attackerEloChangeList,
			List<Integer> defenderEloChangeList) {
		//temp variables
		int attackerEloChange = 0;
		int defenderEloChange = 0;

		if (attackerWon) {
			log.info("getEloChanges attacker won.");

			attackerEloChange = pvpBattleInfo.getAttackerWinEloChange(); //positive value
			defenderEloChange = pvpBattleInfo.getDefenderLoseEloChange(); //negative value

			//don't cap fake player's elo
			if (null != defender && null != pvpBattleInfo.getDefenderId()
					&& !pvpBattleInfo.getDefenderId().isEmpty()) {
				log.info(String
						.format("getEloChanges attacker fought real player. battleInfo=%s",
								pvpBattleInfo));

				//make sure defender's elo doesn't go below 0
				defenderEloChange = capPlayerMinimumElo(defenderPlfu,
						defenderEloChange);
			} else {
				log.info(String
						.format("getEloChanges attacker fought fake player. battleInfo=%s",
								pvpBattleInfo));
			}

			log.info(String.format("getEloChanges attackerEloChange=%s",
					attackerEloChange));
			log.info(String.format("getEloChanges defenderEloChange=%s",
					defenderEloChange));

		} else {
			log.info("getEloChanges attacker lost.");
			attackerEloChange = pvpBattleInfo.getAttackerLoseEloChange(); //negative value
			defenderEloChange = pvpBattleInfo.getDefenderWinEloChange(); // positive value

			//make sure attacker's elo doesn't go below 0
			attackerEloChange = capPlayerMinimumElo(attackerPlfu,
					attackerEloChange);

		}

		attackerEloChangeList.add(attackerEloChange);
		defenderEloChangeList.add(defenderEloChange);

	}

	private int capPlayerMinimumElo(PvpLeagueForUser playerPlfu,
			int playerEloChange) {
		int playerElo = playerPlfu.getElo();
		log.info(String.format("capPlayerMinimumElo plfu=%s,  eloChange=%s",
				playerPlfu, playerEloChange));

		if (playerElo + playerEloChange < 0) {
			log.info("capPlayerMinimumElo player loses more elo than has atm. playerElo="
					+ playerElo + "\t playerEloChange=" + playerEloChange);
			playerEloChange = -1 * playerElo;
		}

		log.info(String.format(
				"capPlayerMinimumElo updated playerEloChange=%s",
				playerEloChange));
		return playerEloChange;
	}

	private void updateAttacker(User attacker, String attackerId,
			PvpLeagueForUser attackerPlfu, int attackerCashChange,
			int attackerOilChange, int attackerEloChange,
			float nuPvpDmgMultiplier, boolean attackerWon) {

		//update attacker's cash, oil
		if (0 != attackerOilChange || 0 != attackerCashChange) {
			log.info(String.format("attacker before currency update: %s",
					attacker));
			int numUpdated = attacker.updateRelativeCashAndOilAndGems(
					attackerCashChange, attackerOilChange, 0);
			log.info(String.format("attacker after currency update: %s",
					attacker));
			log.info(String.format(
					"num updated when changing attacker's currency=%s",
					numUpdated));
		}

		log.info(String.format(
				"attacker PvpLeagueForUser before battle outcome: %s",
				attackerPlfu));
		int prevElo = attackerPlfu.getElo();
		int attackerPrevLeague = attackerPlfu.getPvpLeagueId();
		int attacksWon = attackerPlfu.getAttacksWon();
		int attacksLost = attackerPlfu.getAttacksLost();

		int attacksWonDelta = 0;
		int defensesWonDelta = 0;
		int attacksLostDelta = 0;
		int defensesLostDelta = 0;

		if (attackerWon) {
			attacksWonDelta = 1;
			attacksWon += attacksWonDelta;
		} else {
			attacksLostDelta = 1;
			attacksLost += attacksLostDelta;
		}

		int curElo = prevElo + attackerEloChange;
		int attackerCurLeague = pvpLeagueRetrieveUtils.getLeagueIdForElo(
				curElo, attackerPrevLeague);
		int attackerCurRank = pvpLeagueRetrieveUtils.getRankForElo(curElo,
				attackerCurLeague);

		//don't update his shields and elo
		int numUpdated = UpdateUtils.get().updatePvpLeagueForUser(attackerId,
				attackerCurLeague, attackerCurRank, attackerEloChange, null,
				null, attacksWonDelta, defensesWonDelta, attacksLostDelta,
				defensesLostDelta, nuPvpDmgMultiplier);
		log.info("num updated when changing attacker's elo=" + numUpdated);

		//modify object to return back to user
		attackerPlfu.setElo(curElo);
		attackerPlfu.setPvpLeagueId(attackerCurLeague);
		attackerPlfu.setRank(attackerCurRank);
		attackerPlfu.setAttacksWon(attacksWon);
		attackerPlfu.setAttacksLost(attacksLost);

		//update hazelcast's object
		PvpUser attackerPu = new PvpUser(attackerPlfu);
		getHazelcastPvpUtil().replacePvpUser(attackerPu, attackerId);
		log.info("attacker PvpLeagueForUser after battle outcome:"
				+ attackerPlfu);
	}

	//the elo, oil, cash, display lists are return values
	private void updateDefender(User attacker, User defender,
			String defenderId, PvpLeagueForUser defenderPlfu,
			PvpBattleForUser pvpBattleInfo, int defenderEloChange,
			int oilStolen, int cashStolen, Date clientDate,
			boolean attackerWon, List<Integer> defenderEloChangeList,
			List<Integer> defenderOilChangeList,
			List<Integer> defenderCashChangeList,
			List<Boolean> displayToDefenderList,
			Map<String, Map<String, Integer>> changeMap,
			Map<String, Map<String, Integer>> previousCurrencyMap) {
		if (null == defender) {
			log.info(String.format(
					"attacker attacked fake defender. attacker=%s", attacker));
			defenderEloChangeList.clear();
			defenderEloChangeList.add(0);
			defenderOilChangeList.add(0);
			defenderCashChangeList.add(0);
			displayToDefenderList.add(false);
			return;
		}
		//processing real user data
		int previousCash = defender.getCash();
		int previousOil = defender.getOil();

		boolean defenderWon = !attackerWon;
		int defenderCashChange = calculateMaxCashChange(defender,
				defender.getCash(), cashStolen, defenderWon);
		int defenderOilChange = calculateMaxOilChange(defender,
				defender.getOil(), oilStolen, defenderWon);
		boolean displayToDefender = true;

		//if DEFENDER HAS SHIELD THEN DEFENDER SHOULD NOT BE PENALIZED, and
		//the history for this battle should have the display_to_defender set to false;
		Date shieldEndTime = defenderPlfu.getShieldEndTime();
		//	  if (getTimeUtils().isFirstEarlierThanSecond(clientDate, shieldEndTime)) {
		//		  log.warn("some how attacker attacked a defender with a shield!! pvpBattleInfo=" +
		//				  pvpBattleInfo + "\t attacker=" + attacker + "\t defender=" + defender);
		//		  defenderCashChange = 0;
		//		  defenderOilChange = 0;
		//		  defenderEloChange = 0;
		//		  displayToDefender = false;
		//
		//	  } else {
		log.info(String.format(
				"penalizing/rewarding for losing/winning. defenderWon=%s",
				defenderWon));
		updateUnshieldedDefender(attacker, defenderId, defender, defenderPlfu,
				shieldEndTime, pvpBattleInfo, clientDate, attackerWon,
				defenderEloChange, defenderCashChange, defenderOilChange);
		//	  }

		defenderEloChangeList.add(defenderEloChange);
		defenderCashChangeList.add(defenderCashChange);
		defenderOilChangeList.add(defenderOilChange);
		displayToDefenderList.add(displayToDefender);

		//user currency stuff
		Map<String, Integer> defenderChangeMap = new HashMap<String, Integer>();
		defenderChangeMap.put(miscMethods.cash, defenderCashChange);
		defenderChangeMap.put(miscMethods.oil, defenderOilChange);
		changeMap.put(defenderId, defenderChangeMap);

		Map<String, Integer> defenderPreviousCurrency = new HashMap<String, Integer>();
		defenderPreviousCurrency.put(miscMethods.cash, previousCash);
		defenderPreviousCurrency.put(miscMethods.oil, previousOil);
		previousCurrencyMap.put(defenderId, defenderPreviousCurrency);
	}

	private void updateUnshieldedDefender(User attacker, String defenderId,
			User defender, PvpLeagueForUser defenderPlfu,
			Date defenderShieldEndTime, PvpBattleForUser pvpBattleInfo,
			Date clientDate, boolean attackerWon, int defenderEloChange,
			int defenderCashChange, int defenderOilChange) {
		//NO ONE ELSE ATTACKING DEFENDER. update defender's cash, oil, elo and shields
		log.info("attacker attacked unshielded defender. attacker=" + attacker
				+ "\t defender=" + defender + "\t battleInfo=" + pvpBattleInfo);

		//old info before battle
		int prevElo = defenderPlfu.getElo();
		int prevPvpLeague = defenderPlfu.getPvpLeagueId();
		int defensesLost = defenderPlfu.getDefensesLost();
		int defensesWon = defenderPlfu.getDefensesWon();

		int attacksWonDelta = 0;
		int defensesWonDelta = 0;
		int attacksLostDelta = 0;
		int defensesLostDelta = 0;

		//if attacker won then defender money would need to be updated
		if (attackerWon) {
			log.info(String
					.format("updateUnshieldedDefender  defender before currency update:%s",
							defender));
			int numUpdated = defender.updateRelativeCashAndOilAndGems(
					defenderCashChange, defenderOilChange, 0);
			log.info(String
					.format("updateUnshieldedDefender num updated when changing defender's currency=%s",
							numUpdated));
			log.info(String
					.format("updateUnshieldedDefender  defender after currency update: %s",
							defender));

			int hoursAddend = ControllerConstants.PVP__LOST_BATTLE_SHIELD_DURATION_HOURS;
			defenderShieldEndTime = getTimeUtils().createDateAddHours(
					clientDate, hoursAddend);
			defensesLostDelta = 1;
			defensesLost += defensesLostDelta;

		} else {
			log.info("updateUnshieldedDefender defender won!");
			defensesWonDelta = 1;
			defensesWon += defensesWonDelta;
		}
		log.info(String
				.format("updateUnshieldedDefender defender PvpLeagueForUser before battle outcome: %s",
						defenderPlfu));

		//regardless if user won or lost, this is shield time
		Date inBattleShieldEndTime = defenderShieldEndTime;

		int curElo = prevElo + defenderEloChange;
		int curPvpLeague = pvpLeagueRetrieveUtils.getLeagueIdForElo(curElo,
				prevPvpLeague);
		int curRank = pvpLeagueRetrieveUtils
				.getRankForElo(curElo, curPvpLeague);

		//update pvp stuff: elo most likely changed, shields might have if attackerWon
		Timestamp shieldEndTimestamp = new Timestamp(
				defenderShieldEndTime.getTime());
		Timestamp inBattleTimestamp = new Timestamp(
				inBattleShieldEndTime.getTime());
		int numUpdated = UpdateUtils.get().updatePvpLeagueForUser(defenderId,
				curPvpLeague, curRank, defenderEloChange, shieldEndTimestamp,
				inBattleTimestamp, attacksWonDelta, defensesWonDelta,
				attacksLostDelta, defensesLostDelta, -1);

		log.info(String.format("num updated when changing defender's elo=%s",
				numUpdated));

		//modify object to return back to user
		defenderPlfu.setShieldEndTime(defenderShieldEndTime);
		defenderPlfu.setInBattleShieldEndTime(inBattleShieldEndTime);

		defenderPlfu.setElo(curElo);
		defenderPlfu.setPvpLeagueId(curPvpLeague);
		defenderPlfu.setRank(curRank);
		defenderPlfu.setDefensesLost(defensesLost);
		defenderPlfu.setDefensesWon(defensesWon);

		//update hazelcast's object
		PvpUser defenderPu = new PvpUser(defenderPlfu);
		getHazelcastPvpUtil().replacePvpUser(defenderPu, defenderId);
		log.info(String.format(
				"defender PvpLeagueForUser after battle outcome: %s",
				defenderPlfu));
	}

	//new method created so as to reduce clutter in calling method
	private void writePvpBattleHistory(String attackerId,
			int attackerEloBefore, String defenderId, int defenderEloBefore,
			int attackerPrevLeague, int attackerCurLeague,
			int defenderPrevLeague, int defenderCurLeague,
			int attackerPrevRank, int attackerCurRank, int defenderPrevRank,
			int defenderCurRank, Timestamp endTime,
			PvpBattleForUser pvpBattleInfo, int attackerEloChange,
			int defenderEloChange, int attackerOilChange,
			int defenderOilChange, int attackerCashChange,
			int defenderCashChange, float nuPvpDmgMultiplier,
			boolean attackerWon, boolean cancelled, boolean gotRevenge,
			boolean displayToDefender) {

		Date startDate = pvpBattleInfo.getBattleStartTime();
		Timestamp battleStartTime = new Timestamp(startDate.getTime());
		int numInserted = InsertUtils.get().insertIntoPvpBattleHistory(
				attackerId, defenderId, endTime, battleStartTime,
				attackerEloChange, attackerEloBefore, defenderEloChange,
				defenderEloBefore, attackerPrevLeague, attackerCurLeague,
				defenderPrevLeague, defenderCurLeague, attackerPrevRank,
				attackerCurRank, defenderPrevRank, defenderCurRank,
				attackerOilChange, defenderOilChange, attackerCashChange,
				defenderCashChange, nuPvpDmgMultiplier, attackerWon, cancelled,
				gotRevenge, displayToDefender);
		log.info(String.format("num inserted into history=%s", numInserted));
	}

	//new method created so as to reduce clutter in calling method
	private void writePvpBattleHistoryNotcancelled(String attackerId,
			PvpLeagueForUser attackerPrevPlfu, PvpLeagueForUser attackerPlfu,
			String defenderId, PvpLeagueForUser defenderPrevPlfu,
			PvpLeagueForUser defenderPlfu, PvpBattleForUser pvpBattleInfo,
			Timestamp clientTime, boolean attackerWon, int attackerCashChange,
			int attackerOilChange, int attackerEloChange,
			int defenderEloChange, List<Integer> defenderOilChangeList,
			List<Integer> defenderCashChangeList,
			List<Boolean> displayToDefenderList, float nuPvpDmgMultiplier,
			boolean cancelled) {

		int attackerEloBefore = attackerPrevPlfu.getElo();
		int attackerPrevLeague = attackerPrevPlfu.getPvpLeagueId();
		int attackerPrevRank = attackerPrevPlfu.getRank();
		int attackerCurLeague = attackerPlfu.getPvpLeagueId();
		int attackerCurRank = attackerPlfu.getRank();

		int defenderEloBefore = 0;
		int defenderPrevLeague = 0;
		int defenderPrevRank = 0;
		int defenderCurLeague = 0;
		int defenderCurRank = 0;

		//user could have fought a fake person
		if (null != defenderPrevPlfu) {
			defenderEloBefore = defenderPrevPlfu.getElo();
			defenderPrevLeague = defenderPrevPlfu.getPvpLeagueId();
			defenderPrevRank = defenderPrevPlfu.getRank();

			defenderCurLeague = defenderPlfu.getPvpLeagueId();
			defenderCurRank = defenderPlfu.getRank();
		}

		int defenderOilChange = defenderOilChangeList.get(0);
		int defenderCashChange = defenderCashChangeList.get(0);
		boolean displayToDefender = displayToDefenderList.get(0);

		log.info("writing to pvp history that user finished battle");
		log.info(String
				.format("attackerEloChange=%s, defenderEloChange=%s, attackerOilChange=%s, defenderOilChange=%s, attackerCashChange=%s, defenderCashChange=%s",
						attackerEloChange, defenderEloChange,
						attackerOilChange, defenderOilChange,
						attackerCashChange, defenderCashChange));
		writePvpBattleHistory(attackerId, attackerEloBefore, defenderId,
				defenderEloBefore, attackerPrevLeague, attackerCurLeague,
				defenderPrevLeague, defenderCurLeague, attackerPrevRank,
				attackerCurRank, defenderPrevRank, defenderCurRank, clientTime,
				pvpBattleInfo, attackerEloChange, defenderEloChange,
				attackerOilChange, defenderOilChange, attackerCashChange,
				defenderCashChange, nuPvpDmgMultiplier, attackerWon, cancelled,
				false, displayToDefender);
	}

	private void writeToUserCurrencyHistory(String attackerId, User attacker,
			String defenderId, User defender, boolean attackerWon,
			Timestamp curTime, Map<String, Map<String, Integer>> changeMap,
			Map<String, Map<String, Integer>> previousCurrencyMap) {

		Map<String, Map<String, Integer>> currentCurrencyMap = new HashMap<String, Map<String, Integer>>();
		Map<String, Map<String, String>> changeReasonsMap = new HashMap<String, Map<String, String>>();
		Map<String, Map<String, String>> detailsMap = new HashMap<String, Map<String, String>>();
		String reasonForChange = ControllerConstants.UCHRFC__PVP_BATTLE;
		String oil = miscMethods.oil;
		String cash = miscMethods.cash;

		//reasons
		Map<String, String> reasonMap = new HashMap<String, String>();
		reasonMap.put(cash, reasonForChange);
		reasonMap.put(oil, reasonForChange);
		changeReasonsMap.put(attackerId, reasonMap);
		changeReasonsMap.put(defenderId, reasonMap);

		//attacker stuff
		//current currency stuff
		int attackerCash = attacker.getCash();
		int attackerOil = attacker.getOil();
		Map<String, Integer> attackerCurrency = new HashMap<String, Integer>();
		attackerCurrency.put(cash, attackerCash);
		attackerCurrency.put(oil, attackerOil);
		//aggregate currency
		currentCurrencyMap.put(attackerId, attackerCurrency);

		//details
		StringBuilder attackerDetailsSb = new StringBuilder();
		if (attackerWon) {
			attackerDetailsSb.append("beat ");
		} else {
			attackerDetailsSb.append("lost to ");
		}
		attackerDetailsSb.append(defenderId);
		String attackerDetails = attackerDetailsSb.toString();
		Map<String, String> attackerDetailsMap = new HashMap<String, String>();
		attackerDetailsMap.put(cash, attackerDetails);
		attackerDetailsMap.put(oil, attackerDetails);
		//aggregate details
		detailsMap.put(attackerId, attackerDetailsMap);

		//defender stuff
		if (null != defender) {
			//current currency stuff
			int defenderCash = defender.getCash();
			int defenderOil = defender.getOil();
			Map<String, Integer> defenderCurrency = new HashMap<String, Integer>();
			defenderCurrency.put(cash, defenderCash);
			defenderCurrency.put(oil, defenderOil);
			//aggregate currency
			currentCurrencyMap.put(defenderId, defenderCurrency);

			//details
			StringBuilder defenderDetailsSb = new StringBuilder();
			if (attackerWon) {
				defenderDetailsSb.append("lost to ");
			} else {
				defenderDetailsSb.append("beat ");
			}
			defenderDetailsSb.append(attackerId);
			String defenderDetails = defenderDetailsSb.toString();
			Map<String, String> defenderDetailsMap = new HashMap<String, String>();
			defenderDetailsMap.put(cash, defenderDetails);
			defenderDetailsMap.put(oil, defenderDetails);
			//aggregate details
			detailsMap.put(defenderId, defenderDetailsMap);

		}

		List<String> userIds = new ArrayList<String>();
		userIds.add(attackerId);

		if (null != defender && !defenderId.isEmpty()) {
			userIds.add(defenderId);
		}

		miscMethods.writeToUserCurrencyUsers(userIds, curTime, changeMap,
				previousCurrencyMap, currentCurrencyMap, changeReasonsMap,
				detailsMap);

	}

	//TODO: CLEAN UP: copied from SetPvpBattleHistoryAction, pasted, and modified
	private PvpHistoryProto createPvpProto(User attacker, User defender,
			Date curDate, PvpBattleHistory gotAttackedHistory) {
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
		List<PvpBattleHistory> gotAttackedHistoryList = Collections
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

		//need the drop rates
		Map<String, Map<String, Integer>> userIdToUserMonsterIdToDroppedId = monsterStuffUtils
				.calculatePvpDrops(userIdsToUserMonsters);

		//need to calculate the resources defender can steal
		PvpBattleOutcome potentialResult = new PvpBattleOutcome(defender,
				defenderElo, attacker, attackerElo, serverToggleRetrieveUtil);

		Map<String, Integer> attackerIdsToProspectiveCashWinnings = Collections
				.singletonMap(attackerId,
						potentialResult.getUnsignedCashAttackerWins());
		Map<String, Integer> attackerIdsToProspectiveOilWinnings = Collections
				.singletonMap(attackerId,
						potentialResult.getUnsignedOilAttackerWins());

		//this PvpHistoryProto contains information for a person who
		//is going to attack
		List<PvpHistoryProto> historyProtoList = createInfoProtoUtils
				.createGotAttackedPvpHistoryProto(gotAttackedHistoryList,
						idsToAttackers, attackerIdsToClans,
						userIdsToUserMonsters,
						userIdToUserMonsterIdToDroppedId,
						attackerIdsToProspectiveCashWinnings,
						attackerIdsToProspectiveOilWinnings);

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

	public TimeUtils getTimeUtils() {
		return timeUtils;
	}

	public void setTimeUtils(TimeUtils timeUtils) {
		this.timeUtils = timeUtils;
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

}
