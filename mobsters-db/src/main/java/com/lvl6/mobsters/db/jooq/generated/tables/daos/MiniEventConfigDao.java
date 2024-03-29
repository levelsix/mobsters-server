/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.MiniEventConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.MiniEventConfigPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.records.MiniEventConfigRecord;

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
public class MiniEventConfigDao extends DAOImpl<MiniEventConfigRecord, MiniEventConfigPojo, Integer> {

	/**
	 * Create a new MiniEventConfigDao without any configuration
	 */
	public MiniEventConfigDao() {
		super(MiniEventConfig.MINI_EVENT_CONFIG, MiniEventConfigPojo.class);
	}

	/**
	 * Create a new MiniEventConfigDao with an attached configuration
	 */
	public MiniEventConfigDao(Configuration configuration) {
		super(MiniEventConfig.MINI_EVENT_CONFIG, MiniEventConfigPojo.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer getId(MiniEventConfigPojo object) {
		return object.getId();
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<MiniEventConfigPojo> fetchById(Integer... values) {
		return fetch(MiniEventConfig.MINI_EVENT_CONFIG.ID, values);
	}

	/**
	 * Fetch a unique record that has <code>id = value</code>
	 */
	public MiniEventConfigPojo fetchOneById(Integer value) {
		return fetchOne(MiniEventConfig.MINI_EVENT_CONFIG.ID, value);
	}

	/**
	 * Fetch records that have <code>start_time IN (values)</code>
	 */
	public List<MiniEventConfigPojo> fetchByStartTime(Timestamp... values) {
		return fetch(MiniEventConfig.MINI_EVENT_CONFIG.START_TIME, values);
	}

	/**
	 * Fetch records that have <code>end_time IN (values)</code>
	 */
	public List<MiniEventConfigPojo> fetchByEndTime(Timestamp... values) {
		return fetch(MiniEventConfig.MINI_EVENT_CONFIG.END_TIME, values);
	}

	/**
	 * Fetch records that have <code>name IN (values)</code>
	 */
	public List<MiniEventConfigPojo> fetchByName(String... values) {
		return fetch(MiniEventConfig.MINI_EVENT_CONFIG.NAME, values);
	}

	/**
	 * Fetch records that have <code>description IN (values)</code>
	 */
	public List<MiniEventConfigPojo> fetchByDescription(String... values) {
		return fetch(MiniEventConfig.MINI_EVENT_CONFIG.DESCRIPTION, values);
	}

	/**
	 * Fetch records that have <code>img IN (values)</code>
	 */
	public List<MiniEventConfigPojo> fetchByImg(String... values) {
		return fetch(MiniEventConfig.MINI_EVENT_CONFIG.IMG, values);
	}

	/**
	 * Fetch records that have <code>icon IN (values)</code>
	 */
	public List<MiniEventConfigPojo> fetchByIcon(String... values) {
		return fetch(MiniEventConfig.MINI_EVENT_CONFIG.ICON, values);
	}
}
