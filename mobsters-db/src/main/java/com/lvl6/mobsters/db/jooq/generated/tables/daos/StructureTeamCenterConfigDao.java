/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.StructureTeamCenterConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureTeamCenterConfigPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.records.StructureTeamCenterConfigRecord;

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
public class StructureTeamCenterConfigDao extends DAOImpl<StructureTeamCenterConfigRecord, StructureTeamCenterConfigPojo, Integer> {

	/**
	 * Create a new StructureTeamCenterConfigDao without any configuration
	 */
	public StructureTeamCenterConfigDao() {
		super(StructureTeamCenterConfig.STRUCTURE_TEAM_CENTER_CONFIG, StructureTeamCenterConfigPojo.class);
	}

	/**
	 * Create a new StructureTeamCenterConfigDao with an attached configuration
	 */
	public StructureTeamCenterConfigDao(Configuration configuration) {
		super(StructureTeamCenterConfig.STRUCTURE_TEAM_CENTER_CONFIG, StructureTeamCenterConfigPojo.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer getId(StructureTeamCenterConfigPojo object) {
		return object.getStructId();
	}

	/**
	 * Fetch records that have <code>struct_id IN (values)</code>
	 */
	public List<StructureTeamCenterConfigPojo> fetchByStructId(Integer... values) {
		return fetch(StructureTeamCenterConfig.STRUCTURE_TEAM_CENTER_CONFIG.STRUCT_ID, values);
	}

	/**
	 * Fetch a unique record that has <code>struct_id = value</code>
	 */
	public StructureTeamCenterConfigPojo fetchOneByStructId(Integer value) {
		return fetchOne(StructureTeamCenterConfig.STRUCTURE_TEAM_CENTER_CONFIG.STRUCT_ID, value);
	}

	/**
	 * Fetch records that have <code>team_cost_limit IN (values)</code>
	 */
	public List<StructureTeamCenterConfigPojo> fetchByTeamCostLimit(Integer... values) {
		return fetch(StructureTeamCenterConfig.STRUCTURE_TEAM_CENTER_CONFIG.TEAM_COST_LIMIT, values);
	}
}