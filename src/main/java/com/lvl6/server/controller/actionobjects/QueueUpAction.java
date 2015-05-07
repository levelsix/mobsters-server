package com.lvl6.server.controller.actionobjects;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.EloPair;
import com.lvl6.info.MonsterForPvp;
import com.lvl6.info.PvpLeagueForUser;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventPvpProto.QueueUpResponseProto.Builder;
import com.lvl6.proto.EventPvpProto.QueueUpResponseProto.QueueUpStatus;
import com.lvl6.pvp.HazelcastPvpUtil;
import com.lvl6.pvp.PvpUser;
import com.lvl6.pvp.PvpUtil2;
import com.lvl6.retrieveutils.PvpLeagueForUserRetrieveUtil2;
import com.lvl6.retrieveutils.rarechange.MonsterForPvpRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ServerToggleRetrieveUtils;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

public class QueueUpAction {
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String attackerId;
	private Set<String> userIdBlackList;
	private Date clientDate;
	private PvpLeagueForUserRetrieveUtil2 pvpLeagueForUserRetrieveUtil;
	private HazelcastPvpUtil hazelcastPvpUtil;
	private MonsterForPvpRetrieveUtils monsterForPvpRetrieveUtil;
	private TimeUtils timeUtil;
	private ServerToggleRetrieveUtils serverToggleRetrieveUtils;

	public QueueUpAction(String attackerId, Set<String> userIdBlackList,
			Date clientDate,
			PvpLeagueForUserRetrieveUtil2 pvpLeagueForUserRetrieveUtil,
			HazelcastPvpUtil hazelcastPvpUtil,
			MonsterForPvpRetrieveUtils monsterForPvpRetrieveUtil,
			TimeUtils timeUtil, ServerToggleRetrieveUtils serverToggleRetrieveUtils) {
		super();
		this.attackerId = attackerId;
		this.userIdBlackList = userIdBlackList;
		this.clientDate = clientDate;
		this.pvpLeagueForUserRetrieveUtil = pvpLeagueForUserRetrieveUtil;
		this.hazelcastPvpUtil = hazelcastPvpUtil;
		this.monsterForPvpRetrieveUtil = monsterForPvpRetrieveUtil;
		this.timeUtil = timeUtil;
		this.serverToggleRetrieveUtils = serverToggleRetrieveUtils;
	}

	//	//encapsulates the return value from this Action Object
	//	static class QueueUpResource {
	//		
	//		
	//		public QueueUpResource() {
	//			
	//		}
	//	}
	//
	//	public QueueUpResource execute() {
	//		
	//	}

	//derived state
	private PvpLeagueForUser attackerPlfu;
	private int attackerElo;
	private List<EloPair> listOfEloPairs;
	private int minElo;
	private int maxElo;
	private List<String> queuedOpponentIdsList;
	//	private Map<String, PvpUser> userIdToPvpUser;
	private List<List<MonsterForPvp>> fakeUserMonsters;

	public void execute(Builder resBuilder) {
		resBuilder.setStatus(QueueUpStatus.FAIL_OTHER);

		//check out inputs before db interaction
		boolean valid = verifySyntax(resBuilder);

		if (!valid) {
			return;
		}

		valid = verifySemantics(resBuilder);

		if (!valid) {
			return;
		}

		boolean success = writeChangesToDB(resBuilder);
		if (!success) {
			return;
		}

		resBuilder.setStatus(QueueUpStatus.SUCCESS);

	}

	private boolean verifySyntax(Builder resBuilder) {

		return true;
	}

	private boolean verifySemantics(Builder resBuilder) {
		attackerPlfu = pvpLeagueForUserRetrieveUtil
				.getUserPvpLeagueForId(attackerId);

		if (null == attackerPlfu) {
			log.error("no PvpLeagueForUser with id={}", attackerId);
			return false;
		}

		return true;
	}

	private boolean writeChangesToDB(Builder resBuilder) {
		selectRealUsers();
		selectFakeDefenders();

		//TODO: this is the same logic in BeginPvpBattleController 
		//turn off user's shield if he has one active
		Date curShieldEndTime = attackerPlfu.getShieldEndTime();
		if (timeUtil.isFirstEarlierThanSecond(clientDate, curShieldEndTime)) {
			log.info("shield end time is now being reset since he's attacking with a shield");
			log.info("1)cur pvpuser={}",
					hazelcastPvpUtil.getPvpUser(attackerId));
			//			Date login = attacker.getLastLogin();
			//			Timestamp loginTime = new Timestamp(login.getTime());
			Timestamp queueTime = new Timestamp(clientDate.getTime());
			UpdateUtils.get().updatePvpLeagueForUserShields(attackerId,
					queueTime, queueTime);

			PvpUser attackerOpu = new PvpUser(attackerPlfu);
			attackerOpu.setShieldEndTime(clientDate);
			attackerOpu.setInBattleEndTime(clientDate);
			hazelcastPvpUtil.replacePvpUser(attackerOpu, attackerId);
			log.info("2)cur pvpuser={}",
					hazelcastPvpUtil.getPvpUser(attackerId));
			log.info(" (should be same as 2cur pvpUser) 3cur pvpuser={}",
					attackerOpu);
		}

		return true;
	}

