package com.lvl6.server.controller.actionobjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.Clan;
import com.lvl6.info.ClanMemberTeamDonation;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.MonsterSnapshotForUser;
import com.lvl6.info.PvpBoardObstacleForUser;
import com.lvl6.info.PvpLeagueForUser;
import com.lvl6.info.ResearchForUser;
import com.lvl6.info.User;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventMonsterProto.RetrieveUserMonsterTeamResponseProto.Builder;
import com.lvl6.proto.EventMonsterProto.RetrieveUserMonsterTeamResponseProto.RetrieveUserMonsterTeamStatus;
import com.lvl6.proto.MonsterStuffProto.UserMonsterSnapshotProto.SnapshotType;
import com.lvl6.pvp.HazelcastPvpUtil;
import com.lvl6.pvp.PvpBattleOutcome;
import com.lvl6.pvp.PvpUser;
import com.lvl6.retrieveutils.ClanMemberTeamDonationRetrieveUtil;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2;
import com.lvl6.retrieveutils.MonsterSnapshotForUserRetrieveUtil;
import com.lvl6.retrieveutils.PvpBoardObstacleForUserRetrieveUtil;
import com.lvl6.retrieveutils.PvpLeagueForUserRetrieveUtil2;
import com.lvl6.retrieveutils.ResearchForUserRetrieveUtils;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.MonsterLevelInfoRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ServerToggleRetrieveUtils;
import com.lvl6.server.controller.utils.MonsterStuffUtils;

