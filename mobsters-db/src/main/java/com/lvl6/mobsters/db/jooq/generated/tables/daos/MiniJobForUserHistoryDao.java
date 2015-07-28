/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.MiniJobForUserHistory;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.MiniJobForUserHistoryPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.records.MiniJobForUserHistoryRecord;

import java.sql.Timestamp;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.Record3;
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
public class MiniJobForUserHistoryDao extends DAOImpl<MiniJobForUserHistoryRecord, MiniJobForUserHistoryPojo, Record3<String, String, Timestamp>> {

	/**
	 * Create a new MiniJobForUserHistoryDao without any configuration
	 */
	public MiniJobForUserHistoryDao() {
		super(MiniJobForUserHistory.MINI_JOB_FOR_USER_HISTORY, MiniJobForUserHistoryPojo.class);
	}

	/**
	 * Create a new MiniJobForUserHistoryDao with an attached configuration
	 */
	public MiniJobForUserHistoryDao(Configuration configuration) {
		super(MiniJobForUserHistory.MINI_JOB_FOR_USER_HISTORY, MiniJobForUserHistoryPojo.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Record3<String, String, Timestamp> getId(MiniJobForUserHistoryPojo object) {
		return compositeKeyRecord(object.getUserId(), object.getMiniJobId(), object.getTimeCompleted());
	}

	/**
	 * Fetch records that have <code>user_id IN (values)</code>
	 */
	public List<MiniJobForUserHistoryPojo> fetchByUserId(String... values) {
		return fetch(MiniJobForUserHistory.MINI_JOB_FOR_USER_HISTORY.USER_ID, values);
	}

	/**
	 * Fetch records that have <code>mini_job_id IN (values)</code>
	 */
	public List<MiniJobForUserHistoryPojo> fetchByMiniJobId(String... values) {
		return fetch(MiniJobForUserHistory.MINI_JOB_FOR_USER_HISTORY.MINI_JOB_ID, values);
	}

	/**
	 * Fetch records that have <code>time_completed IN (values)</code>
	 */
	public List<MiniJobForUserHistoryPojo> fetchByTimeCompleted(Timestamp... values) {
		return fetch(MiniJobForUserHistory.MINI_JOB_FOR_USER_HISTORY.TIME_COMPLETED, values);
	}

	/**
	 * Fetch records that have <code>base_dmg_received IN (values)</code>
	 */
	public List<MiniJobForUserHistoryPojo> fetchByBaseDmgReceived(Integer... values) {
		return fetch(MiniJobForUserHistory.MINI_JOB_FOR_USER_HISTORY.BASE_DMG_RECEIVED, values);
	}

	/**
	 * Fetch records that have <code>time_started IN (values)</code>
	 */
	public List<MiniJobForUserHistoryPojo> fetchByTimeStarted(Timestamp... values) {
		return fetch(MiniJobForUserHistory.MINI_JOB_FOR_USER_HISTORY.TIME_STARTED, values);
	}

	/**
	 * Fetch records that have <code>user_monster_ids IN (values)</code>
	 */
	public List<MiniJobForUserHistoryPojo> fetchByUserMonsterIds(String... values) {
		return fetch(MiniJobForUserHistory.MINI_JOB_FOR_USER_HISTORY.USER_MONSTER_IDS, values);
	}
}