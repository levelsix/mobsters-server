/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.StructureMoneyTreeConfigRecord;

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
public class StructureMoneyTreeConfig extends TableImpl<StructureMoneyTreeConfigRecord> {

	private static final long serialVersionUID = 1606541389;

	/**
	 * The reference instance of <code>mobsters.structure_money_tree_config</code>
	 */
	public static final StructureMoneyTreeConfig STRUCTURE_MONEY_TREE_CONFIG = new StructureMoneyTreeConfig();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<StructureMoneyTreeConfigRecord> getRecordType() {
		return StructureMoneyTreeConfigRecord.class;
	}

	/**
	 * The column <code>mobsters.structure_money_tree_config.struct_id</code>.
	 */
	public final TableField<StructureMoneyTreeConfigRecord, Integer> STRUCT_ID = createField("struct_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>mobsters.structure_money_tree_config.production_rate</code>.
	 */
	public final TableField<StructureMoneyTreeConfigRecord, Double> PRODUCTION_RATE = createField("production_rate", org.jooq.impl.SQLDataType.FLOAT.nullable(false), this, "");

	/**
	 * The column <code>mobsters.structure_money_tree_config.capacity</code>.
	 */
	public final TableField<StructureMoneyTreeConfigRecord, Integer> CAPACITY = createField("capacity", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>mobsters.structure_money_tree_config.days_of_duration</code>.
	 */
	public final TableField<StructureMoneyTreeConfigRecord, Integer> DAYS_OF_DURATION = createField("days_of_duration", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.structure_money_tree_config.days_for_renewal</code>.
	 */
	public final TableField<StructureMoneyTreeConfigRecord, Integer> DAYS_FOR_RENEWAL = createField("days_for_renewal", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.structure_money_tree_config.iap_product_id</code>.
	 */
	public final TableField<StructureMoneyTreeConfigRecord, String> IAP_PRODUCT_ID = createField("iap_product_id", org.jooq.impl.SQLDataType.VARCHAR.length(100).nullable(false), this, "");

	/**
	 * The column <code>mobsters.structure_money_tree_config.fake_iap_product_id</code>.
	 */
	public final TableField<StructureMoneyTreeConfigRecord, String> FAKE_IAP_PRODUCT_ID = createField("fake_iap_product_id", org.jooq.impl.SQLDataType.VARCHAR.length(100), this, "");

	/**
	 * Create a <code>mobsters.structure_money_tree_config</code> table reference
	 */
	public StructureMoneyTreeConfig() {
		this("structure_money_tree_config", null);
	}

	/**
	 * Create an aliased <code>mobsters.structure_money_tree_config</code> table reference
	 */
	public StructureMoneyTreeConfig(String alias) {
		this(alias, STRUCTURE_MONEY_TREE_CONFIG);
	}

	private StructureMoneyTreeConfig(String alias, Table<StructureMoneyTreeConfigRecord> aliased) {
		this(alias, aliased, null);
	}

	private StructureMoneyTreeConfig(String alias, Table<StructureMoneyTreeConfigRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<StructureMoneyTreeConfigRecord> getPrimaryKey() {
		return Keys.KEY_STRUCTURE_MONEY_TREE_CONFIG_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<StructureMoneyTreeConfigRecord>> getKeys() {
		return Arrays.<UniqueKey<StructureMoneyTreeConfigRecord>>asList(Keys.KEY_STRUCTURE_MONEY_TREE_CONFIG_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureMoneyTreeConfig as(String alias) {
		return new StructureMoneyTreeConfig(alias, this);
	}

	/**
	 * Rename this table
	 */
	public StructureMoneyTreeConfig rename(String name) {
		return new StructureMoneyTreeConfig(name, null);
	}
}
