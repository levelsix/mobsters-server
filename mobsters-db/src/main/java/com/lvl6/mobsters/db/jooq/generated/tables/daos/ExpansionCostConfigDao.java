/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.ExpansionCostConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.ExpansionCostConfigPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.records.ExpansionCostConfigRecord;

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
public class ExpansionCostConfigDao extends DAOImpl<ExpansionCostConfigRecord, ExpansionCostConfigPojo, Integer> {

	/**
	 * Create a new ExpansionCostConfigDao without any configuration
	 */
	public ExpansionCostConfigDao() {
		super(ExpansionCostConfig.EXPANSION_COST_CONFIG, ExpansionCostConfigPojo.class);
	}

	/**
	 * Create a new ExpansionCostConfigDao with an attached configuration
	 */
	public ExpansionCostConfigDao(Configuration configuration) {
		super(ExpansionCostConfig.EXPANSION_COST_CONFIG, ExpansionCostConfigPojo.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer getId(ExpansionCostConfigPojo object) {
		return object.getId();
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<ExpansionCostConfigPojo> fetchById(Integer... values) {
		return fetch(ExpansionCostConfig.EXPANSION_COST_CONFIG.ID, values);
	}

	/**
	 * Fetch a unique record that has <code>id = value</code>
	 */
	public ExpansionCostConfigPojo fetchOneById(Integer value) {
		return fetchOne(ExpansionCostConfig.EXPANSION_COST_CONFIG.ID, value);
	}

	/**
	 * Fetch records that have <code>expansion_cost_cash IN (values)</code>
	 */
	public List<ExpansionCostConfigPojo> fetchByExpansionCostCash(Integer... values) {
		return fetch(ExpansionCostConfig.EXPANSION_COST_CONFIG.EXPANSION_COST_CASH, values);
	}

	/**
	 * Fetch records that have <code>num_minutes_to_expand IN (values)</code>
	 */
	public List<ExpansionCostConfigPojo> fetchByNumMinutesToExpand(Integer... values) {
		return fetch(ExpansionCostConfig.EXPANSION_COST_CONFIG.NUM_MINUTES_TO_EXPAND, values);
	}

	/**
	 * Fetch records that have <code>speedup_expansion_gem_cost IN (values)</code>
	 */
	public List<ExpansionCostConfigPojo> fetchBySpeedupExpansionGemCost(Integer... values) {
		return fetch(ExpansionCostConfig.EXPANSION_COST_CONFIG.SPEEDUP_EXPANSION_GEM_COST, values);
	}
}
