/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.StructureResearchHouseConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.records.StructureResearchHouseConfigRecord;

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
public class StructureResearchHouseConfigDao extends DAOImpl<StructureResearchHouseConfigRecord, com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureResearchHouseConfig, Integer> {

	/**
	 * Create a new StructureResearchHouseConfigDao without any configuration
	 */
	public StructureResearchHouseConfigDao() {
		super(StructureResearchHouseConfig.STRUCTURE_RESEARCH_HOUSE_CONFIG, com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureResearchHouseConfig.class);
	}

	/**
	 * Create a new StructureResearchHouseConfigDao with an attached configuration
	 */
	public StructureResearchHouseConfigDao(Configuration configuration) {
		super(StructureResearchHouseConfig.STRUCTURE_RESEARCH_HOUSE_CONFIG, com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureResearchHouseConfig.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer getId(com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureResearchHouseConfig object) {
		return object.getStructId();
	}

	/**
	 * Fetch records that have <code>struct_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureResearchHouseConfig> fetchByStructId(Integer... values) {
		return fetch(StructureResearchHouseConfig.STRUCTURE_RESEARCH_HOUSE_CONFIG.STRUCT_ID, values);
	}

	/**
	 * Fetch a unique record that has <code>struct_id = value</code>
	 */
	public com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureResearchHouseConfig fetchOneByStructId(Integer value) {
		return fetchOne(StructureResearchHouseConfig.STRUCTURE_RESEARCH_HOUSE_CONFIG.STRUCT_ID, value);
	}

	/**
	 * Fetch records that have <code>research_speed_multiplier IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureResearchHouseConfig> fetchByResearchSpeedMultiplier(Double... values) {
		return fetch(StructureResearchHouseConfig.STRUCTURE_RESEARCH_HOUSE_CONFIG.RESEARCH_SPEED_MULTIPLIER, values);
	}
}