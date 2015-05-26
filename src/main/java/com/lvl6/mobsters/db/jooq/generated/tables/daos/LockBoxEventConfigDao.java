/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.LockBoxEventConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.records.LockBoxEventConfigRecord;

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
public class LockBoxEventConfigDao extends DAOImpl<LockBoxEventConfigRecord, com.lvl6.mobsters.db.jooq.generated.tables.pojos.LockBoxEventConfig, Integer> {

	/**
	 * Create a new LockBoxEventConfigDao without any configuration
	 */
	public LockBoxEventConfigDao() {
		super(LockBoxEventConfig.LOCK_BOX_EVENT_CONFIG, com.lvl6.mobsters.db.jooq.generated.tables.pojos.LockBoxEventConfig.class);
	}

	/**
	 * Create a new LockBoxEventConfigDao with an attached configuration
	 */
	public LockBoxEventConfigDao(Configuration configuration) {
		super(LockBoxEventConfig.LOCK_BOX_EVENT_CONFIG, com.lvl6.mobsters.db.jooq.generated.tables.pojos.LockBoxEventConfig.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer getId(com.lvl6.mobsters.db.jooq.generated.tables.pojos.LockBoxEventConfig object) {
		return object.getId();
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.LockBoxEventConfig> fetchById(Integer... values) {
		return fetch(LockBoxEventConfig.LOCK_BOX_EVENT_CONFIG.ID, values);
	}

	/**
	 * Fetch a unique record that has <code>id = value</code>
	 */
	public com.lvl6.mobsters.db.jooq.generated.tables.pojos.LockBoxEventConfig fetchOneById(Integer value) {
		return fetchOne(LockBoxEventConfig.LOCK_BOX_EVENT_CONFIG.ID, value);
	}

	/**
	 * Fetch records that have <code>start_time IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.LockBoxEventConfig> fetchByStartTime(Timestamp... values) {
		return fetch(LockBoxEventConfig.LOCK_BOX_EVENT_CONFIG.START_TIME, values);
	}

	/**
	 * Fetch records that have <code>end_time IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.LockBoxEventConfig> fetchByEndTime(Timestamp... values) {
		return fetch(LockBoxEventConfig.LOCK_BOX_EVENT_CONFIG.END_TIME, values);
	}

	/**
	 * Fetch records that have <code>lock_box_image_name IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.LockBoxEventConfig> fetchByLockBoxImageName(String... values) {
		return fetch(LockBoxEventConfig.LOCK_BOX_EVENT_CONFIG.LOCK_BOX_IMAGE_NAME, values);
	}

	/**
	 * Fetch records that have <code>event_name IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.LockBoxEventConfig> fetchByEventName(String... values) {
		return fetch(LockBoxEventConfig.LOCK_BOX_EVENT_CONFIG.EVENT_NAME, values);
	}

	/**
	 * Fetch records that have <code>prize_equip_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.LockBoxEventConfig> fetchByPrizeEquipId(Integer... values) {
		return fetch(LockBoxEventConfig.LOCK_BOX_EVENT_CONFIG.PRIZE_EQUIP_ID, values);
	}

	/**
	 * Fetch records that have <code>description_string IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.LockBoxEventConfig> fetchByDescriptionString(String... values) {
		return fetch(LockBoxEventConfig.LOCK_BOX_EVENT_CONFIG.DESCRIPTION_STRING, values);
	}

	/**
	 * Fetch records that have <code>description_image_name IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.LockBoxEventConfig> fetchByDescriptionImageName(String... values) {
		return fetch(LockBoxEventConfig.LOCK_BOX_EVENT_CONFIG.DESCRIPTION_IMAGE_NAME, values);
	}

	/**
	 * Fetch records that have <code>tag_image_name IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.LockBoxEventConfig> fetchByTagImageName(String... values) {
		return fetch(LockBoxEventConfig.LOCK_BOX_EVENT_CONFIG.TAG_IMAGE_NAME, values);
	}
}
