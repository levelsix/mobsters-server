/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.GiftForUserRecord;

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
public class GiftForUser extends TableImpl<GiftForUserRecord> {

	private static final long serialVersionUID = 311910126;

	/**
	 * The reference instance of <code>mobsters.gift_for_user</code>
	 */
	public static final GiftForUser GIFT_FOR_USER = new GiftForUser();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<GiftForUserRecord> getRecordType() {
		return GiftForUserRecord.class;
	}

	/**
	 * The column <code>mobsters.gift_for_user.id</code>.
	 */
	public final TableField<GiftForUserRecord, String> ID = createField("id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>mobsters.gift_for_user.gifter_user_id</code>.
	 */
	public final TableField<GiftForUserRecord, String> GIFTER_USER_ID = createField("gifter_user_id", org.jooq.impl.SQLDataType.CHAR.length(36), this, "");

	/**
	 * The column <code>mobsters.gift_for_user.receiver_user_id</code>.
	 */
	public final TableField<GiftForUserRecord, String> RECEIVER_USER_ID = createField("receiver_user_id", org.jooq.impl.SQLDataType.CHAR.length(36), this, "");

	/**
	 * The column <code>mobsters.gift_for_user.gift_id</code>.
	 */
	public final TableField<GiftForUserRecord, Integer> GIFT_ID = createField("gift_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.gift_for_user.time_of_entry</code>.
	 */
	public final TableField<GiftForUserRecord, Timestamp> TIME_OF_ENTRY = createField("time_of_entry", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

	/**
	 * The column <code>mobsters.gift_for_user.reward_id</code>.
	 */
	public final TableField<GiftForUserRecord, Integer> REWARD_ID = createField("reward_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.gift_for_user.collected</code>.
	 */
	public final TableField<GiftForUserRecord, Boolean> COLLECTED = createField("collected", org.jooq.impl.SQLDataType.BIT.defaulted(true), this, "");

	/**
	 * The column <code>mobsters.gift_for_user.minutes_till_expiration</code>. this has the lowest expiration priority. Expiration also determined by gift_type
	 */
	public final TableField<GiftForUserRecord, Integer> MINUTES_TILL_EXPIRATION = createField("minutes_till_expiration", org.jooq.impl.SQLDataType.INTEGER, this, "this has the lowest expiration priority. Expiration also determined by gift_type");

	/**
	 * The column <code>mobsters.gift_for_user.reason_for_gift</code>.
	 */
	public final TableField<GiftForUserRecord, String> REASON_FOR_GIFT = createField("reason_for_gift", org.jooq.impl.SQLDataType.VARCHAR.length(75), this, "");

	/**
	 * Create a <code>mobsters.gift_for_user</code> table reference
	 */
	public GiftForUser() {
		this("gift_for_user", null);
	}

	/**
	 * Create an aliased <code>mobsters.gift_for_user</code> table reference
	 */
	public GiftForUser(String alias) {
		this(alias, GIFT_FOR_USER);
	}

	private GiftForUser(String alias, Table<GiftForUserRecord> aliased) {
		this(alias, aliased, null);
	}

	private GiftForUser(String alias, Table<GiftForUserRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<GiftForUserRecord> getPrimaryKey() {
		return Keys.KEY_GIFT_FOR_USER_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<GiftForUserRecord>> getKeys() {
		return Arrays.<UniqueKey<GiftForUserRecord>>asList(Keys.KEY_GIFT_FOR_USER_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GiftForUser as(String alias) {
		return new GiftForUser(alias, this);
	}

	/**
	 * Rename this table
	 */
	public GiftForUser rename(String name) {
		return new GiftForUser(name, null);
	}
}
