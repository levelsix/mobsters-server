/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.UserClanBossRewardHistoryRecord;

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
public class UserClanBossRewardHistory extends TableImpl<UserClanBossRewardHistoryRecord> {

	private static final long serialVersionUID = -1533611250;

	/**
	 * The reference instance of <code>mobsters.user_clan_boss_reward_history</code>
	 */
	public static final UserClanBossRewardHistory USER_CLAN_BOSS_REWARD_HISTORY = new UserClanBossRewardHistory();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<UserClanBossRewardHistoryRecord> getRecordType() {
		return UserClanBossRewardHistoryRecord.class;
	}

	/**
	 * The column <code>mobsters.user_clan_boss_reward_history.id</code>.
	 */
	public final TableField<UserClanBossRewardHistoryRecord, String> ID = createField("id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>mobsters.user_clan_boss_reward_history.user_id</code>.
	 */
	public final TableField<UserClanBossRewardHistoryRecord, String> USER_ID = createField("user_id", org.jooq.impl.SQLDataType.CHAR.length(36), this, "");

	/**
	 * The column <code>mobsters.user_clan_boss_reward_history.clan_boss_reward_id</code>.
	 */
	public final TableField<UserClanBossRewardHistoryRecord, Integer> CLAN_BOSS_REWARD_ID = createField("clan_boss_reward_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.user_clan_boss_reward_history.time_of_entry</code>.
	 */
	public final TableField<UserClanBossRewardHistoryRecord, Timestamp> TIME_OF_ENTRY = createField("time_of_entry", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

	/**
	 * Create a <code>mobsters.user_clan_boss_reward_history</code> table reference
	 */
	public UserClanBossRewardHistory() {
		this("user_clan_boss_reward_history", null);
	}

	/**
	 * Create an aliased <code>mobsters.user_clan_boss_reward_history</code> table reference
	 */
	public UserClanBossRewardHistory(String alias) {
		this(alias, USER_CLAN_BOSS_REWARD_HISTORY);
	}

	private UserClanBossRewardHistory(String alias, Table<UserClanBossRewardHistoryRecord> aliased) {
		this(alias, aliased, null);
	}

	private UserClanBossRewardHistory(String alias, Table<UserClanBossRewardHistoryRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<UserClanBossRewardHistoryRecord> getPrimaryKey() {
		return Keys.KEY_USER_CLAN_BOSS_REWARD_HISTORY_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<UserClanBossRewardHistoryRecord>> getKeys() {
		return Arrays.<UniqueKey<UserClanBossRewardHistoryRecord>>asList(Keys.KEY_USER_CLAN_BOSS_REWARD_HISTORY_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserClanBossRewardHistory as(String alias) {
		return new UserClanBossRewardHistory(alias, this);
	}

	/**
	 * Rename this table
	 */
	public UserClanBossRewardHistory rename(String name) {
		return new UserClanBossRewardHistory(name, null);
	}
}
