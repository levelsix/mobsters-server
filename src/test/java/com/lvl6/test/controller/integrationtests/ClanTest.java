package com.lvl6.test.controller.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

import com.lvl6.events.request.ApproveOrRejectRequestToJoinClanRequestEvent;
import com.lvl6.events.request.BootPlayerFromClanRequestEvent;
import com.lvl6.events.request.ChangeClanSettingsRequestEvent;
import com.lvl6.events.request.CreateClanRequestEvent;
import com.lvl6.events.request.LeaveClanRequestEvent;
import com.lvl6.events.request.PromoteDemoteClanMemberRequestEvent;
import com.lvl6.events.request.RequestJoinClanRequestEvent;
import com.lvl6.events.request.RetractRequestJoinClanRequestEvent;
import com.lvl6.events.request.TransferClanOwnershipRequestEvent;
import com.lvl6.info.Clan;
import com.lvl6.info.User;
import com.lvl6.info.UserClan;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.properties.DBConstants;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.EventClanProto.ApproveOrRejectRequestToJoinClanRequestProto;
import com.lvl6.proto.EventClanProto.BootPlayerFromClanRequestProto;
import com.lvl6.proto.EventClanProto.ChangeClanSettingsRequestProto;
import com.lvl6.proto.EventClanProto.CreateClanRequestProto;
import com.lvl6.proto.EventClanProto.LeaveClanRequestProto;
import com.lvl6.proto.EventClanProto.PromoteDemoteClanMemberRequestProto;
import com.lvl6.proto.EventClanProto.RequestJoinClanRequestProto;
import com.lvl6.proto.EventClanProto.RetractRequestJoinClanRequestProto;
import com.lvl6.proto.EventClanProto.TransferClanOwnershipRequestProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.controller.ApproveOrRejectRequestToJoinClanController;
import com.lvl6.server.controller.BootPlayerFromClanController;
import com.lvl6.server.controller.ChangeClanSettingsController;
import com.lvl6.server.controller.CreateClanController;
import com.lvl6.server.controller.PromoteDemoteClanMemberController;
import com.lvl6.server.controller.RequestJoinClanController;
import com.lvl6.server.controller.RetractRequestJoinClanController;
import com.lvl6.server.controller.TransferClanOwnershipController;
import com.lvl6.server.eventsender.EventsUtil;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.InsertUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-spring-application-context.xml")
public class ClanTest {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private JdbcTemplate jdbcTemplate;
	private static boolean endOfTesting;
	private static User userCreatingClan;
	private static User userJoiningClan;
	private static User userRetractingRequest;
	private static User userLeaving;

	private static MinimumUserProto mup;
	private static String userId1;
	private static String userId2;
	private static String userId3;
	private static String userId4;
	private static String clanUuid;

	@Autowired
	InsertUtil insertUtil;

	@Autowired
	UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	ClanRetrieveUtils2 clanRetrieveUtil;

	@Autowired
	UserClanRetrieveUtils2 userClanRetrieveUtil;

	@Autowired
	CreateClanController createClanController;

	@Autowired
	ChangeClanSettingsController changeClanSettingsController;

	@Autowired
	RequestJoinClanController requestJoinClanController;

	@Autowired
	TransferClanOwnershipController transferClanOwnershipController;

	@Autowired
	ApproveOrRejectRequestToJoinClanController approveOrRejectRequestToJoinClanController;

	@Autowired
	PromoteDemoteClanMemberController promoteDemoteClanMemberController;
	
	@Autowired
	RetractRequestJoinClanController retractRequestJoinClanController;
	
	@Autowired
	BootPlayerFromClanController bootPlayerFromClanController;