	private void selectRealUsers() {
		attackerElo = attackerPlfu.getElo();

		listOfEloPairs = PvpUtil2
				.getMinAndMaxElo(attackerElo);

		boolean attackerBelowSomeElo = attackerElo < ControllerConstants.PVP__MAX_ELO_TO_DISPLAY_ONLY_BOTS;
		boolean showBotsBelowSomeElo = serverToggleRetrieveUtils
				.getToggleValueForName(ControllerConstants.SERVER_TOGGLE__PVP_BOTS_ONLY_BELOW_SOME_ELO);
		boolean pvpBotsOnly = showBotsBelowSomeElo && attackerBelowSomeElo;
		int numNeeded = ControllerConstants.PVP__MAX_QUEUE_SIZE;
		
		if (!pvpBotsOnly) {
			//get the users that the attacker will fight
			log.info("im here1");
			getQueuedOpponentIds(numNeeded, listOfEloPairs);
		}
		else {
			//get half bots and half real players
			log.info("im here2");
			getQueuedOpponentIds(numNeeded/2, createListOfEloPairs());
			log.info("how many real users i got: ", queuedOpponentIdsList.size());
		}
	}
	
	private List<EloPair> createListOfEloPairs() {
		List<EloPair> eloPairList = new ArrayList<EloPair>();
		eloPairList.add(new EloPair(1100, 1200));
		eloPairList.add(new EloPair(1100, 1200));
		eloPairList.add(new EloPair(1100, 1200));
		eloPairList.add(new EloPair(1100, 1200));
		eloPairList.add(new EloPair(1100, 1200));
		return eloPairList;
	}

	private void getQueuedOpponentIds(int numNeeded, List<EloPair> eloPairList) {
		Set<PvpUser> prospectiveDefenders = hazelcastPvpUtil.retrievePvpUsers(
				eloPairList, clientDate, numNeeded, userIdBlackList);

		int numDefenders = prospectiveDefenders.size();
		//		log.info("users returned from hazelcast pvp util. users={}", prospectiveDefenders);

		//choose users either randomly or all of them
		queuedOpponentIdsList = new ArrayList<String>();

		for(PvpUser pUser : prospectiveDefenders) {
			queuedOpponentIdsList.add(pUser.getUserId());
		}
		
//		selectUsers(numNeeded, numDefenders, prospectiveDefenders,
//				queuedOpponentIdsList);//, userIdToPvpUser);

		log.info("the lucky ids who get to be attacked! ids={}",
				queuedOpponentIdsList);
	}

	//prospectiveUserIds and userIdToPvpUser are the return values
//	private void selectUsers(int numNeeded, int numDefenders,
//			Set<PvpUser> prospectiveDefenders, List<String> prospectiveUserIds)//,
//	//Map<String, PvpUser> userIdToPvpUser)
//	{
//		Random rand = ControllerConstants.RAND;
//
//		float numNeededSoFar = numNeeded;
//		float numDefendersLeft = numDefenders;
//		//go through them and select the one that has not been seen yet
//		for (PvpUser pvpUser : prospectiveDefenders) {
//			//regardless of numNeededSoFar
//			//			log.info("pvp opponents, numNeeded={}", numNeededSoFar);
//			//			log.info("pvp opponents, numAvailable={}", numDefendersLeft);
//
//			if (prospectiveUserIds.size() >= ControllerConstants.PVP__MAX_QUEUE_SIZE) {
//				//don't want to send every eligible victim to user.
//				log.info("reached queue length of {}",
//						ControllerConstants.PVP__MAX_QUEUE_SIZE);
//				break;
//			}
//
//			String userId = pvpUser.getUserId();
//			//if we whittle down the entire applicant pool to the minimum,
//			//we want to select all of them
//			if (numNeededSoFar >= numDefendersLeft) {
//				prospectiveUserIds.add(userId);
//				//userIdToPvpUser.put(userId, pvpUser);
//				numNeededSoFar -= 1;
//				numDefendersLeft -= 1;
//				continue;
//			}
//
//			//randomly pick people
//			float randFloat = rand.nextFloat();
//			float probabilityToBeSelected = numNeededSoFar / numDefendersLeft;
//			//			log.info("randFloat={}", randFloat);
//			//			log.info("probabilityToBeSelected={}", probabilityToBeSelected);
//			if (randFloat < probabilityToBeSelected) {
//				//we have a winner!
//				prospectiveUserIds.add(userId);
//				//userIdToPvpUser.put(userId, pvpUser);
//				numNeededSoFar -= 1;
//			}
//			numDefendersLeft -= 1;
//		}
//	}

