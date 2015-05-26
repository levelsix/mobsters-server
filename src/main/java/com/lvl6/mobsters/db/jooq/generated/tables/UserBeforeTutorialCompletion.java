/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.UserBeforeTutorialCompletionRecord;

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
public class UserBeforeTutorialCompletion extends TableImpl<UserBeforeTutorialCompletionRecord> {

	private static final long serialVersionUID = 1918152844;

	/**
	 * The reference instance of <code>mobsters.user_before_tutorial_completion</code>
	 */
	public static final UserBeforeTutorialCompletion USER_BEFORE_TUTORIAL_COMPLETION = new UserBeforeTutorialCompletion();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<UserBeforeTutorialCompletionRecord> getRecordType() {
		return UserBeforeTutorialCompletionRecord.class;
	}

	/**
	 * The column <code>mobsters.user_before_tutorial_completion.id</code>.
	 */
	public final TableField<UserBeforeTutorialCompletionRecord, String> ID = createField("id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>mobsters.user_before_tutorial_completion.open_udid</code>.
	 */
	public final TableField<UserBeforeTutorialCompletionRecord, String> OPEN_UDID = createField("open_udid", org.jooq.impl.SQLDataType.VARCHAR.length(250), this, "");

	/**
	 * The column <code>mobsters.user_before_tutorial_completion.udid</code>.
	 */
	public final TableField<UserBeforeTutorialCompletionRecord, String> UDID = createField("udid", org.jooq.impl.SQLDataType.VARCHAR.length(250), this, "");

	/**
	 * The column <code>mobsters.user_before_tutorial_completion.mac</code>.
	 */
	public final TableField<UserBeforeTutorialCompletionRecord, String> MAC = createField("mac", org.jooq.impl.SQLDataType.VARCHAR.length(250), this, "");

	/**
	 * The column <code>mobsters.user_before_tutorial_completion.advertiser_id</code>.
	 */
	public final TableField<UserBeforeTutorialCompletionRecord, String> ADVERTISER_ID = createField("advertiser_id", org.jooq.impl.SQLDataType.VARCHAR.length(250), this, "");

	/**
	 * The column <code>mobsters.user_before_tutorial_completion.create_time</code>.
	 */
	public final TableField<UserBeforeTutorialCompletionRecord, Timestamp> CREATE_TIME = createField("create_time", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

	/**
	 * Create a <code>mobsters.user_before_tutorial_completion</code> table reference
	 */
	public UserBeforeTutorialCompletion() {
		this("user_before_tutorial_completion", null);
	}

	/**
	 * Create an aliased <code>mobsters.user_before_tutorial_completion</code> table reference
	 */
	public UserBeforeTutorialCompletion(String alias) {
		this(alias, USER_BEFORE_TUTORIAL_COMPLETION);
	}

	private UserBeforeTutorialCompletion(String alias, Table<UserBeforeTutorialCompletionRecord> aliased) {
		this(alias, aliased, null);
	}

	private UserBeforeTutorialCompletion(String alias, Table<UserBeforeTutorialCompletionRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<UserBeforeTutorialCompletionRecord> getPrimaryKey() {
		return Keys.KEY_USER_BEFORE_TUTORIAL_COMPLETION_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<UserBeforeTutorialCompletionRecord>> getKeys() {
		return Arrays.<UniqueKey<UserBeforeTutorialCompletionRecord>>asList(Keys.KEY_USER_BEFORE_TUTORIAL_COMPLETION_PRIMARY, Keys.KEY_USER_BEFORE_TUTORIAL_COMPLETION_USER_BEFORE_TUTORIAL_COMPLETION_2eOPEN_UDID, Keys.KEY_USER_BEFORE_TUTORIAL_COMPLETION_USER_BEFORE_TUTORIAL_COMPLETION_2eCREATE_TIME);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserBeforeTutorialCompletion as(String alias) {
		return new UserBeforeTutorialCompletion(alias, this);
	}

	/**
	 * Rename this table
	 */
	public UserBeforeTutorialCompletion rename(String name) {
		return new UserBeforeTutorialCompletion(name, null);
	}
}
