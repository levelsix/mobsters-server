/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.TaskStageConfigRecord;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Identity;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;


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
public class TaskStageConfig extends TableImpl<TaskStageConfigRecord> {

	private static final long serialVersionUID = 221374139;

	/**
	 * The reference instance of <code>mobsters.task_stage_config</code>
	 */
	public static final TaskStageConfig TASK_STAGE_CONFIG = new TaskStageConfig();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<TaskStageConfigRecord> getRecordType() {
		return TaskStageConfigRecord.class;
	}

	/**
	 * The column <code>mobsters.task_stage_config.id</code>.
	 */
	public final TableField<TaskStageConfigRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>mobsters.task_stage_config.task_id</code>.
	 */
	public final TableField<TaskStageConfigRecord, Integer> TASK_ID = createField("task_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.task_stage_config.stage_num</code>.
	 */
	public final TableField<TaskStageConfigRecord, Integer> STAGE_NUM = createField("stage_num", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.task_stage_config.attacker_always_hits_first</code>.
	 */
	public final TableField<TaskStageConfigRecord, Boolean> ATTACKER_ALWAYS_HITS_FIRST = createField("attacker_always_hits_first", org.jooq.impl.SQLDataType.BIT.defaulted(true), this, "");

	/**
	 * Create a <code>mobsters.task_stage_config</code> table reference
	 */
	public TaskStageConfig() {
		this("task_stage_config", null);
	}

	/**
	 * Create an aliased <code>mobsters.task_stage_config</code> table reference
	 */
	public TaskStageConfig(String alias) {
		this(alias, TASK_STAGE_CONFIG);
	}

	private TaskStageConfig(String alias, Table<TaskStageConfigRecord> aliased) {
		this(alias, aliased, null);
	}

	private TaskStageConfig(String alias, Table<TaskStageConfigRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Identity<TaskStageConfigRecord, Integer> getIdentity() {
		return Keys.IDENTITY_TASK_STAGE_CONFIG;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<TaskStageConfigRecord> getPrimaryKey() {
		return Keys.KEY_TASK_STAGE_CONFIG_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<TaskStageConfigRecord>> getKeys() {
		return Arrays.<UniqueKey<TaskStageConfigRecord>>asList(Keys.KEY_TASK_STAGE_CONFIG_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TaskStageConfig as(String alias) {
		return new TaskStageConfig(alias, this);
	}

	/**
	 * Rename this table
	 */
	public TaskStageConfig rename(String name) {
		return new TaskStageConfig(name, null);
	}
}
