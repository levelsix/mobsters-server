package com.lvl6.test.controller.utilstests;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.lvl6.info.BoosterItem;
import com.lvl6.info.ItemForUser;
import com.lvl6.info.Monster;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.MonsterLevelInfo;
import com.lvl6.info.User;
import com.lvl6.retrieveutils.IAPHistoryRetrieveUtils;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.rarechange.MonsterLevelInfoRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.RewardRetrieveUtils;
import com.lvl6.server.controller.StartupControllerOld;
import com.lvl6.server.controller.actionobjects.InAppPurchaseSalesAction;
import com.lvl6.server.controller.utils.BoosterItemUtils;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.utilmethods.UpdateUtil;

public class BoosterItemUtilsTest {

	private static Logger log = LoggerFactory.getLogger(StartupControllerOld.class);

	@Autowired
	protected BoosterItemUtils boosterItemUtils;
	
	@Autowired
	protected RewardRetrieveUtils rewardRetrieveUtils;
	
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
			log.error("BoosterItemUtilsTest setup() exception.", e);
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
	public void testCheckIfMonstersExist() {
		Map<Integer, Monster> idsToMonsters = new HashMap<Integer, Monster>();
		idsToMonsters.put(1, new Monster());
		idsToMonsters.put(2, new Monster());
		idsToMonsters.put(3, new Monster());
		idsToMonsters.put(4, new Monster());

		when(mockedMonsterRetrieveUtils.getMonsterIdsToMonsters()).thenReturn(idsToMonsters);

		List<BoosterItem> boosterItemsUserReceives = new ArrayList<BoosterItem>();
		BoosterItem bi = new BoosterItem();
		bi.setMonsterId(1);

		BoosterItem bi2 = new BoosterItem();
		bi2.setMonsterId(4);

		BoosterItem bi3 = new BoosterItem();
		bi3.setMonsterId(5);

		boosterItemsUserReceives.add(bi);
		boosterItemsUserReceives.add(bi2);

		assertTrue(boosterItemUtils.checkIfMonstersExist(boosterItemsUserReceives, mockedMonsterRetrieveUtils));

		boosterItemsUserReceives.add(bi3);
		assertFalse(boosterItemUtils.checkIfMonstersExist(boosterItemsUserReceives, mockedMonsterRetrieveUtils));
	}

