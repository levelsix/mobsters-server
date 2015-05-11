/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.TaskStageConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.records.TaskStageConfigRecord;

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
public class TaskStageConfigDao extends DAOImpl<TaskStageConfigRecord, com.lvl6.mobsters.db.jooq.generated.tables.pojos.TaskStageConfig, Integer> {

	/**
	 * Create a new TaskStageConfigDao without any configuration
	 */
	public TaskStageConfigDao() {
		super(TaskStageConfig.TASK_STAGE_CONFIG, com.lvl6.mobsters.db.jooq.generated.tables.pojos.TaskStageConfig.class);
	}

	/**
	 * Create a new TaskStageConfigDao with an attached configuration
	 */
	public TaskStageConfigDao(Configuration configuration) {
		super(TaskStageConfig.TASK_STAGE_CONFIG, com.lvl6.mobsters.db.jooq.generated.tables.pojos.TaskStageConfig.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer getId(com.lvl6.mobsters.db.jooq.generated.tables.pojos.TaskStageConfig object) {
		return object.getId();
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.TaskStageConfig> fetchById(Integer... values) {
		return fetch(TaskStageConfig.TASK_STAGE_CONFIG.ID, values);
	}

	/**
	 * Fetch a unique record that has <code>id = value</code>
	 */
	public com.lvl6.mobsters.db.jooq.generated.tables.pojos.TaskStageConfig fetchOneById(Integer value) {
		return fetchOne(TaskStageConfig.TASK_STAGE_CONFIG.ID, value);
	}

	/**
	 * Fetch records that have <code>task_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.TaskStageConfig> fetchByTaskId(Integer... values) {
		return fetch(TaskStageConfig.TASK_STAGE_CONFIG.TASK_ID, values);
	}

	/**
	 * Fetch records that have <code>stage_num IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.TaskStageConfig> fetchByStageNum(Integer... values) {
		return fetch(TaskStageConfig.TASK_STAGE_CONFIG.STAGE_NUM, values);
	}

	/**
	 * Fetch records that have <code>attacker_always_hits_first IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.TaskStageConfig> fetchByAttackerAlwaysHitsFirst(Boolean... values) {
		return fetch(TaskStageConfig.TASK_STAGE_CONFIG.ATTACKER_ALWAYS_HITS_FIRST, values);
	}
}