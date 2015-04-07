package com.lvl6.server.controller.actionobjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.Clan;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.PvpBattleHistory;
import com.lvl6.info.User;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.BattleProto.PvpHistoryProto;
import com.lvl6.proto.EventStartupProto.StartupResponseProto;
import com.lvl6.pvp.HazelcastPvpUtil;
import com.lvl6.pvp.PvpBattleOutcome;
import com.lvl6.pvp.PvpUser;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2;
import com.lvl6.retrieveutils.PvpBattleHistoryRetrieveUtil2;
import com.lvl6.retrieveutils.rarechange.ServerToggleRetrieveUtils;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.CreateInfoProtoUtils;

public class SetPvpBattleHistoryAction implements StartUpAction {
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private final StartupResponseProto.Builder resBuilder;
	private final User user;
	private final String userId;
	private final PvpBattleHistoryRetrieveUtil2 pvpBattleHistoryRetrieveUtil;
	private final MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils;
	private final ClanRetrieveUtils2 clanRetrieveUtils;
	private final HazelcastPvpUtil hazelcastPvpUtil;
	private final MonsterStuffUtils monsterStuffUtils;
	private final CreateInfoProtoUtils createInfoProtoUtils;
	private final ServerToggleRetrieveUtils serverToggleRetrieveUtil;

	public SetPvpBattleHistoryAction(StartupResponseProto.Builder resBuilder,
			User user, String userId,
			PvpBattleHistoryRetrieveUtil2 pvpBattleHistoryRetrieveUtil,
			MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils,
			ClanRetrieveUtils2 clanRetrieveUtils,
			HazelcastPvpUtil hazelcastPvpUtil,
			MonsterStuffUtils monsterStuffUtils,
			CreateInfoProtoUtils createInfoProtoUtils,
			ServerToggleRetrieveUtils serverToggleRetrieveUtil)
 	{
		this.resBuilder = resBuilder;
		this.user = user;
		this.userId = userId;
		this.pvpBattleHistoryRetrieveUtil = pvpBattleHistoryRetrieveUtil;
		this.hazelcastPvpUtil = hazelcastPvpUtil;
		this.monsterForUserRetrieveUtils = monsterForUserRetrieveUtils;
		this.clanRetrieveUtils = clanRetrieveUtils;
		this.monsterStuffUtils = monsterStuffUtils;
		this.createInfoProtoUtils = createInfoProtoUtils;
		this.serverToggleRetrieveUtil = serverToggleRetrieveUtil;
	}

	//derived state
	private List<PvpBattleHistory> historyList;
	private List<PvpBattleHistory> gotAttackedHistoryList;
	private List<PvpBattleHistory> attackedOthersHistoryList;
	private Set<String> userIds;
	private Set<String> attackerIds;

	private List<String> attackerIdsList;
	private Map<String, List<MonsterForUser>> userIdsToUserMonsters;
	private Map<String, Integer> attackerIdsToProspectiveCashWinnings;
	private Map<String, Integer> attackerIdsToProspectiveOilWinnings;

	//Extracted from Startup
	@Override
	public void setUp(StartUpResource fillMe) {
		int n = ControllerConstants.PVP_HISTORY__NUM_RECENT_BATTLES;

		//NOTE: AN ATTACKER MIGHT SHOW UP MORE THAN ONCE DUE TO REVENGE
		historyList = pvpBattleHistoryRetrieveUtil.getRecentNBattlesForUserId(
				userId, n);
		log.info(String.format("historyList=%s", historyList));

		userIds = pvpBattleHistoryRetrieveUtil
				.getUserIdsFromHistory(historyList);
		log.info(String.format("attacker and defender ids=%s", userIds));

		attackerIds = pvpBattleHistoryRetrieveUtil
				.getAttackerIdsFromHistory(historyList);
		log.info(String.format("attacker ids=%s", attackerIds));

		if (null == historyList || historyList.isEmpty()) {
			log.info("no valid {} pvp battles for user. ", n);
			return;
		}

		if (null != userIds || !userIds.isEmpty()) {
			//don't need the current user
			userIds.remove(userId);
			fillMe.addUserId(userIds);
		}

		if (null != attackerIds || !attackerIds.isEmpty()) {
			//don't need the current user
			attackerIds.remove(userId);
		}

		//separate history list into ones where this user got attacked,
		//and where this user did the attacking
		separateHistory();

		//only need the attacker ids, in order to calculate revenge stuff
		//if this user takes revenge
	}

	public void separateHistory() {
		gotAttackedHistoryList = new ArrayList<PvpBattleHistory>();
		attackedOthersHistoryList = new ArrayList<PvpBattleHistory>();
		for (PvpBattleHistory history : historyList) {
			String attackerId = history.getAttackerId();
			if (attackerId.equals(userId)) {
				//user attacked someone
				attackedOthersHistoryList.add(history);
			} else {
				gotAttackedHistoryList.add(history);
			}
		}
	}

	@Override
	public void execute(StartUpResource useMe) {
		if (null == historyList || historyList.isEmpty()) {
			return;
		}

		if (null != gotAttackedHistoryList && !gotAttackedHistoryList.isEmpty()) {
			setGotAttackedProtos(useMe);
		}

		if (null != attackedOthersHistoryList
				&& !attackedOthersHistoryList.isEmpty()) {
			setAttackedOthersProtos(useMe);
		}

		//create PvpHistory for battles where this user is the one attacking

		//  	log.info("historyProtoList=" + historyProtoList);
	}

