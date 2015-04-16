package com.lvl6.test.controller.unittests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;

import com.lvl6.info.SalesPackage;
import com.lvl6.info.User;
import com.lvl6.retrieveutils.IAPHistoryRetrieveUtils;
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
	}

	@Test
	public void testUserSalesValueLessThanSalesPackage() {
		when(mockedUser.isSalesJumpTwoTiers()).thenReturn(true);

		when(mockedUser.getSalesValue()).thenReturn(0);

		SalesPackage sp = new SalesPackage();
		sp.setId(2);
		sp.setPrice(5);

		iapa.setSalesPackage(sp);

		assertFalse(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(1);
		assertTrue(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(2);
		assertTrue(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(3);
		assertTrue(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(4);
		assertTrue(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(5);
		assertTrue(iapa.userSalesValueLessThanSalesPackage());

		sp.setPrice(10);
		iapa.setSalesPackage(sp);

		when(mockedUser.getSalesValue()).thenReturn(1);
		assertTrue(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(2);
		assertTrue(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(3);
		assertTrue(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(4);
		assertTrue(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(5);
		assertTrue(iapa.userSalesValueLessThanSalesPackage());

		sp.setPrice(20);
		iapa.setSalesPackage(sp);

		when(mockedUser.getSalesValue()).thenReturn(1);
		assertFalse(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(2);
		assertTrue(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(3);
		assertTrue(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(4);
		assertTrue(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(5);
		assertTrue(iapa.userSalesValueLessThanSalesPackage());

		sp.setPrice(50);
		iapa.setSalesPackage(sp);

		when(mockedUser.getSalesValue()).thenReturn(1);
		assertFalse(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(2);
		assertFalse(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(3);
		assertTrue(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(4);
		assertTrue(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(5);
		assertTrue(iapa.userSalesValueLessThanSalesPackage());

		sp.setPrice(100);
		iapa.setSalesPackage(sp);

		when(mockedUser.getSalesValue()).thenReturn(1);
		assertFalse(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(2);
		assertFalse(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(3);
		assertFalse(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(4);
		assertFalse(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(5);
		assertFalse(iapa.userSalesValueLessThanSalesPackage());



		when(mockedUser.isSalesJumpTwoTiers()).thenReturn(false);

		sp.setPrice(5);
		iapa.setSalesPackage(sp);

		when(mockedUser.getSalesValue()).thenReturn(0);
		assertFalse(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(1);
		assertTrue(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(2);
		assertTrue(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(3);
		assertTrue(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(4);
		assertTrue(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(5);
		assertTrue(iapa.userSalesValueLessThanSalesPackage());

		sp.setPrice(10);
		iapa.setSalesPackage(sp);

		when(mockedUser.getSalesValue()).thenReturn(1);
		assertFalse(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(2);
		assertTrue(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(3);
		assertTrue(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(4);
		assertTrue(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(5);
		assertTrue(iapa.userSalesValueLessThanSalesPackage());

		sp.setPrice(20);
		iapa.setSalesPackage(sp);

		when(mockedUser.getSalesValue()).thenReturn(1);
		assertFalse(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(2);
		assertFalse(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(3);
		assertTrue(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(4);
		assertTrue(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(5);
		assertTrue(iapa.userSalesValueLessThanSalesPackage());

		sp.setPrice(50);
		iapa.setSalesPackage(sp);

		when(mockedUser.getSalesValue()).thenReturn(1);
		assertFalse(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(2);
		assertFalse(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(3);
		assertFalse(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(4);
		assertTrue(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(5);
		assertTrue(iapa.userSalesValueLessThanSalesPackage());

		sp.setPrice(100);
		iapa.setSalesPackage(sp);

		when(mockedUser.getSalesValue()).thenReturn(1);
		assertFalse(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(2);
		assertFalse(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(3);
		assertFalse(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(4);
		assertFalse(iapa.userSalesValueLessThanSalesPackage());

		when(mockedUser.getSalesValue()).thenReturn(5);
		assertFalse(iapa.userSalesValueLessThanSalesPackage());

	}

	@Test
	public void testUpdateUserSalesValueAndLastPurchaseTime() {
		when(mockedUser.isSalesJumpTwoTiers()).thenReturn(true);
		SalesPackage sp = new SalesPackage();
		sp.setId(2);
		sp.setPrice(5);

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
