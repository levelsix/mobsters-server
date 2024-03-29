/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.SalesDisplayItemConfigRecord;

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
public class SalesDisplayItemConfig extends TableImpl<SalesDisplayItemConfigRecord> {

	private static final long serialVersionUID = 839891531;

	/**
	 * The reference instance of <code>mobsters.sales_display_item_config</code>
	 */
	public static final SalesDisplayItemConfig SALES_DISPLAY_ITEM_CONFIG = new SalesDisplayItemConfig();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<SalesDisplayItemConfigRecord> getRecordType() {
		return SalesDisplayItemConfigRecord.class;
	}

	/**
	 * The column <code>mobsters.sales_display_item_config.id</code>.
	 */
	public final TableField<SalesDisplayItemConfigRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>mobsters.sales_display_item_config.sales_package_id</code>.
	 */
	public final TableField<SalesDisplayItemConfigRecord, Integer> SALES_PACKAGE_ID = createField("sales_package_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.sales_display_item_config.reward_id</code>.
	 */
	public final TableField<SalesDisplayItemConfigRecord, Integer> REWARD_ID = createField("reward_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * Create a <code>mobsters.sales_display_item_config</code> table reference
	 */
	public SalesDisplayItemConfig() {
		this("sales_display_item_config", null);
	}

	/**
	 * Create an aliased <code>mobsters.sales_display_item_config</code> table reference
	 */
	public SalesDisplayItemConfig(String alias) {
		this(alias, SALES_DISPLAY_ITEM_CONFIG);
	}

	private SalesDisplayItemConfig(String alias, Table<SalesDisplayItemConfigRecord> aliased) {
		this(alias, aliased, null);
	}

	private SalesDisplayItemConfig(String alias, Table<SalesDisplayItemConfigRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Identity<SalesDisplayItemConfigRecord, Integer> getIdentity() {
		return Keys.IDENTITY_SALES_DISPLAY_ITEM_CONFIG;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<SalesDisplayItemConfigRecord> getPrimaryKey() {
		return Keys.KEY_SALES_DISPLAY_ITEM_CONFIG_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<SalesDisplayItemConfigRecord>> getKeys() {
		return Arrays.<UniqueKey<SalesDisplayItemConfigRecord>>asList(Keys.KEY_SALES_DISPLAY_ITEM_CONFIG_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SalesDisplayItemConfig as(String alias) {
		return new SalesDisplayItemConfig(alias, this);
	}

	/**
	 * Rename this table
	 */
	public SalesDisplayItemConfig rename(String name) {
		return new SalesDisplayItemConfig(name, null);
	}
}