public class RetrieveUserMonsterTeamAction {
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());
	private static String typeDonation = SnapshotType.TEAM_DONATE.name();

	private String retrieverUserId;
	private Collection<String> userUuids;
	private UserRetrieveUtils2 userRetrieveUtil;
	private ClanRetrieveUtils2 clanRetrieveUtil;
	private MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtil;
	private ClanMemberTeamDonationRetrieveUtil clanMemberTeamDonationRetrieveUtil;
	private MonsterSnapshotForUserRetrieveUtil monsterSnapshotForUserRetrieveUtil;
	private HazelcastPvpUtil hazelcastPvpUtil;
	private PvpLeagueForUserRetrieveUtil2 pvpLeagueForUserRetrieveUtil;
	private PvpBoardObstacleForUserRetrieveUtil pvpBoardObstacleForUserRetrieveUtil;
	private ResearchForUserRetrieveUtils researchForUserRetrieveUtil;
	private MonsterStuffUtils monsterStuffUtils;
	private ServerToggleRetrieveUtils serverToggleRetrieveUtil;
	private MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils;

	public RetrieveUserMonsterTeamAction(
			String retrieverUserId,
			Collection<String> userUuids,
			UserRetrieveUtils2 userRetrieveUtil,
			ClanRetrieveUtils2 clanRetrieveUtil,
			MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtil,
			ClanMemberTeamDonationRetrieveUtil clanMemberTeamDonationRetrieveUtil,
			MonsterSnapshotForUserRetrieveUtil monsterSnapshotForUserRetrieveUtil,
			HazelcastPvpUtil hazelcastPvpUtil,
			PvpLeagueForUserRetrieveUtil2 pvpLeagueForUserRetrieveUtil,
			PvpBoardObstacleForUserRetrieveUtil pvpBoardObstacleForUserRetrieveUtil,
			ResearchForUserRetrieveUtils researchForUserRetrieveUtil,
			MonsterStuffUtils monsterStuffUtils,
			ServerToggleRetrieveUtils serverToggleRetrieveUtil,
			MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils)
	{
		super();
		this.retrieverUserId = retrieverUserId;
		this.userUuids = userUuids;
		this.userRetrieveUtil = userRetrieveUtil;
		this.clanRetrieveUtil = clanRetrieveUtil;
		this.monsterForUserRetrieveUtil = monsterForUserRetrieveUtil;
		this.clanMemberTeamDonationRetrieveUtil = clanMemberTeamDonationRetrieveUtil;
		this.monsterSnapshotForUserRetrieveUtil = monsterSnapshotForUserRetrieveUtil;
		this.hazelcastPvpUtil = hazelcastPvpUtil;
		this.pvpLeagueForUserRetrieveUtil = pvpLeagueForUserRetrieveUtil;
		this.pvpBoardObstacleForUserRetrieveUtil = pvpBoardObstacleForUserRetrieveUtil;
		this.researchForUserRetrieveUtil = researchForUserRetrieveUtil;
		this.monsterStuffUtils = monsterStuffUtils;
		this.serverToggleRetrieveUtil = serverToggleRetrieveUtil;
		this.monsterLevelInfoRetrieveUtils = monsterLevelInfoRetrieveUtils;
	}

	//	//encapsulates the return value from this Action Object
	//	static class RetrieveUserMonsterTeamResource {
	//
	//
	//		public RetrieveUserMonsterTeamResource() {
	//
	//		}
	//	}
	//
	//	public RetrieveUserMonsterTeamResource execute() {
	//
	//	}

	//derived state
	private PvpUser retrieverPu;
	private Map<String, PvpUser> userIdToPvpUsers;
	private Map<String, List<MonsterForUser>> allButRetrieverUserIdToUserMonsters;
	private Map<String, Map<String, Integer>> allButRetrieverUserIdToUserMonsterIdToDroppedId;
	private Map<String, User> userIdToUser;
	private Map<String, Clan> userIdToClan;
	private Map<String, Float> allButRetrieverUserIdToCashLost;
	private Map<String, Float> allButRetrieverUserIdToOilLost;

	private Map<String, ClanMemberTeamDonation> allButRetrieverUserIdToCmtd;
	private Map<String, MonsterSnapshotForUser> allButRetrieverUserIdToMsfu;
	private Map<String, Integer> allButRetrieverUserIdToMsfuMonsterDropId;
	private Map<String, List<PvpBoardObstacleForUser>> allButRetrieverUserIdToPvpBoardObstacles;
	private Map<String, List<ResearchForUser>> allButRetrieverUserIdToUserResearch;

	private List<String> allUsersIdsExceptRetriever;
	private List<User> allUsersExceptRetriever;

	public void execute(Builder resBuilder) {
		resBuilder.setStatus(RetrieveUserMonsterTeamStatus.FAIL_OTHER);

		//check out inputs before db interaction
		boolean valid = verifySyntax(resBuilder);

		if (!valid) {
			return;
		}

		valid = verifySemantics(resBuilder);

		if (!valid) {
			return;
		}

		gatherDbInfo();

		resBuilder.setStatus(RetrieveUserMonsterTeamStatus.SUCCESS);

	}

	private boolean verifySyntax(Builder resBuilder) {

		if (userUuids.isEmpty()) {
			log.error("invalid userItemUsedIdList={}", userUuids);
			return false;
		}

		return true;
	}

	private boolean verifySemantics(Builder resBuilder) {
		return true;
	}

	public void gatherDbInfo() {
		getPvpInfo();

		if (userIdToPvpUsers.isEmpty()) {
			log.error("no users with PvpUsers!!!!!");
			return;
		}

		//get the monster team for all userIds with PvpUser
		log.info("calculating the Pvp monsters");
		List<String> userIdsExceptRetriever = new ArrayList<String>(
				userIdToPvpUsers.keySet());
		userIdsExceptRetriever.remove(retrieverUserId);
		allButRetrieverUserIdToUserMonsters = selectMonstersForUsers(userIdsExceptRetriever);

		//calculate the PvpDrops
		log.info("calculating the Pvp drops");
		allButRetrieverUserIdToUserMonsterIdToDroppedId = monsterStuffUtils
				.calculatePvpDrops(allButRetrieverUserIdToUserMonsters, 
						monsterLevelInfoRetrieveUtils);

		//calculate the PvpBattleOutcome
		StartUpResource sup = new StartUpResource(userRetrieveUtil,
				clanRetrieveUtil);
		sup.addUserId(userIdsExceptRetriever);
		sup.addUserId(retrieverUserId);
		sup.fetch();
		userIdToUser = sup.getUserIdsToUsers();
		userIdToClan = sup.getUserIdsToClans(userIdsExceptRetriever);

		allButRetrieverUserIdToCashLost = new HashMap<String, Float>();
		allButRetrieverUserIdToOilLost = new HashMap<String, Float>();
		log.info("calculating the PvpBattleOutcomes");
		int retrieverElo = this.retrieverPu.getElo();
		User retrieveUser = userRetrieveUtil.getUserById(retrieverUserId);
		for (String userId : userIdToUser.keySet()) {
			if (userId.equals(retrieverUserId)) {
				continue;
			}

			PvpUser pu = userIdToPvpUsers.get(userId);

			User u = userIdToUser.get(userId);
			PvpBattleOutcome potentialResult = new PvpBattleOutcome(
					retrieveUser, retrieverElo, u, pu.getElo(),
					serverToggleRetrieveUtil);

			allButRetrieverUserIdToCashLost.put(userId,
					potentialResult.getProspectiveCashPercentage());
			allButRetrieverUserIdToOilLost.put(userId,
					potentialResult.getProspectiveOilPercentage());
		}

		//get the team monster donation solicitations by all clans
		Collection<String> clanIds = sup.getClanIdsToClans().keySet();
		List<ClanMemberTeamDonation> allClansSolicitations = null;
		if (clanIds.isEmpty()) {
			allClansSolicitations = new ArrayList<ClanMemberTeamDonation>();
		} else {
			allClansSolicitations = clanMemberTeamDonationRetrieveUtil
					.getClanMemberTeamDonationForClanIds(clanIds);
		}

		//mapify those donation solicitations for easier access later on
		Map<String, String> cmtdIdToAllButRetrieverUserId = new HashMap<String, String>();
		allButRetrieverUserIdToCmtd = new HashMap<String, ClanMemberTeamDonation>();
		for (ClanMemberTeamDonation cmtd : allClansSolicitations) {
			String userId = cmtd.getUserId();
			if (userId.equals(retrieverUserId)) {
				continue;
			}

			String cmtdId = cmtd.getId();

			cmtdIdToAllButRetrieverUserId.put(cmtdId, userId);
			allButRetrieverUserIdToCmtd.put(userId, cmtd);

		}

		//make it easier to access (via userId) later on
		List<MonsterSnapshotForUser> allClanDonations = new ArrayList<MonsterSnapshotForUser>();
		if (!cmtdIdToAllButRetrieverUserId.isEmpty()) {
			allClanDonations = monsterSnapshotForUserRetrieveUtil
					.getMonstersSnapshots(typeDonation,
							cmtdIdToAllButRetrieverUserId.keySet());
		}
		allButRetrieverUserIdToMsfu = new HashMap<String, MonsterSnapshotForUser>();
		for (MonsterSnapshotForUser msfu : allClanDonations) {
			//the userId in msfu is the person who created msfu,
			//not to whom the msfu is donated
			String cmtdId = msfu.getIdInTable();
			String userId = cmtdIdToAllButRetrieverUserId.get(cmtdId);

			allButRetrieverUserIdToMsfu.put(userId, msfu);
		}

		//need to calculate whether or not these donated monsters drop a piece
		if (!allButRetrieverUserIdToMsfu.isEmpty()) {
			allButRetrieverUserIdToMsfuMonsterDropId = monsterStuffUtils
					.calculateMsfuPvpDrops(allButRetrieverUserIdToMsfu, 
							monsterLevelInfoRetrieveUtils);
		} else {
			allButRetrieverUserIdToMsfuMonsterDropId = new HashMap<String, Integer>();
		}

		//pvp board obstacles
		allButRetrieverUserIdToPvpBoardObstacles = pvpBoardObstacleForUserRetrieveUtil
				.getPvpBoardObstacleForUserIds(userIdsExceptRetriever);
		log.info("allButRetrieverUserIdToPvpBoardObstacles={}",
				allButRetrieverUserIdToPvpBoardObstacles);

		allButRetrieverUserIdToUserResearch = researchForUserRetrieveUtil
				.getResearchForUserIds(userIdsExceptRetriever);
	}

	private void getPvpInfo() {
		userIdToPvpUsers = new HashMap<String, PvpUser>();
		//get the PvpUser for all userIds including the retriever user
		Set<String> userIdsSansPvpUser = new HashSet<String>();
		for (String userUuid : userUuids) {
			PvpUser userPu = hazelcastPvpUtil.getPvpUser(userUuid);

			if (null != userPu) {
				userIdToPvpUsers.put(userUuid, userPu);
			} else {
				userIdsSansPvpUser.add(userUuid);
			}
		}

		PvpUser userPu = hazelcastPvpUtil.getPvpUser(retrieverUserId);
		if (null != userPu) {
			this.retrieverPu = userPu;
		} else {
			userIdsSansPvpUser.add(retrieverUserId);
		}

		//get the PvpLeagueForUser for userIds who don't have PvpUser
		//and convert the PvpLeagueForUser into PvpUser
		if (!userIdsSansPvpUser.isEmpty()) {
			getPvpLeagueForUserInfo(userIdsSansPvpUser);
		}

	}

	private void getPvpLeagueForUserInfo(Set<String> userIdsSansPvpUser) {
		log.warn("users with no PvpUser {}. querying pvp_league_for_user",
				userIdsSansPvpUser);
		List<String> userUuidStrs = new ArrayList<String>();
		userUuidStrs.addAll(userIdsSansPvpUser);
		//case if the retriever doesn't have a PvpUser, caller function already adds the id
		//		if (null == this.retrieverPu) {
		//			userUuidStrs.add(retrieverUserId);
		//		}

		Map<String, PvpLeagueForUser> plfuMap = pvpLeagueForUserRetrieveUtil
				.getUserPvpLeagueForUsers(userUuidStrs);

		for (String userUuidStr : userUuidStrs) {
			if (!plfuMap.containsKey(userUuidStr)) {
				log.error("no PvpLeagueForUser userId {}", userUuidStr);
				continue;
			}

			PvpLeagueForUser plfu = plfuMap.get(userUuidStr);
			PvpUser userPu = new PvpUser(plfu);
			userIdToPvpUsers.put(userUuidStr, userPu);
			if (userUuidStr.equals(retrieverUserId)) {
				this.retrieverPu = userPu;
			}
		}
	}

	//TODO: copy pasted from QueueUpController, figure another way to do this
	private Map<String, List<MonsterForUser>> selectMonstersForUsers(
			List<String> userIdList) {

		//return value
		Map<String, List<MonsterForUser>> userIdsToUserMonsters = new HashMap<String, List<MonsterForUser>>();

		//for all these users, get all their complete monsters
		Map<String, Map<String, MonsterForUser>> userIdsToMfuIdsToMonsters = monsterForUserRetrieveUtil
				.getCompleteMonstersForUser(userIdList);

		for (int index = 0; index < userIdList.size(); index++) {
			//extract a user's monsters
			String defenderId = userIdList.get(index);
			Map<String, MonsterForUser> mfuIdsToMonsters = userIdsToMfuIdsToMonsters
					.get(defenderId);

			if (null == mfuIdsToMonsters || mfuIdsToMonsters.isEmpty()) {
				log.error("WTF!!!!!!!! user has no monsters!!!!! userId="
						+ defenderId + "\t will move on to next guy.");
				continue;
			}
			//try to select at most 3 monsters for this user
			List<MonsterForUser> defenderMonsters = selectMonstersForUser(mfuIdsToMonsters);

			//if the user still doesn't have 3 monsters, then too bad
			userIdsToUserMonsters.put(defenderId, defenderMonsters);
		}

		return userIdsToUserMonsters;
	}

	private List<MonsterForUser> selectMonstersForUser(
			Map<String, MonsterForUser> mfuIdsToMonsters) {

		//get all the monsters the user has on a team (at the moment, max is 3)
		List<MonsterForUser> defenderMonsters = getEquippedMonsters(mfuIdsToMonsters);

		//		if (defenderMonsters.size() < 3) {
		if (defenderMonsters.isEmpty()) {
			//need more monsters so select them randomly, fill up "defenderMonsters" list
			defenderMonsters = getRandomMonsters(mfuIdsToMonsters);
		}

		//		if (defenderMonsters.size() > 3) {
		//			//only get three monsters
		//			defenderMonsters = defenderMonsters.subList(0, 3);
		//		}

		//order by team slot num
		Collections.sort(defenderMonsters, new Comparator<MonsterForUser>() {
			@Override
			public int compare(MonsterForUser o1, MonsterForUser o2) {
				int o1TeamSlotNum = o1.getTeamSlotNum();
				int o2TeamSlotNum = o2.getTeamSlotNum();
				if (o1TeamSlotNum < o2TeamSlotNum) {
					return -1;
				} else if (o1TeamSlotNum > o2TeamSlotNum) {
					return 1;
				} else if (o1.getMonsterId() < o2.getMonsterId()) {
					return -1;
				} else if (o1.getMonsterId() > o2.getMonsterId()) {
					return 1;
				}
				return 0;
			}
		});

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

	private List<MonsterForUser> getRandomMonsters(
			Map<String, MonsterForUser> possibleMonsters) {
		List<MonsterForUser> retVal = new ArrayList<MonsterForUser>();

		Map<String, MonsterForUser> possibleMonstersTemp = new HashMap<String, MonsterForUser>(
				possibleMonsters);
		//		//remove the defender monsters from possibleMonstersTemp, since defenderMonsters
		//		//were already selected from possibleMonsters
		//		for (MonsterForUser m : defenderMonsters) {
		//			String mfuId = m.getId();
		//
		//			possibleMonstersTemp.remove(mfuId);
		//		}

		float amountNeeded = 1;//3 - defenderMonsters.size();

		float amountLeftOver = possibleMonstersTemp.size();
		if (amountLeftOver <= amountNeeded) {
			retVal.addAll(possibleMonstersTemp.values());
			return retVal;
		}

		//randomly select one monster
		List<MonsterForUser> mfuList = new ArrayList<MonsterForUser>(
				possibleMonstersTemp.values());
		Random rand = ControllerConstants.RAND;

		//for each monster gen rand float, and if it "drops" select it
		for (int i = 0; i < mfuList.size(); i++) {

			//IGNORE //eg. need 2, have 3. If first one is not picked, then need 2, have 2.
			float denom = amountLeftOver - i;
			float probToBeChosen = amountNeeded / denom;
			float randFloat = rand.nextFloat();

			if (randFloat < probToBeChosen) {
				//we have a winner! select this monster
				MonsterForUser mfu = mfuList.get(i);
				retVal.add(mfu);
				break;

				//need to decrement amount needed
				//amountNeeded--;
			}

			//stop at three monsters, don't want to get more
			//			if (defenderMonsters.size() >= 3) {
			//				break;
			//			}
		}

		return retVal;
	}

	public User getRetriever() {
		if (null != userIdToUser && userIdToUser.containsKey(retrieverUserId)) {
			return userIdToUser.get(retrieverUserId);
		}
		return null;
	}

	public List<String> getAllUsersIdsExceptRetriever() {
		if (null == allUsersIdsExceptRetriever
				|| null == allUsersExceptRetriever) {
			allUsersIdsExceptRetriever = new ArrayList<String>();
			if (null != userIdToUser) {
				filterOutRetriever();
			}
		}
		return allUsersIdsExceptRetriever;
	}

	public List<User> getAllUsersExceptRetriever() {
		if (null == allUsersExceptRetriever
				|| null == allUsersIdsExceptRetriever) {
			allUsersExceptRetriever = new ArrayList<User>();
			allUsersIdsExceptRetriever = new ArrayList<String>();
			if (null != userIdToUser) {
				filterOutRetriever();
			}
		}
		return allUsersExceptRetriever;
	}

	private void filterOutRetriever() {
		for (User u : userIdToUser.values()) {
			String uId = u.getId();
			if (!uId.equals(retrieverUserId)) {
				allUsersExceptRetriever.add(u);
				allUsersIdsExceptRetriever.add(uId);
			}
		}
	}

	public Map<String, Clan> getUserIdToClan() {
		return userIdToClan;
	}

	public Map<String, PvpUser> getUserIdToPvpUsers() {
		return userIdToPvpUsers;
	}

	public Map<String, List<MonsterForUser>> getAllButRetrieverUserIdToUserMonsters() {
		return allButRetrieverUserIdToUserMonsters;
	}

	public Map<String, Map<String, Integer>> getAllButRetrieverUserIdToUserMonsterIdToDroppedId() {
		return allButRetrieverUserIdToUserMonsterIdToDroppedId;
	}

	public Map<String, Float> getAllButRetrieverUserIdToCashLost() {
		return allButRetrieverUserIdToCashLost;
	}

	public Map<String, Float> getAllButRetrieverUserIdToOilLost() {
		return allButRetrieverUserIdToOilLost;
	}

	public Map<String, ClanMemberTeamDonation> getAllButRetrieverUserIdToCmtd() {
		return allButRetrieverUserIdToCmtd;
	}

	public Map<String, MonsterSnapshotForUser> getAllButRetrieverUserIdToMsfu() {
		return allButRetrieverUserIdToMsfu;
	}

	public Map<String, Integer> getAllButRetrieverUserIdToMsfuMonsterDropId() {
		return allButRetrieverUserIdToMsfuMonsterDropId;
	}

	public Map<String, List<PvpBoardObstacleForUser>> getAllButRetrieverUserIdToPvpBoardObstacles() {
		return allButRetrieverUserIdToPvpBoardObstacles;
	}

	public Map<String, List<ResearchForUser>> getAllButRetrieverUserIdToUserResearch() {
		return allButRetrieverUserIdToUserResearch;
	}
	
	public Map<String, User> getAllUsers() {
		return userIdToUser;
	}

}
