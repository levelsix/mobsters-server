package com.lvl6.server.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.QueueUpRequestEvent;
import com.lvl6.events.response.QueueUpResponseEvent;
import com.lvl6.info.Monster;
import com.lvl6.info.MonsterForPvp;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.BattleProto.PvpProto;
import com.lvl6.proto.EventMonsterProto.RetrieveUserMonsterTeamResponseProto;
import com.lvl6.proto.EventMonsterProto.RetrieveUserMonsterTeamResponseProto.RetrieveUserMonsterTeamStatus;
import com.lvl6.proto.EventPvpProto.QueueUpRequestProto;
import com.lvl6.proto.EventPvpProto.QueueUpResponseProto;
import com.lvl6.proto.EventPvpProto.QueueUpResponseProto.Builder;
import com.lvl6.proto.EventPvpProto.QueueUpResponseProto.QueueUpStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.pvp.HazelcastPvpUtil;
import com.lvl6.pvp.PvpBattleOutcome;
import com.lvl6.retrieveutils.ClanMemberTeamDonationRetrieveUtil;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2;
import com.lvl6.retrieveutils.MonsterSnapshotForUserRetrieveUtil;
import com.lvl6.retrieveutils.PvpBoardObstacleForUserRetrieveUtil;
import com.lvl6.retrieveutils.PvpLeagueForUserRetrieveUtil2;
import com.lvl6.retrieveutils.ResearchForUserRetrieveUtils;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.MonsterForPvpRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterLevelInfoRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ServerToggleRetrieveUtils;
import com.lvl6.server.controller.actionobjects.QueueUpAction;
import com.lvl6.server.controller.actionobjects.RetrieveUserMonsterTeamAction;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.CreateInfoProtoUtils;

