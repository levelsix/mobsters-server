package com.lvl6.test.ControllerTests;

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

import com.lvl6.events.request.CompleteBattleItemRequestEvent;
import com.lvl6.events.request.CreateBattleItemRequestEvent;
import com.lvl6.events.request.DiscardBattleItemRequestEvent;
import com.lvl6.info.BattleItemForUser;
import com.lvl6.info.BattleItemQueueForUser;
import com.lvl6.info.User;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.properties.DBConstants;
import com.lvl6.proto.BattleItemsProto.BattleItemQueueForUserProto;
import com.lvl6.proto.EventBattleItemProto.CompleteBattleItemRequestProto;
import com.lvl6.proto.EventBattleItemProto.CreateBattleItemRequestProto;
import com.lvl6.proto.EventBattleItemProto.DiscardBattleItemRequestProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.proto.UserProto.MinimumUserProtoWithMaxResources;
import com.lvl6.retrieveutils.BattleItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.BattleItemQueueForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.controller.CompleteBattleItemController;
import com.lvl6.server.controller.CreateBattleItemController;
import com.lvl6.server.controller.DiscardBattleItemController;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.InsertUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-spring-application-context.xml")
public class BattleItemTest {


	private JdbcTemplate jdbcTemplate;

	private User user;
	private MinimumUserProto mup;
	private String userId;


	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	BattleItemQueueForUserRetrieveUtil battleItemQueueForUserRetrieveUtil;
	
	@Autowired
	BattleItemForUserRetrieveUtil battleItemForUserRetrieveUtil;
	
	@Autowired
	InsertUtil insertUtil;

	@Autowired
	CreateBattleItemController createBattleItemController;
	
	@Autowired
	CompleteBattleItemController completeBattleItemController;
	
	@Autowired
	DiscardBattleItemController discardBattleItemController;

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

		userId = insertUtil.insertUser(name, udid, lvl,  playerExp, cash, oil,
				gems, false, deviceToken, createTime, facebookId, avatarMonsterId,
				email, fbData);

		user = userRetrieveUtil.getUserById(userId);

		if (null == user) {
			throw new RuntimeException("no user was created!");
		}

