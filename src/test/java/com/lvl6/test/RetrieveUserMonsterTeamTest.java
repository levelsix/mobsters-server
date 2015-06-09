package com.lvl6.test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.sql.DataSource;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lvl6.info.PvpBoardObstacleForUser;
import com.lvl6.info.PvpLeague;
import com.lvl6.info.User;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.properties.DBConstants;
import com.lvl6.proto.BattleProto.PvpProto;
import com.lvl6.proto.EventMonsterProto.RetrieveUserMonsterTeamResponseProto;
import com.lvl6.pvp.HazelcastPvpUtil;
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
import com.lvl6.retrieveutils.rarechange.PvpLeagueRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ServerToggleRetrieveUtils;
import com.lvl6.server.controller.actionobjects.RetrieveUserMonsterTeamAction;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.InsertUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-spring-application-context.xml")
public class RetrieveUserMonsterTeamTest extends TestCase {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	protected HazelcastPvpUtil hazelcastPvpUtil;

	@Autowired
	protected PvpLeagueRetrieveUtils pvpLeagueRetrieveUtil;

	@Autowired
	protected MonsterForPvpRetrieveUtils monsterForPvpRetrieveUtil;

	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtil;

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
	private MonsterStuffUtils monsterStuffUtil;

	@Autowired
	private ServerToggleRetrieveUtils serverToggleRetrieveUtil;

	@Autowired
	private MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtil;

	@Autowired
	private InsertUtil insertUtil;

	@Autowired
	private DeleteUtil deleteUtil;

	private JdbcTemplate jdbcTemplate;

	private User user;
	private Set<String> userUuidsList;
	private List<PvpBoardObstacleForUser> pvpBoardObstacles;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	@Before
	public void setUp() {
		log.info("setUp");
		Timestamp createTime = new Timestamp((new Date()).getTime());

		String name = "bobUnitTest";
		String udid = "bobUdid";
		int lvl = ControllerConstants.USER_CREATE__START_LEVEL;
		int playerExp = 10;
		int cash = 0;
		int oil = 0;
		int gems = 0;
		String deviceToken = "bobToken";
		String facebookId = null;
		int avatarMonsterId = ControllerConstants.TUTORIAL__STARTING_MONSTER_ID;
		String email = null;
		String fbData = null;

		String userId = insertUtil.insertUser(name, udid, lvl, playerExp, cash,
				oil, gems, false, deviceToken, createTime, facebookId,
				avatarMonsterId, email, fbData, 0);

		user = userRetrieveUtil.getUserById(userId);

		if (null == user) {
			throw new RuntimeException("no user was created!");
		}

		int elo = ControllerConstants.PVP__DEFAULT_MIN_ELO;
		int pvpLeagueId = ControllerConstants.PVP__INITIAL_LEAGUE_ID;
		List<PvpLeague> pvpLeagueList = pvpLeagueRetrieveUtil
				.getLeaguesForElo(elo);
		if (pvpLeagueList.size() > 1) {
			log.error(
					"multiple leagues for init elo: {}\t leagues={}\t choosing first one.",
					elo, pvpLeagueList);
		} else if (pvpLeagueList.isEmpty()) {
			log.error("no pvp league id for elo: {}", elo);
		} else { //size is one
			pvpLeagueId = pvpLeagueList.get(0).getId();
		}

		int rank = pvpLeagueRetrieveUtil.getRankForElo(elo, pvpLeagueId);

		int numInserted = InsertUtils.get().insertPvpLeagueForUser(userId,
				pvpLeagueId, rank, elo, createTime, createTime);

		if (numInserted <= 0) {
			throw new RuntimeException("no pvp info was created!");
		}

		//generate the pvpBoardObstacles
		userUuidsList = getRandoUserIds();
		userUuidsList.remove(userId);

		pvpBoardObstacles = new ArrayList<PvpBoardObstacleForUser>();
		String userUuid = (String) userUuidsList.toArray()[0];
		PvpBoardObstacleForUser pbofu = new PvpBoardObstacleForUser();
		pbofu.setId(1);
		pbofu.setUserId(userUuid);
		pbofu.setObstacleId(1);
		pbofu.setPosX(1);
		pbofu.setPosY(1);

		pvpBoardObstacles.add(pbofu);

		numInserted = insertUtil
				.insertIntoUpdatePvpBoardObstacleForUser(pvpBoardObstacles);
		log.info("numInserted={}, nuOrUpdated={}", numInserted,
				pvpBoardObstacles);

		if (numInserted <= 0) {
			throw new RuntimeException("no PvpBoardObstacles were created!");
		}
	}

