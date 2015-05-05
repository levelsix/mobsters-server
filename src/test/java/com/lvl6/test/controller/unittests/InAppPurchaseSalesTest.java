
package com.lvl6.test.controller.unittests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.lvl6.info.ItemForUser;
import com.lvl6.info.Reward;
import com.lvl6.info.SalesPackage;
import com.lvl6.info.User;
import com.lvl6.retrieveutils.IAPHistoryRetrieveUtils;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.rarechange.MonsterRetrieveUtils;
import com.lvl6.server.controller.actionobjects.InAppPurchaseSalesAction;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.utilmethods.UpdateUtil;

public class InAppPurchaseSalesTest {

	private static User mockedUser;
	private static UpdateUtil mockedUpdateUtil;
	private static IAPHistoryRetrieveUtils mockedIAPHistoryRetrieveUtil;
	private static MonsterRetrieveUtils mockedMonsterRetrieveUtils;
	private static MonsterStuffUtils mockedMonsterStuffUtils;
	private static InAppPurchaseSalesAction iapa;
	private static ItemForUserRetrieveUtil mockedItemForUserRetrieveUtil;


	@BeforeClass
	public static void setUp() {
		mockedUser = mock(User.class);
		when(mockedUser.getId()).thenReturn("userId");

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
	public void testBuilderCheck() {
		mockedItemForUserRetrieveUtil = mock(ItemForUserRetrieveUtil.class);
		List<ItemForUser> ifuList = new ArrayList<ItemForUser>();
		when(mockedItemForUserRetrieveUtil.getSpecificOrAllItemForUser(any(String.class),
				any(List.class))).thenReturn(ifuList);
		List<Reward> listOfRewards = new ArrayList<Reward>();
		Reward r = new Reward(1, 10000, "ITEM", 1);
		listOfRewards.add(r);
		iapa.setListOfRewards(listOfRewards);
		iapa.setItemForUserRetrieveUtil(mockedItemForUserRetrieveUtil);

		iapa.builderCheck();

		ItemForUser ifu = new ItemForUser();
		ifuList.add(ifu);

		iapa.builderCheck();
		assertEquals(0, iapa.getListOfRewards().size());

		ifuList.add(ifu);
		ifuList.add(ifu);

		iapa.builderCheck();
		assertEquals(0, iapa.getListOfRewards().size());

	}

	@Test
	public void testSaleIsWithinTimeConstraints() {
		SalesPackage sp = new SalesPackage();
		sp.setTimeStart(new Date(new Date().getTime()-1000));
		sp.setTimeEnd(new Date(new Date().getTime()-500));

		iapa.setSalesPackage(sp);
		iapa.setNow(new Date());

		assertFalse(iapa.saleIsWithinTimeConstraints());

		sp.setTimeEnd(new Date(new Date().getTime()+100));
		assertTrue(iapa.saleIsWithinTimeConstraints());

		sp.setTimeStart(new Date(new Date().getTime()+50));
		assertFalse(iapa.saleIsWithinTimeConstraints());

		sp.setTimeStart(null);
		sp.setTimeEnd(null);

		assertTrue(iapa.saleIsWithinTimeConstraints());
	}

	@Test
	public void testUpdateUserSalesValueAndLastPurchaseTime() {
		iapa.setSalesValue(0);
		iapa.updateUserSalesValueAndLastPurchaseTime();
		assertEquals(1, iapa.getSalesValue());

		iapa.setSalesValue(1);
		iapa.updateUserSalesValueAndLastPurchaseTime();
		assertEquals(2, iapa.getSalesValue());

		iapa.setSalesValue(2);
		iapa.updateUserSalesValueAndLastPurchaseTime();
		assertEquals(3, iapa.getSalesValue());

		iapa.setSalesValue(3);
		iapa.updateUserSalesValueAndLastPurchaseTime();
		assertEquals(4, iapa.getSalesValue());

		iapa.setSalesValue(4);
		iapa.updateUserSalesValueAndLastPurchaseTime();
		assertEquals(4, iapa.getSalesValue());

		iapa.setSalesValue(5);
		iapa.updateUserSalesValueAndLastPurchaseTime();
		assertEquals(5, iapa.getSalesValue());
	}

	@Test
	public void testSalesPackageLessThanUserSalesValue() {
		when(mockedUser.getSalesValue()).thenReturn(0);
		SalesPackage sp = new SalesPackage();
		sp.setPrice(5);

		iapa.setSalesPackage(sp);
		assertFalse(iapa.salesPackageLessThanUserSalesValue());

		sp.setPrice(100);
		assertFalse(iapa.salesPackageLessThanUserSalesValue());

		when(mockedUser.getSalesValue()).thenReturn(1);

		sp.setPrice(5);
		assertTrue(iapa.salesPackageLessThanUserSalesValue());

		sp.setPrice(10);
		assertFalse(iapa.salesPackageLessThanUserSalesValue());

		sp.setPrice(100);
		assertFalse(iapa.salesPackageLessThanUserSalesValue());

		when(mockedUser.getSalesValue()).thenReturn(2);

		sp.setPrice(5);
		assertTrue(iapa.salesPackageLessThanUserSalesValue());

		sp.setPrice(10);
		assertTrue(iapa.salesPackageLessThanUserSalesValue());

		sp.setPrice(20);
		assertFalse(iapa.salesPackageLessThanUserSalesValue());

		sp.setPrice(100);
		assertFalse(iapa.salesPackageLessThanUserSalesValue());

		when(mockedUser.getSalesValue()).thenReturn(3);

		sp.setPrice(5);
		assertTrue(iapa.salesPackageLessThanUserSalesValue());

		sp.setPrice(50);
		assertFalse(iapa.salesPackageLessThanUserSalesValue());

		sp.setPrice(100);
		assertFalse(iapa.salesPackageLessThanUserSalesValue());

		when(mockedUser.getSalesValue()).thenReturn(4);

		sp.setPrice(5);
		assertTrue(iapa.salesPackageLessThanUserSalesValue());

		sp.setPrice(50);
		assertTrue(iapa.salesPackageLessThanUserSalesValue());

		sp.setPrice(100);
		assertFalse(iapa.salesPackageLessThanUserSalesValue());

		when(mockedUser.getSalesValue()).thenReturn(5);

		sp.setPrice(5);
		assertTrue(iapa.salesPackageLessThanUserSalesValue());

		sp.setPrice(50);
		assertTrue(iapa.salesPackageLessThanUserSalesValue());

		sp.setPrice(100);
		assertFalse(iapa.salesPackageLessThanUserSalesValue());


	}




}
