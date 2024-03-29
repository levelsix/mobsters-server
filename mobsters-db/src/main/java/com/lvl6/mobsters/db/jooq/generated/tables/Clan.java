/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.ClanRecord;

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
public class Clan extends TableImpl<ClanRecord> {

	private static final long serialVersionUID = -1032769042;

	/**
	 * The reference instance of <code>mobsters.clan</code>
	 */
	public static final Clan CLAN = new Clan();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<ClanRecord> getRecordType() {
		return ClanRecord.class;
	}

	/**
	 * The column <code>mobsters.clan.id</code>.
	 */
	public final TableField<ClanRecord, String> ID = createField("id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>mobsters.clan.name</code>.
	 */
	public final TableField<ClanRecord, String> NAME = createField("name", org.jooq.impl.SQLDataType.VARCHAR.length(127), this, "");

	/**
	 * The column <code>mobsters.clan.create_time</code>.
	 */
	public final TableField<ClanRecord, Timestamp> CREATE_TIME = createField("create_time", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

	/**
	 * The column <code>mobsters.clan.description</code>.
	 */
	public final TableField<ClanRecord, String> DESCRIPTION = createField("description", org.jooq.impl.SQLDataType.CLOB, this, "");

	/**
	 * The column <code>mobsters.clan.tag</code>.
	 */
	public final TableField<ClanRecord, String> TAG = createField("tag", org.jooq.impl.SQLDataType.VARCHAR.length(127), this, "");

	/**
	 * The column <code>mobsters.clan.request_to_join_required</code>. This determines if the clan requires people to send a request to join this clan or whether people can just join this clan.
	 */
	public final TableField<ClanRecord, Byte> REQUEST_TO_JOIN_REQUIRED = createField("request_to_join_required", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaulted(true), this, "This determines if the clan requires people to send a request to join this clan or whether people can just join this clan.");

	/**
	 * The column <code>mobsters.clan.clan_icon_id</code>.
	 */
	public final TableField<ClanRecord, Integer> CLAN_ICON_ID = createField("clan_icon_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * Create a <code>mobsters.clan</code> table reference
	 */
	public Clan() {
		this("clan", null);
	}

	/**
	 * Create an aliased <code>mobsters.clan</code> table reference
	 */
	public Clan(String alias) {
		this(alias, CLAN);
	}

	private Clan(String alias, Table<ClanRecord> aliased) {
		this(alias, aliased, null);
	}

	private Clan(String alias, Table<ClanRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<ClanRecord> getPrimaryKey() {
		return Keys.KEY_CLAN_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<ClanRecord>> getKeys() {
		return Arrays.<UniqueKey<ClanRecord>>asList(Keys.KEY_CLAN_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Clan as(String alias) {
		return new Clan(alias, this);
	}

	/**
	 * Rename this table
	 */
	public Clan rename(String name) {
		return new Clan(name, null);
	}
}