@Component
@DependsOn("gameServer")
public class QueueUpController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	protected HazelcastPvpUtil hazelcastPvpUtil;

	@Autowired
	protected MonsterForPvpRetrieveUtils monsterForPvpRetrieveUtil;

	@Autowired
	protected TimeUtils timeUtil;

	@Autowired
	protected PvpLeagueForUserRetrieveUtil2 pvpLeagueForUserRetrieveUtil;

	@Autowired
	protected ClanRetrieveUtils2 clanRetrieveUtil;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	protected MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtil;

	@Autowired
	private ClanMemberTeamDonationRetrieveUtil clanMemberTeamDonationRetrieveUtil;

	@Autowired
	private MonsterSnapshotForUserRetrieveUtil monsterSnapshotForUserRetrieveUtil;

	@Autowired
	private PvpBoardObstacleForUserRetrieveUtil pvpBoardObstacleForUserRetrieveUtil;

	@Autowired
	private ResearchForUserRetrieveUtils researchForUserRetrieveUtil;

	@Autowired
	protected MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils;

	@Autowired
	protected MonsterStuffUtils monsterStuffUtils;

	@Autowired
	protected MonsterRetrieveUtils monsterRetrieveUtils;

	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;

	@Autowired
	protected ServerToggleRetrieveUtils serverToggleRetrieveUtil;


	//	@Autowired
	//	protected PvpUtil pvpUtil;
	//
	//	public PvpUtil getPvpUtil() {
	//		return pvpUtil;
	//	}
	//
	//	public void setPvpUtil(PvpUtil pvpUtil) {
	//		this.pvpUtil = pvpUtil;
	//	}

	public QueueUpController() {
		numAllocatedThreads = 10;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new QueueUpRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_QUEUE_UP_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		QueueUpRequestProto reqProto = ((QueueUpRequestEvent) event)
				.getQueueUpRequestProto();

		log.info("reqProto={}", reqProto);

		MinimumUserProto attackerProto = reqProto.getAttacker();
		String attackerId = attackerProto.getUserUuid();

		List<String> seenUserIds = reqProto.getSeenUserUuidsList();
		Set<String> uniqSeenUserIds = new HashSet<String>(seenUserIds);
		//don't want the attacker to see himself
		uniqSeenUserIds.add(attackerId);

		Date clientDate = new Date(reqProto.getClientTime());

		//set some values to send to the client
		QueueUpResponseProto.Builder resBuilder = QueueUpResponseProto
				.newBuilder();
		resBuilder.setAttacker(attackerProto);
		resBuilder.setStatus(QueueUpStatus.FAIL_OTHER);

		boolean invalidUuids = true;
		try {
			UUID.fromString(attackerId);

			if (uniqSeenUserIds != null) {
				for (String seenUserId : uniqSeenUserIds) {
					UUID.fromString(seenUserId);
				}
			}

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
					"UUID error. incorrect userId=%s, seenUserIds=%s",
					attackerId, uniqSeenUserIds), e);
			invalidUuids = true;
		}

		if (invalidUuids) {
			resBuilder.setStatus(QueueUpStatus.FAIL_OTHER);
			QueueUpResponseEvent resEvent = new QueueUpResponseEvent(attackerId);
			resEvent.setTag(event.getTag());
			resEvent.setQueueUpResponseProto(resBuilder.build());
			server.writeEvent(resEvent);
			return;
		}

		try {
			//			User attacker = userRetrieveUtil.getUserById(attackerId);
			//			PvpLeagueForUser plfu = getPvpLeagueForUserRetrieveUtil()
			//				.getUserPvpLeagueForId(attackerId);

			QueueUpAction qua = new QueueUpAction(attackerId, uniqSeenUserIds,
					clientDate, pvpLeagueForUserRetrieveUtil, hazelcastPvpUtil,
					monsterForPvpRetrieveUtil, timeUtil, serverToggleRetrieveUtil);

			//update the user, and his shield
			qua.execute(resBuilder);
			setProspectivePvpMatches(resBuilder, attackerId, qua);

			//write event to the client
			QueueUpResponseEvent resEvent = new QueueUpResponseEvent(attackerId);
			resEvent.setTag(event.getTag());
			resEvent.setQueueUpResponseProto(resBuilder.build());
			server.writeEvent(resEvent);

			if (QueueUpStatus.SUCCESS.equals(resBuilder.getStatus())) {
				//no need to update client since no currency or elo update
				//UPDATE CLIENT
				//null PvpLeagueFromUser means will pull from hazelcast instead
				//				UpdateClientUserResponseEvent resEventUpdate = MiscMethods
				//					.createUpdateClientUserResponseEventAndUpdateLeaderboard(
				//						attacker, plfu, null);
				//				resEventUpdate.setTag(event.getTag());
				//				server.writeEvent(resEventUpdate);
			}

		} catch (Exception e) {
			log.error("exception in QueueUp processEvent", e);
			resBuilder.setStatus(QueueUpStatus.FAIL_OTHER);
			QueueUpResponseEvent resEvent = new QueueUpResponseEvent(attackerId);
			resEvent.setTag(event.getTag());
			resEvent.setQueueUpResponseProto(resBuilder.build());
			server.writeEvent(resEvent);
		}
	}

	/*
	private boolean checkLegitQueueUp(Builder resBuilder, User u, Date clientDate) {
		//int gemsSpent, int cashChange) {
		if (null == u) {
			resBuilder.setStatus(QueueUpStatus.FAIL_OTHER);
			log.error("problem with QueueUp- attacker is null. user is " + u);
			return false;
		}

		//see if user has enough money to find a person to fight
		//CHECK MONEY
		//		if (!hasEnoughGems(resBuilder, u, gemsSpent, cashChange)) {
		//			return false;
		//		}
		//
		//		if (!hasEnoughCash(resBuilder, u, gemsSpent, cashChange)) {
		//			return false;
		//		}

		//		User queuedOpponent = queuedOpponent(attacker, elo, seenUserIds, clientDate);
		//
		//		queuedOpponentList.add(queuedOpponent);
		resBuilder.setStatus(QueueUpStatus.SUCCESS);
		return true;
	}*/

	//	private boolean hasEnoughGems(Builder resBuilder, User u, int gemsSpent, int cashChange) {
	//		int userGems = u.getGems();
	//		//if user's aggregate gems is < cost, don't allow transaction
	//		if (userGems < gemsSpent) {
	//			log.error("user error: user does not have enough gems. userGems=" + userGems +
	//					"\t gemsSpent=" + gemsSpent + "\t user=" + u);
	//			resBuilder.setStatus(QueueUpStatus.FAIL_NOT_ENOUGH_GEMS);
	//			return false;
	//		}
	//		return true;
	//	}
	//
	//	private boolean hasEnoughCash(Builder resBuilder, User u, int gemsSpent, int cashChange) {
	//		int userCash = u.getCash();
	//		//positive 'cashChange' means refund, negative means charge user
	//		int cost = -1 * cashChange;
	//
	//		//if user not spending gems and is just spending cash, check if he has enough
	//		if (0 == gemsSpent && userCash < cost) {
	//			log.error("user error: user does not have enough cash. userCash=" + userCash +
	//					"\t cost=" + cost + "\t user=" + u);
	//			resBuilder.setStatus(QueueUpStatus.FAIL_NOT_ENOUGH_CASH);
	//			return false;
	//		}
	//		return true;
	//	}

	private void setProspectivePvpMatches(Builder resBuilder,
			String attackerId, QueueUpAction qua) {

		List<PvpProto> pvpProtoList = new ArrayList<PvpProto>();
		List<String> queuedOpponentIdsList = qua.getQueuedOpponentIdsList();
		if (null != queuedOpponentIdsList && !queuedOpponentIdsList.isEmpty()) {
			log.info("there are people to attack!");

			RetrieveUserMonsterTeamAction rumta = new RetrieveUserMonsterTeamAction(
					attackerId, queuedOpponentIdsList, userRetrieveUtil,
					clanRetrieveUtil, monsterForUserRetrieveUtil,
					clanMemberTeamDonationRetrieveUtil,
					monsterSnapshotForUserRetrieveUtil, hazelcastPvpUtil,
					pvpLeagueForUserRetrieveUtil,
					pvpBoardObstacleForUserRetrieveUtil,
					researchForUserRetrieveUtil,
					monsterStuffUtils, serverToggleRetrieveUtil);

			RetrieveUserMonsterTeamResponseProto.Builder tempResBuilder = RetrieveUserMonsterTeamResponseProto
					.newBuilder();
			rumta.execute(tempResBuilder);

			if (RetrieveUserMonsterTeamStatus.SUCCESS.equals(tempResBuilder
					.getStatus())) {
				List<PvpProto> ppList = createInfoProtoUtils
						.createPvpProtos(
								rumta.getAllUsersExceptRetriever(),
								rumta.getUserIdToClan(),
								null,
								rumta.getUserIdToPvpUsers(),
								rumta.getAllButRetrieverUserIdToUserMonsters(),
								rumta.getAllButRetrieverUserIdToUserMonsterIdToDroppedId(),
								rumta.getAllButRetrieverUserIdToCashLost(),
								rumta.getAllButRetrieverUserIdToOilLost(),
								rumta.getAllButRetrieverUserIdToCmtd(),
								rumta.getAllButRetrieverUserIdToMsfu(),
								rumta.getAllButRetrieverUserIdToMsfuMonsterDropId(),
								rumta.getAllButRetrieverUserIdToPvpBoardObstacles(),
								rumta.getAllButRetrieverUserIdToUserResearch());

				log.info("ppList={}", ppList);
				//user should see real people before fake ones
				pvpProtoList.addAll(0, ppList);
			} else {
				log.error("unable to get monsters for users: {}",
						queuedOpponentIdsList);
			}

		}

		List<List<MonsterForPvp>> fakeUserMonsters = qua.getFakeUserMonsters();
		if (null != fakeUserMonsters && !fakeUserMonsters.isEmpty()) {
			int attackerElo = qua.getAttackerElo();
			List<PvpProto> ppList = createPvpProtosFromFakeUser(
					fakeUserMonsters, attackerElo);
			pvpProtoList.addAll(ppList);
		}

		if (pvpProtoList.isEmpty()) {
			resBuilder.setStatus(QueueUpStatus.FAIL_OTHER);
			log.error("no real nor fake players to fight");
			return;
		}

		resBuilder.addAllDefenderInfoList(pvpProtoList);
		//log.info("pvpProtoList={}", pvpProtoList);
	}

	/*
	//unused method. Purpose was user randomly gets users from an elo range
	//out of six possible elo ranges.
	private void getMinMaxElo(int attackerElo, List<Integer> minEloList,
			List<Integer> maxEloList) {

		int firstEloBound = Math.max(0,
				attackerElo - ControllerConstants.PVP__ELO_DISTANCE_THREE);
		int secondEloBound = Math.max(0,
				attackerElo - ControllerConstants.PVP__ELO_DISTANCE_TWO);
		int thirdEloBound = Math.max(0,
				attackerElo - ControllerConstants.PVP__ELO_DISTANCE_ONE);
		int fourthEloBound = attackerElo +
				ControllerConstants.PVP__ELO_DISTANCE_ONE;
		int fifthEloBound = attackerElo +
				ControllerConstants.PVP__ELO_DISTANCE_TWO;
		int sixthEloBound = attackerElo +
				ControllerConstants.PVP__ELO_DISTANCE_THREE;

		//get the min and max elo, initial values are dummy values
		int minElo = 0;
		int maxElo = attackerElo;
		Random rand = new Random();

		float randFloat = rand.nextFloat();
		if(randFloat <
				ControllerConstants.PVP__ELO_CATEGORY_ONE_PAIRING_CHANCE) {
			log.info("in first elo category");
			minElo = firstEloBound;
			maxElo = secondEloBound;

		} else if(randFloat <
				ControllerConstants.PVP__ELO_CATEGORY_TWO_PAIRING_CHANCE) {
			log.info("in second elo category");
			minElo = secondEloBound;
			maxElo = thirdEloBound;

		} else if(randFloat <
				ControllerConstants.PVP__ELO_CATEGORY_THREE_PAIRING_CHANCE) {
			log.info("in third elo category");
			minElo = thirdEloBound;
			maxElo = attackerElo;

		} else if(randFloat <
				ControllerConstants.PVP__ELO_CATEGORY_FOUR_PAIRING_CHANCE) {
			log.info("in fourth elo category");

			minElo = attackerElo;
			maxElo = fourthEloBound;

		} else if(randFloat <
				ControllerConstants.PVP__ELO_CATEGORY_FIVE_PAIRING_CHANCE) {
			log.info("in fifth elo category");
			minElo = fourthEloBound;
			maxElo = fifthEloBound;

		} else {
			log.info("in sixth elo category");
			minElo = fifthEloBound;
			maxElo = sixthEloBound;

		}

		//this is to ensure that elos being searched for are not below 0
		log.info("minElo before maxing with 0: " + minElo);
		log.info("maxElo before maxing with 0: " + maxElo);
		minElo = Math.max(0, minElo);
		maxElo = Math.max(0, maxElo);
		log.info("minElo after maxing with 0: " + minElo);
		log.info("maxElo after maxing with 0: " + maxElo);

		minEloList.add(minElo);
		maxEloList.add(maxElo);
	}*/

	//purpose of userIdList is to prevent another iteration through the return list just
	//to extract the user ids
	/*
	private List<User> getQueuedOpponents( int attackerElo, int minElo,
		int maxElo, Set<String> seenUserIds, Date clientDate,
		List<String> userIdList, Map<String, PvpUser> userIdToPvpUser) {
		//inefficient: 5 db calls
		//		List<User> qList = getUserRetrieveUtils().retrieveCompleteQueueList(
		//				attacker, elo, seenUserIds, clientDate);
		//		if(qList.isEmpty()) {
		//			return null;
		//		}
		//		else {
		//			Random rand = new Random();
		//			User queuedOpponent = qList.get(rand.nextInt(qList.size()));
		//			return queuedOpponent;
		//		}
		//		User defender = null;

		//jedis, redis stuff
		//		//now having elo range, figure out the offset
		//		//could make a range call and do stuff after but eh
		//		int offset = 0;
		//		int limit = ControllerConstants.PVP__NUM_ENEMIES_LIMIT;
		//
		//		Set<Tuple> prospectiveDefenders = getPvpUtil().getEloTopN(minElo, maxElo,
		//				offset, limit);
		//
		//		//go through them and select the one that has not been seen yet
		//		for (Tuple t : prospectiveDefenders) {
		//			int userId = Integer.valueOf(t.getElement());
		//			int elo = (int) t.getScore();
		//
		//			if (!seenUserIds.contains(userId)) {
		//				//we have a winner!
		//				defender = RetrieveUtils.userRetrieveUtils().getUserById(userId);
		//			}
		//
		//
		//		}
		//use hazelcast distributed map to get the defenders, limit the amount
		int numNeeded = ControllerConstants.PVP__MAX_QUEUE_SIZE;
		Set<PvpUser> prospectiveDefenders = hazelcastPvpUtil
			.retrievePvpUsers(minElo, maxElo, clientDate, numNeeded,
				seenUserIds);

		int numDefenders = prospectiveDefenders.size();
		//		log.info("users returned from hazelcast pvp util. users={}", prospectiveDefenders);

		//choose users either randomly or all of them
		selectUsers(numNeeded, numDefenders, prospectiveDefenders,
			userIdList, userIdToPvpUser);

		List<User> selectedDefenders = new ArrayList<User>();
		if (!prospectiveDefenders.isEmpty()) {
			Map<String, User> selectedDefendersMap = getUserRetrieveUtils()
				.getUsersByIds(userIdList);
			selectedDefenders.addAll(selectedDefendersMap.values());
		}

		log.info("the lucky people who get to be attacked! defenders={}", selectedDefenders);
		return selectedDefenders;
	} */

	/*
	private void calculateCashOilRewards(String attackerId, int attackerElo,
		List<User> queuedOpponents, Map<String, PvpUser> userIdToPvpUser,
		Map<String, Integer> userIdToProspectiveCashReward,
		Map<String, Integer> userIdToProspectiveOilReward)
	{
		//TODO: Need to account for the user's uncollected resources
		for (User queuedOpponent : queuedOpponents) {
			String userId = queuedOpponent.getId();
			PvpUser pu = userIdToPvpUser.get(userId);

			PvpBattleOutcome potentialResult = new PvpBattleOutcome(
				attackerId, attackerElo, userId, pu.getElo(),
				queuedOpponent.getCash(), queuedOpponent.getOil());

			userIdToProspectiveCashReward.put(userId,
				potentialResult.getUnsignedCashAttackerWins());

			userIdToProspectiveOilReward.put(userId,
				potentialResult.getUnsignedOilAttackerWins());
		}
	}*/

	private List<PvpProto> createPvpProtosFromFakeUser(
			List<List<MonsterForPvp>> fakeUserMonsters, int attackerElo) {
		log.info("creating fake users for pvp!!!!");
		List<PvpProto> ppList = new ArrayList<PvpProto>();
		boolean setElo = serverToggleRetrieveUtil
				.getToggleValueForName(ControllerConstants.SERVER_TOGGLE__PVP_BOT_SET_ELO);
		boolean displayBotElo = serverToggleRetrieveUtil
				.getToggleValueForName(ControllerConstants.SERVER_TOGGLE__PVP_BOT_SHOW_ELO);

		for (List<MonsterForPvp> mons : fakeUserMonsters) {
			PvpProto user = createFakeUser(mons, setElo, displayBotElo,
					attackerElo);
			ppList.add(user);
		}

		log.info("num fake users created: {}", ppList.size());
		return ppList;
	}

	//CREATES ONE FAKE USER FOR PVP
	private PvpProto createFakeUser(List<MonsterForPvp> mfpList,
			boolean setElo, boolean displayBotElo, int attackerElo) {
		//to create the fake user, need userId=0, some random name, empty clan
		//for lvl do something like (elo / 50)
		//for cur elo avg out the monsters elos

		List<Integer> cashWinnings = new ArrayList<Integer>();
		List<Integer> oilWinnings = new ArrayList<Integer>();
		int avgElo = determineAvgEloAndCashOilReward(mfpList, cashWinnings,
				oilWinnings);

		String userId = null;
		String randomName = hazelcastPvpUtil.getRandomName();
		if (displayBotElo) {
			randomName = String.format("a%sd%s", attackerElo, avgElo);
		}
		int lvl = avgElo / ControllerConstants.PVP__FAKE_USER_LVL_DIVISOR;

		int prospectiveCashWinnings = cashWinnings.get(0);
		int prospectiveOilWinnings = oilWinnings.get(0);

		//		log.info("fake user created: name={} \t avgElo={} \t cash={} \t oil={} \t lvl={}",
		//			new Object[] { randomName, avgElo, prospectiveCashWinnings,
		//			prospectiveOilWinnings, lvl } );

		List<Integer> monsterIdsDropped = calculateDrops(mfpList);

		//it's important that monsterIdsDropped be in the same order
		//as mfpList
		PvpProto fakeUser = createInfoProtoUtils.createFakePvpProto(userId,
				randomName, lvl, avgElo, prospectiveCashWinnings,
				prospectiveOilWinnings, mfpList, monsterIdsDropped, setElo);
		return fakeUser;
	}

	//assumes the List<MonsterForPvp> mons is not empty
	private int determineAvgEloAndCashOilReward(List<MonsterForPvp> mons,
			List<Integer> cashWinnings, List<Integer> oilWinnings) {
		int avgElo = 0;
		int prospectiveCashWinnings = PvpBattleOutcome.CASH__MIN_REWARD;//0;
		int prospectiveOilWinnings = PvpBattleOutcome.OIL__MIN_REWARD;//0;

		for (MonsterForPvp mon : mons) {
			avgElo += mon.getElo();

			prospectiveCashWinnings += mon.getCashDrop();
			prospectiveOilWinnings += mon.getOilDrop();
		}

		avgElo = avgElo / mons.size();
		cashWinnings.add(prospectiveCashWinnings);
		oilWinnings.add(prospectiveOilWinnings);

		return avgElo;
	}

	private List<Integer> calculateDrops(List<MonsterForPvp> mfpList) {
		List<Integer> monsterDropIds = new ArrayList<Integer>();

		for (MonsterForPvp mfp : mfpList) {
			int monsterId = mfp.getMonsterId();
			int monserLvl = mfp.getMonsterLvl();

			boolean monsterDropped = monsterLevelInfoRetrieveUtils
					.didPvpMonsterDrop(monsterId, monserLvl);

			int monsterDropId = ControllerConstants.NOT_SET;

			Monster mon = monsterRetrieveUtils
					.getMonsterForMonsterId(monsterId);
			if (monsterDropped) {
				monsterDropId = mon.getPvpMonsterDropId();
			}

			log.info("for fake monster {}, set pvpMonsterDropId={}", mfp,
					monsterDropId);
			monsterDropIds.add(monsterDropId);
		}

		return monsterDropIds;
	}

	public HazelcastPvpUtil getHazelcastPvpUtil() {
		return hazelcastPvpUtil;
	}

	public void setHazelcastPvpUtil(HazelcastPvpUtil hazelcastPvpUtil) {
		this.hazelcastPvpUtil = hazelcastPvpUtil;
	}

	public MonsterForPvpRetrieveUtils getMonsterForPvpRetrieveUtil() {
		return monsterForPvpRetrieveUtil;
	}

	public void setMonsterForPvpRetrieveUtil(
			MonsterForPvpRetrieveUtils monsterForPvpRetrieveUtil) {
		this.monsterForPvpRetrieveUtil = monsterForPvpRetrieveUtil;
	}

	public TimeUtils getTimeUtil() {
		return timeUtil;
	}

	public void setTimeUtil(TimeUtils timeUtil) {
		this.timeUtil = timeUtil;
	}

	public PvpLeagueForUserRetrieveUtil2 getPvpLeagueForUserRetrieveUtil() {
		return pvpLeagueForUserRetrieveUtil;
	}

	public void setPvpLeagueForUserRetrieveUtil(
			PvpLeagueForUserRetrieveUtil2 pvpLeagueForUserRetrieveUtil) {
		this.pvpLeagueForUserRetrieveUtil = pvpLeagueForUserRetrieveUtil;
	}

	public ClanRetrieveUtils2 getClanRetrieveUtil() {
		return clanRetrieveUtil;
	}

	public void setClanRetrieveUtil(ClanRetrieveUtils2 clanRetrieveUtil) {
		this.clanRetrieveUtil = clanRetrieveUtil;
	}

	public UserRetrieveUtils2 getUserRetrieveUtil() {
		return userRetrieveUtil;
	}

	public void setUserRetrieveUtil(UserRetrieveUtils2 userRetrieveUtil) {
		this.userRetrieveUtil = userRetrieveUtil;
	}

	public MonsterForUserRetrieveUtils2 getMonsterForUserRetrieveUtil() {
		return monsterForUserRetrieveUtil;
	}

	public void setMonsterForUserRetrieveUtil(
			MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtil) {
		this.monsterForUserRetrieveUtil = monsterForUserRetrieveUtil;
	}

	public ClanMemberTeamDonationRetrieveUtil getClanMemberTeamDonationRetrieveUtil() {
		return clanMemberTeamDonationRetrieveUtil;
	}

	public void setClanMemberTeamDonationRetrieveUtil(
			ClanMemberTeamDonationRetrieveUtil clanMemberTeamDonationRetrieveUtil) {
		this.clanMemberTeamDonationRetrieveUtil = clanMemberTeamDonationRetrieveUtil;
	}

	public MonsterSnapshotForUserRetrieveUtil getMonsterSnapshotForUserRetrieveUtil() {
		return monsterSnapshotForUserRetrieveUtil;
	}

	public void setMonsterSnapshotForUserRetrieveUtil(
			MonsterSnapshotForUserRetrieveUtil monsterSnapshotForUserRetrieveUtil) {
		this.monsterSnapshotForUserRetrieveUtil = monsterSnapshotForUserRetrieveUtil;
	}

	public PvpBoardObstacleForUserRetrieveUtil getPvpBoardObstacleForUserRetrieveUtil() {
		return pvpBoardObstacleForUserRetrieveUtil;
	}

	public void setPvpBoardObstacleForUserRetrieveUtil(
			PvpBoardObstacleForUserRetrieveUtil pvpBoardObstacleForUserRetrieveUtil) {
		this.pvpBoardObstacleForUserRetrieveUtil = pvpBoardObstacleForUserRetrieveUtil;
	}

	public ResearchForUserRetrieveUtils getResearchForUserRetrieveUtil() {
		return researchForUserRetrieveUtil;
	}

	public void setResearchForUserRetrieveUtil(
			ResearchForUserRetrieveUtils researchForUserRetrieveUtil) {
		this.researchForUserRetrieveUtil = researchForUserRetrieveUtil;
	}

	public MonsterLevelInfoRetrieveUtils getMonsterLevelInfoRetrieveUtils() {
		return monsterLevelInfoRetrieveUtils;
	}

	public void setMonsterLevelInfoRetrieveUtils(
			MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils) {
		this.monsterLevelInfoRetrieveUtils = monsterLevelInfoRetrieveUtils;
	}

	public MonsterStuffUtils getMonsterStuffUtils() {
		return monsterStuffUtils;
	}

	public void setMonsterStuffUtils(MonsterStuffUtils monsterStuffUtils) {
		this.monsterStuffUtils = monsterStuffUtils;
	}

	public MonsterRetrieveUtils getMonsterRetrieveUtils() {
		return monsterRetrieveUtils;
	}

	public void setMonsterRetrieveUtils(MonsterRetrieveUtils monsterRetrieveUtils) {
		this.monsterRetrieveUtils = monsterRetrieveUtils;
	}

	public CreateInfoProtoUtils getCreateInfoProtoUtils() {
		return createInfoProtoUtils;
	}

	public void setCreateInfoProtoUtils(CreateInfoProtoUtils createInfoProtoUtils) {
		this.createInfoProtoUtils = createInfoProtoUtils;
	}

	public ServerToggleRetrieveUtils getServerToggleRetrieveUtil() {
		return serverToggleRetrieveUtil;
	}

	public void setServerToggleRetrieveUtil(
			ServerToggleRetrieveUtils serverToggleRetrieveUtil) {
		this.serverToggleRetrieveUtil = serverToggleRetrieveUtil;
	}

}
