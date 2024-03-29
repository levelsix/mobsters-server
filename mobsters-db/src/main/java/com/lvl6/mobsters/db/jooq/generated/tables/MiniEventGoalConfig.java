/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.MiniEventGoalConfigRecord;

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
public class MiniEventGoalConfig extends TableImpl<MiniEventGoalConfigRecord> {

	private static final long serialVersionUID = -791077093;

	/**
	 * The reference instance of <code>mobsters.mini_event_goal_config</code>
	 */
	public static final MiniEventGoalConfig MINI_EVENT_GOAL_CONFIG = new MiniEventGoalConfig();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<MiniEventGoalConfigRecord> getRecordType() {
		return MiniEventGoalConfigRecord.class;
	}

	/**
	 * The column <code>mobsters.mini_event_goal_config.id</code>.
	 */
	public final TableField<MiniEventGoalConfigRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>mobsters.mini_event_goal_config.mini_event_id</code>.
	 */
	public final TableField<MiniEventGoalConfigRecord, Integer> MINI_EVENT_ID = createField("mini_event_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.mini_event_goal_config.type</code>.
	 */
	public final TableField<MiniEventGoalConfigRecord, String> TYPE = createField("type", org.jooq.impl.SQLDataType.VARCHAR.length(75), this, "");

	/**
	 * The column <code>mobsters.mini_event_goal_config.amt</code>. this specifies how much of something this goal needs in order to be done
	 */
	public final TableField<MiniEventGoalConfigRecord, Integer> AMT = createField("amt", org.jooq.impl.SQLDataType.INTEGER, this, "this specifies how much of something this goal needs in order to be done");

	/**
	 * The column <code>mobsters.mini_event_goal_config.description</code>.
	 */
	public final TableField<MiniEventGoalConfigRecord, String> DESCRIPTION = createField("description", org.jooq.impl.SQLDataType.CLOB, this, "");

	/**
	 * The column <code>mobsters.mini_event_goal_config.pts_reward</code>. points rewarded for doing this goal
	 */
	public final TableField<MiniEventGoalConfigRecord, Integer> PTS_REWARD = createField("pts_reward", org.jooq.impl.SQLDataType.INTEGER, this, "points rewarded for doing this goal");

	/**
	 * The column <code>mobsters.mini_event_goal_config.action_description</code>.
	 */
	public final TableField<MiniEventGoalConfigRecord, String> ACTION_DESCRIPTION = createField("action_description", org.jooq.impl.SQLDataType.VARCHAR.length(45), this, "");

	/**
	 * Create a <code>mobsters.mini_event_goal_config</code> table reference
	 */
	public MiniEventGoalConfig() {
		this("mini_event_goal_config", null);
	}

	/**
	 * Create an aliased <code>mobsters.mini_event_goal_config</code> table reference
	 */
	public MiniEventGoalConfig(String alias) {
		this(alias, MINI_EVENT_GOAL_CONFIG);
	}

	private MiniEventGoalConfig(String alias, Table<MiniEventGoalConfigRecord> aliased) {
		this(alias, aliased, null);
	}

	private MiniEventGoalConfig(String alias, Table<MiniEventGoalConfigRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<MiniEventGoalConfigRecord> getPrimaryKey() {
		return Keys.KEY_MINI_EVENT_GOAL_CONFIG_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<MiniEventGoalConfigRecord>> getKeys() {
		return Arrays.<UniqueKey<MiniEventGoalConfigRecord>>asList(Keys.KEY_MINI_EVENT_GOAL_CONFIG_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MiniEventGoalConfig as(String alias) {
		return new MiniEventGoalConfig(alias, this);
	}

	/**
	 * Rename this table
	 */
	public MiniEventGoalConfig rename(String name) {
		return new MiniEventGoalConfig(name, null);
	}
}
