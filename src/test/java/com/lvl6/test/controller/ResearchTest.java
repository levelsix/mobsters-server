//package com.lvl6.test.controller;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;
//
//import java.sql.Timestamp;
//import java.util.Date;
//import java.util.List;
//
//import javax.annotation.Resource;
//import javax.sql.DataSource;
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
//import com.lvl6.events.request.FinishPerformingResearchRequestEvent;
//import com.lvl6.events.request.PerformResearchRequestEvent;
//import com.lvl6.info.ResearchForUser;
//import com.lvl6.info.User;
//import com.lvl6.properties.ControllerConstants;
//import com.lvl6.properties.DBConstants;
//import com.lvl6.proto.EventResearchProto.FinishPerformingResearchRequestProto;
//import com.lvl6.proto.EventResearchProto.PerformResearchRequestProto;
//import com.lvl6.proto.StructureProto.ResourceType;
//import com.lvl6.proto.UserProto.MinimumUserProto;
//import com.lvl6.retrieveutils.ResearchForUserRetrieveUtils;
//import com.lvl6.retrieveutils.UserRetrieveUtils2;
//import com.lvl6.server.controller.FinishPerformingResearchController;
//import com.lvl6.server.controller.PerformResearchController;
//import com.lvl6.utils.CreateInfoProtoUtils;
//import com.lvl6.utils.utilmethods.InsertUtil;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration("/test-spring-application-context.xml")
//public class ResearchTest {
//
//	private static Logger log = LoggerFactory.getLogger(new Object() {
//	}.getClass().getEnclosingClass());
//
//	private JdbcTemplate jdbcTemplate;
//	private static boolean endOfTesting;
//	private static User user;
//
//	private static MinimumUserProto mup;
//	private static String userId;
//	private static String userResearchUuid;
//
//	@Autowired
//	InsertUtil insertUtil;
//
//	@Autowired
//	UserRetrieveUtils2 userRetrieveUtil;
//
//	@Autowired
//	ResearchForUserRetrieveUtils researchForUserRetrieveUtil;
//
//	@Autowired
//	PerformResearchController performResearchController;
//
//	@Autowired
//	FinishPerformingResearchController finishPerformingResearchController;
//
//	@Resource
//	public void setDataSource(DataSource dataSource) {
//		log.info("Setting datasource and creating jdbcTemplate");
//		this.jdbcTemplate = new JdbcTemplate(dataSource);
//	}
//
//	@Before
//	public void setUp() {
//
//		if (userId == null) {
//			endOfTesting = false;
//			log.info("setUp");
//			Timestamp createTime = new Timestamp((new Date()).getTime());
//
//			String name = "bobUnitTest";
//			String udid = "bobUdid";
//			int lvl = ControllerConstants.USER_CREATE__START_LEVEL;
//			int playerExp = 10;
//			int cash = 10000;
//			int oil = 10000;
//			int gems = 10000;
//			String deviceToken = "bobToken";
//			String facebookId = null;
//			int avatarMonsterId = ControllerConstants.TUTORIAL__STARTING_MONSTER_ID;
//			String email = null;
//			String fbData = null;
//
//			userId = insertUtil.insertUser(name, udid, lvl, playerExp, cash,
//					oil, gems, false, deviceToken, createTime, facebookId,
//					avatarMonsterId, email, fbData);
//
//			//		if (null == userId) {
//			//			throw new RuntimeException("no user was created!");
//			//		}
//
//			user = userRetrieveUtil.getUserById(userId);
//		}
//	}
//
//	@After
//	public void tearDown() {
//		if (endOfTesting) {
//			if (null == user) {
//				log.info("no user to delete");
//				return;
//			}
//
//			String query1 = String.format("DELETE FROM %s where %s=?",
//					DBConstants.TABLE_RESEARCH_FOR_USER,
//					DBConstants.RESEARCH_FOR_USER__ID);
//			Object[] values1 = new Object[] { userResearchUuid };
//
//			int numDeleted = jdbcTemplate.update(query1, values1);
//			if (numDeleted != 1) {
//				log.error("did not delete test research for user when cleaning up");
//			}
//
//			String query2 = String.format("DELETE FROM %s where %s=?",
//					DBConstants.TABLE_USER, DBConstants.USER__ID);
//			Object[] values2 = new Object[] { user.getId() };
//			int[] types2 = new int[] { java.sql.Types.VARCHAR };
//
//			int numDeleted2 = jdbcTemplate.update(query2, values2, types2);
//			if (numDeleted2 != 1) {
//				log.error("did not delete test user when cleaning up");
//			}
//
//		}
//
//	}
//
//	@Test
//	public void testResearch() {
//
//		User user1 = userRetrieveUtil.getUserById(user.getId());
//		int userGems1 = user1.getGems();
//		int userOil1 = user1.getOil();
//		PerformResearchRequestProto.Builder prrpb = PerformResearchRequestProto
//				.newBuilder();
//
//		prrpb.setSender(CreateInfoProtoUtils
//				.createMinimumUserProtoFromUserAndClan(user1, null));
//		prrpb.setResearchId(1);
//		Date date = new Date();
//		prrpb.setClientTime(date.getTime());
//		prrpb.setGemsCost(25);
//		prrpb.setResourceCost(100);
//		ResourceType rt = ResourceType.OIL;
//		prrpb.setResourceType(rt);
//
//		PerformResearchRequestEvent prre = new PerformResearchRequestEvent();
//		prre.setTag(1);
//		prre.setPerformResearchRequestProto(prrpb.build());
//		performResearchController.handleEvent(prre);
//
//		List<ResearchForUser> rfuList = researchForUserRetrieveUtil
//				.getAllResearchForUser(user1.getId());
//		//assertNotNull(rfuList);
//
//		assertTrue(rfuList.size() == 1);
//		User user2 = userRetrieveUtil.getUserById(user.getId());
//		assertEquals(user2.getOil(), userOil1 - 100);
//		assertEquals(user2.getGems(), userGems1 - 25);
//
//		for (ResearchForUser rfu : rfuList) {
//			assertEquals(rfu.getUserId(), user1.getId());
//			assertFalse(rfu.isComplete());
//			userResearchUuid = rfu.getId();
//		}
//
//	}
//
//	@Test
//	public void testUpgradeResearch() {
//		User user1 = userRetrieveUtil.getUserById(user.getId());
//		Date date = new Date();
//		int userCash = user1.getCash();
//		int userGems2 = user1.getGems();
//
//		PerformResearchRequestProto.Builder prrpb = PerformResearchRequestProto
//				.newBuilder();
//		prrpb.setSender(CreateInfoProtoUtils
//				.createMinimumUserProtoFromUserAndClan(user1, null));
//		prrpb.setResearchId(2);
//		prrpb.setClientTime(date.getTime());
//		prrpb.setGemsCost(25);
//		prrpb.setUserResearchUuid(userResearchUuid);
//		prrpb.setResourceCost(50);
//		ResourceType rt = ResourceType.CASH;
//		prrpb.setResourceType(rt);
//
//		PerformResearchRequestEvent prre2 = new PerformResearchRequestEvent();
//		prre2.setTag(1);
//		prre2.setPerformResearchRequestProto(prrpb.build());
//		performResearchController.handleEvent(prre2);
//
//		List<ResearchForUser> rfuList = researchForUserRetrieveUtil
//				.getAllResearchForUser(user1.getId());
//		User currUser = userRetrieveUtil.getUserById(user.getId());
//
//		assertTrue(rfuList.size() == 1);
//		assertEquals(userCash - 50, currUser.getCash());
//		assertEquals(userGems2 - 25, currUser.getGems());
//
//		for (ResearchForUser rfu : rfuList) {
//			assertEquals(rfu.getUserId(), user1.getId());
//			assertFalse(rfu.isComplete());
//		}
//
//	}
//
//	@Test
//	public void testFinishResearch() {
//		User user1 = userRetrieveUtil.getUserById(user.getId());
//		int userGems = user1.getGems();
//
//		FinishPerformingResearchRequestProto.Builder fprrpb = FinishPerformingResearchRequestProto
//				.newBuilder();
//		fprrpb.setSender(CreateInfoProtoUtils
//				.createMinimumUserProtoFromUserAndClan(user1, null));
//		fprrpb.setUserResearchUuid(userResearchUuid);
//		fprrpb.setGemsCost(50);
//
//		FinishPerformingResearchRequestEvent fprre = new FinishPerformingResearchRequestEvent();
//		fprre.setTag(1);
//		fprre.setFinishPerformingResearchRequestProto(fprrpb.build());
//		finishPerformingResearchController.handleEvent(fprre);
//
//		List<ResearchForUser> rfuList = researchForUserRetrieveUtil
//				.getAllResearchForUser(user1.getId());
//		User currUser = userRetrieveUtil.getUserById(user.getId());
//
//		assertEquals(userGems - 50, currUser.getGems());
//
//		for (ResearchForUser rfu : rfuList) {
//			assertTrue(rfu.isComplete());
//		}
//		endOfTesting = true;
//	}
//
//}