/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.GiftForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.records.GiftForUserRecord;

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
public class GiftForUserDao extends DAOImpl<GiftForUserRecord, com.lvl6.mobsters.db.jooq.generated.tables.pojos.GiftForUser, String> {

	/**
	 * Create a new GiftForUserDao without any configuration
	 */
	public GiftForUserDao() {
		super(GiftForUser.GIFT_FOR_USER, com.lvl6.mobsters.db.jooq.generated.tables.pojos.GiftForUser.class);
	}

	/**
	 * Create a new GiftForUserDao with an attached configuration
	 */
	public GiftForUserDao(Configuration configuration) {
		super(GiftForUser.GIFT_FOR_USER, com.lvl6.mobsters.db.jooq.generated.tables.pojos.GiftForUser.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getId(com.lvl6.mobsters.db.jooq.generated.tables.pojos.GiftForUser object) {
		return object.getId();
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.GiftForUser> fetchById(String... values) {
		return fetch(GiftForUser.GIFT_FOR_USER.ID, values);
	}

	/**
	 * Fetch a unique record that has <code>id = value</code>
	 */
	public com.lvl6.mobsters.db.jooq.generated.tables.pojos.GiftForUser fetchOneById(String value) {
		return fetchOne(GiftForUser.GIFT_FOR_USER.ID, value);
	}

	/**
	 * Fetch records that have <code>gifter_user_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.GiftForUser> fetchByGifterUserId(String... values) {
		return fetch(GiftForUser.GIFT_FOR_USER.GIFTER_USER_ID, values);
	}

	/**
	 * Fetch records that have <code>receiver_user_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.GiftForUser> fetchByReceiverUserId(String... values) {
		return fetch(GiftForUser.GIFT_FOR_USER.RECEIVER_USER_ID, values);
	}

	/**
	 * Fetch records that have <code>gift_type IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.GiftForUser> fetchByGiftType(String... values) {
		return fetch(GiftForUser.GIFT_FOR_USER.GIFT_TYPE, values);
	}

	/**
	 * Fetch records that have <code>static_data_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.GiftForUser> fetchByStaticDataId(Integer... values) {
		return fetch(GiftForUser.GIFT_FOR_USER.STATIC_DATA_ID, values);
	}

	/**
	 * Fetch records that have <code>time_of_entry IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.GiftForUser> fetchByTimeOfEntry(Timestamp... values) {
		return fetch(GiftForUser.GIFT_FOR_USER.TIME_OF_ENTRY, values);
	}

	/**
	 * Fetch records that have <code>reward_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.GiftForUser> fetchByRewardId(Integer... values) {
		return fetch(GiftForUser.GIFT_FOR_USER.REWARD_ID, values);
	}

	/**
	 * Fetch records that have <code>collected IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.GiftForUser> fetchByCollected(Boolean... values) {
		return fetch(GiftForUser.GIFT_FOR_USER.COLLECTED, values);
	}

	/**
	 * Fetch records that have <code>minutes_till_expiration IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.GiftForUser> fetchByMinutesTillExpiration(Integer... values) {
		return fetch(GiftForUser.GIFT_FOR_USER.MINUTES_TILL_EXPIRATION, values);
	}

	/**
	 * Fetch records that have <code>reason_for_gift IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.GiftForUser> fetchByReasonForGift(String... values) {
		return fetch(GiftForUser.GIFT_FOR_USER.REASON_FOR_GIFT, values);
	}
}
