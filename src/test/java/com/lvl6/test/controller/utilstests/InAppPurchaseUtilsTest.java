package com.lvl6.test.controller.utilstests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import com.lvl6.retrieveutils.IAPHistoryRetrieveUtils;
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



	}



}
