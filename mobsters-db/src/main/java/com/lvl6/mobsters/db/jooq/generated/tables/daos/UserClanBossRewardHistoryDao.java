/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.UserClanBossRewardHistory;
import com.lvl6.mobsters.db.jooq.generated.tables.records.UserClanBossRewardHistoryRecord;

import java.sql.Timestamp;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;


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
public class UserClanBossRewardHistoryDao extends DAOImpl<UserClanBossRewardHistoryRecord, com.lvl6.mobsters.db.jooq.generated.tables.pojos.UserClanBossRewardHistory, String> {

	/**
	 * Create a new UserClanBossRewardHistoryDao without any configuration
	 */
	public UserClanBossRewardHistoryDao() {
		super(UserClanBossRewardHistory.USER_CLAN_BOSS_REWARD_HISTORY, com.lvl6.mobsters.db.jooq.generated.tables.pojos.UserClanBossRewardHistory.class);
	}

	/**
	 * Create a new UserClanBossRewardHistoryDao with an attached configuration
	 */
	public UserClanBossRewardHistoryDao(Configuration configuration) {
		super(UserClanBossRewardHistory.USER_CLAN_BOSS_REWARD_HISTORY, com.lvl6.mobsters.db.jooq.generated.tables.pojos.UserClanBossRewardHistory.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getId(com.lvl6.mobsters.db.jooq.generated.tables.pojos.UserClanBossRewardHistory object) {
		return object.getId();
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.UserClanBossRewardHistory> fetchById(String... values) {
		return fetch(UserClanBossRewardHistory.USER_CLAN_BOSS_REWARD_HISTORY.ID, values);
	}

	/**
	 * Fetch a unique record that has <code>id = value</code>
	 */
	public com.lvl6.mobsters.db.jooq.generated.tables.pojos.UserClanBossRewardHistory fetchOneById(String value) {
		return fetchOne(UserClanBossRewardHistory.USER_CLAN_BOSS_REWARD_HISTORY.ID, value);
	}

	/**
	 * Fetch records that have <code>user_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.UserClanBossRewardHistory> fetchByUserId(String... values) {
		return fetch(UserClanBossRewardHistory.USER_CLAN_BOSS_REWARD_HISTORY.USER_ID, values);
	}

	/**
	 * Fetch records that have <code>clan_boss_reward_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.UserClanBossRewardHistory> fetchByClanBossRewardId(Integer... values) {
		return fetch(UserClanBossRewardHistory.USER_CLAN_BOSS_REWARD_HISTORY.CLAN_BOSS_REWARD_ID, values);
	}

	/**
	 * Fetch records that have <code>time_of_entry IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.UserClanBossRewardHistory> fetchByTimeOfEntry(Timestamp... values) {
		return fetch(UserClanBossRewardHistory.USER_CLAN_BOSS_REWARD_HISTORY.TIME_OF_ENTRY, values);
	}
}