		mup = CreateInfoProtoUtils.createMinimumUserProtoFromUserAndClan(user, null);

	}
	
	@After
	public void tearDown() {
		if (null == user) {
			log.info("no user to delete");
			return;
		}
		
		String query2 = String.format(
				"DELETE FROM %s where %s=?",
				DBConstants.TABLE_USER,
				DBConstants.USER__ID);
		Object[] values2 = new Object[] {
				user.getId()
		};
		int[] types2 = new int[] {
				java.sql.Types.VARCHAR
		};

		int numDeleted = jdbcTemplate.update( query2, values2, types2 );
		if(numDeleted != 1) {
			log.error("did not delete test user when cleaning up");
		}
		
	}
	
	@Test
	public void testBattleItems() {
		User user1 = userRetrieveUtil.getUserById(user.getId());
		int userCash1 = user1.getCash();
		int userOil1 = user1.getOil();
		int userGems1 = user1.getGems();
		CreateBattleItemRequestProto.Builder cbirpb = CreateBattleItemRequestProto.newBuilder();
		MinimumUserProtoWithMaxResources mupwmr = CreateInfoProtoUtils.createMinimumUserProtoWithMaxResources(mup, 1000000, 1000000);
		cbirpb.setSender(mupwmr);	

		//create list of battle items add to queue
		List<BattleItemQueueForUserProto> newList = new ArrayList<BattleItemQueueForUserProto>();
		BattleItemQueueForUser biqfu = new BattleItemQueueForUser();
		biqfu.setBattleItemId(1);
		biqfu.setPriority(1);
		Date now = new Date();
		biqfu.setExpectedStartTime(new Timestamp(now.getTime()));
		biqfu.setUserId(user1.getId());
		BattleItemQueueForUserProto biqfup = CreateInfoProtoUtils.createBattleItemQueueForUserProto(biqfu);
		newList.add(biqfup);

		cbirpb.addAllBiqfuNew(newList);
		cbirpb.setCashChange(50);
		cbirpb.setOilChange(0);
		cbirpb.setGemCostForCreating(0);
		
		CreateBattleItemRequestEvent cbire = new CreateBattleItemRequestEvent();
		cbire.setTag(1);
		cbire.setCreateBattleItemRequestProto(cbirpb.build());
		createBattleItemController.handleEvent(cbire);
		
		User user2 = userRetrieveUtil.getUserById(user.getId());

		List<BattleItemQueueForUser> bifuList = battleItemQueueForUserRetrieveUtil.getUserBattleItemQueuesForUser(user1.getId());
		assertTrue(bifuList.size() == 1);
		assertEquals(user2.getCash() + 50, userCash1);
		
		//////////////////second request event//////////////////////////////////////////////////////
		int userCash2 = user2.getCash();
		int userOil2 = user2.getOil();
		int userGems2 = user2.getGems();
		CreateBattleItemRequestProto.Builder cbirpb2 = CreateBattleItemRequestProto.newBuilder();
		MinimumUserProtoWithMaxResources mupwmr2 = CreateInfoProtoUtils.createMinimumUserProtoWithMaxResources(mup, 1000000, 1000000);
		cbirpb.setSender(mupwmr2);	

		//deleted list
		List<BattleItemQueueForUserProto> deletedList2 = new ArrayList<BattleItemQueueForUserProto>();
		BattleItemQueueForUser biqfu2 = new BattleItemQueueForUser();
		biqfu2.setBattleItemId(1);
		biqfu2.setPriority(1);
		Date now2 = new Date();
		biqfu2.setExpectedStartTime(new Timestamp(now2.getTime()));
		biqfu2.setUserId(user2.getId());
		BattleItemQueueForUserProto biqfup2 = CreateInfoProtoUtils.createBattleItemQueueForUserProto(biqfu2);
		deletedList2.add(biqfup2);
		
		//create list of battle items add to queue
		List<BattleItemQueueForUserProto> newList2 = new ArrayList<BattleItemQueueForUserProto>();
		BattleItemQueueForUser biqfu3 = new BattleItemQueueForUser();
		biqfu3.setBattleItemId(1);
		biqfu3.setPriority(1);
		Date now3 = new Date();
		biqfu3.setExpectedStartTime(new Timestamp(now3.getTime()+1000));
		biqfu3.setUserId(user2.getId());
		BattleItemQueueForUserProto biqfup3 = CreateInfoProtoUtils.createBattleItemQueueForUserProto(biqfu3);

		BattleItemQueueForUser biqfu4 = new BattleItemQueueForUser();
		biqfu4.setBattleItemId(2);
		biqfu4.setPriority(2);
		Date now4 = new Date();
		biqfu4.setExpectedStartTime(new Timestamp(now4.getTime()+1000));
		biqfu4.setUserId(user2.getId());
		BattleItemQueueForUserProto biqfup4 = CreateInfoProtoUtils.createBattleItemQueueForUserProto(biqfu4);
		
		BattleItemQueueForUser biqfu5 = new BattleItemQueueForUser();
		biqfu5.setBattleItemId(3);
		biqfu5.setPriority(3);
		Date now5 = new Date();
		biqfu5.setExpectedStartTime(new Timestamp(now5.getTime()+1000));
		biqfu5.setUserId(user2.getId());
		BattleItemQueueForUserProto biqfup5 = CreateInfoProtoUtils.createBattleItemQueueForUserProto(biqfu5);
		
		newList2.add(biqfup3);
		newList2.add(biqfup4);
		newList2.add(biqfup5);

		cbirpb2.addAllBiqfuNew(newList2);
		cbirpb2.addAllBiqfuDelete(deletedList2);
		cbirpb2.setCashChange(75);
		cbirpb2.setOilChange(100);
		cbirpb2.setGemCostForCreating(100);
		
		CreateBattleItemRequestEvent cbire2 = new CreateBattleItemRequestEvent();
		cbire2.setTag(1);
		cbire2.setCreateBattleItemRequestProto(cbirpb2.build());
		createBattleItemController.handleEvent(cbire2);
		
		User user3 = userRetrieveUtil.getUserById(user.getId());

		List<BattleItemQueueForUser> bifuList2 = battleItemQueueForUserRetrieveUtil.getUserBattleItemQueuesForUser(user1.getId());
		assertTrue(bifuList2.size() == 3);
		assertEquals(user3.getCash() + 75, userCash2);
		assertEquals(user3.getOil() + 100, userOil2);
		assertEquals(user3.getGems() + 100, userGems2);

		
		//////////////////third request event//////////////////////////////////////////////////////
		int userCash3 = user3.getCash();
		int userOil3 = user3.getOil();
		int userGems3 = user3.getGems();
		CreateBattleItemRequestProto.Builder cbirpb3 = CreateBattleItemRequestProto.newBuilder();
		MinimumUserProtoWithMaxResources mupwmr3 = CreateInfoProtoUtils.createMinimumUserProtoWithMaxResources(mup, 1000000, 1000000);
		cbirpb.setSender(mupwmr3);	

		//removed list
		List<BattleItemQueueForUserProto> removedList3 = new ArrayList<BattleItemQueueForUserProto>();
		BattleItemQueueForUser biqfu6 = new BattleItemQueueForUser();
		biqfu6.setBattleItemId(1);
		biqfu6.setPriority(1);
		Date now6 = new Date();
		biqfu6.setExpectedStartTime(new Timestamp(now6.getTime()));
		biqfu6.setUserId(user2.getId());
		BattleItemQueueForUserProto biqfup6 = CreateInfoProtoUtils.createBattleItemQueueForUserProto(biqfu6);
		removedList3.add(biqfup6);
		
		//updated list
		List<BattleItemQueueForUserProto> updatedList3 = new ArrayList<BattleItemQueueForUserProto>();
		BattleItemQueueForUser biqfu7 = new BattleItemQueueForUser();
		biqfu7.setBattleItemId(2);
		biqfu7.setPriority(2);
		Date now7 = new Date();
		biqfu7.setExpectedStartTime(new Timestamp(now7.getTime()+2000));
		biqfu7.setUserId(user2.getId());
		BattleItemQueueForUserProto biqfup7 = CreateInfoProtoUtils.createBattleItemQueueForUserProto(biqfu7);

		BattleItemQueueForUser biqfu8 = new BattleItemQueueForUser();
		biqfu8.setBattleItemId(3);
		biqfu8.setPriority(3);
		Date now8 = new Date();
		biqfu8.setExpectedStartTime(new Timestamp(now8.getTime()+2000));
		biqfu8.setUserId(user2.getId());
		BattleItemQueueForUserProto biqfup8 = CreateInfoProtoUtils.createBattleItemQueueForUserProto(biqfu8);
		updatedList3.add(biqfup7);
		updatedList3.add(biqfup8);

		cbirpb3.addAllBiqfuDelete(removedList3);
		cbirpb3.addAllBiqfuUpdate(updatedList3);
		cbirpb3.setCashChange(-50);
		cbirpb3.setOilChange(0);
		cbirpb3.setGemCostForCreating(0);
		
		CreateBattleItemRequestEvent cbire3 = new CreateBattleItemRequestEvent();
		cbire3.setTag(1);
		cbire3.setCreateBattleItemRequestProto(cbirpb3.build());
		createBattleItemController.handleEvent(cbire3);
		
		User user4 = userRetrieveUtil.getUserById(user.getId());

		List<BattleItemQueueForUser> bifuList3 = battleItemQueueForUserRetrieveUtil.getUserBattleItemQueuesForUser(user4.getId());
		assertTrue(bifuList3.size() == 2);
		assertEquals(user4.getCash() - 50, userCash3);
		assertEquals(user4.getOil(), userOil3);
		assertEquals(user4.getGems(), userGems3);

		/////////////////////////COMPLETE BATTLE ITEM/////////////////////////////////////////////
		
		CompleteBattleItemRequestProto.Builder cobirpb = CompleteBattleItemRequestProto.newBuilder();
		cobirpb.setSender(CreateInfoProtoUtils.createMinimumUserProtoFromUserAndClan(user4, null));
		cobirpb.setGemsForSpeedup(100);
		cobirpb.setIsSpeedup(true);
		cobirpb.addAllBiqfuCompleted(updatedList3);
		
		CompleteBattleItemRequestEvent cobire = new CompleteBattleItemRequestEvent();
		cobire.setTag(1);
		cobire.setCompleteBattleItemRequestProto(cobirpb.build());
		completeBattleItemController.handleEvent(cobire);
		
		User user5 = userRetrieveUtil.getUserById(user.getId());

		List<BattleItemQueueForUser> biqfuList = battleItemQueueForUserRetrieveUtil.getUserBattleItemQueuesForUser(user5.getId());
		assertTrue(biqfuList.size() == 0);
		Map<Integer, BattleItemForUser> bifuMap = battleItemForUserRetrieveUtil.getBattleItemIdsToUserBattleItemForUser(user.getId());
		assertTrue(bifuMap.size() == 2);
		assertEquals(user4.getGems() - 100, user5.getGems());
		
		/////////////////////////DISCARD BATTLE ITEM/////////////////////////////////////////////
		DiscardBattleItemRequestProto.Builder dbirpb = DiscardBattleItemRequestProto.newBuilder();
		dbirpb.setSender(CreateInfoProtoUtils.createMinimumUserProtoFromUserAndClan(user5, null));
		
		List<BattleItemForUser> listOfBattleItems = battleItemForUserRetrieveUtil.getUserBattleItemsForUser(user.getId());
		dbirpb.addAllDiscardedBattleItems(CreateInfoProtoUtils.convertBattleItemForUserListToBattleItemForUserProtoList(listOfBattleItems));
		
		DiscardBattleItemRequestEvent dbire = new DiscardBattleItemRequestEvent();
		dbire.setTag(1);
		dbire.setDiscardBattleItemRequestProto(dbirpb.build());
		discardBattleItemController.handleEvent(dbire);
		
		List<BattleItemForUser> listOfBattleItems2 = battleItemForUserRetrieveUtil.getUserBattleItemsForUser(user.getId());

		assertTrue(listOfBattleItems2.isEmpty());
	}
	
}
