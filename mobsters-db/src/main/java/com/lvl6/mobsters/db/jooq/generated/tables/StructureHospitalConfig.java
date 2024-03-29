/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.StructureHospitalConfigRecord;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
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
public class StructureHospitalConfig extends TableImpl<StructureHospitalConfigRecord> {

	private static final long serialVersionUID = -590007333;

	/**
	 * The reference instance of <code>mobsters.structure_hospital_config</code>
	 */
	public static final StructureHospitalConfig STRUCTURE_HOSPITAL_CONFIG = new StructureHospitalConfig();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<StructureHospitalConfigRecord> getRecordType() {
		return StructureHospitalConfigRecord.class;
	}

	/**
	 * The column <code>mobsters.structure_hospital_config.struct_id</code>.
	 */
	public final TableField<StructureHospitalConfigRecord, Integer> STRUCT_ID = createField("struct_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>mobsters.structure_hospital_config.queue_size</code>.
	 */
	public final TableField<StructureHospitalConfigRecord, Byte> QUEUE_SIZE = createField("queue_size", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>mobsters.structure_hospital_config.health_per_second</code>.
	 */
	public final TableField<StructureHospitalConfigRecord, Double> HEALTH_PER_SECOND = createField("health_per_second", org.jooq.impl.SQLDataType.FLOAT.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>mobsters.structure_hospital_config.secs_to_fully_heal_multiplier</code>.
	 */
	public final TableField<StructureHospitalConfigRecord, Double> SECS_TO_FULLY_HEAL_MULTIPLIER = createField("secs_to_fully_heal_multiplier", org.jooq.impl.SQLDataType.FLOAT.defaulted(true), this, "");

	/**
	 * Create a <code>mobsters.structure_hospital_config</code> table reference
	 */
	public StructureHospitalConfig() {
		this("structure_hospital_config", null);
	}

	/**
	 * Create an aliased <code>mobsters.structure_hospital_config</code> table reference
	 */
	public StructureHospitalConfig(String alias) {
		this(alias, STRUCTURE_HOSPITAL_CONFIG);
	}

	private StructureHospitalConfig(String alias, Table<StructureHospitalConfigRecord> aliased) {
		this(alias, aliased, null);
	}

	private StructureHospitalConfig(String alias, Table<StructureHospitalConfigRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<StructureHospitalConfigRecord> getPrimaryKey() {
		return Keys.KEY_STRUCTURE_HOSPITAL_CONFIG_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<StructureHospitalConfigRecord>> getKeys() {
		return Arrays.<UniqueKey<StructureHospitalConfigRecord>>asList(Keys.KEY_STRUCTURE_HOSPITAL_CONFIG_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureHospitalConfig as(String alias) {
		return new StructureHospitalConfig(alias, this);
	}

	/**
	 * Rename this table
	 */
	public StructureHospitalConfig rename(String name) {
		return new StructureHospitalConfig(name, null);
	}
}