	private void selectFakeDefenders() {
		int numWanted = ControllerConstants.PVP__MAX_QUEUE_SIZE;

		if (null == queuedOpponentIdsList) {
			queuedOpponentIdsList = new ArrayList<String>();
		}

		if (queuedOpponentIdsList.size() >= numWanted) {
			return;
		}
		numWanted = numWanted - queuedOpponentIdsList.size();
		generateFakeDefenders(numWanted);
	}

	private void generateFakeDefenders(int numWanted) { //numWanted not currently used
		//GENERATE THE FAKE DEFENDER AND MONSTERS, not enough enemies, get fake ones
		log.info("no valid users for attacker={}", attackerId);
		log.info("generating fake users.");
		
		fakeUserMonsters = new ArrayList<List<MonsterForPvp>>();
		for(EloPair ep : listOfEloPairs) {
			int minElo = ep.getMinElo();
			int maxElo = ep.getMaxElo();
			
			Set<MonsterForPvp> fakeMonsters = monsterForPvpRetrieveUtil
					.retrievePvpMonsters(minElo, maxElo);

			try {
				//group monsters off
				//25% of the time one monster
				//50% of the time two monsters
				//25% of the time three monsters
				//limit the number of groups of 3
				//NOTE: this is assuming there are more than enough monsters...
				fakeUserMonsters.addAll(createFakeUserMonsters(fakeMonsters, 1)); //used to have numwanted as field here

				if (!fakeUserMonsters.isEmpty()) {
					//				List<PvpProto> pvpProtoListTemp = createPvpProtosFromFakeUser(
					//					fakeUserMonsters, attackerElo);
				} else {
					log.error("no fake users generated. minElo={} \t maxElo={}",
							minElo, maxElo);
				}
			} catch (Exception e) {
				log.error(
						String.format(
								"creating fake user exceptioned out. fakeMonsters=%s, numWanted=%s",
								fakeMonsters, numWanted), e);
			}
		}
	}

	private List<List<MonsterForPvp>> createFakeUserMonsters(
			Set<MonsterForPvp> fakeMonsters, int numGroupsWanted) {
		List<List<MonsterForPvp>> retVal = new ArrayList<List<MonsterForPvp>>();

		if (null == fakeMonsters || fakeMonsters.isEmpty()) {
			return retVal;
		}

		Random rand = ControllerConstants.RAND;
		int numAvailableMonsters = fakeMonsters.size();

		//generate |numGroupsWanted| amount of groups
		//25% of the time one monster
		//50% of the time two monsters
		//25% of the time three monsters
		List<MonsterForPvp> monstersAvailable = new ArrayList<MonsterForPvp>(
				fakeMonsters);
		for (int i = 0; i < numGroupsWanted; i++) {
			//a hack to simulate the probability listed
			int numMonstersWanted = rand.nextInt(4) + 1;
			if (numMonstersWanted % 2 == 0) {
				//50 percent of time, want 2 monsters
				numMonstersWanted = 2;
			}

			List<MonsterForPvp> grouping = createFakeUserMonstersGrouping(rand,
					numAvailableMonsters, monstersAvailable, numMonstersWanted);

			retVal.add(grouping);
		}

		return retVal;
	}

	private List<MonsterForPvp> createFakeUserMonstersGrouping(Random rand,
			int numAvailableMonsters, List<MonsterForPvp> fakeMonstersList,
			int numMonsters) {
		List<MonsterForPvp> grouping = new ArrayList<MonsterForPvp>(numMonsters);

		for (int index = 0; index < numMonsters; index++) {
			int monsterIndex = rand.nextInt(numAvailableMonsters);
			MonsterForPvp mfp = fakeMonstersList.get(monsterIndex);
			grouping.add(mfp);
		}

		return grouping;
	}

	public List<String> getQueuedOpponentIdsList() {
		return queuedOpponentIdsList;
	}

	public List<List<MonsterForPvp>> getFakeUserMonsters() {
		return fakeUserMonsters;
	}

	public int getAttackerElo() {
		return attackerElo;
	}

}
