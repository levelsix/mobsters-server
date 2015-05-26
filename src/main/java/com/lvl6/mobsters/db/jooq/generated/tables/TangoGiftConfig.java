/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.TangoGiftConfigRecord;

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
public class TangoGiftConfig extends TableImpl<TangoGiftConfigRecord> {

	private static final long serialVersionUID = 402682897;

	/**
	 * The reference instance of <code>mobsters.tango_gift_config</code>
	 */
	public static final TangoGiftConfig TANGO_GIFT_CONFIG = new TangoGiftConfig();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<TangoGiftConfigRecord> getRecordType() {
		return TangoGiftConfigRecord.class;
	}

	/**
	 * The column <code>mobsters.tango_gift_config.id</code>.
	 */
	public final TableField<TangoGiftConfigRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>mobsters.tango_gift_config.name</code>.
	 */
	public final TableField<TangoGiftConfigRecord, String> NAME = createField("name", org.jooq.impl.SQLDataType.VARCHAR.length(45), this, "");

	/**
	 * The column <code>mobsters.tango_gift_config.hours_until_expiration</code>.
	 */
	public final TableField<TangoGiftConfigRecord, Integer> HOURS_UNTIL_EXPIRATION = createField("hours_until_expiration", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.tango_gift_config.image_name</code>.
	 */
	public final TableField<TangoGiftConfigRecord, String> IMAGE_NAME = createField("image_name", org.jooq.impl.SQLDataType.VARCHAR.length(45), this, "");

	/**
	 * Create a <code>mobsters.tango_gift_config</code> table reference
	 */
	public TangoGiftConfig() {
		this("tango_gift_config", null);
	}

	/**
	 * Create an aliased <code>mobsters.tango_gift_config</code> table reference
	 */
	public TangoGiftConfig(String alias) {
		this(alias, TANGO_GIFT_CONFIG);
	}

	private TangoGiftConfig(String alias, Table<TangoGiftConfigRecord> aliased) {
		this(alias, aliased, null);
	}

	private TangoGiftConfig(String alias, Table<TangoGiftConfigRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<TangoGiftConfigRecord> getPrimaryKey() {
		return Keys.KEY_TANGO_GIFT_CONFIG_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<TangoGiftConfigRecord>> getKeys() {
		return Arrays.<UniqueKey<TangoGiftConfigRecord>>asList(Keys.KEY_TANGO_GIFT_CONFIG_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TangoGiftConfig as(String alias) {
		return new TangoGiftConfig(alias, this);
	}

	/**
	 * Rename this table
	 */
	public TangoGiftConfig rename(String name) {
		return new TangoGiftConfig(name, null);
	}
}
