package com.lvl6.test.ControllerTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

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

import com.lvl6.events.request.CreateClanRequestEvent;
import com.lvl6.info.Clan;
import com.lvl6.info.User;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.properties.DBConstants;
import com.lvl6.proto.EventClanProto.CreateClanRequestProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.retrieveutils.ResearchForUserRetrieveUtils;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.controller.CreateClanController;
import com.lvl6.server.controller.FinishPerformingResearchController;
import com.lvl6.server.controller.PerformResearchController;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.InsertUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-spring-application-context.xml")
public class ClanTest {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private JdbcTemplate jdbcTemplate;
	private static boolean endOfTesting;
	private static User user;

	private static MinimumUserProto mup;
	private static String userId;
	private static String userResearchUuid;

	@Autowired
	InsertUtil insertUtil;

	@Autowired
	UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	ResearchForUserRetrieveUtils researchForUserRetrieveUtil;

	@Autowired
	PerformResearchController performResearchController;

	@Autowired
	FinishPerformingResearchController finishPerformingResearchController;

	@Autowired
	ClanRetrieveUtils2 clanRetrieveUtil;

	@Autowired
	CreateClanController createClanController;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Before
	public void setUp() {

		if (userId == null) {
			endOfTesting = false;
			log.info("setUp");
			Timestamp createTime = new Timestamp((new Date()).getTime());

			String name = "bobUnitTest";
			String udid = "bobUdid";
			int lvl = ControllerConstants.USER_CREATE__START_LEVEL;
			int playerExp = 10;
			int cash = 10000;
			int oil = 10000;
			int gems = 10000;
			String deviceToken = "bobToken";
			String facebookId = null;
			int avatarMonsterId = ControllerConstants.TUTORIAL__STARTING_MONSTER_ID;
			String email = null;
			String fbData = null;

			userId = insertUtil.insertUser(name, udid, lvl, playerExp, cash,
					oil, gems, false, deviceToken, createTime, facebookId,
					avatarMonsterId, email, fbData);

			//		if (null == userId) {
			//			throw new RuntimeException("no user was created!");
			//		}

			user = userRetrieveUtil.getUserById(userId);
		}
	}

	@After
	public void tearDown() {
		if (endOfTesting) {
			if (null == user) {
				log.info("no user to delete");
				return;
			}

			String query1 = String.format("DELETE FROM %s where %s=?",
					DBConstants.TABLE_RESEARCH_FOR_USER,
					DBConstants.RESEARCH_FOR_USER__ID);
			Object[] values1 = new Object[] { userResearchUuid };

			int numDeleted = jdbcTemplate.update(query1, values1);
			if (numDeleted != 1) {
				log.error("did not delete test research for user when cleaning up");
			}

			String query2 = String.format("DELETE FROM %s where %s=?",
					DBConstants.TABLE_USER, DBConstants.USER__ID);
			Object[] values2 = new Object[] { user.getId() };
			int[] types2 = new int[] { java.sql.Types.VARCHAR };

			int numDeleted2 = jdbcTemplate.update(query2, values2, types2);
			if (numDeleted2 != 1) {
				log.error("did not delete test user when cleaning up");
			}

		}

	}

	@Test
	public void testCreatingClan() {
		CreateClanRequestProto.Builder ccrpb = CreateClanRequestProto
				.newBuilder();
		User user = userRetrieveUtil.getUserById(userId);
		int userGems = user.getGems();
		int userCash = user.getCash();

		ccrpb.setSender(CreateInfoProtoUtils
				.createMinimumUserProtoFromUserAndClan(user, null));
		ccrpb.setName("test clan");
		ccrpb.setTag("tes");
		ccrpb.setRequestToJoinClanRequired(true);
		ccrpb.setDescription("description");
		ccrpb.setClanIconId(1);
		ccrpb.setGemsSpent(100);
		ccrpb.setCashChange(100);

		CreateClanRequestEvent ccre = new CreateClanRequestEvent();
		ccre.setTag(1);
		ccre.setCreateClanRequestProto(ccrpb.build());
		createClanController.handleEvent(ccre);

		User user2 = userRetrieveUtil.getUserById(userId);
		assertEquals(userGems - 100, user2.getGems());
		assertEquals(userCash - 100, user2.getCash());

		List<Clan> clanList = clanRetrieveUtil.getClansWithSimilarNameOrTag(
				"test clan", "tes");
		assertTrue(!clanList.isEmpty());

	}

}
