/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.StructureMiniJobConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureMiniJobConfigPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.records.StructureMiniJobConfigRecord;

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
public class StructureMiniJobConfigDao extends DAOImpl<StructureMiniJobConfigRecord, StructureMiniJobConfigPojo, Integer> {

	/**
	 * Create a new StructureMiniJobConfigDao without any configuration
	 */
	public StructureMiniJobConfigDao() {
		super(StructureMiniJobConfig.STRUCTURE_MINI_JOB_CONFIG, StructureMiniJobConfigPojo.class);
	}

	/**
	 * Create a new StructureMiniJobConfigDao with an attached configuration
	 */
	public StructureMiniJobConfigDao(Configuration configuration) {
		super(StructureMiniJobConfig.STRUCTURE_MINI_JOB_CONFIG, StructureMiniJobConfigPojo.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer getId(StructureMiniJobConfigPojo object) {
		return object.getStructId();
	}

	/**
	 * Fetch records that have <code>struct_id IN (values)</code>
	 */
	public List<StructureMiniJobConfigPojo> fetchByStructId(Integer... values) {
		return fetch(StructureMiniJobConfig.STRUCTURE_MINI_JOB_CONFIG.STRUCT_ID, values);
	}

	/**
	 * Fetch a unique record that has <code>struct_id = value</code>
	 */
	public StructureMiniJobConfigPojo fetchOneByStructId(Integer value) {
		return fetchOne(StructureMiniJobConfig.STRUCTURE_MINI_JOB_CONFIG.STRUCT_ID, value);
	}

	/**
	 * Fetch records that have <code>generated_job_limit IN (values)</code>
	 */
	public List<StructureMiniJobConfigPojo> fetchByGeneratedJobLimit(Integer... values) {
		return fetch(StructureMiniJobConfig.STRUCTURE_MINI_JOB_CONFIG.GENERATED_JOB_LIMIT, values);
	}

	/**
	 * Fetch records that have <code>hours_between_job_generation IN (values)</code>
	 */
	public List<StructureMiniJobConfigPojo> fetchByHoursBetweenJobGeneration(Integer... values) {
		return fetch(StructureMiniJobConfig.STRUCTURE_MINI_JOB_CONFIG.HOURS_BETWEEN_JOB_GENERATION, values);
	}
}
