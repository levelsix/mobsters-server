package com.lvl6.test.controller.integrationtests;

<<<<<<< HEAD
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
=======
import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
>>>>>>> added tests for purchasing booster packs, not done yet

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

import com.lvl6.events.request.PurchaseBoosterPackRequestEvent;
<<<<<<< HEAD
import com.lvl6.info.ItemForUser;
=======
>>>>>>> added tests for purchasing booster packs, not done yet
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.User;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.properties.DBConstants;
import com.lvl6.proto.EventBoosterPackProto.PurchaseBoosterPackRequestProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
<<<<<<< HEAD
import com.lvl6.retrieveutils.BattleItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.BattleItemQueueForUserRetrieveUtil;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
=======
>>>>>>> added tests for purchasing booster packs, not done yet
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.controller.PurchaseBoosterPackController;
import com.lvl6.utils.CreateInfoProtoUtils;
<<<<<<< HEAD
import com.lvl6.utils.DBConnection;
import com.lvl6.utils.utilmethods.InsertUtil;
=======
import com.lvl6.utils.utilmethods.InsertUtils;
>>>>>>> added tests for purchasing booster packs, not done yet

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-spring-application-context.xml")
public class PurchaseBoosterPackTest {

	private JdbcTemplate jdbcTemplate;

	private User user;
	private MinimumUserProto mup;
	private String userId;

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
<<<<<<< HEAD
	BattleItemQueueForUserRetrieveUtil battleItemQueueForUserRetrieveUtil;

	@Autowired
	BattleItemForUserRetrieveUtil battleItemForUserRetrieveUtil;

	@Autowired
	InsertUtil insertUtil;

	@Autowired
	PurchaseBoosterPackController purchaseBoosterPackController;

	@Autowired
	MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils;

	@Autowired
	ItemForUserRetrieveUtil itemForUserRetrieveUtil;

	@Autowired
	CreateInfoProtoUtils createInfoProtoUtils;
=======
	CreateInfoProtoUtils createInfoProtoUtils;
	
	@Autowired
	MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils;
	
	@Autowired
	PurchaseBoosterPackController purchaseBoosterPackController;
	