	@Autowired
	CreateInfoProtoUtils createInfoProtoUtils;


	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Before
	public void setUp() {

		if (userId1 == null) {
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

			userId1 = insertUtil.insertUser(name, udid, lvl, playerExp, cash,
					oil, gems, false, deviceToken, createTime, facebookId,
					avatarMonsterId, email, fbData);

			String name2 = "bobUnitTest";
			String udid2 = "bobUdid";
			int lvl2 = ControllerConstants.USER_CREATE__START_LEVEL;
			int playerExp2 = 10;
			int cash2 = 10000;
			int oil2 = 10000;
			int gems2 = 10000;
			String deviceToken2 = "bobToken";
			String facebookId2 = null;
			int avatarMonsterId2 = ControllerConstants.TUTORIAL__STARTING_MONSTER_ID;
			String email2 = null;
			String fbData2 = null;

			userId2 = insertUtil.insertUser(name2, udid2, lvl2, playerExp2, cash2,
					oil2, gems2, false, deviceToken2, createTime, facebookId2,
					avatarMonsterId2, email2, fbData2);

			String name3 = "bobUnitTest";
			String udid3 = "bobUdid";
			int lvl3 = ControllerConstants.USER_CREATE__START_LEVEL;
			int playerExp3 = 10;
			int cash3 = 10000;
			int oil3 = 10000;
			int gems3 = 10000;
			String deviceToken3 = "bobToken";
			String facebookId3 = null;
			int avatarMonsterId3 = ControllerConstants.TUTORIAL__STARTING_MONSTER_ID;
			String email3 = null;
			String fbData3 = null;

			userId3 = insertUtil.insertUser(name3, udid3, lvl3, playerExp3, cash3,
					oil3, gems3, false, deviceToken3, createTime, facebookId3,
					avatarMonsterId3, email3, fbData3);

			String name4 = "bobUnitTest";
			String udid4 = "bobUdid";
			int lvl4 = ControllerConstants.USER_CREATE__START_LEVEL;
			int playerExp4 = 10;
			int cash4 = 10000;
			int oil4 = 10000;
			int gems4 = 10000;
			String deviceToken4 = "bobToken";
			String facebookId4 = null;
			int avatarMonsterId4 = ControllerConstants.TUTORIAL__STARTING_MONSTER_ID;
			String email4 = null;
			String fbData4 = null;

			userId4 = insertUtil.insertUser(name4, udid4, lvl4, playerExp4, cash4,
					oil4, gems4, false, deviceToken4, createTime, facebookId4,
					avatarMonsterId4, email4, fbData4);

			List<String> userIds = new ArrayList<String>();
			userIds.add(userId1);
			userIds.add(userId2);
			userIds.add(userId3);
			userIds.add(userId4);
			Map<String, User> idsToUsers = userRetrieveUtil.getUsersByIds(userIds);

			userCreatingClan = idsToUsers.get(userId1);
			userJoiningClan = idsToUsers.get(userId2);
			userRetractingRequest = idsToUsers.get(userId3);
			userLeaving = idsToUsers.get(userId4);
		}
	}

	@After
	public void tearDown() {
		if (null == userCreatingClan || userJoiningClan == null) {
			log.info("no user or users to delete");
			return;
		}

		String query1 = String.format("DELETE FROM %s where %s=?",
				DBConstants.TABLE_CLANS,
				DBConstants.CLANS__NAME);
		Object[] values1 = new Object[] { "test clan2" };

		int numDeleted = jdbcTemplate.update(query1, values1);
		if (numDeleted != 1) {
			log.error("did not delete test research for user when cleaning up");
		}

		String query2 = String.format("DELETE FROM %s where %s=?",
				DBConstants.TABLE_USER, DBConstants.USER__ID);
		Object[] values2 = new Object[] { userId1 };
		int[] types2 = new int[] { java.sql.Types.VARCHAR };

		int numDeleted2 = jdbcTemplate.update(query2, values2, types2);
		if (numDeleted2 != 1) {
			log.error("did not delete test user when cleaning up");
		}

		String query3 = String.format("DELETE FROM %s where %s=?",
				DBConstants.TABLE_USER, DBConstants.USER__ID);
		Object[] values3 = new Object[] { userId2 };
		int[] types3 = new int[] { java.sql.Types.VARCHAR };

		int numDeleted3 = jdbcTemplate.update(query3, values3, types3);
		if (numDeleted3 != 1) {
			log.error("did not delete test user when cleaning up");
		}

		String query4 = String.format("DELETE FROM %s where %s=?",
				DBConstants.TABLE_CLAN_FOR_USER,
				DBConstants.CLAN_FOR_USER__USER_ID);
		Object[] values4 = new Object[] { userId1 };

		int numDeleted4 = jdbcTemplate.update(query4, values4);
		if (numDeleted4 != 1) {
			log.error("did not delete userclan for user when cleaning up");
		}

		String query5 = String.format("DELETE FROM %s where %s=?",
				DBConstants.TABLE_CLAN_FOR_USER,
				DBConstants.CLAN_FOR_USER__USER_ID);
		Object[] values5 = new Object[] { userId2 };

		int numDeleted5 = jdbcTemplate.update(query5, values5);
		if (numDeleted5 != 1) {
			log.error("did not delete userclan for user when cleaning up");
		}

	}


