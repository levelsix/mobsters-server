/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.LoadTestingEvents;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.LoadTestingEventsPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.records.LoadTestingEventsRecord;

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
public class LoadTestingEventsDao extends DAOImpl<LoadTestingEventsRecord, LoadTestingEventsPojo, Integer> {

	/**
	 * Create a new LoadTestingEventsDao without any configuration
	 */
	public LoadTestingEventsDao() {
		super(LoadTestingEvents.LOAD_TESTING_EVENTS, LoadTestingEventsPojo.class);
	}

	/**
	 * Create a new LoadTestingEventsDao with an attached configuration
	 */
	public LoadTestingEventsDao(Configuration configuration) {
		super(LoadTestingEvents.LOAD_TESTING_EVENTS, LoadTestingEventsPojo.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer getId(LoadTestingEventsPojo object) {
		return object.getId();
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<LoadTestingEventsPojo> fetchById(Integer... values) {
		return fetch(LoadTestingEvents.LOAD_TESTING_EVENTS.ID, values);
	}

	/**
	 * Fetch a unique record that has <code>id = value</code>
	 */
	public LoadTestingEventsPojo fetchOneById(Integer value) {
		return fetchOne(LoadTestingEvents.LOAD_TESTING_EVENTS.ID, value);
	}

	/**
	 * Fetch records that have <code>user_id IN (values)</code>
	 */
	public List<LoadTestingEventsPojo> fetchByUserId(String... values) {
		return fetch(LoadTestingEvents.LOAD_TESTING_EVENTS.USER_ID, values);
	}

	/**
	 * Fetch records that have <code>log_time IN (values)</code>
	 */
	public List<LoadTestingEventsPojo> fetchByLogTime(Timestamp... values) {
		return fetch(LoadTestingEvents.LOAD_TESTING_EVENTS.LOG_TIME, values);
	}

	/**
	 * Fetch records that have <code>event_type IN (values)</code>
	 */
	public List<LoadTestingEventsPojo> fetchByEventType(Integer... values) {
		return fetch(LoadTestingEvents.LOAD_TESTING_EVENTS.EVENT_TYPE, values);
	}

	/**
	 * Fetch records that have <code>event_bytes IN (values)</code>
	 */
	public List<LoadTestingEventsPojo> fetchByEventBytes(byte[]... values) {
		return fetch(LoadTestingEvents.LOAD_TESTING_EVENTS.EVENT_BYTES, values);
	}
}