	@Override
	@After
	public void tearDown() {
		if (null == user) {
			log.info("no user to delete");
			return;
		}

		String query = String.format("DELETE FROM %s where %s=?",
				DBConstants.TABLE_USER, DBConstants.USER__ID);
		Object[] values = new Object[] { user.getId() };
		int[] types = new int[] { java.sql.Types.VARCHAR };

		int numDeleted = jdbcTemplate.update(query, values, types);

		query = String.format("DELETE FROM %s where %s=?",
				DBConstants.TABLE_PVP_LEAGUE_FOR_USER,
				DBConstants.PVP_LEAGUE_FOR_USER__USER_ID);
		numDeleted = jdbcTemplate.update(query, values, types);

		PvpBoardObstacleForUser pbofu = pvpBoardObstacles.get(0);
		List<Integer> pbofuIds = Collections.singletonList(pbofu.getId());
		String userUuid = pbofu.getUserId();
		numDeleted = deleteUtil.deletePvpBoardObstacleForUser(pbofuIds,
				userUuid);
		log.info("numDeleted={}. deletedIds={}", numDeleted, pbofuIds);
	}

	@Test
	//	@Rollback(true) //doesn't roll back transaction >:C
	//	@Transactional //just manually undo...
	public void testRetrieveUserMonsterTeam() {
		String userId = user.getId();

		int amount = userUuidsList.size();
		assertNotNull(userUuidsList);
		assertTrue(!userUuidsList.isEmpty());

		RetrieveUserMonsterTeamAction rumta = new RetrieveUserMonsterTeamAction(
				userId, userUuidsList, userRetrieveUtil, clanRetrieveUtil,
				monsterForUserRetrieveUtil, clanMemberTeamDonationRetrieveUtil,
				monsterSnapshotForUserRetrieveUtil, hazelcastPvpUtil,
				pvpLeagueForUserRetrieveUtil,
				pvpBoardObstacleForUserRetrieveUtil, researchForUserRetrieveUtil,
				monsterStuffUtil, serverToggleRetrieveUtil, monsterLevelInfoRetrieveUtil, false);
		RetrieveUserMonsterTeamResponseProto.Builder resBuilder = RetrieveUserMonsterTeamResponseProto
				.newBuilder();
		rumta.execute(resBuilder);
		//every user should have monsters
		assertNotNull(rumta.getAllButRetrieverUserIdToUserMonsters());
		assertEquals(
				"userIds=" + userUuidsList + "\t monsters="
						+ rumta.getAllButRetrieverUserIdToUserMonsters(),
				amount, rumta.getAllButRetrieverUserIdToUserMonsters().size());

		List<PvpProto> ppList = createInfoProtoUtil.createPvpProtos(
				rumta.getAllUsersExceptRetriever(), rumta.getUserIdToClan(),
				null, rumta.getUserIdToPvpUsers(),
				rumta.getAllButRetrieverUserIdToUserMonsters(),
				rumta.getAllButRetrieverUserIdToUserMonsterIdToDroppedId(),
				rumta.getAllButRetrieverUserIdToCashLost(),
				rumta.getAllButRetrieverUserIdToOilLost(),
				rumta.getAllButRetrieverUserIdToCmtd(),
				rumta.getAllButRetrieverUserIdToMsfu(),
				rumta.getAllButRetrieverUserIdToMsfuMonsterDropId(),
				rumta.getAllButRetrieverUserIdToPvpBoardObstacles(),
				rumta.getAllButRetrieverUserIdToUserResearch());

		//not every user will have pvp board obstacles
		//		assertNotNull(rumta.getAllButRetrieverUserIdToPvpBoardObstacles());
	}

