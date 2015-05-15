/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.MiniJobRefreshItemConfigRecord;

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
public class MiniJobRefreshItemConfig extends TableImpl<MiniJobRefreshItemConfigRecord> {

	private static final long serialVersionUID = 648240165;

	/**
	 * The reference instance of <code>mobsters.mini_job_refresh_item_config</code>
	 */
	public static final MiniJobRefreshItemConfig MINI_JOB_REFRESH_ITEM_CONFIG = new MiniJobRefreshItemConfig();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<MiniJobRefreshItemConfigRecord> getRecordType() {
		return MiniJobRefreshItemConfigRecord.class;
	}

	/**
	 * The column <code>mobsters.mini_job_refresh_item_config.struct_id</code>.
	 */
	public final TableField<MiniJobRefreshItemConfigRecord, Integer> STRUCT_ID = createField("struct_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>mobsters.mini_job_refresh_item_config.item_id</code>.
	 */
	public final TableField<MiniJobRefreshItemConfigRecord, Integer> ITEM_ID = createField("item_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>mobsters.mini_job_refresh_item_config.gem_price</code>.
	 */
	public final TableField<MiniJobRefreshItemConfigRecord, Integer> GEM_PRICE = createField("gem_price", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * Create a <code>mobsters.mini_job_refresh_item_config</code> table reference
	 */
	public MiniJobRefreshItemConfig() {
		this("mini_job_refresh_item_config", null);
	}

	/**
	 * Create an aliased <code>mobsters.mini_job_refresh_item_config</code> table reference
	 */
	public MiniJobRefreshItemConfig(String alias) {
		this(alias, MINI_JOB_REFRESH_ITEM_CONFIG);
	}

	private MiniJobRefreshItemConfig(String alias, Table<MiniJobRefreshItemConfigRecord> aliased) {
		this(alias, aliased, null);
	}

	private MiniJobRefreshItemConfig(String alias, Table<MiniJobRefreshItemConfigRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<MiniJobRefreshItemConfigRecord> getPrimaryKey() {
		return Keys.KEY_MINI_JOB_REFRESH_ITEM_CONFIG_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<MiniJobRefreshItemConfigRecord>> getKeys() {
		return Arrays.<UniqueKey<MiniJobRefreshItemConfigRecord>>asList(Keys.KEY_MINI_JOB_REFRESH_ITEM_CONFIG_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MiniJobRefreshItemConfig as(String alias) {
		return new MiniJobRefreshItemConfig(alias, this);
	}

	/**
	 * Rename this table
	 */
	public MiniJobRefreshItemConfig rename(String name) {
		return new MiniJobRefreshItemConfig(name, null);
	}
}
