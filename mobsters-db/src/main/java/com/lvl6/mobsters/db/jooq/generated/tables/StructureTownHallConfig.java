/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.StructureTownHallConfigRecord;

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
public class StructureTownHallConfig extends TableImpl<StructureTownHallConfigRecord> {

	private static final long serialVersionUID = -1912497144;

	/**
	 * The reference instance of <code>mobsters.structure_town_hall_config</code>
	 */
	public static final StructureTownHallConfig STRUCTURE_TOWN_HALL_CONFIG = new StructureTownHallConfig();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<StructureTownHallConfigRecord> getRecordType() {
		return StructureTownHallConfigRecord.class;
	}

	/**
	 * The column <code>mobsters.structure_town_hall_config.struct_id</code>.
	 */
	public final TableField<StructureTownHallConfigRecord, Integer> STRUCT_ID = createField("struct_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>mobsters.structure_town_hall_config.num_resource_one_generators</code>.
	 */
	public final TableField<StructureTownHallConfigRecord, Byte> NUM_RESOURCE_ONE_GENERATORS = createField("num_resource_one_generators", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>mobsters.structure_town_hall_config.num_resource_one_storages</code>.
	 */
	public final TableField<StructureTownHallConfigRecord, Byte> NUM_RESOURCE_ONE_STORAGES = createField("num_resource_one_storages", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>mobsters.structure_town_hall_config.num_resource_two_generators</code>.
	 */
	public final TableField<StructureTownHallConfigRecord, Byte> NUM_RESOURCE_TWO_GENERATORS = createField("num_resource_two_generators", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>mobsters.structure_town_hall_config.num_resource_two_storages</code>.
	 */
	public final TableField<StructureTownHallConfigRecord, Byte> NUM_RESOURCE_TWO_STORAGES = createField("num_resource_two_storages", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>mobsters.structure_town_hall_config.num_hospitals</code>.
	 */
	public final TableField<StructureTownHallConfigRecord, Byte> NUM_HOSPITALS = createField("num_hospitals", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>mobsters.structure_town_hall_config.num_residences</code>.
	 */
	public final TableField<StructureTownHallConfigRecord, Byte> NUM_RESIDENCES = createField("num_residences", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>mobsters.structure_town_hall_config.num_monster_slots</code>.
	 */
	public final TableField<StructureTownHallConfigRecord, Integer> NUM_MONSTER_SLOTS = createField("num_monster_slots", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>mobsters.structure_town_hall_config.num_labs</code>.
	 */
	public final TableField<StructureTownHallConfigRecord, Byte> NUM_LABS = createField("num_labs", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>mobsters.structure_town_hall_config.pvp_queue_cash_cost</code>. Cost for seeing one possible enemy to attack for pvp battles.
	 */
	public final TableField<StructureTownHallConfigRecord, Integer> PVP_QUEUE_CASH_COST = createField("pvp_queue_cash_cost", org.jooq.impl.SQLDataType.INTEGER, this, "Cost for seeing one possible enemy to attack for pvp battles.");

	/**
	 * The column <code>mobsters.structure_town_hall_config.resource_capacity</code>. applies to cash, oil, not gems
	 */
	public final TableField<StructureTownHallConfigRecord, Integer> RESOURCE_CAPACITY = createField("resource_capacity", org.jooq.impl.SQLDataType.INTEGER, this, "applies to cash, oil, not gems");

	/**
	 * The column <code>mobsters.structure_town_hall_config.num_evo_chambers</code>.
	 */
	public final TableField<StructureTownHallConfigRecord, Byte> NUM_EVO_CHAMBERS = createField("num_evo_chambers", org.jooq.impl.SQLDataType.TINYINT.defaulted(true), this, "");

	/**
	 * Create a <code>mobsters.structure_town_hall_config</code> table reference
	 */
	public StructureTownHallConfig() {
		this("structure_town_hall_config", null);
	}

	/**
	 * Create an aliased <code>mobsters.structure_town_hall_config</code> table reference
	 */
	public StructureTownHallConfig(String alias) {
		this(alias, STRUCTURE_TOWN_HALL_CONFIG);
	}

	private StructureTownHallConfig(String alias, Table<StructureTownHallConfigRecord> aliased) {
		this(alias, aliased, null);
	}

	private StructureTownHallConfig(String alias, Table<StructureTownHallConfigRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<StructureTownHallConfigRecord> getPrimaryKey() {
		return Keys.KEY_STRUCTURE_TOWN_HALL_CONFIG_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<StructureTownHallConfigRecord>> getKeys() {
		return Arrays.<UniqueKey<StructureTownHallConfigRecord>>asList(Keys.KEY_STRUCTURE_TOWN_HALL_CONFIG_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureTownHallConfig as(String alias) {
		return new StructureTownHallConfig(alias, this);
	}

	/**
	 * Rename this table
	 */
	public StructureTownHallConfig rename(String name) {
		return new StructureTownHallConfig(name, null);
	}
}