/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.StructurePvpBoardConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.records.StructurePvpBoardConfigRecord;

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
public class StructurePvpBoardConfigDao extends DAOImpl<StructurePvpBoardConfigRecord, com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructurePvpBoardConfig, Integer> {

	/**
	 * Create a new StructurePvpBoardConfigDao without any configuration
	 */
	public StructurePvpBoardConfigDao() {
		super(StructurePvpBoardConfig.STRUCTURE_PVP_BOARD_CONFIG, com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructurePvpBoardConfig.class);
	}

	/**
	 * Create a new StructurePvpBoardConfigDao with an attached configuration
	 */
	public StructurePvpBoardConfigDao(Configuration configuration) {
		super(StructurePvpBoardConfig.STRUCTURE_PVP_BOARD_CONFIG, com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructurePvpBoardConfig.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer getId(com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructurePvpBoardConfig object) {
		return object.getStructId();
	}

	/**
	 * Fetch records that have <code>struct_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructurePvpBoardConfig> fetchByStructId(Integer... values) {
		return fetch(StructurePvpBoardConfig.STRUCTURE_PVP_BOARD_CONFIG.STRUCT_ID, values);
	}

	/**
	 * Fetch a unique record that has <code>struct_id = value</code>
	 */
	public com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructurePvpBoardConfig fetchOneByStructId(Integer value) {
		return fetchOne(StructurePvpBoardConfig.STRUCTURE_PVP_BOARD_CONFIG.STRUCT_ID, value);
	}

	/**
	 * Fetch records that have <code>power_limit IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructurePvpBoardConfig> fetchByPowerLimit(Integer... values) {
		return fetch(StructurePvpBoardConfig.STRUCTURE_PVP_BOARD_CONFIG.POWER_LIMIT, values);
	}
}