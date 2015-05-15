/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.ClanChatPostRecord;

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
public class ClanChatPost extends TableImpl<ClanChatPostRecord> {

	private static final long serialVersionUID = -1597729000;

	/**
	 * The reference instance of <code>mobsters.clan_chat_post</code>
	 */
	public static final ClanChatPost CLAN_CHAT_POST = new ClanChatPost();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<ClanChatPostRecord> getRecordType() {
		return ClanChatPostRecord.class;
	}

	/**
	 * The column <code>mobsters.clan_chat_post.id</code>.
	 */
	public final TableField<ClanChatPostRecord, String> ID = createField("id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>mobsters.clan_chat_post.poster_id</code>.
	 */
	public final TableField<ClanChatPostRecord, String> POSTER_ID = createField("poster_id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>mobsters.clan_chat_post.clan_id</code>.
	 */
	public final TableField<ClanChatPostRecord, String> CLAN_ID = createField("clan_id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>mobsters.clan_chat_post.time_of_post</code>.
	 */
	public final TableField<ClanChatPostRecord, Timestamp> TIME_OF_POST = createField("time_of_post", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>mobsters.clan_chat_post.content</code>.
	 */
	public final TableField<ClanChatPostRecord, String> CONTENT = createField("content", org.jooq.impl.SQLDataType.CLOB, this, "");

	/**
	 * Create a <code>mobsters.clan_chat_post</code> table reference
	 */
	public ClanChatPost() {
		this("clan_chat_post", null);
	}

	/**
	 * Create an aliased <code>mobsters.clan_chat_post</code> table reference
	 */
	public ClanChatPost(String alias) {
		this(alias, CLAN_CHAT_POST);
	}

	private ClanChatPost(String alias, Table<ClanChatPostRecord> aliased) {
		this(alias, aliased, null);
	}

	private ClanChatPost(String alias, Table<ClanChatPostRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<ClanChatPostRecord> getPrimaryKey() {
		return Keys.KEY_CLAN_CHAT_POST_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<ClanChatPostRecord>> getKeys() {
		return Arrays.<UniqueKey<ClanChatPostRecord>>asList(Keys.KEY_CLAN_CHAT_POST_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanChatPost as(String alias) {
		return new ClanChatPost(alias, this);
	}

	/**
	 * Rename this table
	 */
	public ClanChatPost rename(String name) {
		return new ClanChatPost(name, null);
	}
}
