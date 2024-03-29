/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.LockBoxEventForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.LockBoxEventForUserPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.records.LockBoxEventForUserRecord;

import java.sql.Timestamp;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.Record2;
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
public class LockBoxEventForUserDao extends DAOImpl<LockBoxEventForUserRecord, LockBoxEventForUserPojo, Record2<Integer, String>> {

	/**
	 * Create a new LockBoxEventForUserDao without any configuration
	 */
	public LockBoxEventForUserDao() {
		super(LockBoxEventForUser.LOCK_BOX_EVENT_FOR_USER, LockBoxEventForUserPojo.class);
	}

	/**
	 * Create a new LockBoxEventForUserDao with an attached configuration
	 */
	public LockBoxEventForUserDao(Configuration configuration) {
		super(LockBoxEventForUser.LOCK_BOX_EVENT_FOR_USER, LockBoxEventForUserPojo.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Record2<Integer, String> getId(LockBoxEventForUserPojo object) {
		return compositeKeyRecord(object.getLockBoxEventId(), object.getUserId());
	}

	/**
	 * Fetch records that have <code>lock_box_event_id IN (values)</code>
	 */
	public List<LockBoxEventForUserPojo> fetchByLockBoxEventId(Integer... values) {
		return fetch(LockBoxEventForUser.LOCK_BOX_EVENT_FOR_USER.LOCK_BOX_EVENT_ID, values);
	}

	/**
	 * Fetch records that have <code>user_id IN (values)</code>
	 */
	public List<LockBoxEventForUserPojo> fetchByUserId(String... values) {
		return fetch(LockBoxEventForUser.LOCK_BOX_EVENT_FOR_USER.USER_ID, values);
	}

	/**
	 * Fetch records that have <code>num_boxes IN (values)</code>
	 */
	public List<LockBoxEventForUserPojo> fetchByNumBoxes(Integer... values) {
		return fetch(LockBoxEventForUser.LOCK_BOX_EVENT_FOR_USER.NUM_BOXES, values);
	}

	/**
	 * Fetch records that have <code>last_opening_time IN (values)</code>
	 */
	public List<LockBoxEventForUserPojo> fetchByLastOpeningTime(Timestamp... values) {
		return fetch(LockBoxEventForUser.LOCK_BOX_EVENT_FOR_USER.LAST_OPENING_TIME, values);
	}

	/**
	 * Fetch records that have <code>num_times_completed IN (values)</code>
	 */
	public List<LockBoxEventForUserPojo> fetchByNumTimesCompleted(Integer... values) {
		return fetch(LockBoxEventForUser.LOCK_BOX_EVENT_FOR_USER.NUM_TIMES_COMPLETED, values);
	}

	/**
	 * Fetch records that have <code>has_been_redeemed IN (values)</code>
	 */
	public List<LockBoxEventForUserPojo> fetchByHasBeenRedeemed(Byte... values) {
		return fetch(LockBoxEventForUser.LOCK_BOX_EVENT_FOR_USER.HAS_BEEN_REDEEMED, values);
	}
}
