/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.ClanGiftForUserRecord;

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
public class ClanGiftForUser extends TableImpl<ClanGiftForUserRecord> {

	private static final long serialVersionUID = 793044239;

	/**
	 * The reference instance of <code>mobsters.clan_gift_for_user</code>
	 */
	public static final ClanGiftForUser CLAN_GIFT_FOR_USER = new ClanGiftForUser();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<ClanGiftForUserRecord> getRecordType() {
		return ClanGiftForUserRecord.class;
	}

	/**
	 * The column <code>mobsters.clan_gift_for_user.id</code>.
	 */
	public final TableField<ClanGiftForUserRecord, String> ID = createField("id", org.jooq.impl.SQLDataType.VARCHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>mobsters.clan_gift_for_user.receiver_user_id</code>.
	 */
	public final TableField<ClanGiftForUserRecord, String> RECEIVER_USER_ID = createField("receiver_user_id", org.jooq.impl.SQLDataType.VARCHAR.length(45), this, "");

	/**
	 * The column <code>mobsters.clan_gift_for_user.gifter_user_id</code>.
	 */
	public final TableField<ClanGiftForUserRecord, String> GIFTER_USER_ID = createField("gifter_user_id", org.jooq.impl.SQLDataType.VARCHAR.length(45), this, "");

	/**
	 * The column <code>mobsters.clan_gift_for_user.clan_gift_id</code>.
	 */
	public final TableField<ClanGiftForUserRecord, Integer> CLAN_GIFT_ID = createField("clan_gift_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.clan_gift_for_user.reward_id</code>.
	 */
	public final TableField<ClanGiftForUserRecord, Integer> REWARD_ID = createField("reward_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.clan_gift_for_user.time_received</code>.
	 */
	public final TableField<ClanGiftForUserRecord, Timestamp> TIME_RECEIVED = createField("time_received", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

	/**
	 * The column <code>mobsters.clan_gift_for_user.reason_for_gift</code>.
	 */
	public final TableField<ClanGiftForUserRecord, String> REASON_FOR_GIFT = createField("reason_for_gift", org.jooq.impl.SQLDataType.VARCHAR.length(45), this, "");

	/**
	 * The column <code>mobsters.clan_gift_for_user.has_been_collected</code>.
	 */
	public final TableField<ClanGiftForUserRecord, Byte> HAS_BEEN_COLLECTED = createField("has_been_collected", org.jooq.impl.SQLDataType.TINYINT, this, "");

	/**
	 * Create a <code>mobsters.clan_gift_for_user</code> table reference
	 */
	public ClanGiftForUser() {
		this("clan_gift_for_user", null);
	}

	/**
	 * Create an aliased <code>mobsters.clan_gift_for_user</code> table reference
	 */
	public ClanGiftForUser(String alias) {
		this(alias, CLAN_GIFT_FOR_USER);
	}

	private ClanGiftForUser(String alias, Table<ClanGiftForUserRecord> aliased) {
		this(alias, aliased, null);
	}

	private ClanGiftForUser(String alias, Table<ClanGiftForUserRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<ClanGiftForUserRecord> getPrimaryKey() {
		return Keys.KEY_CLAN_GIFT_FOR_USER_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<ClanGiftForUserRecord>> getKeys() {
		return Arrays.<UniqueKey<ClanGiftForUserRecord>>asList(Keys.KEY_CLAN_GIFT_FOR_USER_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanGiftForUser as(String alias) {
		return new ClanGiftForUser(alias, this);
	}

	/**
	 * Rename this table
	 */
	public ClanGiftForUser rename(String name) {
		return new ClanGiftForUser(name, null);
	}
}