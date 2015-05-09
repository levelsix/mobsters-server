package com.lvl6.test.controller.utilstests;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.server.controller.utils.ResourceUtil;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration("/test-spring-application-context.xml")
public class ResourceUtilTest {

	private static Logger log = LoggerFactory.getLogger(ResourceUtilTest.class);


//	@Autowired
	protected static ResourceUtil resourceUtil;

	@BeforeClass
	public static void setUp() {
		resourceUtil = new ResourceUtil();

	}

	////////////////CASH
	@Test
	public void testCashGained() {
		User mockedUser = mock(User.class);
		when(mockedUser.getCash()).thenReturn(10);

		int maxResource = 50;
		int minResource = 0;
		int expectedCashChange = 10;
		boolean gainResources = true;
		String cash = MiscMethods.cash;
		int actualResourceChange = resourceUtil.calculateMaxResourceChange(
				mockedUser, maxResource, minResource, expectedCashChange, gainResources, cash);

		String msg = String.format("max=%s gainResources=%s amtToGain=%s curAmt=%s",
				maxResource, gainResources, expectedCashChange, mockedUser.getCash());
		Assert.assertEquals(msg, expectedCashChange, actualResourceChange);
	}

	@Test
	public void testCashGainedCapped() {
		User mockedUser = mock(User.class);
		when(mockedUser.getCash()).thenReturn(10);

		int maxResource = 50;
		int minResource = 0;
		int cashChange = 50;
		boolean gainResources = true;
		String cash = MiscMethods.cash;
		int actualResourceChange = resourceUtil.calculateMaxResourceChange(
				mockedUser, maxResource, minResource, cashChange, gainResources, cash);

		int expectedCashChange = maxResource - mockedUser.getCash();
		String msg = String.format("max=%s gainResources=%s amtToGain=%s curAmt=%s",
				maxResource, gainResources, cashChange, mockedUser.getCash());
		Assert.assertEquals(msg, expectedCashChange, actualResourceChange);

	}

	@Test
	public void testCashLost() {
		User mockedUser = mock(User.class);
		when(mockedUser.getCash()).thenReturn(10);

		int maxResource = 50;
		int minResource = 0;
		int cashChange = 10;
		boolean gainResources = false;
		String cash = MiscMethods.cash;
		int actualResourceChange = resourceUtil.calculateMaxResourceChange(
				mockedUser, maxResource, minResource, cashChange, gainResources, cash);

		int expectedCashChange = -cashChange;
		String msg = String.format("max=%s gainResources=%s amtToGain=%s curAmt=%s",
				maxResource, gainResources, expectedCashChange, mockedUser.getCash());

		Assert.assertEquals(msg, expectedCashChange, actualResourceChange);
	}

	@Test
	public void testCashLostCapped() {
		User mockedUser = mock(User.class);
		when(mockedUser.getCash()).thenReturn(10);

		int maxResource = 50;
		int minResource = 0;
		int cashChange = 50;
		boolean gainResources = false;
		String cash = MiscMethods.cash;
		int resourceChange = resourceUtil.calculateMaxResourceChange(
				mockedUser, maxResource, minResource, cashChange, gainResources, cash);

		int expectedCashChange = -mockedUser.getCash();
		String msg = String.format("max=%s gainResources=%s amtToGain=%s curAmt=%s",
				maxResource, gainResources, expectedCashChange, mockedUser.getCash());
		Assert.assertEquals(msg, expectedCashChange, resourceChange);
	}


	@Test
	public void testCashLostCappedAtMin() {
		User mockedUser = mock(User.class);
		when(mockedUser.getCash()).thenReturn(10);

		int maxResource = 50;
		int minResource = 10;
		int cashChange = 50;
		boolean gainResources = false;
		String cash = MiscMethods.cash;
		int resourceChange = resourceUtil.calculateMaxResourceChange(
				mockedUser, maxResource, minResource, cashChange, gainResources, cash);

		int expectedCashChange = 0;
		String msg = String.format("max=%s gainResources=%s amtToGain=%s curAmt=%s",
				maxResource, gainResources, expectedCashChange, mockedUser.getCash());

		Assert.assertEquals(msg, expectedCashChange, resourceChange);
	}

	@Test
	public void testCashLostCappedAtMinV2() {
		User mockedUser = mock(User.class);
		when(mockedUser.getCash()).thenReturn(50);

		int maxResource = 50;
		int minResource = 10;
		int cashChange = 100;
		boolean gainResources = false;
		String cash = MiscMethods.cash;

		int resourceChange = resourceUtil.calculateMaxResourceChange(
				mockedUser, maxResource, minResource, cashChange, gainResources, cash);

		int expectedCashChange = -(maxResource - minResource);
		String msg = String.format("max=%s gainResources=%s amtToGain=%s curAmt=%s",
				maxResource, gainResources, expectedCashChange, mockedUser.getCash());
		Assert.assertEquals(msg, expectedCashChange, resourceChange);
	}

