/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.ClanGiftConfigRecord;

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
public class ClanGiftConfig extends TableImpl<ClanGiftConfigRecord> {

	private static final long serialVersionUID = 705957895;

	/**
	 * The reference instance of <code>mobsters.clan_gift_config</code>
	 */
	public static final ClanGiftConfig CLAN_GIFT_CONFIG = new ClanGiftConfig();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<ClanGiftConfigRecord> getRecordType() {
		return ClanGiftConfigRecord.class;
	}

	/**
	 * The column <code>mobsters.clan_gift_config.id</code>.
	 */
	public final TableField<ClanGiftConfigRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>mobsters.clan_gift_config.name</code>.
	 */
	public final TableField<ClanGiftConfigRecord, String> NAME = createField("name", org.jooq.impl.SQLDataType.VARCHAR.length(45), this, "");

	/**
	 * The column <code>mobsters.clan_gift_config.hours_until_expiration</code>.
	 */
	public final TableField<ClanGiftConfigRecord, Integer> HOURS_UNTIL_EXPIRATION = createField("hours_until_expiration", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.clan_gift_config.image_name</code>.
	 */
	public final TableField<ClanGiftConfigRecord, String> IMAGE_NAME = createField("image_name", org.jooq.impl.SQLDataType.VARCHAR.length(45), this, "");

	/**
	 * The column <code>mobsters.clan_gift_config.quality</code>.
	 */
	public final TableField<ClanGiftConfigRecord, String> QUALITY = createField("quality", org.jooq.impl.SQLDataType.VARCHAR.length(45), this, "");

	/**
	 * Create a <code>mobsters.clan_gift_config</code> table reference
	 */
	public ClanGiftConfig() {
		this("clan_gift_config", null);
	}

	/**
	 * Create an aliased <code>mobsters.clan_gift_config</code> table reference
	 */
	public ClanGiftConfig(String alias) {
		this(alias, CLAN_GIFT_CONFIG);
	}

	private ClanGiftConfig(String alias, Table<ClanGiftConfigRecord> aliased) {
		this(alias, aliased, null);
	}

	private ClanGiftConfig(String alias, Table<ClanGiftConfigRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<ClanGiftConfigRecord> getPrimaryKey() {
		return Keys.KEY_CLAN_GIFT_CONFIG_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<ClanGiftConfigRecord>> getKeys() {
		return Arrays.<UniqueKey<ClanGiftConfigRecord>>asList(Keys.KEY_CLAN_GIFT_CONFIG_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanGiftConfig as(String alias) {
		return new ClanGiftConfig(alias, this);
	}

	/**
	 * Rename this table
	 */
	public ClanGiftConfig rename(String name) {
		return new ClanGiftConfig(name, null);
	}
}