	@Test
	public void testCreatingClan() {
		CreateClanRequestProto.Builder ccrpb = CreateClanRequestProto
				.newBuilder();
		int userGems = userCreatingClan.getGems();
		int userCash = userCreatingClan.getCash();

		ccrpb.setSender(createInfoProtoUtils
				.createMinimumUserProtoFromUserAndClan(userCreatingClan, null));
		ccrpb.setName("test clan2");
		ccrpb.setTag("tc2");
		ccrpb.setRequestToJoinClanRequired(false);
		ccrpb.setDescription("description");
		ccrpb.setClanIconId(1);
		ccrpb.setGemsSpent(100);
		ccrpb.setCashChange(-100);

		CreateClanRequestEvent ccre = new CreateClanRequestEvent();
		ccre.setTag(1);
		ccre.setCreateClanRequestProto(ccrpb.build());
		createClanController.processRequestEvent(ccre, EventsUtil.getToClientEvents());

		User user2 = userRetrieveUtil.getUserById(userId1);
		assertEquals(userGems - 100, user2.getGems());
		assertEquals(userCash - 100, user2.getCash());

		List<Clan> clanList = clanRetrieveUtil.getClansWithSimilarNameOrTag(
				"test clan2", "tc2");

		assertTrue(!clanList.isEmpty());
		clanUuid = clanList.get(0).getId();

		UserClan uc = userClanRetrieveUtil.getSpecificUserClan(userId1, clanUuid);
		assertTrue(uc.getStatus().equals("LEADER"));

		//CHANGE CLAN SETTINGS
		ChangeClanSettingsRequestProto.Builder ccsrpb = ChangeClanSettingsRequestProto
				.newBuilder();
		User user1 = userRetrieveUtil.getUserById(userId1);

		Clan testClan1 = clanRetrieveUtil.getClanWithNameOrTag(
				"test clan2", "tc2");

		ccsrpb.setSender(createInfoProtoUtils
				.createMinimumUserProtoFromUserAndClan(user1, testClan1));
		ccsrpb.setIsChangeDescription(true);
		ccsrpb.setDescriptionNow("new description");
		ccsrpb.setIsChangeJoinType(true);
		ccsrpb.setRequestToJoinRequired(true);
		ccsrpb.setIsChangeIcon(true);
		ccsrpb.setIconId(7);

		ChangeClanSettingsRequestEvent ccsre = new ChangeClanSettingsRequestEvent();
		ccsre.setTag(1);
		ccsre.setChangeClanSettingsRequestProto(ccsrpb.build());
		changeClanSettingsController.processRequestEvent(ccsre, EventsUtil.getToClientEvents());

		Clan testClan2 = clanRetrieveUtil.getClanWithNameOrTag(
				"test clan2", "tc2");
		assertTrue(testClan2 != null);
		log.info("testclan: "+ testClan2);
		assertTrue(testClan2.getDescription().equals("new description"));
		assertTrue(testClan2.isRequestToJoinRequired());
		assertTrue(testClan2.getClanIconId() == 7);

		//REQUEST JOIN CLAN
		RequestJoinClanRequestProto.Builder rjcrpb = RequestJoinClanRequestProto
				.newBuilder();
		User user3 = userRetrieveUtil.getUserById(userId2);

		rjcrpb.setSender(createInfoProtoUtils
				.createMinimumUserProtoFromUserAndClan(user3, null));
		rjcrpb.setClanUuid(clanUuid);
		rjcrpb.setClientTime(new Date().getTime());

		RequestJoinClanRequestEvent rjcre = new RequestJoinClanRequestEvent();
		rjcre.setTag(1);
		rjcre.setRequestJoinClanRequestProto(rjcrpb.build());
		requestJoinClanController.processRequestEvent(rjcre, EventsUtil.getToClientEvents());

		List<Clan> clanList3 = clanRetrieveUtil.getClansWithSimilarNameOrTag(
				"test clan2", "tc2");

		assertTrue(clanList3.size() == 1);

		UserClan uc2 = userClanRetrieveUtil.getSpecificUserClan(userId2, clanUuid);
		assertTrue(uc2.getStatus().equals("REQUESTING"));

		//APPROVE REJECT REQUEST TO JOIN
		ApproveOrRejectRequestToJoinClanRequestProto.Builder aorrtjcrpb = ApproveOrRejectRequestToJoinClanRequestProto
				.newBuilder();
		User user4 = userRetrieveUtil.getUserById(userId1);

		Clan testClan3 = clanRetrieveUtil.getClanWithNameOrTag(
				"test clan2", "tc2");

		aorrtjcrpb.setSender(createInfoProtoUtils
				.createMinimumUserProtoFromUserAndClan(user4, testClan3));
		aorrtjcrpb.setRequesterUuid(userId2);
		aorrtjcrpb.setAccept(true);

		ApproveOrRejectRequestToJoinClanRequestEvent aorrtjcre = new ApproveOrRejectRequestToJoinClanRequestEvent();
		aorrtjcre.setTag(1);
		aorrtjcre.setApproveOrRejectRequestToJoinClanRequestProto(aorrtjcrpb.build());
		approveOrRejectRequestToJoinClanController.processRequestEvent(aorrtjcre, EventsUtil.getToClientEvents());

		List<Clan> clanList4 = clanRetrieveUtil.getClansWithSimilarNameOrTag(
				"test clan2", "tc2");

		assertTrue(clanList4.size() == 1);

		UserClan uc3 = userClanRetrieveUtil.getSpecificUserClan(userId2, clanUuid);
		assertTrue(uc3.getStatus().equals("MEMBER"));

		//TRANSFER CLAN OWNERSHIP
		TransferClanOwnershipRequestProto.Builder tcorpb = TransferClanOwnershipRequestProto
				.newBuilder();
		User user5 = userRetrieveUtil.getUserById(userId1);

		tcorpb.setSender(createInfoProtoUtils
				.createMinimumUserProtoFromUserAndClan(user5, testClan3));
		tcorpb.setClanOwnerUuidNew(userId2);

		TransferClanOwnershipRequestEvent tcore = new TransferClanOwnershipRequestEvent();
		tcore.setTag(1);
		tcore.setTransferClanOwnershipRequestProto(tcorpb.build());
		transferClanOwnershipController.processRequestEvent(tcore, EventsUtil.getToClientEvents());

		UserClan uc4 = userClanRetrieveUtil.getSpecificUserClan(userId2, clanUuid);
		assertTrue(uc4.getStatus().equals("LEADER"));

		UserClan uc5 = userClanRetrieveUtil.getSpecificUserClan(userId1, clanUuid);
		assertTrue(uc5.getStatus().equals("JUNIOR_LEADER"));

		//PROMOTE DEMOTE
		PromoteDemoteClanMemberRequestProto.Builder pdcmrpb = PromoteDemoteClanMemberRequestProto
				.newBuilder();
		User user6 = userRetrieveUtil.getUserById(userId2);

		pdcmrpb.setSender(createInfoProtoUtils
				.createMinimumUserProtoFromUserAndClan(user6, testClan3));
		pdcmrpb.setVictimUuid(userId1);
		UserClanStatus ucs = UserClanStatus.valueOf("CAPTAIN");
		pdcmrpb.setUserClanStatus(ucs);

		PromoteDemoteClanMemberRequestEvent pdcmre = new PromoteDemoteClanMemberRequestEvent();
		pdcmre.setTag(1);
		pdcmre.setPromoteDemoteClanMemberRequestProto(pdcmrpb.build());
		promoteDemoteClanMemberController.processRequestEvent(pdcmre, EventsUtil.getToClientEvents());

		UserClan uc6 = userClanRetrieveUtil.getSpecificUserClan(userId1, clanUuid);
		assertTrue(uc6.getStatus().equals("CAPTAIN"));

		//demote him back to member
		PromoteDemoteClanMemberRequestProto.Builder pdcmrpb2 = PromoteDemoteClanMemberRequestProto
				.newBuilder();
		User user7 = userRetrieveUtil.getUserById(userId2);

		pdcmrpb2.setSender(createInfoProtoUtils
				.createMinimumUserProtoFromUserAndClan(user7, testClan3));
		pdcmrpb2.setVictimUuid(userId1);
		UserClanStatus ucs2 = UserClanStatus.valueOf("MEMBER");
		pdcmrpb2.setUserClanStatus(ucs2);

		PromoteDemoteClanMemberRequestEvent pdcmre2 = new PromoteDemoteClanMemberRequestEvent();
		pdcmre2.setTag(1);
		pdcmre2.setPromoteDemoteClanMemberRequestProto(pdcmrpb2.build());
		promoteDemoteClanMemberController.processRequestEvent(pdcmre2, EventsUtil.getToClientEvents());

		UserClan uc7 = userClanRetrieveUtil.getSpecificUserClan(userId1, clanUuid);
		assertTrue(uc7.getStatus().equals("MEMBER"));

		//REQUEST JOIN CLAN
		RequestJoinClanRequestProto.Builder rjcrpb2 = RequestJoinClanRequestProto
				.newBuilder();
		User user8 = userRetrieveUtil.getUserById(userId3);

		rjcrpb2.setSender(createInfoProtoUtils
				.createMinimumUserProtoFromUserAndClan(user8, testClan3));
		rjcrpb2.setClanUuid(clanUuid);
		rjcrpb2.setClientTime(new Date().getTime());

		RequestJoinClanRequestEvent rjcre2 = new RequestJoinClanRequestEvent();
		rjcre2.setTag(1);
		rjcre2.setRequestJoinClanRequestProto(rjcrpb2.build());
		requestJoinClanController.processRequestEvent(rjcre2, EventsUtil.getToClientEvents());

		List<Clan> clanList5 = clanRetrieveUtil.getClansWithSimilarNameOrTag(
				"test clan2", "tc2");

		assertTrue(clanList5.size() == 1);

		UserClan uc8 = userClanRetrieveUtil.getSpecificUserClan(userId3, clanUuid);
		assertTrue(uc2.getStatus().equals("REQUESTING"));

		//RETRACTING REQUEST
		RetractRequestJoinClanRequestProto.Builder rrjcrpb = RetractRequestJoinClanRequestProto
				.newBuilder();
		User user9 = userRetrieveUtil.getUserById(userId3);

		rrjcrpb.setSender(createInfoProtoUtils
				.createMinimumUserProtoFromUserAndClan(user9, testClan3));
		rrjcrpb.setClanUuid(clanUuid);
		
		RetractRequestJoinClanRequestEvent rrjcre = new RetractRequestJoinClanRequestEvent();
		rrjcre.setTag(1);
		rrjcre.setRetractRequestJoinClanRequestProto(rrjcrpb.build());
		retractRequestJoinClanController.processRequestEvent(rrjcre, EventsUtil.getToClientEvents());
		
		List<Clan> clanList6 = clanRetrieveUtil.getClansWithSimilarNameOrTag(
				"test clan2", "tc2");

		assertTrue(clanList6.size() == 1);

		UserClan uc9 = userClanRetrieveUtil.getSpecificUserClan(userId3, clanUuid);
		assertTrue(uc9 == null);
		
		//BOOTING PLAYER
		BootPlayerFromClanRequestProto.Builder bpfcrpb = BootPlayerFromClanRequestProto
				.newBuilder();
		User user10 = userRetrieveUtil.getUserById(userId1);

		//member trying to boot leader
		bpfcrpb.setSender(createInfoProtoUtils
				.createMinimumUserProtoFromUserAndClan(user10, testClan3));
		bpfcrpb.setPlayerToBootUuid(userId2);
		
		BootPlayerFromClanRequestEvent bpfcre = new BootPlayerFromClanRequestEvent();
		bpfcre.setTag(1);
		bpfcre.setBootPlayerFromClanRequestProto(bpfcrpb.build());
		bootPlayerFromClanController.processRequestEvent(bpfcre, EventsUtil.getToClientEvents());

		User leader = userRetrieveUtil.getUserById(userId2);
		assertTrue(leader.getClanId().equals(clanUuid));
		
		//leader booting a member
		BootPlayerFromClanRequestProto.Builder bpfcrpb2 = BootPlayerFromClanRequestProto
				.newBuilder();
		User user11 = userRetrieveUtil.getUserById(userId2);

		//member trying to boot leader
		bpfcrpb2.setSender(createInfoProtoUtils
				.createMinimumUserProtoFromUserAndClan(user11, testClan3));
		bpfcrpb2.setPlayerToBootUuid(userId1);
		
		BootPlayerFromClanRequestEvent bpfcre2 = new BootPlayerFromClanRequestEvent();
		bpfcre2.setTag(1);
		bpfcre2.setBootPlayerFromClanRequestProto(bpfcrpb2.build());
		bootPlayerFromClanController.processRequestEvent(bpfcre2, EventsUtil.getToClientEvents());
		
		UserClan uc10 = userClanRetrieveUtil.getSpecificUserClan(userId1, clanUuid);
		assertTrue(uc10 == null);		
		
		//LEAVE CLAN
		LeaveClanRequestProto.Builder lcrpb = LeaveClanRequestProto
				.newBuilder();
		User user12 = userRetrieveUtil.getUserById(userId1);

		//member trying to boot leader
		lcrpb.setSender(createInfoProtoUtils
				.createMinimumUserProtoFromUserAndClan(user12, testClan3));
		
		LeaveClanRequestEvent lcre = new LeaveClanRequestEvent();
		lcre.setTag(1);
		lcre.setLeaveClanRequestProto(lcrpb.build());
		bootPlayerFromClanController.processRequestEvent(bpfcre, EventsUtil.getToClientEvents());

		User leader1 = userRetrieveUtil.getUserById(userId2);
		assertTrue(leader1.getClanId().equals(clanUuid));
	}

}
