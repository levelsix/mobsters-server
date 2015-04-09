package com.lvl6.server.controller.utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.lvl6.properties.IAPValues;
import com.lvl6.retrieveutils.IAPHistoryRetrieveUtils;

@Component
public class InAppPurchaseUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	public static boolean checkIfDuplicateReceipt(JSONObject receiptFromApple,
			IAPHistoryRetrieveUtils iapHistoryRetrieveUtil) { //returns true if duplicate
		String transactionId = null;
		try {
			transactionId = receiptFromApple
					.getString(IAPValues.TRANSACTION_ID);
		} catch (JSONException e) {
			log.error(String.format("error verifying InAppPurchase request. "
					+ "receiptFromApple={}", receiptFromApple), e);
			e.printStackTrace();
		}

		long transactionIdLong = Long.parseLong(transactionId);
		if (iapHistoryRetrieveUtil
				.checkIfDuplicateTransaction(transactionIdLong)) {
			return true;
		}
		else return false;
	}


}
