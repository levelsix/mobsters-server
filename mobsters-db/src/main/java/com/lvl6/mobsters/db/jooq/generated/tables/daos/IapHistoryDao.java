/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.IapHistory;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.IapHistoryPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.records.IapHistoryRecord;

import java.sql.Timestamp;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.1"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class IapHistoryDao extends DAOImpl<IapHistoryRecord, IapHistoryPojo, String> {

	/**
	 * Create a new IapHistoryDao without any configuration
	 */
	public IapHistoryDao() {
		super(IapHistory.IAP_HISTORY, IapHistoryPojo.class);
	}

	/**
	 * Create a new IapHistoryDao with an attached configuration
	 */
	public IapHistoryDao(Configuration configuration) {
		super(IapHistory.IAP_HISTORY, IapHistoryPojo.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getId(IapHistoryPojo object) {
		return object.getId();
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<IapHistoryPojo> fetchById(String... values) {
		return fetch(IapHistory.IAP_HISTORY.ID, values);
	}

	/**
	 * Fetch a unique record that has <code>id = value</code>
	 */
	public IapHistoryPojo fetchOneById(String value) {
		return fetchOne(IapHistory.IAP_HISTORY.ID, value);
	}

	/**
	 * Fetch records that have <code>user_id IN (values)</code>
	 */
	public List<IapHistoryPojo> fetchByUserId(String... values) {
		return fetch(IapHistory.IAP_HISTORY.USER_ID, values);
	}

	/**
	 * Fetch records that have <code>transaction_id IN (values)</code>
	 */
	public List<IapHistoryPojo> fetchByTransactionId(Long... values) {
		return fetch(IapHistory.IAP_HISTORY.TRANSACTION_ID, values);
	}

	/**
	 * Fetch a unique record that has <code>transaction_id = value</code>
	 */
	public IapHistoryPojo fetchOneByTransactionId(Long value) {
		return fetchOne(IapHistory.IAP_HISTORY.TRANSACTION_ID, value);
	}

	/**
	 * Fetch records that have <code>purchase_date IN (values)</code>
	 */
	public List<IapHistoryPojo> fetchByPurchaseDate(Timestamp... values) {
		return fetch(IapHistory.IAP_HISTORY.PURCHASE_DATE, values);
	}

	/**
	 * Fetch records that have <code>premiumcur_purchased IN (values)</code>
	 */
	public List<IapHistoryPojo> fetchByPremiumcurPurchased(Integer... values) {
		return fetch(IapHistory.IAP_HISTORY.PREMIUMCUR_PURCHASED, values);
	}

	/**
	 * Fetch records that have <code>cash_spent IN (values)</code>
	 */
	public List<IapHistoryPojo> fetchByCashSpent(Double... values) {
		return fetch(IapHistory.IAP_HISTORY.CASH_SPENT, values);
	}

	/**
	 * Fetch records that have <code>udid IN (values)</code>
	 */
	public List<IapHistoryPojo> fetchByUdid(String... values) {
		return fetch(IapHistory.IAP_HISTORY.UDID, values);
	}

	/**
	 * Fetch records that have <code>fb_id IN (values)</code>
	 */
	public List<IapHistoryPojo> fetchByFbId(String... values) {
		return fetch(IapHistory.IAP_HISTORY.FB_ID, values);
	}

	/**
	 * Fetch records that have <code>product_id IN (values)</code>
	 */
	public List<IapHistoryPojo> fetchByProductId(String... values) {
		return fetch(IapHistory.IAP_HISTORY.PRODUCT_ID, values);
	}

	/**
	 * Fetch records that have <code>quantity IN (values)</code>
	 */
	public List<IapHistoryPojo> fetchByQuantity(Integer... values) {
		return fetch(IapHistory.IAP_HISTORY.QUANTITY, values);
	}

	/**
	 * Fetch records that have <code>bid IN (values)</code>
	 */
	public List<IapHistoryPojo> fetchByBid(String... values) {
		return fetch(IapHistory.IAP_HISTORY.BID, values);
	}

	/**
	 * Fetch records that have <code>bvrs IN (values)</code>
	 */
	public List<IapHistoryPojo> fetchByBvrs(String... values) {
		return fetch(IapHistory.IAP_HISTORY.BVRS, values);
	}

	/**
	 * Fetch records that have <code>app_item_id IN (values)</code>
	 */
	public List<IapHistoryPojo> fetchByAppItemId(String... values) {
		return fetch(IapHistory.IAP_HISTORY.APP_ITEM_ID, values);
	}

	/**
	 * Fetch records that have <code>regcur_purchased IN (values)</code>
	 */
	public List<IapHistoryPojo> fetchByRegcurPurchased(Integer... values) {
		return fetch(IapHistory.IAP_HISTORY.REGCUR_PURCHASED, values);
	}

	/**
	 * Fetch records that have <code>sales_uuid IN (values)</code>
	 */
	public List<IapHistoryPojo> fetchBySalesUuid(String... values) {
		return fetch(IapHistory.IAP_HISTORY.SALES_UUID, values);
	}
}