//package com.lvl6.test.controllertests;
//
//import static org.junit.Assert.assertEquals;
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
//import com.lvl6.events.request.ApproveOrRejectRequestToJoinClanRequestEvent;
//import com.lvl6.events.request.ChangeClanSettingsRequestEvent;
//import com.lvl6.events.request.CreateClanRequestEvent;
//import com.lvl6.events.request.PromoteDemoteClanMemberRequestEvent;
//import com.lvl6.events.request.RequestJoinClanRequestEvent;
//import com.lvl6.events.request.TransferClanOwnershipRequestEvent;
//import com.lvl6.info.Clan;
//import com.lvl6.info.User;
//import com.lvl6.info.UserClan;
//import com.lvl6.properties.ControllerConstants;
//import com.lvl6.properties.DBConstants;
//import com.lvl6.proto.ClanProto.UserClanStatus;
//import com.lvl6.proto.EventClanProto.ApproveOrRejectRequestToJoinClanRequestProto;
//import com.lvl6.proto.EventClanProto.ChangeClanSettingsRequestProto;
//import com.lvl6.proto.EventClanProto.CreateClanRequestProto;
//import com.lvl6.proto.EventClanProto.PromoteDemoteClanMemberRequestProto;
//import com.lvl6.proto.EventClanProto.RequestJoinClanRequestProto;
//import com.lvl6.proto.EventClanProto.TransferClanOwnershipRequestProto;
//import com.lvl6.proto.UserProto.MinimumUserProto;
//import com.lvl6.retrieveutils.ClanRetrieveUtils2;
//import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
//import com.lvl6.retrieveutils.UserRetrieveUtils2;
//import com.lvl6.server.controller.ApproveOrRejectRequestToJoinClanController;
//import com.lvl6.server.controller.ChangeClanSettingsController;
//import com.lvl6.server.controller.CreateClanController;
//import com.lvl6.server.controller.PromoteDemoteClanMemberController;
//import com.lvl6.server.controller.RequestJoinClanController;
//import com.lvl6.server.controller.TransferClanOwnershipController;
//import com.lvl6.utils.CreateInfoProtoUtils;
//import com.lvl6.utils.utilmethods.InsertUtil;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration("/test-spring-application-context.xml")
//public class ClanTest {
//
//	private static Logger log = LoggerFactory.getLogger(new Object() {
//	}.getClass().getEnclosingClass());
//
//	private JdbcTemplate jdbcTemplate;
//	private static boolean endOfTesting;
//	private static User userCreatingClan;
//	private static User userJoiningClan;
//
//	private static MinimumUserProto mup;
//	private static String userId1;
//	private static String userId2;
//	private static String clanUuid;
//
//	@Autowired
//	InsertUtil insertUtil;
//
//	@Autowired
//	UserRetrieveUtils2 userRetrieveUtil;
//
//	@Autowired
//	ClanRetrieveUtils2 clanRetrieveUtil;
//	
//	@Autowired
//	UserClanRetrieveUtils2 userClanRetrieveUtil;
//
//	@Autowired
//	CreateClanController createClanController;
//	
//	@Autowired
//	ChangeClanSettingsController changeClanSettingsController;
//	
//	@Autowired
//	RequestJoinClanController requestJoinClanController;
//	
//	@Autowired
//	TransferClanOwnershipController transferClanOwnershipController;
//	
//	@Autowired
//	ApproveOrRejectRequestToJoinClanController approveOrRejectRequestToJoinClanController;
//	
//	@Autowired
//	PromoteDemoteClanMemberController promoteDemoteClanMemberController;
//
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
//		if (userId1 == null) {
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
//			userId1 = insertUtil.insertUser(name, udid, lvl, playerExp, cash,
//					oil, gems, false, deviceToken, createTime, facebookId,
//					avatarMonsterId, email, fbData);
//
//			String name2 = "bobUnitTest";
//			String udid2 = "bobUdid";
//			int lvl2 = ControllerConstants.USER_CREATE__START_LEVEL;
//			int playerExp2 = 10;
//			int cash2 = 10000;
//			int oil2 = 10000;
//			int gems2 = 10000;
//			String deviceToken2 = "bobToken";
//			String facebookId2 = null;
//			int avatarMonsterId2 = ControllerConstants.TUTORIAL__STARTING_MONSTER_ID;
//			String email2 = null;
//			String fbData2 = null;
//
//			userId2 = insertUtil.insertUser(name2, udid2, lvl2, playerExp2, cash2,
//					oil2, gems2, false, deviceToken2, createTime, facebookId2,
//					avatarMonsterId2, email2, fbData2);
//
//			userCreatingClan = userRetrieveUtil.getUserById(userId1);
//			userJoiningClan = userRetrieveUtil.getUserById(userId2);
//		}
//	}
//
//	@After
//	public void tearDown() {
////		if (endOfTesting) {
////			if (null == userCreatingClan || userJoiningClan == null) {
////				log.info("no user or users to delete");
////				return;
////			}
////
////			String query1 = String.format("DELETE FROM %s where %s=?",
////					DBConstants.TABLE_RESEARCH_FOR_USER,
////					DBConstants.RESEARCH_FOR_USER__ID);
////			Object[] values1 = new Object[] { userResearchUuid };
////
////			int numDeleted = jdbcTemplate.update(query1, values1);
////			if (numDeleted != 1) {
////				log.error("did not delete test research for user when cleaning up");
////			}
////
////			String query2 = String.format("DELETE FROM %s where %s=?",
////					DBConstants.TABLE_USER, DBConstants.USER__ID);
////			Object[] values2 = new Object[] { user.getId() };
////			int[] types2 = new int[] { java.sql.Types.VARCHAR };
////
////			int numDeleted2 = jdbcTemplate.update(query2, values2, types2);
////			if (numDeleted2 != 1) {
////				log.error("did not delete test user when cleaning up");
////			}
////
////		}
//
//	}
//
//	@Test
//	public void testCreatingClan() {
//		CreateClanRequestProto.Builder ccrpb = CreateClanRequestProto
//				.newBuilder();
//		int userGems = userCreatingClan.getGems();
//		int userCash = userCreatingClan.getCash();
//
//		ccrpb.setSender(CreateInfoProtoUtils
//				.createMinimumUserProtoFromUserAndClan(userCreatingClan, null));
//		ccrpb.setName("test clan");
//		ccrpb.setTag("tes");
//		ccrpb.setRequestToJoinClanRequired(false);
//		ccrpb.setDescription("description");
//		ccrpb.setClanIconId(1);
//		ccrpb.setGemsSpent(100);
//		ccrpb.setCashChange(100);
//
//		CreateClanRequestEvent ccre = new CreateClanRequestEvent();
//		ccre.setTag(1);
//		ccre.setCreateClanRequestProto(ccrpb.build());
//		createClanController.handleEvent(ccre);
//
//		User user2 = userRetrieveUtil.getUserById(userId1);
//		assertEquals(userGems - 100, user2.getGems());
//		assertEquals(userCash - 100, user2.getCash());
//
//		List<Clan> clanList = clanRetrieveUtil.getClansWithSimilarNameOrTag(
//				"test clan", "tes");
//		
//		assertTrue(!clanList.isEmpty());
//		clanUuid = clanList.get(0).getId();
//		
//		UserClan uc = userClanRetrieveUtil.getSpecificUserClan(userId1, clanUuid);
//		assertTrue(uc.getStatus().equals("LEADER"));
//
//	}
//	
//	@Test
//	public void testChangeClanSettings() {
//		ChangeClanSettingsRequestProto.Builder ccsrpb = ChangeClanSettingsRequestProto
//				.newBuilder();
//		User user1 = userRetrieveUtil.getUserById(userId1);
//		
//		ccsrpb.setSender(CreateInfoProtoUtils
//				.createMinimumUserProtoFromUserAndClan(user1, null));
//		ccsrpb.setIsChangeDescription(true);
//		ccsrpb.setDescriptionNow("new description");
//		ccsrpb.setIsChangeJoinType(true);
//		ccsrpb.setRequestToJoinRequired(true);
//		ccsrpb.setIsChangeIcon(true);
//		ccsrpb.setIconId(7);
//		
//		ChangeClanSettingsRequestEvent ccsre = new ChangeClanSettingsRequestEvent();
//		ccsre.setTag(1);
//		ccsre.setChangeClanSettingsRequestProto(ccsrpb.build());
//		changeClanSettingsController.handleEvent(ccsre);
//		
//		List<Clan> clanList = clanRetrieveUtil.getClansWithSimilarNameOrTag(
//				"test clan", "tes");
//		assertTrue(!clanList.isEmpty());
//		Clan testClan = clanList.get(0);
//		assertTrue(testClan.getDescription().equals("new description"));
//		assertTrue(testClan.isRequestToJoinRequired());
//		assertTrue(testClan.getClanIconId() == 7);
//		
//	}
//	
//	@Test
//	public void testRequestJoinClan() {
//		RequestJoinClanRequestProto.Builder rjcrpb = RequestJoinClanRequestProto
//				.newBuilder();
//		User user1 = userRetrieveUtil.getUserById(userId1);
//		
//		rjcrpb.setSender(CreateInfoProtoUtils
//				.createMinimumUserProtoFromUserAndClan(user1, null));
//		rjcrpb.setClanUuid(clanUuid);
//		rjcrpb.setClientTime(new Date().getTime());
//		
//		RequestJoinClanRequestEvent rjcre = new RequestJoinClanRequestEvent();
//		rjcre.setTag(1);
//		rjcre.setRequestJoinClanRequestProto(rjcrpb.build());
//		requestJoinClanController.handleEvent(rjcre);
//	
//		List<Clan> clanList = clanRetrieveUtil.getClansWithSimilarNameOrTag(
//				"test clan", "tes");
//		
//		assertTrue(clanList.size() == 1);
//		
//		UserClan uc = userClanRetrieveUtil.getSpecificUserClan(userId2, clanUuid);
//		assertTrue(uc.getStatus().equals("REQUESTING"));
//		
//	}
//	
//	@Test
//	public void testApproveOrRejectRequestToJoinClan() {
//		ApproveOrRejectRequestToJoinClanRequestProto.Builder aorrtjcrpb = ApproveOrRejectRequestToJoinClanRequestProto
//				.newBuilder();
//		User user1 = userRetrieveUtil.getUserById(userId1);
//		
//		aorrtjcrpb.setSender(CreateInfoProtoUtils
//				.createMinimumUserProtoFromUserAndClan(user1, null));
//		aorrtjcrpb.setRequesterUuid(userId2);
//		aorrtjcrpb.setAccept(true);
//		
//		ApproveOrRejectRequestToJoinClanRequestEvent aorrtjcre = new ApproveOrRejectRequestToJoinClanRequestEvent();
//		aorrtjcre.setTag(1);
//		aorrtjcre.setApproveOrRejectRequestToJoinClanRequestProto(aorrtjcrpb.build());
//		approveOrRejectRequestToJoinClanController.handleEvent(aorrtjcre);
//		
//		List<Clan> clanList = clanRetrieveUtil.getClansWithSimilarNameOrTag(
//				"test clan", "tes");
//		
//		assertTrue(clanList.size() == 2);
//		
//		UserClan uc = userClanRetrieveUtil.getSpecificUserClan(userId2, clanUuid);
//		assertTrue(uc.getStatus().equals("MEMBER"));
//	}
//	
//	@Test 
//	public void testTransferClanOwnership() {
//		TransferClanOwnershipRequestProto.Builder tcorpb = TransferClanOwnershipRequestProto
//				.newBuilder();
//		User user1 = userRetrieveUtil.getUserById(userId1);
//		
//		tcorpb.setSender(CreateInfoProtoUtils
//				.createMinimumUserProtoFromUserAndClan(user1, null));
//		tcorpb.setClanOwnerUuidNew(userId2);
//		
//		TransferClanOwnershipRequestEvent tcore = new TransferClanOwnershipRequestEvent();
//		tcore.setTag(1);
//		tcore.setTransferClanOwnershipRequestProto(tcorpb.build());
//		transferClanOwnershipController.handleEvent(tcore);
//		
//		UserClan uc = userClanRetrieveUtil.getSpecificUserClan(userId2, clanUuid);
//		assertTrue(uc.getStatus().equals("LEADER"));
//		
//		UserClan uc2 = userClanRetrieveUtil.getSpecificUserClan(userId1, clanUuid);
//		assertTrue(uc2.getStatus().equals("MEMBER"));
//		
//	}
//	
//	@Test 
//	public void testPromoteDemoteClanMember() {
//		PromoteDemoteClanMemberRequestProto.Builder pdcmrpb = PromoteDemoteClanMemberRequestProto
//				.newBuilder();
//		User user2 = userRetrieveUtil.getUserById(userId2);
//		
//		pdcmrpb.setSender(CreateInfoProtoUtils
//				.createMinimumUserProtoFromUserAndClan(user2, null));
//		pdcmrpb.setVictimUuid(userId1);
//		UserClanStatus ucs = UserClanStatus.valueOf("CAPTAIN");
//		pdcmrpb.setUserClanStatus(ucs);
//		
//		PromoteDemoteClanMemberRequestEvent pdcmre = new PromoteDemoteClanMemberRequestEvent();
//		pdcmre.setTag(1);
//		pdcmre.setPromoteDemoteClanMemberRequestProto(pdcmrpb.build());
//		promoteDemoteClanMemberController.handleEvent(pdcmre);
//		
//		UserClan uc2 = userClanRetrieveUtil.getSpecificUserClan(userId1, clanUuid);
//		assertTrue(uc2.getStatus().equals("CAPTAIN"));
//		
//		//demote him back to member
//		PromoteDemoteClanMemberRequestProto.Builder pdcmrpb2 = PromoteDemoteClanMemberRequestProto
//				.newBuilder();
//		User user3 = userRetrieveUtil.getUserById(userId2);
//		
//		pdcmrpb2.setSender(CreateInfoProtoUtils
//				.createMinimumUserProtoFromUserAndClan(user3, null));
//		pdcmrpb2.setVictimUuid(userId1);
//		UserClanStatus ucs2 = UserClanStatus.valueOf("MEMBER");
//		pdcmrpb2.setUserClanStatus(ucs2);
//		
//		PromoteDemoteClanMemberRequestEvent pdcmre2 = new PromoteDemoteClanMemberRequestEvent();
//		pdcmre2.setTag(1);
//		pdcmre2.setPromoteDemoteClanMemberRequestProto(pdcmrpb2.build());
//		promoteDemoteClanMemberController.handleEvent(pdcmre2);
//		
//		UserClan uc = userClanRetrieveUtil.getSpecificUserClan(userId1, clanUuid);
//		assertTrue(uc.getStatus().equals("MEMBER"));
//			
//	}
//	
//	
//
//	
//
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//
//}
