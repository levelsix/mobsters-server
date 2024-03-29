/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.ClanHelpCountLast_24HoursRecord;

import java.math.BigInteger;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.TableImpl;


/**
 * VIEW
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.1"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ClanHelpCountLast_24Hours extends TableImpl<ClanHelpCountLast_24HoursRecord> {

	private static final long serialVersionUID = 1052762496;

	/**
	 * The reference instance of <code>mobsters.clan_help_count_last_24_hours</code>
	 */
	public static final ClanHelpCountLast_24Hours CLAN_HELP_COUNT_LAST_24_HOURS = new ClanHelpCountLast_24Hours();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<ClanHelpCountLast_24HoursRecord> getRecordType() {
		return ClanHelpCountLast_24HoursRecord.class;
	}

	/**
	 * The column <code>mobsters.clan_help_count_last_24_hours.clan_id</code>.
	 */
	public final TableField<ClanHelpCountLast_24HoursRecord, String> CLAN_ID = createField("clan_id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>mobsters.clan_help_count_last_24_hours.helps_given</code>.
	 */
	public final TableField<ClanHelpCountLast_24HoursRecord, BigInteger> HELPS_GIVEN = createField("helps_given", org.jooq.impl.SQLDataType.DECIMAL_INTEGER.precision(32), this, "");

	/**
	 * Create a <code>mobsters.clan_help_count_last_24_hours</code> table reference
	 */
	public ClanHelpCountLast_24Hours() {
		this("clan_help_count_last_24_hours", null);
	}

	/**
	 * Create an aliased <code>mobsters.clan_help_count_last_24_hours</code> table reference
	 */
	public ClanHelpCountLast_24Hours(String alias) {
		this(alias, CLAN_HELP_COUNT_LAST_24_HOURS);
	}

	private ClanHelpCountLast_24Hours(String alias, Table<ClanHelpCountLast_24HoursRecord> aliased) {
		this(alias, aliased, null);
	}

	private ClanHelpCountLast_24Hours(String alias, Table<ClanHelpCountLast_24HoursRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "VIEW");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanHelpCountLast_24Hours as(String alias) {
		return new ClanHelpCountLast_24Hours(alias, this);
	}

	/**
	 * Rename this table
	 */
	public ClanHelpCountLast_24Hours rename(String name) {
		return new ClanHelpCountLast_24Hours(name, null);
	}
}