	private void setGotAttackedProtos(StartUpResource useMe) {
		log.info("setting gotAttackedPvpProtos, attackerIds={}", attackerIds);
		Map<String, User> idsToAttackers = useMe.getUserIdsToUsers(attackerIds);
		//		log.info(String.format(
		//			"idsToAttackers=%s", idsToAttackers));

		attackerIdsList = new ArrayList<String>(idsToAttackers.keySet());
		selectMonstersForUsers();

		//		log.info(String.format(
		//			"history monster teams=%s", userIdsToUserMonsters));

		Map<String, Map<String, Integer>> userIdToUserMonsterIdToDroppedId = monsterStuffUtils
				.calculatePvpDrops(userIdsToUserMonsters);

		attackerIdsToProspectiveCashWinnings = new HashMap<String, Integer>();
		attackerIdsToProspectiveOilWinnings = new HashMap<String, Integer>();
		PvpUser attackerPu = hazelcastPvpUtil.getPvpUser(userId);
		calculateCashOilRewardFromPvpUsers(attackerPu.getElo(), idsToAttackers,
				attackerIdsToProspectiveCashWinnings,
				attackerIdsToProspectiveOilWinnings);

		Map<String, Clan> attackerIdsToClans = useMe
				.getUserIdsToClans(attackerIds);

		//create PvpHistory for battles where this user got attacked
		List<PvpHistoryProto> historyProtoList = createInfoProtoUtils
				.createGotAttackedPvpHistoryProto(gotAttackedHistoryList,
						idsToAttackers, attackerIdsToClans,
						userIdsToUserMonsters,
						userIdToUserMonsterIdToDroppedId,
						attackerIdsToProspectiveCashWinnings,
						attackerIdsToProspectiveOilWinnings);

		resBuilder.addAllRecentNBattles(historyProtoList);
	}

	//SOOOOOO DISGUSTING.............ALL THIS FUCKING CODE. SO GROSS.
	//COPIED FROM QueueUpController;
	//given users, get the 3 monsters for each user
	private void selectMonstersForUsers() {

		//return value
		userIdsToUserMonsters = new HashMap<String, List<MonsterForUser>>();

		//for all these users, get all their complete monsters
		Map<String, Map<String, MonsterForUser>> userIdsToMfuIdsToMonsters = monsterForUserRetrieveUtils
				.getCompleteMonstersForUser(attackerIdsList);

		for (int index = 0; index < attackerIdsList.size(); index++) {
			//extract a user's monsters
			String defenderId = attackerIdsList.get(index);
			Map<String, MonsterForUser> mfuIdsToMonsters = userIdsToMfuIdsToMonsters
					.get(defenderId);

			if (null == mfuIdsToMonsters || mfuIdsToMonsters.isEmpty()) {
				log.error(String
						.format("WTF!!!!!!!! user has no monsters!!!!! userId=%s will move on to next guy.",
								defenderId));
				continue;
			}
			//try to select at most 3 monsters for this user
			List<MonsterForUser> defenderMonsters = selectMonstersForUser(mfuIdsToMonsters);

			//if the user still doesn't have 3 monsters, then too bad
			userIdsToUserMonsters.put(defenderId, defenderMonsters);
		}

	}

	private List<MonsterForUser> selectMonstersForUser(
			Map<String, MonsterForUser> mfuIdsToMonsters) {

		//get all the monsters the user has on a team (at the moment, max is 3)
		List<MonsterForUser> defenderMonsters = getEquippedMonsters(mfuIdsToMonsters);

		//so users can have no monsters equipped, so just choose one fucking monster for him
		if (defenderMonsters.isEmpty()) {
			List<MonsterForUser> randMonsters = new ArrayList<MonsterForUser>(
					mfuIdsToMonsters.values());
			defenderMonsters.add(randMonsters.get(0));
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

	//Similar logic to calculateCashOilRewards in QueueUpController
	private void calculateCashOilRewardFromPvpUsers(int attackerElo,
			Map<String, User> userIdsToUsers,
			Map<String, Integer> userIdToCashStolen,
			Map<String, Integer> userIdToOilStolen) {
		Collection<String> userIdz = userIdsToUsers.keySet();
		Map<String, PvpUser> idsToPvpUsers = hazelcastPvpUtil
				.getPvpUsers(userIdz);

		for (String defenderId : userIdz) {
			String defenderEyed = defenderId.toString();

			User defender = userIdsToUsers.get(defenderId);
			PvpUser defenderPu = idsToPvpUsers.get(defenderEyed);

			PvpBattleOutcome potentialResult = new PvpBattleOutcome(user,
					attackerElo, defender, defenderPu.getElo(),
					serverToggleRetrieveUtil);

			userIdToCashStolen.put(defenderId,
					potentialResult.getUnsignedCashAttackerWins());
			userIdToOilStolen.put(defenderId,
					potentialResult.getUnsignedOilAttackerWins());
		}
	}

	private void setAttackedOthersProtos(StartUpResource useMe) {
		//bool attackerWon, defenderCash, defenderOil
		//optional int64 battleEndTime = 9; //defined above

		//for the attacker/defender
		//need the prev and cur rank
		//need the prev and cur league
		//optional UserPvpLeagueProto attackerBefore = 10; //before the battle
		//optional UserPvpLeagueProto attackerAfter = 11; //after the battle

		//optional UserPvpLeagueProto defenderBefore = 12; //before the battle
		//optional UserPvpLeagueProto defenderA

		Map<String, User> idsToUsers = useMe.getUserIdsToUsers(userIds);

		//create PvpHistory for battles where this user attacked others
		List<PvpHistoryProto> historyProtoList = createInfoProtoUtils
				.createAttackedOthersPvpHistoryProto(userId, idsToUsers,
						attackedOthersHistoryList);

		resBuilder.addAllRecentNBattles(historyProtoList);

	}
}
