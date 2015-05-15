/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.ClanHelpRecord;

import java.sql.Timestamp;
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
public class ClanHelp extends TableImpl<ClanHelpRecord> {

	private static final long serialVersionUID = -1652735756;

	/**
	 * The reference instance of <code>mobsters.clan_help</code>
	 */
	public static final ClanHelp CLAN_HELP = new ClanHelp();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<ClanHelpRecord> getRecordType() {
		return ClanHelpRecord.class;
	}

	/**
	 * The column <code>mobsters.clan_help.id</code>.
	 */
	public final TableField<ClanHelpRecord, String> ID = createField("id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>mobsters.clan_help.user_id</code>.
	 */
	public final TableField<ClanHelpRecord, String> USER_ID = createField("user_id", org.jooq.impl.SQLDataType.CHAR.length(36), this, "");

	/**
	 * The column <code>mobsters.clan_help.user_data_id</code>.
	 */
	public final TableField<ClanHelpRecord, String> USER_DATA_ID = createField("user_data_id", org.jooq.impl.SQLDataType.CHAR.length(36), this, "");

	/**
	 * The column <code>mobsters.clan_help.help_type</code>.
	 */
	public final TableField<ClanHelpRecord, String> HELP_TYPE = createField("help_type", org.jooq.impl.SQLDataType.VARCHAR.length(45), this, "");

	/**
	 * The column <code>mobsters.clan_help.clan_id</code>.
	 */
	public final TableField<ClanHelpRecord, String> CLAN_ID = createField("clan_id", org.jooq.impl.SQLDataType.CHAR.length(36), this, "");

	/**
	 * The column <code>mobsters.clan_help.time_of_entry</code>.
	 */
	public final TableField<ClanHelpRecord, Timestamp> TIME_OF_ENTRY = createField("time_of_entry", org.jooq.impl.SQLDataType.TIMESTAMP.defaulted(true), this, "");

	/**
	 * The column <code>mobsters.clan_help.max_helpers</code>.
	 */
	public final TableField<ClanHelpRecord, Integer> MAX_HELPERS = createField("max_helpers", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.clan_help.helpers</code>.
	 */
	public final TableField<ClanHelpRecord, String> HELPERS = createField("helpers", org.jooq.impl.SQLDataType.CLOB, this, "");

	/**
	 * The column <code>mobsters.clan_help.open</code>.
	 */
	public final TableField<ClanHelpRecord, Boolean> OPEN = createField("open", org.jooq.impl.SQLDataType.BIT.defaulted(true), this, "");

	/**
	 * The column <code>mobsters.clan_help.static_data_id</code>.
	 */
	public final TableField<ClanHelpRecord, Integer> STATIC_DATA_ID = createField("static_data_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * Create a <code>mobsters.clan_help</code> table reference
	 */
	public ClanHelp() {
		this("clan_help", null);
	}

	/**
	 * Create an aliased <code>mobsters.clan_help</code> table reference
	 */
	public ClanHelp(String alias) {
		this(alias, CLAN_HELP);
	}

	private ClanHelp(String alias, Table<ClanHelpRecord> aliased) {
		this(alias, aliased, null);
	}

	private ClanHelp(String alias, Table<ClanHelpRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<ClanHelpRecord> getPrimaryKey() {
		return Keys.KEY_CLAN_HELP_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<ClanHelpRecord>> getKeys() {
		return Arrays.<UniqueKey<ClanHelpRecord>>asList(Keys.KEY_CLAN_HELP_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanHelp as(String alias) {
		return new ClanHelp(alias, this);
	}

	/**
	 * Rename this table
	 */
	public ClanHelp rename(String name) {
		return new ClanHelp(name, null);
	}
}
