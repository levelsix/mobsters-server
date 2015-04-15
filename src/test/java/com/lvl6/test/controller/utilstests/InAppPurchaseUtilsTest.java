package com.lvl6.test.controller.utilstests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import com.lvl6.info.SalesDisplayItem;
import com.lvl6.info.SalesItem;
import com.lvl6.retrieveutils.IAPHistoryRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.SalesDisplayItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.SalesItemRetrieveUtils;
import com.lvl6.server.controller.utils.InAppPurchaseUtils;

public class InAppPurchaseUtilsTest {

	private static InAppPurchaseUtils inAppPurchaseUtils;


	@BeforeClass
	public static void setUp() {
		inAppPurchaseUtils = new InAppPurchaseUtils();
	}

	@Test
	public void testCheckIfDuplicateReceipt() {

		JSONObject mockedReceiptFromApple1 = mock(JSONObject.class);
		JSONObject mockedReceiptFromApple2 = mock(JSONObject.class);
		try {
			when(mockedReceiptFromApple1.getString(any(String.class))).thenReturn("123");
			when(mockedReceiptFromApple2.getString(any(String.class))).thenReturn("456");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		IAPHistoryRetrieveUtils mockedIAPHistoryRetrieveUtil = mock(IAPHistoryRetrieveUtils.class);
		long uniqueLong = 123;
		long duplicateLong = 456;
		when(mockedIAPHistoryRetrieveUtil.checkIfDuplicateTransaction(uniqueLong)).thenReturn(false);
		when(mockedIAPHistoryRetrieveUtil.checkIfDuplicateTransaction(duplicateLong)).thenReturn(true);

		assertFalse(inAppPurchaseUtils.checkIfDuplicateReceipt(mockedReceiptFromApple1,
				mockedIAPHistoryRetrieveUtil));

		assertTrue(inAppPurchaseUtils.checkIfDuplicateReceipt(mockedReceiptFromApple2,
				mockedIAPHistoryRetrieveUtil));
	}

	@Test
	public void testCreateSalesPackageProto() {
		SalesItemRetrieveUtils mockedSalesItemRetrieveUtils = mock(SalesItemRetrieveUtils.class);
		SalesDisplayItemRetrieveUtils mockedSalesDisplayItemRetrieveUtils = mock(SalesDisplayItemRetrieveUtils.class);

		Map<Integer, List<SalesItem>> salesItemReturn = new HashMap<Integer, List<SalesItem>>();
		salesItemReturn.put(1, new ArrayList<SalesItem>());
		SalesItem si1 = new SalesItem();
		si1.setId(1);
		si1.setGemReward(0);
		si1.setItemId(1);
		si1.setItemQuantity(10);
		si1.setMonsterId(0);
		si1.setMonsterLevel(0);
		si1.setMonsterQuantity(0);
		si1.setSalesPackageId(1);

		SalesItem si2 = new SalesItem();
		si2.setId(2);
		si2.setGemReward(0);
		si2.setItemId(0);
		si2.setItemQuantity(0);
		si2.setMonsterId(1);
		si2.setMonsterLevel(10);
		si2.setMonsterQuantity(1);
		si2.setSalesPackageId(1);

		SalesItem si3 = new SalesItem();
		si3.setId(3);
		si3.setGemReward(100);
		si3.setItemId(0);
		si3.setItemQuantity(0);
		si3.setMonsterId(0);
		si3.setMonsterLevel(0);
		si3.setMonsterQuantity(0);
		si3.setSalesPackageId(1);

		salesItemReturn.get(1).add(si1);
		salesItemReturn.get(1).add(si2);
		salesItemReturn.get(1).add(si3);

		when(mockedSalesItemRetrieveUtils.getSalesItemIdsToSalesItemsForSalesPackIds()).thenReturn(salesItemReturn);

		Map<Integer, Map<Integer, SalesDisplayItem>> salesPackageIdToDisplayIdsToDisplayItems =
				new HashMap<Integer, Map<Integer, SalesDisplayItem>>();





//		when(mockedSalesDisplayItemRetrieveUtils.getSalesDisplayItemIdsToSalesDisplayItemsForSalesPackIds()).thenReturn(value);



	}



}
