/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.SecretGiftForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.SecretGiftForUserPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.records.SecretGiftForUserRecord;

import java.sql.Timestamp;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.Record3;
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
public class SecretGiftForUserDao extends DAOImpl<SecretGiftForUserRecord, SecretGiftForUserPojo, Record3<String, String, Integer>> {

	/**
	 * Create a new SecretGiftForUserDao without any configuration
	 */
	public SecretGiftForUserDao() {
		super(SecretGiftForUser.SECRET_GIFT_FOR_USER, SecretGiftForUserPojo.class);
	}

	/**
	 * Create a new SecretGiftForUserDao with an attached configuration
	 */
	public SecretGiftForUserDao(Configuration configuration) {
		super(SecretGiftForUser.SECRET_GIFT_FOR_USER, SecretGiftForUserPojo.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Record3<String, String, Integer> getId(SecretGiftForUserPojo object) {
		return compositeKeyRecord(object.getId(), object.getUserId(), object.getRewardId());
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<SecretGiftForUserPojo> fetchById(String... values) {
		return fetch(SecretGiftForUser.SECRET_GIFT_FOR_USER.ID, values);
	}

	/**
	 * Fetch records that have <code>user_id IN (values)</code>
	 */
	public List<SecretGiftForUserPojo> fetchByUserId(String... values) {
		return fetch(SecretGiftForUser.SECRET_GIFT_FOR_USER.USER_ID, values);
	}

	/**
	 * Fetch records that have <code>reward_id IN (values)</code>
	 */
	public List<SecretGiftForUserPojo> fetchByRewardId(Integer... values) {
		return fetch(SecretGiftForUser.SECRET_GIFT_FOR_USER.REWARD_ID, values);
	}

	/**
	 * Fetch records that have <code>secs_until_collection IN (values)</code>
	 */
	public List<SecretGiftForUserPojo> fetchBySecsUntilCollection(Integer... values) {
		return fetch(SecretGiftForUser.SECRET_GIFT_FOR_USER.SECS_UNTIL_COLLECTION, values);
	}

	/**
	 * Fetch records that have <code>create_time IN (values)</code>
	 */
	public List<SecretGiftForUserPojo> fetchByCreateTime(Timestamp... values) {
		return fetch(SecretGiftForUser.SECRET_GIFT_FOR_USER.CREATE_TIME, values);
	}
}
