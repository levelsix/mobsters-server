package com.lvl6.test.mocktests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import com.lvl6.info.ItemForUser;
import com.lvl6.info.Monster;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.MonsterLevelInfo;
import com.lvl6.info.SalesItem;
import com.lvl6.info.SalesPackage;
import com.lvl6.info.User;
import com.lvl6.retrieveutils.IAPHistoryRetrieveUtils;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.rarechange.MonsterLevelInfoRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterRetrieveUtils;
import com.lvl6.server.controller.actionobjects.InAppPurchaseSalesAction;
import com.lvl6.server.controller.utils.InAppPurchaseUtils;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.utilmethods.UpdateUtil;

public class InAppPurchaseSalesUnitTests {

	private static User mockedUser;
	private static UpdateUtil mockedUpdateUtil;
	private static JSONObject mockedReceiptFromApple1;
	private static JSONObject mockedReceiptFromApple2;
	private static IAPHistoryRetrieveUtils mockedIAPHistoryRetrieveUtil;
	private static MonsterRetrieveUtils mockedMonsterRetrieveUtils;
	private static MonsterStuffUtils mockedMonsterStuffUtils;
	private static InAppPurchaseSalesAction iapa;

	@BeforeClass
	public static void setUp() {
		mockedUser = mock(User.class);
		when(mockedUser.getId()).thenReturn("userId");

		mockedReceiptFromApple1 = mock(JSONObject.class);
		mockedReceiptFromApple2 = mock(JSONObject.class);
		try {
			when(mockedReceiptFromApple1.getString(any(String.class))).thenReturn("123");
			when(mockedReceiptFromApple2.getString(any(String.class))).thenReturn("456");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mockedIAPHistoryRetrieveUtil = mock(IAPHistoryRetrieveUtils.class);
		long uniqueLong = 123;
		long duplicateLong = 456;
		when(mockedIAPHistoryRetrieveUtil.checkIfDuplicateTransaction(uniqueLong)).thenReturn(false);
		when(mockedIAPHistoryRetrieveUtil.checkIfDuplicateTransaction(duplicateLong)).thenReturn(true);

		mockedMonsterRetrieveUtils = mock(MonsterRetrieveUtils.class);

		mockedMonsterStuffUtils = mock(MonsterStuffUtils.class);

		mockedUpdateUtil = mock(UpdateUtil.class);
		when(mockedUpdateUtil.updateUserSalesValue(any(String.class), any(Integer.class),
				any(Date.class))).thenReturn(true);

		iapa = new InAppPurchaseSalesAction();
		iapa.setUser(mockedUser);
		iapa.setUpdateUtil(mockedUpdateUtil);
	}

	@Test
	public void testCheckIfDuplicateReceipt() {
		assertFalse(InAppPurchaseUtils.checkIfDuplicateReceipt(mockedReceiptFromApple1,
				mockedIAPHistoryRetrieveUtil));

		assertTrue(InAppPurchaseUtils.checkIfDuplicateReceipt(mockedReceiptFromApple2,
				mockedIAPHistoryRetrieveUtil));
	}

//	@Test
//	public void testPackageIsSalesPackage() {
//		Map<String, SalesPackage> salesPackageNamesToSalesPackages =
//				new HashMap<String, SalesPackage>();
//		salesPackageNamesToSalesPackages.put("name1", new SalesPackage());
//		salesPackageNamesToSalesPackages.put("name2", new SalesPackage());
//		salesPackageNamesToSalesPackages.put("name3", new SalesPackage());
//
//		assertTrue(iapa.packageIsSalesPackage("name1", salesPackageNamesToSalesPackages));
//		assertTrue(iapa.packageIsSalesPackage("name2", salesPackageNamesToSalesPackages));
//		assertTrue(iapa.packageIsSalesPackage("name3", salesPackageNamesToSalesPackages));
//		assertFalse(iapa.packageIsSalesPackage("name0", salesPackageNamesToSalesPackages));
//	}

	@Test
	public void testUserSalesValueMatchesSalesPackage() {
		when(mockedUser.isSalesJumpTwoTiers()).thenReturn(true);
		SalesPackage sp = new SalesPackage();
		iapa.setUser(mockedUser);

		when(mockedUser.getSalesValue()).thenReturn(0);
		sp.setPrice(4.99);
		iapa.setSalesPackage(sp);
		assertTrue(iapa.userSalesValueMatchesSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(1);
		sp.setPrice(19.99);
		iapa.setSalesPackage(sp);
		assertTrue(iapa.userSalesValueMatchesSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(2);
		sp.setPrice(49.99);
		iapa.setSalesPackage(sp);
		assertTrue(iapa.userSalesValueMatchesSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(3);
		sp.setPrice(99.99);
		iapa.setSalesPackage(sp);
		assertTrue(iapa.userSalesValueMatchesSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(4);
		sp.setPrice(99.99);
		iapa.setSalesPackage(sp);
		assertTrue(iapa.userSalesValueMatchesSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(5);
		sp.setPrice(99.99);
		iapa.setSalesPackage(sp);
		assertTrue(iapa.userSalesValueMatchesSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(6);
		sp.setPrice(99.99);
		iapa.setSalesPackage(sp);
		assertTrue(iapa.userSalesValueMatchesSalesPackage());

		//set salesjumptwotiers to false, this is when user hasn't
		//bought in a while
		when(mockedUser.isSalesJumpTwoTiers()).thenReturn(false);

		when(mockedUser.getSalesValue()).thenReturn(0);
		sp.setPrice(4.99);
		iapa.setSalesPackage(sp);
		assertTrue(iapa.userSalesValueMatchesSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(1);
		sp.setPrice(9.99);
		iapa.setSalesPackage(sp);
		assertTrue(iapa.userSalesValueMatchesSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(2);
		sp.setPrice(19.99);
		iapa.setSalesPackage(sp);
		assertTrue(iapa.userSalesValueMatchesSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(3);
		sp.setPrice(49.99);
		iapa.setSalesPackage(sp);
		assertTrue(iapa.userSalesValueMatchesSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(4);
		sp.setPrice(99.99);
		iapa.setSalesPackage(sp);
		assertTrue(iapa.userSalesValueMatchesSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(5);
		sp.setPrice(99.99);
		iapa.setSalesPackage(sp);
		assertTrue(iapa.userSalesValueMatchesSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(6);
		sp.setPrice(99.99);
		iapa.setSalesPackage(sp);
		assertTrue(iapa.userSalesValueMatchesSalesPackage());
	}

	@Test
	public void testGetDiamondsForSalesPackage() {
		Map<Integer, List<SalesItem>> salesItemIdsToSalesItemsForSalesPackIds =
				new HashMap<Integer, List<SalesItem>>();

		salesItemIdsToSalesItemsForSalesPackIds.put(1, new ArrayList<SalesItem>());

		List<SalesItem> siList = new ArrayList<SalesItem>();
		SalesItem si = new SalesItem();
		si.setGemReward(1);
		salesItemIdsToSalesItemsForSalesPackIds.get(1).add(si);

		SalesItem si2 = new SalesItem();
		si2.setGemReward(10);
		salesItemIdsToSalesItemsForSalesPackIds.get(1).add(si2);

		SalesItem si3 = new SalesItem();
		si3.setGemReward(100);
		salesItemIdsToSalesItemsForSalesPackIds.get(1).add(si3);

		assertEquals(111, iapa.getDiamondsForSalesPackage(1, salesItemIdsToSalesItemsForSalesPackIds));
		assertEquals(0, iapa.getDiamondsForSalesPackage(11, salesItemIdsToSalesItemsForSalesPackIds));
	}

	@Test
	public void testCheckIfMonstersExistInSalesItem() {
		Map<Integer, Monster> map = new HashMap<Integer, Monster>();
		map.put(1, new Monster());
		map.put(2, new Monster());
		map.put(3, new Monster());
		when(mockedMonsterRetrieveUtils.getMonsterIdsToMonsters()).thenReturn(map);

		List<SalesItem> salesItemList = new ArrayList<SalesItem>();
		SalesItem si = new SalesItem();
		si.setMonsterId(1);
		salesItemList.add(si);

		SalesItem si2 = new SalesItem();
		si2.setMonsterId(3);
		salesItemList.add(si2);

		iapa.setMonsterRetrieveUtils(mockedMonsterRetrieveUtils);
		assertTrue(iapa.checkIfMonstersExistInSalesItem(salesItemList));

		SalesItem si3 = new SalesItem();
		si2.setMonsterId(4);
		salesItemList.add(si3);

		assertFalse(iapa.checkIfMonstersExistInSalesItem(salesItemList));
	}

	@Test
	public void testCreateUpdateUserMonsterArgumentsForSales() {
		Monster monster = new Monster();
		monster.setId(1);
		monster.setNumPuzzlePieces(2);
		monster.setBaseDefensiveSkillId(0);
		monster.setBaseOffensiveSkillId(0);

		when(mockedMonsterRetrieveUtils.getMonsterForMonsterId(1)).thenReturn(monster);

		MonsterLevelInfoRetrieveUtils mockedMonsterLevelInfoRetrieveUtils = mock(MonsterLevelInfoRetrieveUtils.class);
		Map<Integer, MonsterLevelInfo> levelToInfo = new HashMap<Integer, MonsterLevelInfo>();
		MonsterLevelInfo mli = new MonsterLevelInfo();
		mli.setCurLvlRequiredExp(100);
		mli.setHp(100);
		levelToInfo.put(1, mli);

		when(mockedMonsterLevelInfoRetrieveUtils.getAllPartialMonsterLevelInfo(1)).thenReturn(levelToInfo);

//		ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);

		//this assumes createLeveledMonsterForUserFromQuantity works as intended
		when(mockedMonsterStuffUtils.createLeveledMonsterForUserFromQuantity(any(String.class), any(Monster.class),
				any(Integer.class), any(Date.class), any(Integer.class), any(MonsterLevelInfoRetrieveUtils.class))).
				thenReturn(new ArrayList<MonsterForUser>(Collections.nCopies(3, new MonsterForUser())));

		List<SalesItem> salesItemList = new ArrayList<SalesItem>();
		SalesItem si = new SalesItem();
		si.setMonsterId(1);
		si.setMonsterLevel(1);
		si.setMonsterQuantity(3);
		salesItemList.add(si);

		iapa.setMonsterRetrieveUtils(mockedMonsterRetrieveUtils);
		iapa.setMonsterStuffUtils(mockedMonsterStuffUtils);
		iapa.setMonsterLevelInfoRetrieveUtils(mockedMonsterLevelInfoRetrieveUtils);

		Map<Integer, Integer> monsterIdToNumPieces = new HashMap<Integer, Integer>();
		List<MonsterForUser> completeUserMonsters = new ArrayList<MonsterForUser>();
		iapa.createUpdateUserMonsterArgumentsForSales(mockedUser.getId(), 1, salesItemList, monsterIdToNumPieces,
				completeUserMonsters, new Date());

		assertEquals(3, completeUserMonsters.size());
		assertTrue(monsterIdToNumPieces.isEmpty());
	}

	@Test
	public void testCalculateItemRewards() {
		ItemForUserRetrieveUtil mockedItemForUserRetrieveUtil = mock(ItemForUserRetrieveUtil.class);

		Map<Integer, ItemForUser> userExistingItems = new HashMap<Integer, ItemForUser>();
		ItemForUser ifu = new ItemForUser();
		ifu.setItemId(1);
		ifu.setQuantity(5);

		ItemForUser ifu2 = new ItemForUser();
		ifu2.setItemId(2);
		ifu2.setQuantity(2);

		ItemForUser ifu3 = new ItemForUser();
		ifu3.setItemId(3);
		ifu3.setQuantity(10);

		userExistingItems.put(1, ifu);
		userExistingItems.put(2, ifu2);
		userExistingItems.put(3, ifu3);

		when(mockedItemForUserRetrieveUtil.getSpecificOrAllItemForUserMap(any(String.class), any(Collection.class))).
			thenReturn(userExistingItems);

		Map<Integer, Integer> itemsReceived = new HashMap<Integer, Integer>();
		itemsReceived.put(1, 5);
		itemsReceived.put(2, 8);
		itemsReceived.put(4, 10);

		List<ItemForUser> returnList = iapa.calculateItemRewards(mockedUser.getId(), mockedItemForUserRetrieveUtil,
				itemsReceived);

		for(ItemForUser ifu1 : returnList) {
			assertTrue(ifu1.getQuantity() == 10);
		}
	}

	@Test
	public void testUpdateUserSalesValueAndLastPurchaseTime() {
		when(mockedUser.getSalesValue()).thenReturn(0);
		assertTrue(iapa.updateUserSalesValueAndLastPurchaseTime());

		when(mockedUser.getSalesValue()).thenReturn(1);
		assertTrue(iapa.updateUserSalesValueAndLastPurchaseTime());

		when(mockedUser.getSalesValue()).thenReturn(2);
		assertTrue(iapa.updateUserSalesValueAndLastPurchaseTime());

		when(mockedUser.getSalesValue()).thenReturn(3);
		assertTrue(iapa.updateUserSalesValueAndLastPurchaseTime());

		when(mockedUser.getSalesValue()).thenReturn(4);
		assertTrue(iapa.updateUserSalesValueAndLastPurchaseTime());

		when(mockedUser.getSalesValue()).thenReturn(5);
		assertTrue(iapa.updateUserSalesValueAndLastPurchaseTime());

		when(mockedUser.getSalesValue()).thenReturn(6);
		assertTrue(iapa.updateUserSalesValueAndLastPurchaseTime());

		when(mockedUser.getSalesValue()).thenReturn(-1);
		assertFalse(iapa.updateUserSalesValueAndLastPurchaseTime());
	}



}
