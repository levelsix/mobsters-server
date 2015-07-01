/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.ClanMembersCountRecord;

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
public class ClanMembersCount extends TableImpl<ClanMembersCountRecord> {

	private static final long serialVersionUID = -888403748;

	/**
	 * The reference instance of <code>mobsters.clan_members_count</code>
	 */
	public static final ClanMembersCount CLAN_MEMBERS_COUNT = new ClanMembersCount();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<ClanMembersCountRecord> getRecordType() {
		return ClanMembersCountRecord.class;
	}

	/**
	 * The column <code>mobsters.clan_members_count.clan_id</code>.
	 */
	public final TableField<ClanMembersCountRecord, String> CLAN_ID = createField("clan_id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>mobsters.clan_members_count.members</code>.
	 */
	public final TableField<ClanMembersCountRecord, Long> MEMBERS = createField("members", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaulted(true), this, "");

	/**
	 * Create a <code>mobsters.clan_members_count</code> table reference
	 */
	public ClanMembersCount() {
		this("clan_members_count", null);
	}

	/**
	 * Create an aliased <code>mobsters.clan_members_count</code> table reference
	 */
	public ClanMembersCount(String alias) {
		this(alias, CLAN_MEMBERS_COUNT);
	}

	private ClanMembersCount(String alias, Table<ClanMembersCountRecord> aliased) {
		this(alias, aliased, null);
	}

	private ClanMembersCount(String alias, Table<ClanMembersCountRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "VIEW");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanMembersCount as(String alias) {
		return new ClanMembersCount(alias, this);
	}

	/**
	 * Rename this table
	 */
	public ClanMembersCount rename(String name) {
		return new ClanMembersCount(name, null);
	}
}
