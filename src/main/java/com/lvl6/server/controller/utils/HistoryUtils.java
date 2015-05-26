package com.lvl6.server.controller.utils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.db.jooq.generated.tables.daos.IapHistoryDao;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.UserCurrencyHistoryDao;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.User;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.UserCurrencyHistory;
import com.lvl6.properties.IAPValues;

@Component
public class HistoryUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());
	
	
	/**
	 *  Inserting into IAPHistory using same parameters as old insertUtils method
	 */
	public void insertIAPHistoryElem(JSONObject appleReceipt, int gemChange,
			User user, double cashCost, String salesUuid, IapHistoryDao iapDao) {
		com.lvl6.mobsters.db.jooq.generated.tables.pojos.IapHistory iapHistory = 
				new com.lvl6.mobsters.db.jooq.generated.tables.pojos.IapHistory();
		
		String id = randomUUID();
		iapHistory.setId(id);
		iapHistory.setUserId(user.getId());
		
		try {
			iapHistory.setTransactionId(Long.parseLong(appleReceipt.getString(IAPValues.TRANSACTION_ID)));
			iapHistory.setPurchaseDate(new Timestamp(appleReceipt
					.getLong(IAPValues.PURCHASE_DATE_MS)));
			iapHistory.setProductId(appleReceipt.getString(IAPValues.PRODUCT_ID));
			iapHistory.setQuantity(Integer.parseInt(appleReceipt.getString(IAPValues.QUANTITY)));
			iapHistory.setBid(appleReceipt.getString(IAPValues.BID));
			iapHistory.setBvrs(appleReceipt.getString(IAPValues.BVRS));
			if (appleReceipt.has(IAPValues.APP_ITEM_ID)) {
				iapHistory.setAppItemId(appleReceipt.getString(IAPValues.APP_ITEM_ID));
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		iapHistory.setPremiumcurPurchased(gemChange);
		iapHistory.setCashSpent(cashCost);
		iapHistory.setUdid(user.getUdid());
		iapHistory.setFbId(user.getFacebookId());
		
		if(salesUuid != null) {
			iapHistory.setSalesUuid(salesUuid);
		}
		iapDao.insert(iapHistory);
	}
	
	private String randomUUID() {
		return UUID.randomUUID().toString();
	}
	
	public void insertUserCurrencyHistoryForGacha(String userId, Date now, int currChange,
			int currBeforeChange, int currAfterChange, String reason, String detail,
			UserCurrencyHistoryDao uchDao, String resourceType) {
		UserCurrencyHistory uch = new UserCurrencyHistory();
		uch.setId(randomUUID());
		uch.setUserId(userId);
		uch.setDate(new Timestamp(now.getTime()));
		uch.setResourceType(resourceType);
		uch.setCurrencyChange(currChange);
		uch.setCurrencyBeforeChange(currBeforeChange);
		uch.setCurrencyAfterChange(currAfterChange);
		uch.setReasonForChange(reason);
		uch.setDetails(detail);
		uchDao.insert(uch);
	}

	
	
	
	
}
