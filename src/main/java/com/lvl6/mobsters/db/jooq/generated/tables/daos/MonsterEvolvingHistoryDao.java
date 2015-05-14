/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.MonsterEvolvingHistory;
import com.lvl6.mobsters.db.jooq.generated.tables.records.MonsterEvolvingHistoryRecord;

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
public class MonsterEvolvingHistoryDao extends DAOImpl<MonsterEvolvingHistoryRecord, com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterEvolvingHistory, String> {

	/**
	 * Create a new MonsterEvolvingHistoryDao without any configuration
	 */
	public MonsterEvolvingHistoryDao() {
		super(MonsterEvolvingHistory.MONSTER_EVOLVING_HISTORY, com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterEvolvingHistory.class);
	}

	/**
	 * Create a new MonsterEvolvingHistoryDao with an attached configuration
	 */
	public MonsterEvolvingHistoryDao(Configuration configuration) {
		super(MonsterEvolvingHistory.MONSTER_EVOLVING_HISTORY, com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterEvolvingHistory.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getId(com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterEvolvingHistory object) {
		return object.getId();
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterEvolvingHistory> fetchById(String... values) {
		return fetch(MonsterEvolvingHistory.MONSTER_EVOLVING_HISTORY.ID, values);
	}

	/**
	 * Fetch a unique record that has <code>id = value</code>
	 */
	public com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterEvolvingHistory fetchOneById(String value) {
		return fetchOne(MonsterEvolvingHistory.MONSTER_EVOLVING_HISTORY.ID, value);
	}

	/**
	 * Fetch records that have <code>user_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterEvolvingHistory> fetchByUserId(String... values) {
		return fetch(MonsterEvolvingHistory.MONSTER_EVOLVING_HISTORY.USER_ID, values);
	}

	/**
	 * Fetch records that have <code>user_monster_id_one IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterEvolvingHistory> fetchByUserMonsterIdOne(String... values) {
		return fetch(MonsterEvolvingHistory.MONSTER_EVOLVING_HISTORY.USER_MONSTER_ID_ONE, values);
	}

	/**
	 * Fetch records that have <code>user_monster_id_two IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterEvolvingHistory> fetchByUserMonsterIdTwo(String... values) {
		return fetch(MonsterEvolvingHistory.MONSTER_EVOLVING_HISTORY.USER_MONSTER_ID_TWO, values);
	}

	/**
	 * Fetch records that have <code>catalyst_user_monster_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterEvolvingHistory> fetchByCatalystUserMonsterId(String... values) {
		return fetch(MonsterEvolvingHistory.MONSTER_EVOLVING_HISTORY.CATALYST_USER_MONSTER_ID, values);
	}

	/**
	 * Fetch records that have <code>start_time IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterEvolvingHistory> fetchByStartTime(Timestamp... values) {
		return fetch(MonsterEvolvingHistory.MONSTER_EVOLVING_HISTORY.START_TIME, values);
	}

	/**
	 * Fetch records that have <code>time_of_entry IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterEvolvingHistory> fetchByTimeOfEntry(Timestamp... values) {
		return fetch(MonsterEvolvingHistory.MONSTER_EVOLVING_HISTORY.TIME_OF_ENTRY, values);
	}

	/**
	 * Fetch records that have <code>end_time IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterEvolvingHistory> fetchByEndTime(Timestamp... values) {
		return fetch(MonsterEvolvingHistory.MONSTER_EVOLVING_HISTORY.END_TIME, values);
	}
}