	////////////////OIL
	@Test
	public void testOilGained() {
		User mockedUser = mock(User.class);
		when(mockedUser.getOil()).thenReturn(10);

		int maxResource = 50;
		int minResource = 0;
		int expectedOilChange = 10;
		boolean gainResources = true;
		String oil = MiscMethods.oil;
		int actualResourceChange = resourceUtil.calculateMaxResourceChange(
				mockedUser, maxResource, minResource, expectedOilChange, gainResources, oil);

		String msg = String.format("max=%s gainResources=%s amtToGain=%s curAmt=%s",
				maxResource, gainResources, expectedOilChange, mockedUser.getOil());
		Assert.assertEquals(msg, expectedOilChange, actualResourceChange);
	}

	@Test
	public void testOilGainedCapped() {
		User mockedUser = mock(User.class);
		when(mockedUser.getOil()).thenReturn(10);

		int maxResource = 50;
		int minResource = 0;
		int oilChange = 50;
		boolean gainResources = true;
		String oil = MiscMethods.oil;
		int actualResourceChange = resourceUtil.calculateMaxResourceChange(
				mockedUser, maxResource, minResource, oilChange, gainResources, oil);

		int expectedOilChange = maxResource - mockedUser.getOil();
		String msg = String.format("max=%s gainResources=%s amtToGain=%s curAmt=%s",
				maxResource, gainResources, oilChange, mockedUser.getOil());
		Assert.assertEquals(msg, expectedOilChange, actualResourceChange);

	}

	@Test
	public void testOilLost() {
		User mockedUser = mock(User.class);
		when(mockedUser.getOil()).thenReturn(10);

		int maxResource = 50;
		int minResource = 0;
		int oilChange = 10;
		boolean gainResources = false;
		String oil = MiscMethods.oil;
		int actualResourceChange = resourceUtil.calculateMaxResourceChange(
				mockedUser, maxResource, minResource, oilChange, gainResources, oil);

		int expectedOilChange = -oilChange;
		String msg = String.format("max=%s gainResources=%s amtToGain=%s curAmt=%s",
				maxResource, gainResources, expectedOilChange, mockedUser.getOil());

		Assert.assertEquals(msg, expectedOilChange, actualResourceChange);
	}

	@Test
	public void testOilLostCapped() {
		User mockedUser = mock(User.class);
		when(mockedUser.getOil()).thenReturn(10);

		int maxResource = 50;
		int minResource = 0;
		int oilChange = 50;
		boolean gainResources = false;
		String oil = MiscMethods.oil;
		int resourceChange = resourceUtil.calculateMaxResourceChange(
				mockedUser, maxResource, minResource, oilChange, gainResources, oil);

		int expectedOilChange = -mockedUser.getOil();
		String msg = String.format("max=%s gainResources=%s amtToGain=%s curAmt=%s",
				maxResource, gainResources, expectedOilChange, mockedUser.getOil());
		Assert.assertEquals(msg, expectedOilChange, resourceChange);
	}

	@Test
	public void testOilLostCappedAtMin() {
		User mockedUser = mock(User.class);
		when(mockedUser.getOil()).thenReturn(10);

		int maxResource = 50;
		int minResource = 10;
		int oilChange = 50;
		boolean gainResources = false;
		String oil = MiscMethods.oil;
		int resourceChange = resourceUtil.calculateMaxResourceChange(
				mockedUser, maxResource, minResource, oilChange, gainResources, oil);

		int expectedOilChange = 0;
		String msg = String.format("max=%s gainResources=%s amtToGain=%s curAmt=%s",
				maxResource, gainResources, expectedOilChange, mockedUser.getOil());

		Assert.assertEquals(msg, expectedOilChange, resourceChange);
	}

	@Test
	public void testOilLostCappedAtMinV2() {
		User mockedUser = mock(User.class);
		when(mockedUser.getOil()).thenReturn(50);

		int maxResource = 50;
		int minResource = 10;
		int oilChange = 100;
		boolean gainResources = false;
		String oil = MiscMethods.oil;

		int resourceChange = resourceUtil.calculateMaxResourceChange(
				mockedUser, maxResource, minResource, oilChange, gainResources, oil);

		int expectedOilChange = -(maxResource - minResource);
		String msg = String.format("max=%s gainResources=%s amtToGain=%s curAmt=%s",
				maxResource, gainResources, expectedOilChange, mockedUser.getOil());
		Assert.assertEquals(msg, expectedOilChange, resourceChange);
	}

