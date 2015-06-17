package com.lvl6.server.controller.utils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.db.jooq.generated.tables.daos.IapHistoryDao;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.UserCurrencyHistoryDao;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.IapHistoryPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.UserPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.UserCurrencyHistoryPojo;
import com.lvl6.properties.IAPValues;

@Component
public class HistoryUtils {

	private static final Logger log = LoggerFactory
			.getLogger(HistoryUtils.class);

	private String randomUUID() {
		return UUID.randomUUID().toString();
	}

	/**
	 *  Inserting into IAPHistory using same parameters as old insertUtils method
	 */
	public void insertIAPHistoryElem(JSONObject appleReceipt, int gemChange,
			UserPojo user, double cashCost, String salesUuid, IapHistoryDao iapDao) {
		IapHistoryPojo iapHistory = new IapHistoryPojo();

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
			log.error("NumberFormatException", e);
		} catch (JSONException e) {
			log.error("JSONException", e);
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

	public void insertUserCurrencyHistory(String userId, List<UserCurrencyHistoryPojo> uchList,
			Date now, String reasonForChange, String details, UserCurrencyHistoryDao userCurrencyHistoryDao) {
		for(UserCurrencyHistoryPojo uch : uchList) {
			uch.setId(randomUUID());
			uch.setUserId(userId);
			uch.setDate(new Timestamp(now.getTime()));
			uch.setReasonForChange(reasonForChange);
			uch.setDetails(details);
			userCurrencyHistoryDao.insert(uch);
		}
	}

	public UserCurrencyHistoryPojo createUserCurrencyHistory(String userId, Date now,
			String resourceType, int currChange,
			int currBeforeChange, int currAfterChange, String reason, String detail)
	{
		UserCurrencyHistoryPojo uch = new UserCurrencyHistoryPojo();
		uch.setId(randomUUID());
		uch.setUserId(userId);
		uch.setDate(new Timestamp(now.getTime()));
		uch.setResourceType(resourceType);
		uch.setCurrencyChange(currChange);
		uch.setCurrencyBeforeChange(currBeforeChange);
		uch.setCurrencyAfterChange(currAfterChange);
		uch.setReasonForChange(reason);
		uch.setDetails(detail);

		return uch;
	}
}
