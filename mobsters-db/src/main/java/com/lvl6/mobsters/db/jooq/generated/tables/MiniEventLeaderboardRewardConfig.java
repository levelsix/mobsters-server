/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.MiniEventLeaderboardRewardConfigRecord;

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
public class MiniEventLeaderboardRewardConfig extends TableImpl<MiniEventLeaderboardRewardConfigRecord> {

	private static final long serialVersionUID = 1561413260;

	/**
	 * The reference instance of <code>mobsters.mini_event_leaderboard_reward_config</code>
	 */
	public static final MiniEventLeaderboardRewardConfig MINI_EVENT_LEADERBOARD_REWARD_CONFIG = new MiniEventLeaderboardRewardConfig();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<MiniEventLeaderboardRewardConfigRecord> getRecordType() {
		return MiniEventLeaderboardRewardConfigRecord.class;
	}

	/**
	 * The column <code>mobsters.mini_event_leaderboard_reward_config.id</code>.
	 */
	public final TableField<MiniEventLeaderboardRewardConfigRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>mobsters.mini_event_leaderboard_reward_config.mini_event_id</code>.
	 */
	public final TableField<MiniEventLeaderboardRewardConfigRecord, Integer> MINI_EVENT_ID = createField("mini_event_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.mini_event_leaderboard_reward_config.reward_id</code>.
	 */
	public final TableField<MiniEventLeaderboardRewardConfigRecord, Integer> REWARD_ID = createField("reward_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.mini_event_leaderboard_reward_config.leaderboard_pos</code>.
	 */
	public final TableField<MiniEventLeaderboardRewardConfigRecord, Integer> LEADERBOARD_POS = createField("leaderboard_pos", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * Create a <code>mobsters.mini_event_leaderboard_reward_config</code> table reference
	 */
	public MiniEventLeaderboardRewardConfig() {
		this("mini_event_leaderboard_reward_config", null);
	}

	/**
	 * Create an aliased <code>mobsters.mini_event_leaderboard_reward_config</code> table reference
	 */
	public MiniEventLeaderboardRewardConfig(String alias) {
		this(alias, MINI_EVENT_LEADERBOARD_REWARD_CONFIG);
	}

	private MiniEventLeaderboardRewardConfig(String alias, Table<MiniEventLeaderboardRewardConfigRecord> aliased) {
		this(alias, aliased, null);
	}

	private MiniEventLeaderboardRewardConfig(String alias, Table<MiniEventLeaderboardRewardConfigRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<MiniEventLeaderboardRewardConfigRecord> getPrimaryKey() {
		return Keys.KEY_MINI_EVENT_LEADERBOARD_REWARD_CONFIG_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<MiniEventLeaderboardRewardConfigRecord>> getKeys() {
		return Arrays.<UniqueKey<MiniEventLeaderboardRewardConfigRecord>>asList(Keys.KEY_MINI_EVENT_LEADERBOARD_REWARD_CONFIG_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MiniEventLeaderboardRewardConfig as(String alias) {
		return new MiniEventLeaderboardRewardConfig(alias, this);
	}

	/**
	 * Rename this table
	 */
	public MiniEventLeaderboardRewardConfig rename(String name) {
		return new MiniEventLeaderboardRewardConfig(name, null);
	}
}