	@Test
	public void testDetermineGemReward() {
		List<BoosterItem> boosterItemsUserReceives = new ArrayList<BoosterItem>();
		BoosterItem bi = new BoosterItem();
		bi.setGemReward(100);

		BoosterItem bi2 = new BoosterItem();
		bi2.setGemReward(100);

		BoosterItem bi3 = new BoosterItem();
		bi3.setGemReward(0);

		boosterItemsUserReceives.add(bi);
		assertEquals(100, boosterItemUtils.determineGemReward(boosterItemsUserReceives, rewardRetrieveUtils));

		boosterItemsUserReceives.add(bi3);
		assertEquals(100, boosterItemUtils.determineGemReward(boosterItemsUserReceives, rewardRetrieveUtils));

		boosterItemsUserReceives.add(bi2);
		assertEquals(200, boosterItemUtils.determineGemReward(boosterItemsUserReceives, rewardRetrieveUtils));
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

		when(mockedMonsterLevelInfoRetrieveUtils.getMonsterLevelInfoForMonsterId(1)).thenReturn(levelToInfo);

//		ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);

		//this assumes createLeveledMonsterForUserFromQuantity works as intended
		when(mockedMonsterStuffUtils.createLeveledMonsterForUserFromQuantity(any(String.class), any(Monster.class),
				any(Integer.class), any(Date.class), any(Integer.class), any(MonsterLevelInfoRetrieveUtils.class))).
				thenReturn(new ArrayList<MonsterForUser>(Collections.nCopies(3, new MonsterForUser())));

		List<BoosterItem> boosterItemList = new ArrayList<BoosterItem>();
		BoosterItem bi = new BoosterItem();
		bi.setMonsterId(1);
		bi.setComplete(true);
		boosterItemList.add(bi);

		iapa.setMonsterRetrieveUtils(mockedMonsterRetrieveUtils);
		iapa.setMonsterStuffUtils(mockedMonsterStuffUtils);
		iapa.setMonsterLevelInfoRetrieveUtils(mockedMonsterLevelInfoRetrieveUtils);

		Map<Integer, Integer> monsterIdToNumPieces = new HashMap<Integer, Integer>();
		List<MonsterForUser> completeUserMonsters = new ArrayList<MonsterForUser>();
		BoosterItemUtils.createUpdateUserMonsterArguments(mockedUser.getId(), 1, boosterItemList,
				monsterIdToNumPieces, completeUserMonsters, new Date(), mockedMonsterLevelInfoRetrieveUtils,
				mockedMonsterRetrieveUtils, mockedMonsterStuffUtils);

		assertEquals(completeUserMonsters.size(), 1);
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

//		List<ItemForUser> returnList = iapa.calculateItemRewards(mockedUser.getId(), mockedItemForUserRetrieveUtil,
//				itemsReceived);
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;
//import static org.mockito.Matchers.any;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import com.lvl6.info.BoosterItem;
//import com.lvl6.info.ItemForUser;
//import com.lvl6.info.Monster;
//import com.lvl6.info.MonsterForUser;
//import com.lvl6.info.MonsterLevelInfo;
//import com.lvl6.info.User;
//import com.lvl6.retrieveutils.IAPHistoryRetrieveUtils;
//import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
//import com.lvl6.retrieveutils.rarechange.MonsterLevelInfoRetrieveUtils;
//import com.lvl6.retrieveutils.rarechange.MonsterRetrieveUtils;
//import com.lvl6.retrieveutils.rarechange.RewardRetrieveUtils;
//import com.lvl6.server.controller.StartupControllerOld;
//import com.lvl6.server.controller.actionobjects.InAppPurchaseSalesAction;
//import com.lvl6.server.controller.utils.BoosterItemUtils;
//import com.lvl6.server.controller.utils.MonsterStuffUtils;
//import com.lvl6.utils.utilmethods.UpdateUtil;
//
//public class BoosterItemUtilsTest {
//
//	private static Logger log = LoggerFactory.getLogger(StartupControllerOld.class);
//
//	@Autowired
//	protected BoosterItemUtils boosterItemUtils;
//	
//	@Autowired
//	protected RewardRetrieveUtils rewardRetrieveUtils;
//	
//	private static User mockedUser;
//	private static UpdateUtil mockedUpdateUtil;
//	private static JSONObject mockedReceiptFromApple1;
//	private static JSONObject mockedReceiptFromApple2;
//	private static IAPHistoryRetrieveUtils mockedIAPHistoryRetrieveUtil;
//	private static MonsterRetrieveUtils mockedMonsterRetrieveUtils;
//	private static MonsterStuffUtils mockedMonsterStuffUtils;
//	private static InAppPurchaseSalesAction iapa;
//
//	@BeforeClass
//	public static void setUp() {
//		mockedUser = mock(User.class);
//		when(mockedUser.getId()).thenReturn("userId");
//
//		mockedReceiptFromApple1 = mock(JSONObject.class);
//		mockedReceiptFromApple2 = mock(JSONObject.class);
//		try {
//			when(mockedReceiptFromApple1.getString(any(String.class))).thenReturn("123");
//			when(mockedReceiptFromApple2.getString(any(String.class))).thenReturn("456");
//		} catch (JSONException e) {
//			log.error("BoosterItemUtilsTest setup() exception.", e);
//		}
//
//		mockedIAPHistoryRetrieveUtil = mock(IAPHistoryRetrieveUtils.class);
//		long uniqueLong = 123;
//		long duplicateLong = 456;
//		when(mockedIAPHistoryRetrieveUtil.checkIfDuplicateTransaction(uniqueLong)).thenReturn(false);
//		when(mockedIAPHistoryRetrieveUtil.checkIfDuplicateTransaction(duplicateLong)).thenReturn(true);
//
//		mockedMonsterRetrieveUtils = mock(MonsterRetrieveUtils.class);
//
//		mockedMonsterStuffUtils = mock(MonsterStuffUtils.class);
//
//		mockedUpdateUtil = mock(UpdateUtil.class);
//		when(mockedUpdateUtil.updateUserSalesValue(any(String.class), any(Integer.class),
//				any(Date.class))).thenReturn(true);
//
//		iapa = new InAppPurchaseSalesAction();
//		iapa.setUser(mockedUser);
//		iapa.setUpdateUtil(mockedUpdateUtil);
//	}
//
//	@Test
//	public void testCheckIfMonstersExist() {
//		Map<Integer, Monster> idsToMonsters = new HashMap<Integer, Monster>();
//		idsToMonsters.put(1, new Monster());
//		idsToMonsters.put(2, new Monster());
//		idsToMonsters.put(3, new Monster());
//		idsToMonsters.put(4, new Monster());
//
//		when(mockedMonsterRetrieveUtils.getMonsterIdsToMonsters()).thenReturn(idsToMonsters);
//
//		List<BoosterItem> boosterItemsUserReceives = new ArrayList<BoosterItem>();
//		BoosterItem bi = new BoosterItem();
//		bi.setMonsterId(1);
//
//		BoosterItem bi2 = new BoosterItem();
//		bi2.setMonsterId(4);
//
//		BoosterItem bi3 = new BoosterItem();
//		bi3.setMonsterId(5);
//
//		boosterItemsUserReceives.add(bi);
//		boosterItemsUserReceives.add(bi2);
//
//		assertTrue(boosterItemUtils.checkIfMonstersExist(boosterItemsUserReceives, mockedMonsterRetrieveUtils));
//
//		boosterItemsUserReceives.add(bi3);
//		assertFalse(boosterItemUtils.checkIfMonstersExist(boosterItemsUserReceives, mockedMonsterRetrieveUtils));
//	}
//
//	@Test
//	public void testDetermineGemReward() {
//		List<BoosterItem> boosterItemsUserReceives = new ArrayList<BoosterItem>();
//		BoosterItem bi = new BoosterItem();
//		bi.setGemReward(100);
//
//		BoosterItem bi2 = new BoosterItem();
//		bi2.setGemReward(100);
//
//		BoosterItem bi3 = new BoosterItem();
//		bi3.setGemReward(0);
//
//		boosterItemsUserReceives.add(bi);
//		assertEquals(100, boosterItemUtils.determineGemReward(boosterItemsUserReceives, rewardRetrieveUtils));
//
//		boosterItemsUserReceives.add(bi3);
//		assertEquals(100, boosterItemUtils.determineGemReward(boosterItemsUserReceives, rewardRetrieveUtils));
//
//		boosterItemsUserReceives.add(bi2);
//		assertEquals(200, boosterItemUtils.determineGemReward(boosterItemsUserReceives, rewardRetrieveUtils));
//	}
//
//	@Test
//	public void testCreateUpdateUserMonsterArgumentsForSales() {
//		Monster monster = new Monster();
//		monster.setId(1);
//		monster.setNumPuzzlePieces(2);
//		monster.setBaseDefensiveSkillId(0);
//		monster.setBaseOffensiveSkillId(0);
//
//		when(mockedMonsterRetrieveUtils.getMonsterForMonsterId(1)).thenReturn(monster);
//
//		MonsterLevelInfoRetrieveUtils mockedMonsterLevelInfoRetrieveUtils = mock(MonsterLevelInfoRetrieveUtils.class);
//
//		Map<Integer, MonsterLevelInfo> levelToInfo = new HashMap<Integer, MonsterLevelInfo>();
//		MonsterLevelInfo mli = new MonsterLevelInfo();
//		mli.setCurLvlRequiredExp(100);
//		mli.setHp(100);
//		levelToInfo.put(1, mli);
//
//		when(mockedMonsterLevelInfoRetrieveUtils.getMonsterLevelInfoForMonsterId(1)).thenReturn(levelToInfo);
//
////		ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
//
//		//this assumes createLeveledMonsterForUserFromQuantity works as intended
//		when(mockedMonsterStuffUtils.createLeveledMonsterForUserFromQuantity(any(String.class), any(Monster.class),
//				any(Integer.class), any(Date.class), any(Integer.class), any(MonsterLevelInfoRetrieveUtils.class))).
//				thenReturn(new ArrayList<MonsterForUser>(Collections.nCopies(3, new MonsterForUser())));
//
//		List<BoosterItem> boosterItemList = new ArrayList<BoosterItem>();
//		BoosterItem bi = new BoosterItem();
//		bi.setMonsterId(1);
//		bi.setComplete(true);
//		boosterItemList.add(bi);
//
//		iapa.setMonsterRetrieveUtils(mockedMonsterRetrieveUtils);
//		iapa.setMonsterStuffUtils(mockedMonsterStuffUtils);
//		iapa.setMonsterLevelInfoRetrieveUtils(mockedMonsterLevelInfoRetrieveUtils);
//
//		Map<Integer, Integer> monsterIdToNumPieces = new HashMap<Integer, Integer>();
//		List<MonsterForUser> completeUserMonsters = new ArrayList<MonsterForUser>();
//		BoosterItemUtils.createUpdateUserMonsterArguments(mockedUser.getId(), 1, boosterItemList,
//				monsterIdToNumPieces, completeUserMonsters, new Date(), mockedMonsterLevelInfoRetrieveUtils,
//				mockedMonsterRetrieveUtils, mockedMonsterStuffUtils);
//
//		assertEquals(completeUserMonsters.size(), 1);
//		assertTrue(monsterIdToNumPieces.isEmpty());
//	}
//
//	@Test
//	public void testCalculateItemRewards() {
//		ItemForUserRetrieveUtil mockedItemForUserRetrieveUtil = mock(ItemForUserRetrieveUtil.class);
//
//		Map<Integer, ItemForUser> userExistingItems = new HashMap<Integer, ItemForUser>();
//		ItemForUser ifu = new ItemForUser();
//		ifu.setItemId(1);
//		ifu.setQuantity(5);
//
//		ItemForUser ifu2 = new ItemForUser();
//		ifu2.setItemId(2);
//		ifu2.setQuantity(2);
//
//		ItemForUser ifu3 = new ItemForUser();
//		ifu3.setItemId(3);
//		ifu3.setQuantity(10);
//
//		userExistingItems.put(1, ifu);
//		userExistingItems.put(2, ifu2);
//		userExistingItems.put(3, ifu3);
//
//		when(mockedItemForUserRetrieveUtil.getSpecificOrAllItemForUserMap(any(String.class), any(Collection.class))).
//			thenReturn(userExistingItems);
//
//		Map<Integer, Integer> itemsReceived = new HashMap<Integer, Integer>();
//		itemsReceived.put(1, 5);
//		itemsReceived.put(2, 8);
//		itemsReceived.put(4, 10);
//
////		List<ItemForUser> returnList = iapa.calculateItemRewards(mockedUser.getId(), mockedItemForUserRetrieveUtil,
////				itemsReceived);
////
////import static org.junit.Assert.assertEquals;
////import static org.junit.Assert.assertFalse;
////import static org.junit.Assert.assertTrue;
////import static org.mockito.Matchers.any;
////import static org.mockito.Mockito.mock;
////import static org.mockito.Mockito.when;
////
////import java.util.ArrayList;
////import java.util.Collection;
////import java.util.Collections;
////import java.util.Date;
////import java.util.HashMap;
////import java.util.List;
////import java.util.Map;
////
////import org.json.JSONException;
////import org.json.JSONObject;
////import org.junit.BeforeClass;
////import org.junit.Test;
////import org.slf4j.Logger;
////import org.slf4j.LoggerFactory;
////import org.springframework.beans.factory.annotation.Autowired;
////
////import com.lvl6.info.BoosterItem;
////import com.lvl6.info.ItemForUser;
////import com.lvl6.info.Monster;
////import com.lvl6.info.MonsterForUser;
////import com.lvl6.info.MonsterLevelInfo;
////import com.lvl6.info.User;
////import com.lvl6.retrieveutils.IAPHistoryRetrieveUtils;
////import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
////import com.lvl6.retrieveutils.rarechange.MonsterLevelInfoRetrieveUtils;
////import com.lvl6.retrieveutils.rarechange.MonsterRetrieveUtils;
////import com.lvl6.retrieveutils.rarechange.RewardRetrieveUtils;
////import com.lvl6.server.controller.StartupControllerOld;
////import com.lvl6.server.controller.actionobjects.InAppPurchaseSalesAction;
////import com.lvl6.server.controller.utils.BoosterItemUtils;
////import com.lvl6.server.controller.utils.MonsterStuffUtils;
////import com.lvl6.utils.utilmethods.UpdateUtil;
////
////public class BoosterItemUtilsTest {
////
////	private static Logger log = LoggerFactory.getLogger(StartupControllerOld.class);
////	
////	@Autowired
////	protected RewardRetrieveUtils rewardRetrieveUtils;
////	
////	@Autowired
////	protected BoosterItemUtils boosterItemUtils;
////
////	private static User mockedUser;
////	private static UpdateUtil mockedUpdateUtil;
////	private static JSONObject mockedReceiptFromApple1;
////	private static JSONObject mockedReceiptFromApple2;
////	private static IAPHistoryRetrieveUtils mockedIAPHistoryRetrieveUtil;
////	private static MonsterRetrieveUtils mockedMonsterRetrieveUtils;
////	private static MonsterStuffUtils mockedMonsterStuffUtils;
////	private static InAppPurchaseSalesAction iapa;
////
////	@BeforeClass
////	public static void setUp() {
////		mockedUser = mock(User.class);
////		when(mockedUser.getId()).thenReturn("userId");
////
////		mockedReceiptFromApple1 = mock(JSONObject.class);
////		mockedReceiptFromApple2 = mock(JSONObject.class);
////		try {
////			when(mockedReceiptFromApple1.getString(any(String.class))).thenReturn("123");
////			when(mockedReceiptFromApple2.getString(any(String.class))).thenReturn("456");
////		} catch (JSONException e) {
////			log.error("BoosterItemUtilsTest setup() exception.", e);
////		}
////
////		mockedIAPHistoryRetrieveUtil = mock(IAPHistoryRetrieveUtils.class);
////		long uniqueLong = 123;
////		long duplicateLong = 456;
////		when(mockedIAPHistoryRetrieveUtil.checkIfDuplicateTransaction(uniqueLong)).thenReturn(false);
////		when(mockedIAPHistoryRetrieveUtil.checkIfDuplicateTransaction(duplicateLong)).thenReturn(true);
////
////		mockedMonsterRetrieveUtils = mock(MonsterRetrieveUtils.class);
////
////		mockedMonsterStuffUtils = mock(MonsterStuffUtils.class);
////
////		mockedUpdateUtil = mock(UpdateUtil.class);
////		when(mockedUpdateUtil.updateUserSalesValue(any(String.class), any(Integer.class),
////				any(Date.class))).thenReturn(true);
////
////		iapa = new InAppPurchaseSalesAction();
////		iapa.setUser(mockedUser);
////		iapa.setUpdateUtil(mockedUpdateUtil);
////	}
////
////	@Test
////	public void testCheckIfMonstersExist() {
////		Map<Integer, Monster> idsToMonsters = new HashMap<Integer, Monster>();
////		idsToMonsters.put(1, new Monster());
////		idsToMonsters.put(2, new Monster());
////		idsToMonsters.put(3, new Monster());
////		idsToMonsters.put(4, new Monster());
////
////		when(mockedMonsterRetrieveUtils.getMonsterIdsToMonsters()).thenReturn(idsToMonsters);
////
////		List<BoosterItem> boosterItemsUserReceives = new ArrayList<BoosterItem>();
////		BoosterItem bi = new BoosterItem();
////		bi.setMonsterId(1);
////
////		BoosterItem bi2 = new BoosterItem();
////		bi2.setMonsterId(4);
////
////		BoosterItem bi3 = new BoosterItem();
////		bi3.setMonsterId(5);
////
////		boosterItemsUserReceives.add(bi);
////		boosterItemsUserReceives.add(bi2);
////
////		assertTrue(boosterItemUtils.checkIfMonstersExist(boosterItemsUserReceives, mockedMonsterRetrieveUtils, rewardRetrieveUtils));
////
////		boosterItemsUserReceives.add(bi3);
////		assertFalse(boosterItemUtils.checkIfMonstersExist(boosterItemsUserReceives, mockedMonsterRetrieveUtils, rewardRetrieveUtils));
////	}
////
////	@Test
////	public void testDetermineGemReward() {
////		List<BoosterItem> boosterItemsUserReceives = new ArrayList<BoosterItem>();
////		BoosterItem bi = new BoosterItem();
////		bi.setGemReward(100);
////
////		BoosterItem bi2 = new BoosterItem();
////		bi2.setGemReward(100);
////
////		BoosterItem bi3 = new BoosterItem();
////		bi3.setGemReward(0);
////
////		boosterItemsUserReceives.add(bi);
////		assertEquals(100, boosterItemUtils.determineGemReward(boosterItemsUserReceives));
////
////		boosterItemsUserReceives.add(bi3);
////		assertEquals(100, boosterItemUtils.determineGemReward(boosterItemsUserReceives));
////
////		boosterItemsUserReceives.add(bi2);
////		assertEquals(200, boosterItemUtils.determineGemReward(boosterItemsUserReceives));
////	}
////
////	@Test
////	public void testCreateUpdateUserMonsterArgumentsForSales() {
////		Monster monster = new Monster();
////		monster.setId(1);
////		monster.setNumPuzzlePieces(2);
////		monster.setBaseDefensiveSkillId(0);
////		monster.setBaseOffensiveSkillId(0);
////
////		when(mockedMonsterRetrieveUtils.getMonsterForMonsterId(1)).thenReturn(monster);
////
////		MonsterLevelInfoRetrieveUtils mockedMonsterLevelInfoRetrieveUtils = mock(MonsterLevelInfoRetrieveUtils.class);
////
////		Map<Integer, MonsterLevelInfo> levelToInfo = new HashMap<Integer, MonsterLevelInfo>();
////		MonsterLevelInfo mli = new MonsterLevelInfo();
////		mli.setCurLvlRequiredExp(100);
////		mli.setHp(100);
////		levelToInfo.put(1, mli);
////
////		when(mockedMonsterLevelInfoRetrieveUtils.getMonsterLevelInfoForMonsterId(1)).thenReturn(levelToInfo);
////
//////		ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
////
////		//this assumes createLeveledMonsterForUserFromQuantity works as intended
////		when(mockedMonsterStuffUtils.createLeveledMonsterForUserFromQuantity(any(String.class), any(Monster.class),
////				any(Integer.class), any(Date.class), any(Integer.class), any(MonsterLevelInfoRetrieveUtils.class))).
////				thenReturn(new ArrayList<MonsterForUser>(Collections.nCopies(3, new MonsterForUser())));
////
////		List<BoosterItem> boosterItemList = new ArrayList<BoosterItem>();
////		BoosterItem bi = new BoosterItem();
////		bi.setMonsterId(1);
////		bi.setComplete(true);
////		boosterItemList.add(bi);
////
////		iapa.setMonsterRetrieveUtils(mockedMonsterRetrieveUtils);
////		iapa.setMonsterStuffUtils(mockedMonsterStuffUtils);
////		iapa.setMonsterLevelInfoRetrieveUtils(mockedMonsterLevelInfoRetrieveUtils);
////
////		Map<Integer, Integer> monsterIdToNumPieces = new HashMap<Integer, Integer>();
////		List<MonsterForUser> completeUserMonsters = new ArrayList<MonsterForUser>();
////		boosterItemUtils.createUpdateUserMonsterArguments(mockedUser.getId(), 1, boosterItemList,
////				monsterIdToNumPieces, completeUserMonsters, new Date(), mockedMonsterLevelInfoRetrieveUtils,
////				mockedMonsterRetrieveUtils, mockedMonsterStuffUtils);
////
////		assertEquals(completeUserMonsters.size(), 1);
////		assertTrue(monsterIdToNumPieces.isEmpty());
////	}
////
////	@Test
////	public void testCalculateItemRewards() {
////		ItemForUserRetrieveUtil mockedItemForUserRetrieveUtil = mock(ItemForUserRetrieveUtil.class);
////
////		Map<Integer, ItemForUser> userExistingItems = new HashMap<Integer, ItemForUser>();
////		ItemForUser ifu = new ItemForUser();
////		ifu.setItemId(1);
////		ifu.setQuantity(5);
////
////		ItemForUser ifu2 = new ItemForUser();
////		ifu2.setItemId(2);
////		ifu2.setQuantity(2);
////
////		ItemForUser ifu3 = new ItemForUser();
////		ifu3.setItemId(3);
////		ifu3.setQuantity(10);
////
////		userExistingItems.put(1, ifu);
////		userExistingItems.put(2, ifu2);
////		userExistingItems.put(3, ifu3);
////
////		when(mockedItemForUserRetrieveUtil.getSpecificOrAllItemForUserMap(any(String.class), any(Collection.class))).
////			thenReturn(userExistingItems);
////
////		Map<Integer, Integer> itemsReceived = new HashMap<Integer, Integer>();
////		itemsReceived.put(1, 5);
////		itemsReceived.put(2, 8);
////		itemsReceived.put(4, 10);
////
//////		List<ItemForUser> returnList = iapa.calculateItemRewards(mockedUser.getId(), mockedItemForUserRetrieveUtil,
//////				itemsReceived);
//////
//////		for(ItemForUser ifu1 : returnList) {
//////			assertTrue(ifu1.getQuantity() == 10);
//////		}
////	}
////}
