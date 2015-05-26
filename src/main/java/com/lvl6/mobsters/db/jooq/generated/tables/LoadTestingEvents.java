/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.LoadTestingEventsRecord;

import java.sql.Timestamp;
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
public class LoadTestingEvents extends TableImpl<LoadTestingEventsRecord> {

	private static final long serialVersionUID = -1002425764;

	/**
	 * The reference instance of <code>mobsters.load_testing_events</code>
	 */
	public static final LoadTestingEvents LOAD_TESTING_EVENTS = new LoadTestingEvents();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<LoadTestingEventsRecord> getRecordType() {
		return LoadTestingEventsRecord.class;
	}

	/**
	 * The column <code>mobsters.load_testing_events.id</code>.
	 */
	public final TableField<LoadTestingEventsRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>mobsters.load_testing_events.user_id</code>.
	 */
	public final TableField<LoadTestingEventsRecord, String> USER_ID = createField("user_id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>mobsters.load_testing_events.log_time</code>.
	 */
	public final TableField<LoadTestingEventsRecord, Timestamp> LOG_TIME = createField("log_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>mobsters.load_testing_events.event_type</code>.
	 */
	public final TableField<LoadTestingEventsRecord, Integer> EVENT_TYPE = createField("event_type", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>mobsters.load_testing_events.event_bytes</code>.
	 */
	public final TableField<LoadTestingEventsRecord, byte[]> EVENT_BYTES = createField("event_bytes", org.jooq.impl.SQLDataType.BLOB.nullable(false), this, "");

	/**
	 * Create a <code>mobsters.load_testing_events</code> table reference
	 */
	public LoadTestingEvents() {
		this("load_testing_events", null);
	}

	/**
	 * Create an aliased <code>mobsters.load_testing_events</code> table reference
	 */
	public LoadTestingEvents(String alias) {
		this(alias, LOAD_TESTING_EVENTS);
	}

	private LoadTestingEvents(String alias, Table<LoadTestingEventsRecord> aliased) {
		this(alias, aliased, null);
	}

	private LoadTestingEvents(String alias, Table<LoadTestingEventsRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Identity<LoadTestingEventsRecord, Integer> getIdentity() {
		return Keys.IDENTITY_LOAD_TESTING_EVENTS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<LoadTestingEventsRecord> getPrimaryKey() {
		return Keys.KEY_LOAD_TESTING_EVENTS_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<LoadTestingEventsRecord>> getKeys() {
		return Arrays.<UniqueKey<LoadTestingEventsRecord>>asList(Keys.KEY_LOAD_TESTING_EVENTS_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LoadTestingEvents as(String alias) {
		return new LoadTestingEvents(alias, this);
	}

	/**
	 * Rename this table
	 */
	public LoadTestingEvents rename(String name) {
		return new LoadTestingEvents(name, null);
	}
}
