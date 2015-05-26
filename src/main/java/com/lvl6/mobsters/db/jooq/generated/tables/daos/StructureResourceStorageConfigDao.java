/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.StructureResourceStorageConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.records.StructureResourceStorageConfigRecord;

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
public class StructureResourceStorageConfigDao extends DAOImpl<StructureResourceStorageConfigRecord, com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureResourceStorageConfig, Integer> {

	/**
	 * Create a new StructureResourceStorageConfigDao without any configuration
	 */
	public StructureResourceStorageConfigDao() {
		super(StructureResourceStorageConfig.STRUCTURE_RESOURCE_STORAGE_CONFIG, com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureResourceStorageConfig.class);
	}

	/**
	 * Create a new StructureResourceStorageConfigDao with an attached configuration
	 */
	public StructureResourceStorageConfigDao(Configuration configuration) {
		super(StructureResourceStorageConfig.STRUCTURE_RESOURCE_STORAGE_CONFIG, com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureResourceStorageConfig.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer getId(com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureResourceStorageConfig object) {
		return object.getStructId();
	}

	/**
	 * Fetch records that have <code>struct_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureResourceStorageConfig> fetchByStructId(Integer... values) {
		return fetch(StructureResourceStorageConfig.STRUCTURE_RESOURCE_STORAGE_CONFIG.STRUCT_ID, values);
	}

	/**
	 * Fetch a unique record that has <code>struct_id = value</code>
	 */
	public com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureResourceStorageConfig fetchOneByStructId(Integer value) {
		return fetchOne(StructureResourceStorageConfig.STRUCTURE_RESOURCE_STORAGE_CONFIG.STRUCT_ID, value);
	}

	/**
	 * Fetch records that have <code>resource_type_stored IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureResourceStorageConfig> fetchByResourceTypeStored(String... values) {
		return fetch(StructureResourceStorageConfig.STRUCTURE_RESOURCE_STORAGE_CONFIG.RESOURCE_TYPE_STORED, values);
	}

	/**
	 * Fetch records that have <code>capacity IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureResourceStorageConfig> fetchByCapacity(Integer... values) {
		return fetch(StructureResourceStorageConfig.STRUCTURE_RESOURCE_STORAGE_CONFIG.CAPACITY, values);
	}
}
