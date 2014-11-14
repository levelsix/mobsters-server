package com.lvl6.server.controller.actionobjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.lvl6.info.MonsterForUser;
import com.lvl6.info.PvpBattleHistory;
import com.lvl6.info.User;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.BattleProto.PvpHistoryProto;
import com.lvl6.proto.EventStartupProto.StartupResponseProto;
import com.lvl6.pvp.HazelcastPvpUtil;
import com.lvl6.pvp.PvpBattleOutcome;
import com.lvl6.pvp.PvpUser;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2;
import com.lvl6.retrieveutils.PvpBattleHistoryRetrieveUtil2;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;

public class SetPvpBattleHistoryAction implements StartUpAction
{
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private final StartupResponseProto.Builder resBuilder;
	private final User user;
	private final String userId;
  private final PvpBattleHistoryRetrieveUtil2 pvpBattleHistoryRetrieveUtil;
  private final MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils;
	private final HazelcastPvpUtil hazelcastPvpUtil;
	
	public SetPvpBattleHistoryAction(
		StartupResponseProto.Builder resBuilder, User user,
		String userId, PvpBattleHistoryRetrieveUtil2 pvpBattleHistoryRetrieveUtil,
		MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils,
		HazelcastPvpUtil hazelcastPvpUtil)
	{
		this.resBuilder = resBuilder;
		this.user = user;
		this.userId = userId;
		this.pvpBattleHistoryRetrieveUtil = pvpBattleHistoryRetrieveUtil; 
		this.hazelcastPvpUtil = hazelcastPvpUtil;
		this.monsterForUserRetrieveUtils = monsterForUserRetrieveUtils;
	}
	
	//derived state
	private List<PvpBattleHistory> historyList;
	private Set<String> attackerIds;
	
	
	private List<String> attackerIdsList;
	private Map<String, List<MonsterForUser>> userIdsToUserMonsters;
	private Map<String, Integer> attackerIdsToProspectiveCashWinnings;
	private Map<String, Integer> attackerIdsToProspectiveOilWinnings;
	
	//Extracted from Startup
	@Override
	public void setUp(StartUpResource fillMe)
	{
		int n = ControllerConstants.PVP_HISTORY__NUM_RECENT_BATTLES;

		//NOTE: AN ATTACKER MIGHT SHOW UP MORE THAN ONCE DUE TO REVENGE
		historyList = pvpBattleHistoryRetrieveUtil
			.getRecentNBattlesForDefenderId(userId, n);
		log.info(String.format(
			"historyList=%s", historyList));

		attackerIds = pvpBattleHistoryRetrieveUtil
			.getAttackerIdsFromHistory(historyList);
		log.info(String.format(
			"attackerIds=%s", attackerIds));

		if (null == attackerIds || attackerIds.isEmpty()) {
			log.info("no valid 10 pvp battles for user. ");
			return;
		}
		
		fillMe.addUserId(attackerIds);
	}

	@Override
	public void execute( StartUpResource useMe )
	{
		if (null == attackerIds || attackerIds.isEmpty()) {
			return;
		}
		
		Map<String, User> idsToAttackers = useMe.getUserIdsToUsers(attackerIds);
		log.info(String.format(
			"idsToAttackers=%s", idsToAttackers));

		attackerIdsList = new ArrayList<String>(idsToAttackers.keySet());
		selectMonstersForUsers();
		
		log.info(String.format(
			"history monster teams=%s", userIdsToUserMonsters));

		attackerIdsToProspectiveCashWinnings = new HashMap<String, Integer>();
		attackerIdsToProspectiveOilWinnings = new HashMap<String, Integer>();
		PvpUser attackerPu = hazelcastPvpUtil.getPvpUser(userId);
		calculateCashOilRewardFromPvpUsers(attackerPu.getElo(),
			idsToAttackers, attackerIdsToProspectiveCashWinnings,
			attackerIdsToProspectiveOilWinnings);

		List<PvpHistoryProto> historyProtoList = CreateInfoProtoUtils
			.createPvpHistoryProto(historyList, idsToAttackers, userIdsToUserMonsters,
				attackerIdsToProspectiveCashWinnings, attackerIdsToProspectiveOilWinnings);

		//  	log.info("historyProtoList=" + historyProtoList);
		resBuilder.addAllRecentNBattles(historyProtoList);
	}
	
	//SOOOOOO DISGUSTING.............ALL THIS FUCKING CODE. SO GROSS.
	//COPIED FROM QueueUpController;
	//given users, get the 3 monsters for each user
	private void selectMonstersForUsers() {

		//return value
		userIdsToUserMonsters = new HashMap<String, List<MonsterForUser>>();

		//for all these users, get all their complete monsters
		Map<String, Map<String, MonsterForUser>> userIdsToMfuIdsToMonsters = 
		    monsterForUserRetrieveUtils.getCompleteMonstersForUser(attackerIdsList);


		for (int index = 0; index < attackerIdsList.size(); index++) {
			//extract a user's monsters
		  String defenderId = attackerIdsList.get(index);
			Map<String, MonsterForUser> mfuIdsToMonsters = userIdsToMfuIdsToMonsters.get(defenderId);

			if (null == mfuIdsToMonsters || mfuIdsToMonsters.isEmpty()) {
				log.error(String.format(
					"WTF!!!!!!!! user has no monsters!!!!! userId=%s will move on to next guy.",
					defenderId));
				continue;
			}
			//try to select at most 3 monsters for this user
			List<MonsterForUser> defenderMonsters = selectMonstersForUser(mfuIdsToMonsters);

			//if the user still doesn't have 3 monsters, then too bad
			userIdsToUserMonsters.put(defenderId, defenderMonsters);
		}

	}
	private List<MonsterForUser> selectMonstersForUser(Map<String, MonsterForUser> mfuIdsToMonsters) {

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
	private List<MonsterForUser> getEquippedMonsters(Map<String, MonsterForUser> userMonsters) {
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
	private void calculateCashOilRewardFromPvpUsers(
		int attackerElo, Map<String, User> userIdsToUsers,
		Map<String, Integer> userIdToCashStolen,
		Map<String, Integer> userIdToOilStolen )
	{
		Collection<String> userIdz = userIdsToUsers.keySet() ;
		Map<String, PvpUser> idsToPvpUsers = hazelcastPvpUtil
			.getPvpUsers(userIdz);

		for (String defenderId : userIdz) {
			String defenderEyed = defenderId.toString();

			User defender = userIdsToUsers.get(defenderId);
			PvpUser defenderPu = idsToPvpUsers.get(defenderEyed);

			PvpBattleOutcome potentialResult = new PvpBattleOutcome(
				userId, attackerElo, defenderId, defenderPu.getElo(),
				defender.getCash(), defender.getOil());

			userIdToCashStolen.put(defenderId, 
				potentialResult.getUnsignedCashAttackerWins());
			userIdToOilStolen.put(defenderId, 
				potentialResult.getUnsignedOilAttackerWins());
		}
	}
}
