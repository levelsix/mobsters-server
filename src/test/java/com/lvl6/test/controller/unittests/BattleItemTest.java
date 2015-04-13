package com.lvl6.test.controller.unittests;
//package com.lvl6.test.mocktests;
//
//import static org.mockito.Matchers.any;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//import java.sql.Timestamp;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Matchers;
//import org.powermock.api.mockito.PowerMockito;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.TestingAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import com.lvl6.events.request.CompleteBattleItemRequestEvent;
//import com.lvl6.events.request.CreateBattleItemRequestEvent;
//import com.lvl6.events.request.DiscardBattleItemRequestEvent;
//import com.lvl6.info.BattleItemQueueForUser;
//import com.lvl6.info.SalesItem;
//import com.lvl6.info.SalesPackage;
//import com.lvl6.info.User;
//import com.lvl6.misc.MiscMethods;
//import com.lvl6.proto.BattleItemsProto.BattleItemQueueForUserProto;
//import com.lvl6.proto.EventBattleItemProto.CompleteBattleItemRequestProto;
//import com.lvl6.proto.EventBattleItemProto.CreateBattleItemRequestProto;
//import com.lvl6.proto.EventBattleItemProto.DiscardBattleItemRequestProto;
//import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
//import com.lvl6.proto.UserProto.MinimumUserProto;
//import com.lvl6.proto.UserProto.MinimumUserProtoWithMaxResources;
//import com.lvl6.retrieveutils.BattleItemForUserRetrieveUtil;
//import com.lvl6.retrieveutils.IAPHistoryRetrieveUtils;
//import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
//import com.lvl6.retrieveutils.UserRetrieveUtils2;
//import com.lvl6.server.controller.CompleteBattleItemController;
//import com.lvl6.server.controller.CreateBattleItemController;
//import com.lvl6.server.controller.DiscardBattleItemController;
//import com.lvl6.server.controller.InAppPurchaseController;
//import com.lvl6.server.controller.utils.MonsterStuffUtils;
//import com.lvl6.utils.CreateInfoProtoUtils;
//import com.lvl6.utils.utilmethods.DeleteUtil;
//import com.lvl6.utils.utilmethods.InsertUtil;
//import com.lvl6.utils.utilmethods.UpdateUtil;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration("/test-spring-application-context.xml")
//public class BattleItemTest {
//
//	private static User mockedUser;
//	private static InsertUtil mockedInsertUtil;
//	private static UpdateUtil mockedUpdateUtil;
//	private static DeleteUtil mockedDeleteUtil;
//	private static ItemForUserRetrieveUtil mockedItemForUserRetrieveUtil;
//	private static UserRetrieveUtils2 mockedUserRetrieveUtil;
//	private static IAPHistoryRetrieveUtils mockedIAPHistoryRetrieveUtil;
//	private static MiscMethods mockedMiscMethods;
//	private static BattleItemForUserRetrieveUtil mockedBattleItemForUserRetrieveUtil;
//
//
//	@Autowired
//	protected InAppPurchaseController inAppPurchaseController;
//	
//	@Autowired
//	protected CreateInfoProtoUtils createInfoProtoUtils;
//	
//	@Autowired
//	protected CreateBattleItemController createBattleItemController;
//	
//	@Autowired
//	protected CompleteBattleItemController completeBattleItemController;
//	
//	@Autowired
//	protected DiscardBattleItemController discardBattleItemController;
//	
//	@Autowired
//	protected MiscMethods miscMethods;
//	
//	private static Logger log = LoggerFactory.getLogger(new Object() {
//	}.getClass().getEnclosingClass());
//
//	@Before
//	public void setUp() {
//		log.info("/////////////////////////SALES TEST////////////////////////////////////");
//		
//		TestingAuthenticationToken testToken = new TestingAuthenticationToken("testUser", "");
//	    SecurityContextHolder.getContext().setAuthentication(testToken);
//		
//		mockedUser = mock(User.class);
//		when(mockedUser.getId()).thenReturn("user id");
//		when(mockedUser.getCash()).thenReturn(10000);
//		when(mockedUser.getOil()).thenReturn(10000);
//		when(mockedUser.getGems()).thenReturn(10000);
//		when(mockedUser.getName()).thenReturn("name");
//		Date now = new Date();
//
//		
//		
//		
//		
////		List<String> returnList = new ArrayList<String>();
////		returnList.add("list");
////		when(mockedInsertUtil.insertIntoMonsterForUserReturnIds(any(String.class), any(List.class), 
////				any(String.class), any(Date.class))).thenReturn(returnList);
////		
////		mockedItemForUserRetrieveUtil = PowerMockito.mock(ItemForUserRetrieveUtil.class);
////		Map<Integer, Integer> itemMap = new HashMap<Integer, Integer>();
////		itemMap.put(1, 5);
////		ItemForUser ifu = new ItemForUser();
////		ifu.setItemId(1);
////		ifu.setQuantity(2);
////		ifu.setUserId("user id");
////		Map<Integer, ItemForUser> ifuMap = new HashMap<Integer, ItemForUser>();
////		ifuMap.put(1, ifu);
////		when(mockedItemForUserRetrieveUtil.getSpecificOrAllItemForUserMap(any(String.class), Matchers.eq(itemMap.keySet()))).thenReturn(ifuMap);
//		
//		ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
//
//		mockedInsertUtil = mock(InsertUtil.class);
//		when(mockedInsertUtil.insertIntoBattleItemQueueForUser(captor.capture())).thenReturn(captor.getValue().size()); 
//		
//		mockedUpdateUtil = mock(UpdateUtil.class);
//		when(mockedUpdateUtil.updateItemForUser(any(List.class))).thenReturn(1);
//		
//		mockedDeleteUtil = mock(DeleteUtil.class);
//		when(mockedDeleteUtil.deleteFromBattleItemQueueForUser(any(String.class), captor.capture())).thenReturn(captor.getValue().size());
//		
//		
//		mockedUserRetrieveUtil = mock(UserRetrieveUtils2.class);
//		when(mockedUserRetrieveUtil.getUserById(any(String.class))).thenReturn(mockedUser);
//		
//		mockedIAPHistoryRetrieveUtil = PowerMockito.mock(IAPHistoryRetrieveUtils.class);
//		when(mockedIAPHistoryRetrieveUtil.checkIfDuplicateTransaction(any(Long.class))).thenReturn(false);
//		
//		mockedMiscMethods = mock(MiscMethods.class);
//
//		mockedBattleItemForUserRetrieveUtil = mock(BattleItemForUserRetrieveUtil.class);
//		when(mockedBattleItemForUserRetrieveUtil.getBattleItemIdsToUserBattleItemForUser(mockedUser.getId())).thenReturn()
//		
//		
//		
//		////////////////////////////////mock retrieveutils with static methods/////////////////////
//        PowerMockito.mockStatic(SalesPackageRetrieveUtils.class);
//        
//        SalesPackage sp = new SalesPackage();
//        sp.setId(1);
//        sp.setName("name");
//        sp.setPrice(4.99);
//        sp.setUuid("uuid");
//        Map<Integer, SalesPackage> salesPackageMap = new HashMap<Integer, SalesPackage>();
//        salesPackageMap.put(1, sp);
//        
//        when(SalesPackageRetrieveUtils.getSalesPackageIdsToSalesPackages()).thenReturn(salesPackageMap);
//		
//        PowerMockito.mockStatic(SalesItemRetrieveUtils.class);
//        Map<Integer, SalesItem> salesItemMap = new HashMap<Integer, SalesItem>();
//        
//        SalesItem si1 = new SalesItem();
//        si1.setId(1);
//        si1.setSalesPackageId(1);
//        si1.setMonsterId(1);
//        si1.setMonsterLevel(5);
//        si1.setMonsterQuantity(1);
//        si1.setGemReward(0);
//        salesItemMap.put(1, si1);
//        
//        SalesItem si2 = new SalesItem();
//        si2.setId(2);
//        si2.setSalesPackageId(1);
//        si2.setItemId(1);
//        si2.setItemQuantity(5);
//        si2.setGemReward(0);
//        salesItemMap.put(2, si2);
//        
//        SalesItem si3 = new SalesItem();
//        si3.setId(3);
//        si3.setSalesPackageId(1);
//        si3.setGemReward(100);
//        salesItemMap.put(3, si3);
//        
//        when(SalesItemRetrieveUtils.getSalesItemIdsToSalesItemsForSalesPackageId(any(Integer.class)))
//        	.thenReturn(salesItemMap);
//        
//        PowerMockito.mockStatic(MonsterStuffUtils.class);
//        List<FullUserMonsterProto> fumpList = new ArrayList<FullUserMonsterProto>();
//        FullUserMonsterProto.Builder fumpb = FullUserMonsterProto.newBuilder();
//        fumpList.add(fumpb.build());
//
//        when(MonsterStuffUtils.updateUserMonsters(any(String.class), any(Map.class), any(Map.class), any(String.class), any(Date.class))).thenReturn(fumpList);
//               
//		////////////////////////////////miscmethods////////////////////////////////
//        PowerMockito.mockStatic(MiscMethods.class);
//        
//        when(MiscMethods.checkIfMonstersExistInSalesItem(any(List.class))).thenReturn(true);
//        when(MiscMethods.determineGemRewardForSale(any(List.class))).thenReturn(100);
//        when(MiscMethods.createUpdateUserMonsterArgumentsForSales(any(String.class), Matchers.eq(1), any(List.class),
//        		any(Map.class), any(List.class), any(Date.class))).thenReturn("string");
//        when(MiscMethods.createFullUserMonsterProtos(any(List.class), any(List.class))).thenReturn(fumpList);
//	}
//	
//	@Test
//	public void testSales() {
//		int userCash = mockedUser.getCash();
//		int userOil = mockedUser.getOil();
//		int userGems = mockedUser.getGems();
//		MinimumUserProto mup = createInfoProtoUtils.createMinimumUserProtoFromUserAndClan(mockedUser,
//				null);
//		CreateBattleItemRequestProto.Builder cbirpb = CreateBattleItemRequestProto
//				.newBuilder();
//		MinimumUserProtoWithMaxResources mupwmr = createInfoProtoUtils
//				.createMinimumUserProtoWithMaxResources(mup, 1000000, 1000000);
//		cbirpb.setSender(mupwmr);
//
//		//create list of battle items add to queue
//		List<BattleItemQueueForUserProto> newList = new ArrayList<BattleItemQueueForUserProto>();
//		BattleItemQueueForUser biqfu = new BattleItemQueueForUser();
//		biqfu.setBattleItemId(1);
//		biqfu.setPriority(1);
//		Date now = new Date();
//		biqfu.setExpectedStartTime(new Timestamp(now.getTime()));
//		biqfu.setUserId(mockedUser.getId());
//		BattleItemQueueForUserProto biqfup = createInfoProtoUtils
//				.createBattleItemQueueForUserProto(biqfu);
//		newList.add(biqfup);
//
//		cbirpb.addAllBiqfuNew(newList);
//		cbirpb.setCashChange(-50);
//		cbirpb.setOilChange(0);
//		cbirpb.setGemCostForCreating(0);
//
//		CreateBattleItemRequestEvent cbire = new CreateBattleItemRequestEvent();
//		cbire.setTag(1);
//		cbire.setCreateBattleItemRequestProto(cbirpb.build());
//		createBattleItemController.handleEvent(cbire);
//
//		
//		//////////////////second request event//////////////////////////////////////////////////////
//		CreateBattleItemRequestProto.Builder cbirpb2 = CreateBattleItemRequestProto
//				.newBuilder();
//		MinimumUserProtoWithMaxResources mupwmr2 = createInfoProtoUtils
//				.createMinimumUserProtoWithMaxResources(mup, 1000000, 1000000);
//		cbirpb2.setSender(mupwmr2);
//
//		//deleted list
//		List<BattleItemQueueForUserProto> deletedList2 = new ArrayList<BattleItemQueueForUserProto>();
//		BattleItemQueueForUser biqfu2 = new BattleItemQueueForUser();
//		biqfu2.setBattleItemId(1);
//		biqfu2.setPriority(1);
//		Date now2 = new Date();
//		biqfu2.setExpectedStartTime(new Timestamp(now2.getTime()));
//		biqfu2.setUserId(mockedUser.getId());
//		BattleItemQueueForUserProto biqfup2 = createInfoProtoUtils
//				.createBattleItemQueueForUserProto(biqfu2);
//		deletedList2.add(biqfup2);
//
//		//create list of battle items add to queue
//		List<BattleItemQueueForUserProto> newList2 = new ArrayList<BattleItemQueueForUserProto>();
//		BattleItemQueueForUser biqfu3 = new BattleItemQueueForUser();
//		biqfu3.setBattleItemId(1);
//		biqfu3.setPriority(1);
//		Date now3 = new Date();
//		biqfu3.setExpectedStartTime(new Timestamp(now3.getTime() + 1000));
//		biqfu3.setUserId(mockedUser.getId());
//		BattleItemQueueForUserProto biqfup3 = createInfoProtoUtils
//				.createBattleItemQueueForUserProto(biqfu3);
//
//		BattleItemQueueForUser biqfu4 = new BattleItemQueueForUser();
//		biqfu4.setBattleItemId(2);
//		biqfu4.setPriority(2);
//		Date now4 = new Date();
//		biqfu4.setExpectedStartTime(new Timestamp(now4.getTime() + 1000));
//		biqfu4.setUserId(mockedUser.getId());
//		BattleItemQueueForUserProto biqfup4 = createInfoProtoUtils
//				.createBattleItemQueueForUserProto(biqfu4);
//
//		BattleItemQueueForUser biqfu5 = new BattleItemQueueForUser();
//		biqfu5.setBattleItemId(3);
//		biqfu5.setPriority(3);
//		Date now5 = new Date();
//		biqfu5.setExpectedStartTime(new Timestamp(now5.getTime() + 1000));
//		biqfu5.setUserId(mockedUser.getId());
//		BattleItemQueueForUserProto biqfup5 = createInfoProtoUtils
//				.createBattleItemQueueForUserProto(biqfu5);
//
//		newList2.add(biqfup3);
//		newList2.add(biqfup4);
//		newList2.add(biqfup5);
//
//		cbirpb2.addAllBiqfuNew(newList2);
//		cbirpb2.addAllBiqfuDelete(deletedList2);
//		cbirpb2.setCashChange(-75);
//		cbirpb2.setOilChange(-100);
//		cbirpb2.setGemCostForCreating(100);
//
//		CreateBattleItemRequestEvent cbire2 = new CreateBattleItemRequestEvent();
//		cbire2.setTag(1);
//		cbire2.setCreateBattleItemRequestProto(cbirpb2.build());
//		createBattleItemController.handleEvent(cbire2);
//
//
//		//////////////////third request event//////////////////////////////////////////////////////
//		CreateBattleItemRequestProto.Builder cbirpb3 = CreateBattleItemRequestProto
//				.newBuilder();
//		MinimumUserProtoWithMaxResources mupwmr3 = createInfoProtoUtils
//				.createMinimumUserProtoWithMaxResources(mup, 1000000, 1000000);
//		cbirpb3.setSender(mupwmr3);
//
//		//removed list
//		List<BattleItemQueueForUserProto> removedList3 = new ArrayList<BattleItemQueueForUserProto>();
//		BattleItemQueueForUser biqfu6 = new BattleItemQueueForUser();
//		biqfu6.setBattleItemId(1);
//		biqfu6.setPriority(1);
//		Date now6 = new Date();
//		biqfu6.setExpectedStartTime(new Timestamp(now6.getTime()));
//		biqfu6.setUserId(mockedUser.getId());
//		BattleItemQueueForUserProto biqfup6 = createInfoProtoUtils
//				.createBattleItemQueueForUserProto(biqfu6);
//		removedList3.add(biqfup6);
//
//		//updated list
//		List<BattleItemQueueForUserProto> updatedList3 = new ArrayList<BattleItemQueueForUserProto>();
//		BattleItemQueueForUser biqfu7 = new BattleItemQueueForUser();
//		biqfu7.setBattleItemId(2);
//		biqfu7.setPriority(2);
//		Date now7 = new Date();
//		biqfu7.setExpectedStartTime(new Timestamp(now7.getTime() + 2000));
//		biqfu7.setUserId(mockedUser.getId());
//		BattleItemQueueForUserProto biqfup7 = createInfoProtoUtils
//				.createBattleItemQueueForUserProto(biqfu7);
//
//		BattleItemQueueForUser biqfu8 = new BattleItemQueueForUser();
//		biqfu8.setBattleItemId(3);
//		biqfu8.setPriority(3);
//		Date now8 = new Date();
//		biqfu8.setExpectedStartTime(new Timestamp(now8.getTime() + 2000));
//		biqfu8.setUserId(mockedUser.getId());
//		BattleItemQueueForUserProto biqfup8 = createInfoProtoUtils
//				.createBattleItemQueueForUserProto(biqfu8);
//		updatedList3.add(biqfup7);
//		updatedList3.add(biqfup8);
//
//		cbirpb3.addAllBiqfuDelete(removedList3);
//		cbirpb3.addAllBiqfuUpdate(updatedList3);
//		cbirpb3.setCashChange(-50);
//		cbirpb3.setOilChange(0);
//		cbirpb3.setGemCostForCreating(0);
//
//		CreateBattleItemRequestEvent cbire3 = new CreateBattleItemRequestEvent();
//		cbire3.setTag(1);
//		cbire3.setCreateBattleItemRequestProto(cbirpb3.build());
//		createBattleItemController.handleEvent(cbire3);
//
//
//		/////////////////////////COMPLETE BATTLE ITEM/////////////////////////////////////////////
//
//		CompleteBattleItemRequestProto.Builder cobirpb = CompleteBattleItemRequestProto
//				.newBuilder();
//		cobirpb.setSender(createInfoProtoUtils
//				.createMinimumUserProtoFromUserAndClan(mockedUser, null));
//		cobirpb.setGemsForSpeedup(100);
//		cobirpb.setIsSpeedup(true);
//		cobirpb.addAllBiqfuCompleted(updatedList3);
//
//		CompleteBattleItemRequestEvent cobire = new CompleteBattleItemRequestEvent();
//		cobire.setTag(1);
//		cobire.setCompleteBattleItemRequestProto(cobirpb.build());
//		completeBattleItemController.handleEvent(cobire);
//
//
//		/////////////////////////DISCARD BATTLE ITEM/////////////////////////////////////////////
//		DiscardBattleItemRequestProto.Builder dbirpb = DiscardBattleItemRequestProto
//				.newBuilder();
//		dbirpb.setSender(createInfoProtoUtils
//				.createMinimumUserProtoFromUserAndClan(mockedUser, null));
//
//		List<Integer> listOfBattleItems = new ArrayList<Integer>();
//		listOfBattleItems.add(2);
//		listOfBattleItems.add(3);
//		
//		dbirpb.addAllDiscardedBattleItemIds(listOfBattleItems);
//		
//		DiscardBattleItemRequestEvent dbire = new DiscardBattleItemRequestEvent();
//		dbire.setTag(1);
//		dbire.setDiscardBattleItemRequestProto(dbirpb.build());
//		discardBattleItemController.handleEvent(dbire);
//
//
//	}
//	
//	
//}
