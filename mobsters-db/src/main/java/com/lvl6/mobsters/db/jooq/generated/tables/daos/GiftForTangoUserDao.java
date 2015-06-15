/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.GiftForTangoUser;
import com.lvl6.mobsters.db.jooq.generated.tables.records.GiftForTangoUserRecord;

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
public class GiftForTangoUserDao extends DAOImpl<GiftForTangoUserRecord, com.lvl6.mobsters.db.jooq.generated.tables.pojos.GiftForTangoUser, String> {

	/**
	 * Create a new GiftForTangoUserDao without any configuration
	 */
	public GiftForTangoUserDao() {
		super(GiftForTangoUser.GIFT_FOR_TANGO_USER, com.lvl6.mobsters.db.jooq.generated.tables.pojos.GiftForTangoUser.class);
	}

	/**
	 * Create a new GiftForTangoUserDao with an attached configuration
	 */
	public GiftForTangoUserDao(Configuration configuration) {
		super(GiftForTangoUser.GIFT_FOR_TANGO_USER, com.lvl6.mobsters.db.jooq.generated.tables.pojos.GiftForTangoUser.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getId(com.lvl6.mobsters.db.jooq.generated.tables.pojos.GiftForTangoUser object) {
		return object.getGiftForUserId();
	}

	/**
	 * Fetch records that have <code>gift_for_user_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.GiftForTangoUser> fetchByGiftForUserId(String... values) {
		return fetch(GiftForTangoUser.GIFT_FOR_TANGO_USER.GIFT_FOR_USER_ID, values);
	}

	/**
	 * Fetch a unique record that has <code>gift_for_user_id = value</code>
	 */
	public com.lvl6.mobsters.db.jooq.generated.tables.pojos.GiftForTangoUser fetchOneByGiftForUserId(String value) {
		return fetchOne(GiftForTangoUser.GIFT_FOR_TANGO_USER.GIFT_FOR_USER_ID, value);
	}

	/**
	 * Fetch records that have <code>gifter_user_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.GiftForTangoUser> fetchByGifterUserId(String... values) {
		return fetch(GiftForTangoUser.GIFT_FOR_TANGO_USER.GIFTER_USER_ID, values);
	}

	/**
	 * Fetch records that have <code>gifter_tango_user_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.GiftForTangoUser> fetchByGifterTangoUserId(String... values) {
		return fetch(GiftForTangoUser.GIFT_FOR_TANGO_USER.GIFTER_TANGO_USER_ID, values);
	}
}