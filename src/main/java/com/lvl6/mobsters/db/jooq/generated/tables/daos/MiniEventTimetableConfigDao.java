/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.MiniEventTimetableConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.records.MiniEventTimetableConfigRecord;

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
public class MiniEventTimetableConfigDao extends DAOImpl<MiniEventTimetableConfigRecord, com.lvl6.mobsters.db.jooq.generated.tables.pojos.MiniEventTimetableConfig, Integer> {

	/**
	 * Create a new MiniEventTimetableConfigDao without any configuration
	 */
	public MiniEventTimetableConfigDao() {
		super(MiniEventTimetableConfig.MINI_EVENT_TIMETABLE_CONFIG, com.lvl6.mobsters.db.jooq.generated.tables.pojos.MiniEventTimetableConfig.class);
	}

	/**
	 * Create a new MiniEventTimetableConfigDao with an attached configuration
	 */
	public MiniEventTimetableConfigDao(Configuration configuration) {
		super(MiniEventTimetableConfig.MINI_EVENT_TIMETABLE_CONFIG, com.lvl6.mobsters.db.jooq.generated.tables.pojos.MiniEventTimetableConfig.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer getId(com.lvl6.mobsters.db.jooq.generated.tables.pojos.MiniEventTimetableConfig object) {
		return object.getId();
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.MiniEventTimetableConfig> fetchById(Integer... values) {
		return fetch(MiniEventTimetableConfig.MINI_EVENT_TIMETABLE_CONFIG.ID, values);
	}

	/**
	 * Fetch a unique record that has <code>id = value</code>
	 */
	public com.lvl6.mobsters.db.jooq.generated.tables.pojos.MiniEventTimetableConfig fetchOneById(Integer value) {
		return fetchOne(MiniEventTimetableConfig.MINI_EVENT_TIMETABLE_CONFIG.ID, value);
	}

	/**
	 * Fetch records that have <code>mini_event_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.MiniEventTimetableConfig> fetchByMiniEventId(Integer... values) {
		return fetch(MiniEventTimetableConfig.MINI_EVENT_TIMETABLE_CONFIG.MINI_EVENT_ID, values);
	}

	/**
	 * Fetch records that have <code>start_time IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.MiniEventTimetableConfig> fetchByStartTime(Timestamp... values) {
		return fetch(MiniEventTimetableConfig.MINI_EVENT_TIMETABLE_CONFIG.START_TIME, values);
	}

	/**
	 * Fetch records that have <code>end_time IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.MiniEventTimetableConfig> fetchByEndTime(Timestamp... values) {
		return fetch(MiniEventTimetableConfig.MINI_EVENT_TIMETABLE_CONFIG.END_TIME, values);
	}
}
