/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.SalesItemConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.records.SalesItemConfigRecord;

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
public class SalesItemConfigDao extends DAOImpl<SalesItemConfigRecord, com.lvl6.mobsters.db.jooq.generated.tables.pojos.SalesItemConfig, Integer> {

	/**
	 * Create a new SalesItemConfigDao without any configuration
	 */
	public SalesItemConfigDao() {
		super(SalesItemConfig.SALES_ITEM_CONFIG, com.lvl6.mobsters.db.jooq.generated.tables.pojos.SalesItemConfig.class);
	}

	/**
	 * Create a new SalesItemConfigDao with an attached configuration
	 */
	public SalesItemConfigDao(Configuration configuration) {
		super(SalesItemConfig.SALES_ITEM_CONFIG, com.lvl6.mobsters.db.jooq.generated.tables.pojos.SalesItemConfig.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer getId(com.lvl6.mobsters.db.jooq.generated.tables.pojos.SalesItemConfig object) {
		return object.getId();
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.SalesItemConfig> fetchById(Integer... values) {
		return fetch(SalesItemConfig.SALES_ITEM_CONFIG.ID, values);
	}

	/**
	 * Fetch a unique record that has <code>id = value</code>
	 */
	public com.lvl6.mobsters.db.jooq.generated.tables.pojos.SalesItemConfig fetchOneById(Integer value) {
		return fetchOne(SalesItemConfig.SALES_ITEM_CONFIG.ID, value);
	}

	/**
	 * Fetch records that have <code>sales_package_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.SalesItemConfig> fetchBySalesPackageId(Integer... values) {
		return fetch(SalesItemConfig.SALES_ITEM_CONFIG.SALES_PACKAGE_ID, values);
	}

	/**
	 * Fetch records that have <code>reward_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.SalesItemConfig> fetchByRewardId(Integer... values) {
		return fetch(SalesItemConfig.SALES_ITEM_CONFIG.REWARD_ID, values);
	}
}