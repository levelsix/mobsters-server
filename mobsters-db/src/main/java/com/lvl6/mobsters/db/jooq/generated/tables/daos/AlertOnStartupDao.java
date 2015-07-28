/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.AlertOnStartup;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.AlertOnStartupPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.records.AlertOnStartupRecord;

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
public class AlertOnStartupDao extends DAOImpl<AlertOnStartupRecord, AlertOnStartupPojo, Integer> {

	/**
	 * Create a new AlertOnStartupDao without any configuration
	 */
	public AlertOnStartupDao() {
		super(AlertOnStartup.ALERT_ON_STARTUP, AlertOnStartupPojo.class);
	}

	/**
	 * Create a new AlertOnStartupDao with an attached configuration
	 */
	public AlertOnStartupDao(Configuration configuration) {
		super(AlertOnStartup.ALERT_ON_STARTUP, AlertOnStartupPojo.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer getId(AlertOnStartupPojo object) {
		return object.getId();
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<AlertOnStartupPojo> fetchById(Integer... values) {
		return fetch(AlertOnStartup.ALERT_ON_STARTUP.ID, values);
	}

	/**
	 * Fetch a unique record that has <code>id = value</code>
	 */
	public AlertOnStartupPojo fetchOneById(Integer value) {
		return fetchOne(AlertOnStartup.ALERT_ON_STARTUP.ID, value);
	}

	/**
	 * Fetch records that have <code>message IN (values)</code>
	 */
	public List<AlertOnStartupPojo> fetchByMessage(String... values) {
		return fetch(AlertOnStartup.ALERT_ON_STARTUP.MESSAGE, values);
	}

	/**
	 * Fetch records that have <code>is_active IN (values)</code>
	 */
	public List<AlertOnStartupPojo> fetchByIsActive(Boolean... values) {
		return fetch(AlertOnStartup.ALERT_ON_STARTUP.IS_ACTIVE, values);
	}
}