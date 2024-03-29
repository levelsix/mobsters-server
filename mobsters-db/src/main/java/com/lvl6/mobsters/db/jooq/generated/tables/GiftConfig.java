/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.GiftConfigRecord;

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
public class GiftConfig extends TableImpl<GiftConfigRecord> {

	private static final long serialVersionUID = 20040476;

	/**
	 * The reference instance of <code>mobsters.gift_config</code>
	 */
	public static final GiftConfig GIFT_CONFIG = new GiftConfig();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<GiftConfigRecord> getRecordType() {
		return GiftConfigRecord.class;
	}

	/**
	 * The column <code>mobsters.gift_config.id</code>.
	 */
	public final TableField<GiftConfigRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>mobsters.gift_config.name</code>.
	 */
	public final TableField<GiftConfigRecord, String> NAME = createField("name", org.jooq.impl.SQLDataType.VARCHAR.length(45), this, "");

	/**
	 * The column <code>mobsters.gift_config.hours_until_expiration</code>.
	 */
	public final TableField<GiftConfigRecord, Integer> HOURS_UNTIL_EXPIRATION = createField("hours_until_expiration", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.gift_config.image_name</code>.
	 */
	public final TableField<GiftConfigRecord, String> IMAGE_NAME = createField("image_name", org.jooq.impl.SQLDataType.VARCHAR.length(75), this, "");

	/**
	 * The column <code>mobsters.gift_config.gift_type</code>.
	 */
	public final TableField<GiftConfigRecord, String> GIFT_TYPE = createField("gift_type", org.jooq.impl.SQLDataType.VARCHAR.length(75), this, "");

	/**
	 * Create a <code>mobsters.gift_config</code> table reference
	 */
	public GiftConfig() {
		this("gift_config", null);
	}

	/**
	 * Create an aliased <code>mobsters.gift_config</code> table reference
	 */
	public GiftConfig(String alias) {
		this(alias, GIFT_CONFIG);
	}

	private GiftConfig(String alias, Table<GiftConfigRecord> aliased) {
		this(alias, aliased, null);
	}

	private GiftConfig(String alias, Table<GiftConfigRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Identity<GiftConfigRecord, Integer> getIdentity() {
		return Keys.IDENTITY_GIFT_CONFIG;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<GiftConfigRecord> getPrimaryKey() {
		return Keys.KEY_GIFT_CONFIG_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<GiftConfigRecord>> getKeys() {
		return Arrays.<UniqueKey<GiftConfigRecord>>asList(Keys.KEY_GIFT_CONFIG_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GiftConfig as(String alias) {
		return new GiftConfig(alias, this);
	}

	/**
	 * Rename this table
	 */
	public GiftConfig rename(String name) {
		return new GiftConfig(name, null);
	}
}