	private Set<String> getRandoUserIds() {

		String query = String.format(
				"select %s from %s order by rand() limit 10",
				DBConstants.USER__ID, DBConstants.TABLE_USER);

		Set<String> uniqIds = null;
		try {
			List<String> userIds = this.jdbcTemplate.queryForList(query,
					String.class);

			uniqIds = new HashSet<>(userIds);

		} catch (Exception e) {
			log.error("structure for user retrieve db error.", e);
			//		} finally {
			//			DBConnection.get().close(rs, null, conn);
		}
		return uniqIds;
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

	public InsertUtil getInsertUtil() {
		return insertUtil;
	}

	public void setInsertUtil(InsertUtil insertUtil) {
		this.insertUtil = insertUtil;
	}

	public PvpBoardObstacleForUserRetrieveUtil getPvpBoardObstacleForUserRetrieveUtil() {
		return pvpBoardObstacleForUserRetrieveUtil;
	}

	public void setPvpBoardObstacleForUserRetrieveUtil(
			PvpBoardObstacleForUserRetrieveUtil pvpBoardObstacleForUserRetrieveUtil) {
		this.pvpBoardObstacleForUserRetrieveUtil = pvpBoardObstacleForUserRetrieveUtil;
	}

	public DeleteUtil getDeleteUtil() {
		return deleteUtil;
	}

	public void setDeleteUtil(DeleteUtil deleteUtil) {
		this.deleteUtil = deleteUtil;
	}

	public PvpLeagueRetrieveUtils getPvpLeagueRetrieveUtil() {
		return pvpLeagueRetrieveUtil;
	}

	public void setPvpLeagueRetrieveUtil(
			PvpLeagueRetrieveUtils pvpLeagueRetrieveUtil) {
		this.pvpLeagueRetrieveUtil = pvpLeagueRetrieveUtil;
	}

	public CreateInfoProtoUtils getCreateInfoProtoUtil() {
		return createInfoProtoUtil;
	}

	public void setCreateInfoProtoUtil(CreateInfoProtoUtils createInfoProtoUtil) {
		this.createInfoProtoUtil = createInfoProtoUtil;
	}

	public ResearchForUserRetrieveUtils getResearchForUserRetrieveUtil() {
		return researchForUserRetrieveUtil;
	}

	public void setResearchForUserRetrieveUtil(
			ResearchForUserRetrieveUtils researchForUserRetrieveUtil) {
		this.researchForUserRetrieveUtil = researchForUserRetrieveUtil;
	}

	public MonsterStuffUtils getMonsterStuffUtil() {
		return monsterStuffUtil;
	}

	public void setMonsterStuffUtil(MonsterStuffUtils monsterStuffUtil) {
		this.monsterStuffUtil = monsterStuffUtil;
	}

	public ServerToggleRetrieveUtils getServerToggleRetrieveUtil() {
		return serverToggleRetrieveUtil;
	}

	public void setServerToggleRetrieveUtil(
			ServerToggleRetrieveUtils serverToggleRetrieveUtil) {
		this.serverToggleRetrieveUtil = serverToggleRetrieveUtil;
	}

	public MonsterLevelInfoRetrieveUtils getMonsterLevelInfoRetrieveUtil() {
		return monsterLevelInfoRetrieveUtil;
	}

	public void setMonsterLevelInfoRetrieveUtil(
			MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtil) {
		this.monsterLevelInfoRetrieveUtil = monsterLevelInfoRetrieveUtil;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

}
