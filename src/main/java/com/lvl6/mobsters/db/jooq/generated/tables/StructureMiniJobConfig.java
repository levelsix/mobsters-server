/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.StructureMiniJobConfigRecord;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;
import org.jooq.types.UInteger;


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
public class StructureMiniJobConfig extends TableImpl<StructureMiniJobConfigRecord> {

	private static final long serialVersionUID = -996938520;

	/**
	 * The reference instance of <code>mobsters.structure_mini_job_config</code>
	 */
	public static final StructureMiniJobConfig STRUCTURE_MINI_JOB_CONFIG = new StructureMiniJobConfig();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<StructureMiniJobConfigRecord> getRecordType() {
		return StructureMiniJobConfigRecord.class;
	}

	/**
	 * The column <code>mobsters.structure_mini_job_config.struct_id</code>.
	 */
	public final TableField<StructureMiniJobConfigRecord, Integer> STRUCT_ID = createField("struct_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>mobsters.structure_mini_job_config.generated_job_limit</code>.
	 */
	public final TableField<StructureMiniJobConfigRecord, UInteger> GENERATED_JOB_LIMIT = createField("generated_job_limit", org.jooq.impl.SQLDataType.INTEGERUNSIGNED, this, "");

	/**
	 * The column <code>mobsters.structure_mini_job_config.hours_between_job_generation</code>.
	 */
	public final TableField<StructureMiniJobConfigRecord, UInteger> HOURS_BETWEEN_JOB_GENERATION = createField("hours_between_job_generation", org.jooq.impl.SQLDataType.INTEGERUNSIGNED, this, "");

	/**
	 * Create a <code>mobsters.structure_mini_job_config</code> table reference
	 */
	public StructureMiniJobConfig() {
		this("structure_mini_job_config", null);
	}

	/**
	 * Create an aliased <code>mobsters.structure_mini_job_config</code> table reference
	 */
	public StructureMiniJobConfig(String alias) {
		this(alias, STRUCTURE_MINI_JOB_CONFIG);
	}

	private StructureMiniJobConfig(String alias, Table<StructureMiniJobConfigRecord> aliased) {
		this(alias, aliased, null);
	}

	private StructureMiniJobConfig(String alias, Table<StructureMiniJobConfigRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<StructureMiniJobConfigRecord> getPrimaryKey() {
		return Keys.KEY_STRUCTURE_MINI_JOB_CONFIG_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<StructureMiniJobConfigRecord>> getKeys() {
		return Arrays.<UniqueKey<StructureMiniJobConfigRecord>>asList(Keys.KEY_STRUCTURE_MINI_JOB_CONFIG_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureMiniJobConfig as(String alias) {
		return new StructureMiniJobConfig(alias, this);
	}

	/**
	 * Rename this table
	 */
	public StructureMiniJobConfig rename(String name) {
		return new StructureMiniJobConfig(name, null);
	}
}
