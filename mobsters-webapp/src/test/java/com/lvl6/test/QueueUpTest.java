//package com.lvl6.test;
//
//import java.sql.Timestamp;
//import java.util.Date;
//import java.util.List;
//
//import javax.annotation.Resource;
//import javax.sql.DataSource;
//
//import junit.framework.TestCase;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import com.lvl6.events.request.QueueUpRequestEvent;
//import com.lvl6.info.PvpLeague;
//import com.lvl6.info.PvpLeagueForUser;
//import com.lvl6.info.User;
//import com.lvl6.properties.ControllerConstants;
//import com.lvl6.properties.DBConstants;
//import com.lvl6.proto.EventPvpProto.QueueUpRequestProto;
//import com.lvl6.proto.UserProto.MinimumUserProto;
//import com.lvl6.pvp.HazelcastPvpUtil;
//import com.lvl6.retrieveutils.ClanMemberTeamDonationRetrieveUtil;
//import com.lvl6.retrieveutils.ClanRetrieveUtils2;
//import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2;
//import com.lvl6.retrieveutils.MonsterSnapshotForUserRetrieveUtil;
//import com.lvl6.retrieveutils.PvpLeagueForUserRetrieveUtil2;
//import com.lvl6.retrieveutils.UserRetrieveUtils2;
//import com.lvl6.retrieveutils.rarechange.MonsterForPvpRetrieveUtils;
//import com.lvl6.retrieveutils.rarechange.PvpLeagueRetrieveUtils;
//import com.lvl6.server.controller.QueueUpController;
//import com.lvl6.server.controller.utils.TimeUtils;
//import com.lvl6.utils.CreateInfoProtoUtils;
//import com.lvl6.utils.utilmethods.InsertUtil;
//import com.lvl6.utils.utilmethods.InsertUtils;
//import com.lvl6.utils.utilmethods.UpdateUtil;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration("/test-spring-application-context.xml")
//public class QueueUpTest extends TestCase {
//
//	private static Logger log = LoggerFactory.getLogger(//	}.getClass().getEnclosingClass());
//
//	@Autowired
//	protected HazelcastPvpUtil hazelcastPvpUtil;
//
//	@Autowired
//	protected MonsterForPvpRetrieveUtils monsterForPvpRetrieveUtil;
//
//	@Autowired
//	protected TimeUtils timeUtil;
//
//	@Autowired
//	protected PvpLeagueForUserRetrieveUtil2 pvpLeagueForUserRetrieveUtil;
//
//	@Autowired
//	protected ClanRetrieveUtils2 clanRetrieveUtil;
//
//	@Autowired
//	protected UserRetrieveUtils2 userRetrieveUtil;
//
//	@Autowired
//	protected MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtil;
//
//	@Autowired
//	private ClanMemberTeamDonationRetrieveUtil clanMemberTeamDonationRetrieveUtil;
//
//	@Autowired
//	private MonsterSnapshotForUserRetrieveUtil monsterSnapshotForUserRetrieveUtil;
//
//	@Autowired
//	protected QueueUpController queueUpController;
//
//	private User user;
//	@Autowired
//	private InsertUtil insertUtil;
//	@Autowired
//	private UpdateUtil updateUtil;
//	private JdbcTemplate jdbcTemplate;
//
//	@Resource
//	public void setDataSource(DataSource dataSource) {
//		log.info("Setting datasource and creating jdbcTemplate");
//		this.jdbcTemplate = new JdbcTemplate(dataSource);
//	}
//
//	@Override
//	@Before
//	public void setUp() {
//		log.info("setUp");
//		Timestamp createTime = new Timestamp((new Date()).getTime());
//
//		String name = "bobUnitTest";
//		String udid = "bobUdid";
//		int lvl = ControllerConstants.USER_CREATE__START_LEVEL;
//		int playerExp = 10;
//		int cash = 0;
//		int oil = 0;
//		int gems = 0;
//		String deviceToken = "bobToken";
//		String facebookId = null;
//		int avatarMonsterId = ControllerConstants.TUTORIAL__STARTING_MONSTER_ID;
//		String email = null;
//		String fbData = null;
//
//		String userId = insertUtil.insertUser(name, udid, lvl, playerExp, cash,
//				oil, gems, false, deviceToken, createTime, facebookId,
//				avatarMonsterId, email, fbData);
//
//		user = userRetrieveUtil.getUserById(userId);
//
//		if (null == user) {
//			throw new RuntimeException("no user was created!");
//		}
//
//		int elo = ControllerConstants.PVP__DEFAULT_MIN_ELO;
//		int pvpLeagueId = ControllerConstants.PVP__INITIAL_LEAGUE_ID;
//		List<PvpLeague> pvpLeagueList = PvpLeagueRetrieveUtils
//				.getLeaguesForElo(elo);
//		if (pvpLeagueList.size() > 1) {
//			log.error(
//					"multiple leagues for init elo: {}\t leagues={}\t choosing first one.",
//					elo, pvpLeagueList);
//		} else if (pvpLeagueList.isEmpty()) {
//			log.error("no pvp league id for elo: {}", elo);
//		} else { //size is one
//			pvpLeagueId = pvpLeagueList.get(0).getId();
//		}
//
//		int rank = PvpLeagueRetrieveUtils.getRankForElo(elo, pvpLeagueId);
//
//		int numInserted = InsertUtils.get().insertPvpLeagueForUser(userId,
//				pvpLeagueId, rank, elo, createTime, createTime);
//
//		if (numInserted <= 0) {
//			throw new RuntimeException("no pvp info was created!");
//		}
//	}
//
//	@Override
//	@After
//	public void tearDown() {
//		if (null == user) {
//			log.info("no user to delete");
//			return;
//		}
//
//		String query = String.format("DELETE FROM %s where %s=?",
//				DBConstants.TABLE_USER, DBConstants.USER__ID);
//		Object[] values = new Object[] { user.getId() };
//		int[] types = new int[] { java.sql.Types.VARCHAR };
//
//		int numDeleted = jdbcTemplate.update(query, values, types);
//
//		query = String.format("DELETE FROM %s where %s=?",
//				DBConstants.TABLE_PVP_LEAGUE_FOR_USER,
//				DBConstants.PVP_LEAGUE_FOR_USER__USER_ID);
//		numDeleted = jdbcTemplate.update(query, values, types);
//	}
//
//	@Test
//	//	@Rollback(true) //doesn't roll back transaction >:C
//	//	@Transactional //just manually undo...
//	public void testQueueUp() {
//		Date clientTime = new Date();
//		String userId = user.getId();
//		PvpLeagueForUser plfu = pvpLeagueForUserRetrieveUtil
//				.getUserPvpLeagueForId(userId);
//		int elo = plfu.getElo();
//		sendQueueUpRequestEvent(user, elo, clientTime);
//
//		//gotta check the logs to see if good or not
//		//trying to simulate not finding anyone
//		int eloChange = 4000;
//		int pvpLeagueId = plfu.getPvpLeagueId();
//		List<PvpLeague> pvpLeagueList = PvpLeagueRetrieveUtils
//				.getLeaguesForElo(elo);
//		if (pvpLeagueList.size() > 1) {
//			log.error(
//					"multiple leagues for init elo: {}\t leagues={}\t choosing first one.",
//					elo, pvpLeagueList);
//		} else if (pvpLeagueList.isEmpty()) {
//			log.error("no pvp league id for elo: {}", elo);
//		} else { //size is one
//			pvpLeagueId = pvpLeagueList.get(0).getId();
//		}
//
//		int rank = PvpLeagueRetrieveUtils.getRankForElo(elo, pvpLeagueId);
//		Timestamp nowish = new Timestamp(clientTime.getTime());
//		int updated = updateUtil.updatePvpLeagueForUser(userId, pvpLeagueId,
//				rank, eloChange, nowish, nowish, 0, 0, 0, 0, 0);
//		assertEquals(1, updated);
//
//		sendQueueUpRequestEvent(user, elo, clientTime);
//
//	}
//
//	protected void sendQueueUpRequestEvent(User user, int elo, Date clientTime) {
//		QueueUpRequestProto qurp = createQueueUpRequestProto(user, elo,
//				clientTime);
//		QueueUpRequestEvent smjre = new QueueUpRequestEvent();
//		smjre.setTag(0);
//		smjre.setQueueUpRequestProto(qurp);
//
//		//SENDING THE REQUEST
//		queueUpController.handleEvent(smjre);
//	}
//
//	protected QueueUpRequestProto createQueueUpRequestProto(User user, int elo,
//			Date clientTime) {
//		assertNotNull(user);
//		MinimumUserProto mup = CreateInfoProtoUtils
//				.createMinimumUserProtoFromUserAndClan(user, null);
//
//		QueueUpRequestProto.Builder smjrpb = QueueUpRequestProto.newBuilder();
//
//		smjrpb.setAttacker(mup);
//		smjrpb.setClientTime(clientTime.getTime());
//
//		return smjrpb.build();
//	}
//
//	public HazelcastPvpUtil getHazelcastPvpUtil() {
//		return hazelcastPvpUtil;
//	}
//
//	public void setHazelcastPvpUtil(HazelcastPvpUtil hazelcastPvpUtil) {
//		this.hazelcastPvpUtil = hazelcastPvpUtil;
//	}
//
//	public MonsterForPvpRetrieveUtils getMonsterForPvpRetrieveUtil() {
//		return monsterForPvpRetrieveUtil;
//	}
//
//	public void setMonsterForPvpRetrieveUtil(
//			MonsterForPvpRetrieveUtils monsterForPvpRetrieveUtil) {
//		this.monsterForPvpRetrieveUtil = monsterForPvpRetrieveUtil;
//	}
//
//	public TimeUtils getTimeUtil() {
//		return timeUtil;
//	}
//
//	public void setTimeUtil(TimeUtils timeUtil) {
//		this.timeUtil = timeUtil;
//	}
//
//	public PvpLeagueForUserRetrieveUtil2 getPvpLeagueForUserRetrieveUtil() {
//		return pvpLeagueForUserRetrieveUtil;
//	}
//
//	public void setPvpLeagueForUserRetrieveUtil(
//			PvpLeagueForUserRetrieveUtil2 pvpLeagueForUserRetrieveUtil) {
//		this.pvpLeagueForUserRetrieveUtil = pvpLeagueForUserRetrieveUtil;
//	}
//
//	public ClanRetrieveUtils2 getClanRetrieveUtil() {
//		return clanRetrieveUtil;
//	}
//
//	public void setClanRetrieveUtil(ClanRetrieveUtils2 clanRetrieveUtil) {
//		this.clanRetrieveUtil = clanRetrieveUtil;
//	}
//
//	public UserRetrieveUtils2 getUserRetrieveUtil() {
//		return userRetrieveUtil;
//	}
//
//	public void setUserRetrieveUtil(UserRetrieveUtils2 userRetrieveUtil) {
//		this.userRetrieveUtil = userRetrieveUtil;
//	}
//
//	public MonsterForUserRetrieveUtils2 getMonsterForUserRetrieveUtil() {
//		return monsterForUserRetrieveUtil;
//	}
//
//	public void setMonsterForUserRetrieveUtil(
//			MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtil) {
//		this.monsterForUserRetrieveUtil = monsterForUserRetrieveUtil;
//	}
//
//	public ClanMemberTeamDonationRetrieveUtil getClanMemberTeamDonationRetrieveUtil() {
//		return clanMemberTeamDonationRetrieveUtil;
//	}
//
//	public void setClanMemberTeamDonationRetrieveUtil(
//			ClanMemberTeamDonationRetrieveUtil clanMemberTeamDonationRetrieveUtil) {
//		this.clanMemberTeamDonationRetrieveUtil = clanMemberTeamDonationRetrieveUtil;
//	}
//
//	public MonsterSnapshotForUserRetrieveUtil getMonsterSnapshotForUserRetrieveUtil() {
//		return monsterSnapshotForUserRetrieveUtil;
//	}
//
//	public void setMonsterSnapshotForUserRetrieveUtil(
//			MonsterSnapshotForUserRetrieveUtil monsterSnapshotForUserRetrieveUtil) {
//		this.monsterSnapshotForUserRetrieveUtil = monsterSnapshotForUserRetrieveUtil;
//	}
//
//	public InsertUtil getInsertUtil() {
//		return insertUtil;
//	}
//
//	public void setInsertUtil(InsertUtil insertUtil) {
//		this.insertUtil = insertUtil;
//	}
//
//	public QueueUpController getQueueUpController() {
//		return queueUpController;
//	}
//
//	public void setQueueUpController(QueueUpController queueUpController) {
//		this.queueUpController = queueUpController;
//	}
//
//	public UpdateUtil getUpdateUtil() {
//		return updateUtil;
//	}
//
//	public void setUpdateUtil(UpdateUtil updateUtil) {
//		this.updateUtil = updateUtil;
//	}
//
//}