	@Autowired
	InsertUtils insertUtils;
>>>>>>> added tests for purchasing booster packs, not done yet

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Before
	public void setUp() {
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

<<<<<<< HEAD
		userId = insertUtil.insertUser(name, udid, lvl, playerExp, cash, oil,
=======
		userId = insertUtils.insertUser(name, udid, lvl, playerExp, cash, oil,
>>>>>>> added tests for purchasing booster packs, not done yet
				gems, false, deviceToken, createTime, facebookId,
				avatarMonsterId, email, fbData);

		user = userRetrieveUtil.getUserById(userId);

		if (null == user) {
			throw new RuntimeException("no user was created!");
		}

		mup = createInfoProtoUtils.createMinimumUserProtoFromUserAndClan(user,
				null);
<<<<<<< HEAD
=======

>>>>>>> added tests for purchasing booster packs, not done yet
	}

	@After
	public void tearDown() {
		if (null == user) {
			log.info("no user to delete");
			return;
		}

<<<<<<< HEAD
		//deleteSalesPurchase(userId);

=======
>>>>>>> added tests for purchasing booster packs, not done yet
		String query2 = String.format("DELETE FROM %s where %s=?",
				DBConstants.TABLE_USER, DBConstants.USER__ID);
		Object[] values2 = new Object[] { user.getId() };
		int[] types2 = new int[] { java.sql.Types.VARCHAR };

		int numDeleted = jdbcTemplate.update(query2, values2, types2);
		if (numDeleted != 1) {
			log.error("did not delete test user when cleaning up");
		}

	}

<<<<<<< HEAD
	private int deleteSalesPurchase(String userId) {
		String tableName = DBConstants.TABLE_IAP_HISTORY;
		String condDelim = "and";
		Map<String, Object> conditionParams = new HashMap<String, Object>();
		int totalDeleted = 0;

		conditionParams.put(DBConstants.IAP_HISTORY__USER_ID,
				userId);
		int numDeleted = DBConnection.get().deleteRows(tableName,
				conditionParams, condDelim);
		totalDeleted += numDeleted;

		return totalDeleted;
	}

	@Test
	public void testBattleItems() {
		User user1 = userRetrieveUtil.getUserById(userId);
		int userGems1 = user1.getGems();
		PurchaseBoosterPackRequestProto.Builder pbprpb = PurchaseBoosterPackRequestProto
				.newBuilder();

		pbprpb.setSender(mup);
		pbprpb.setBoosterPackId(1000);
		pbprpb.setClientTime(new Date().getTime());
		pbprpb.setDailyFreeBoosterPack(false);

		PurchaseBoosterPackRequestEvent pbpre = new PurchaseBoosterPackRequestEvent();
		pbpre.setTag(1);
		pbpre.setPurchaseBoosterPackRequestProto(pbprpb.build());
		purchaseBoosterPackController.handleEvent(pbpre);

		User user2 = userRetrieveUtil.getUserById(user.getId());

		int userGems2 = user2.getGems();

		List<MonsterForUser> mfuList = monsterForUserRetrieveUtils.getMonstersForUser(user.getId());
		List<Integer> itemIds = new ArrayList<Integer>();
		itemIds.add(53);
		itemIds.add(33);
		itemIds.add(3);
		itemIds.add(4);
		itemIds.add(51);
		itemIds.add(31);
		itemIds.add(22);
		itemIds.add(10000);
		itemIds.add(5);
		itemIds.add(55);
		itemIds.add(35);
		itemIds.add(2);
		itemIds.add(34);
		itemIds.add(54);

		List<ItemForUser> ifuList = itemForUserRetrieveUtil.getSpecificOrAllItemForUser(user.getId(), itemIds);

		assertTrue(mfuList.size() == 0);
		assertEquals(userGems1 + 100, userGems2);
		assertTrue(ifuList.size() == 14);
		assertTrue(user2.getNumBeginnerSalesPurchased() == 1);
	}
=======
	@Test
	public void testPurchaseBoosterPacks() {
		User user1 = userRetrieveUtil.getUserById(userId);
		int userCash1 = user1.getCash();
		int userOil1 = user1.getOil();
		int userGems1 = user1.getGems();
		List<MonsterForUser> mfuList1 = monsterForUserRetrieveUtils.getMonstersForUser(userId);
		
		PurchaseBoosterPackRequestProto.Builder pbprp = PurchaseBoosterPackRequestProto
				.newBuilder();
		pbprp.setSender(mup);
		pbprp.setBoosterPackId(1);
		pbprp.setClientTime(new Date().getTime());
		pbprp.setDailyFreeBoosterPack(false);
		pbprp.setBuyingInBulk(false);
		
		PurchaseBoosterPackRequestEvent pbpre = new PurchaseBoosterPackRequestEvent();
		pbpre.setTag(1);
		pbpre.setPurchaseBoosterPackRequestProto(pbprp.build());
		purchaseBoosterPackController.handleEvent(pbpre);

		User user2 = userRetrieveUtil.getUserById(user.getId());
		
		List<MonsterForUser> mfuList2 = monsterForUserRetrieveUtils.getMonstersForUser(userId);
		
		assertTrue(mfuList1.size() + 1 == mfuList2.size());
		assertTrue(userGems1 - 20 == user2.getGems());
		
		//test buying in bulk
		PurchaseBoosterPackRequestProto.Builder pbprp2 = PurchaseBoosterPackRequestProto
				.newBuilder();
		pbprp2.setSender(mup);
		pbprp2.setBoosterPackId(2);
		pbprp2.setClientTime(new Date().getTime());
		pbprp2.setDailyFreeBoosterPack(false);
		pbprp2.setBuyingInBulk(true);

		PurchaseBoosterPackRequestEvent pbpre2 = new PurchaseBoosterPackRequestEvent();
		pbpre2.setTag(1);
		pbpre2.setPurchaseBoosterPackRequestProto(pbprp2.build());
		purchaseBoosterPackController.handleEvent(pbpre2);

		User user3 = userRetrieveUtil.getUserById(user.getId());

		List<MonsterForUser> mfuList3 = monsterForUserRetrieveUtils.getMonstersForUser(userId);

		assertTrue(mfuList3.size() == mfuList2.size() + 11);
		assertTrue(userGems1 - 400 == user2.getGems());

		
		
	}

>>>>>>> added tests for purchasing booster packs, not done yet
}
