/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.LockBoxEventForUserRecord;

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
public class LockBoxEventForUser extends TableImpl<LockBoxEventForUserRecord> {

	private static final long serialVersionUID = 992079202;

	/**
	 * The reference instance of <code>mobsters.lock_box_event_for_user</code>
	 */
	public static final LockBoxEventForUser LOCK_BOX_EVENT_FOR_USER = new LockBoxEventForUser();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<LockBoxEventForUserRecord> getRecordType() {
		return LockBoxEventForUserRecord.class;
	}

	/**
	 * The column <code>mobsters.lock_box_event_for_user.lock_box_event_id</code>.
	 */
	public final TableField<LockBoxEventForUserRecord, Integer> LOCK_BOX_EVENT_ID = createField("lock_box_event_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>mobsters.lock_box_event_for_user.user_id</code>.
	 */
	public final TableField<LockBoxEventForUserRecord, String> USER_ID = createField("user_id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>mobsters.lock_box_event_for_user.num_boxes</code>.
	 */
	public final TableField<LockBoxEventForUserRecord, Integer> NUM_BOXES = createField("num_boxes", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.lock_box_event_for_user.last_opening_time</code>.
	 */
	public final TableField<LockBoxEventForUserRecord, Timestamp> LAST_OPENING_TIME = createField("last_opening_time", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

	/**
	 * The column <code>mobsters.lock_box_event_for_user.num_times_completed</code>.
	 */
	public final TableField<LockBoxEventForUserRecord, Integer> NUM_TIMES_COMPLETED = createField("num_times_completed", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.lock_box_event_for_user.has_been_redeemed</code>.
	 */
	public final TableField<LockBoxEventForUserRecord, Byte> HAS_BEEN_REDEEMED = createField("has_been_redeemed", org.jooq.impl.SQLDataType.TINYINT.defaulted(true), this, "");

	/**
	 * Create a <code>mobsters.lock_box_event_for_user</code> table reference
	 */
	public LockBoxEventForUser() {
		this("lock_box_event_for_user", null);
	}

	/**
	 * Create an aliased <code>mobsters.lock_box_event_for_user</code> table reference
	 */
	public LockBoxEventForUser(String alias) {
		this(alias, LOCK_BOX_EVENT_FOR_USER);
	}

	private LockBoxEventForUser(String alias, Table<LockBoxEventForUserRecord> aliased) {
		this(alias, aliased, null);
	}

	private LockBoxEventForUser(String alias, Table<LockBoxEventForUserRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<LockBoxEventForUserRecord> getPrimaryKey() {
		return Keys.KEY_LOCK_BOX_EVENT_FOR_USER_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<LockBoxEventForUserRecord>> getKeys() {
		return Arrays.<UniqueKey<LockBoxEventForUserRecord>>asList(Keys.KEY_LOCK_BOX_EVENT_FOR_USER_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LockBoxEventForUser as(String alias) {
		return new LockBoxEventForUser(alias, this);
	}

	/**
	 * Rename this table
	 */
	public LockBoxEventForUser rename(String name) {
		return new LockBoxEventForUser(name, null);
	}
}