	////////////////USER IS OVER MAX
	@Test
	public void testOverMaxCashGainedCash() {
		User mockedUser = mock(User.class);
		when(mockedUser.getCash()).thenReturn(60);

		int maxResource = 50;
		int minResource = 10;
		int cashChange = 50;
		boolean gainResources = true;
		String cash = MiscMethods.cash;

		int resourceChange = resourceUtil.calculateMaxResourceChange(
				mockedUser, maxResource, minResource, cashChange, gainResources, cash);

		int expectedCashChange = maxResource - mockedUser.getCash();

		String msg = String.format("max=%s gainResources=%s amtToGain=%s curAmt=%s",
				maxResource, gainResources, expectedCashChange, mockedUser.getOil());
		Assert.assertEquals(msg, expectedCashChange, resourceChange);
	}

	@Test
	public void testOverMaxCashLostCash() {
		User mockedUser = mock(User.class);
		when(mockedUser.getCash()).thenReturn(60);

		int maxResource = 50;
		int minResource = 0;
		int cashChange = 50;
		boolean gainResources = false;
		String cash = MiscMethods.cash;

		int resourceChange = resourceUtil.calculateMaxResourceChange(
				mockedUser, maxResource, minResource, cashChange, gainResources, cash);

		int expectedCashChange = -mockedUser.getCash();
		String msg = String.format("max=%s gainResources=%s amtToGain=%s curAmt=%s",
				maxResource, gainResources, expectedCashChange, mockedUser.getOil());
		Assert.assertEquals(msg, expectedCashChange, resourceChange);
	}

	@Test
	public void testOverMaxCashCappedAtMinLostCash() {
		User mockedUser = mock(User.class);
		when(mockedUser.getCash()).thenReturn(60);

		int maxResource = 50;
		int minResource = 10;
		int cashChange = 60;
		boolean gainResources = false;
		String cash = MiscMethods.cash;

		int resourceChange = resourceUtil.calculateMaxResourceChange(
				mockedUser, maxResource, minResource, cashChange, gainResources, cash);

		int expectedCashChange = -(mockedUser.getCash() - minResource);
		String msg = String.format("max=%s gainResources=%s amtToGain=%s curAmt=%s",
				maxResource, gainResources, expectedCashChange, mockedUser.getOil());
		Assert.assertEquals(msg, expectedCashChange, resourceChange);
	}

	////////////////USER IS BELOW MIN
	@Test
	public void testBelowMinCashGainedCash() {
		User mockedUser = mock(User.class);
		when(mockedUser.getCash()).thenReturn(-10);

		int maxResource = 50;
		int minResource = 0;
		int cashChange = 50;
		boolean gainResources = true;
		String cash = MiscMethods.cash;

		int resourceChange = resourceUtil.calculateMaxResourceChange(
				mockedUser, maxResource, minResource, cashChange, gainResources, cash);

		int expectedCashChange = cashChange;
		String msg = String.format("max=%s gainResources=%s amtToGain=%s curAmt=%s",
				maxResource, gainResources, expectedCashChange, mockedUser.getOil());
		Assert.assertEquals(msg, expectedCashChange, resourceChange);
	}

	@Test
	public void testBelowMinCashGainedCashCapped() {
		User mockedUser = mock(User.class);
		when(mockedUser.getCash()).thenReturn(-10);

		int maxResource = 50;
		int minResource = 0;
		int cashChange = 100;
		boolean gainResources = true;
		String cash = MiscMethods.cash;

		int resourceChange = resourceUtil.calculateMaxResourceChange(
				mockedUser, maxResource, minResource, cashChange, gainResources, cash);

		int expectedCashChange = maxResource + -mockedUser.getCash();
		String msg = String.format("max=%s gainResources=%s amtToGain=%s curAmt=%s",
				maxResource, gainResources, expectedCashChange, mockedUser.getOil());
		Assert.assertEquals(msg, expectedCashChange, resourceChange);
	}

	@Test
	public void testBelowMinCashLostCash() {
		User mockedUser = mock(User.class);
		when(mockedUser.getCash()).thenReturn(-10);

		int maxResource = 50;
		int minResource = 0;
		int cashChange = 50;
		boolean gainResources = false;
		String cash = MiscMethods.cash;

		int resourceChange = resourceUtil.calculateMaxResourceChange(
				mockedUser, maxResource, minResource, cashChange, gainResources, cash);

		int expectedCashChange = 0;

		String msg = String.format("max=%s gainResources=%s amtToGain=%s curAmt=%s",
				maxResource, gainResources, expectedCashChange, mockedUser.getOil());
		Assert.assertEquals(msg, expectedCashChange, resourceChange);
	}
}
