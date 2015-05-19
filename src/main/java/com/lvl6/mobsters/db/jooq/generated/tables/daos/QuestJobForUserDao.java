/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.QuestJobForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.records.QuestJobForUserRecord;

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
public class QuestJobForUserDao extends DAOImpl<QuestJobForUserRecord, com.lvl6.mobsters.db.jooq.generated.tables.pojos.QuestJobForUser, Record3<String, Integer, Integer>> {

	/**
	 * Create a new QuestJobForUserDao without any configuration
	 */
	public QuestJobForUserDao() {
		super(QuestJobForUser.QUEST_JOB_FOR_USER, com.lvl6.mobsters.db.jooq.generated.tables.pojos.QuestJobForUser.class);
	}

	/**
	 * Create a new QuestJobForUserDao with an attached configuration
	 */
	public QuestJobForUserDao(Configuration configuration) {
		super(QuestJobForUser.QUEST_JOB_FOR_USER, com.lvl6.mobsters.db.jooq.generated.tables.pojos.QuestJobForUser.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Record3<String, Integer, Integer> getId(com.lvl6.mobsters.db.jooq.generated.tables.pojos.QuestJobForUser object) {
		return compositeKeyRecord(object.getUserId(), object.getQuestId(), object.getQuestJobId());
	}

	/**
	 * Fetch records that have <code>user_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.QuestJobForUser> fetchByUserId(String... values) {
		return fetch(QuestJobForUser.QUEST_JOB_FOR_USER.USER_ID, values);
	}

	/**
	 * Fetch records that have <code>quest_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.QuestJobForUser> fetchByQuestId(Integer... values) {
		return fetch(QuestJobForUser.QUEST_JOB_FOR_USER.QUEST_ID, values);
	}

	/**
	 * Fetch records that have <code>quest_job_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.QuestJobForUser> fetchByQuestJobId(Integer... values) {
		return fetch(QuestJobForUser.QUEST_JOB_FOR_USER.QUEST_JOB_ID, values);
	}

	/**
	 * Fetch records that have <code>is_complete IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.QuestJobForUser> fetchByIsComplete(Boolean... values) {
		return fetch(QuestJobForUser.QUEST_JOB_FOR_USER.IS_COMPLETE, values);
	}

	/**
	 * Fetch records that have <code>progress IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.QuestJobForUser> fetchByProgress(Integer... values) {
		return fetch(QuestJobForUser.QUEST_JOB_FOR_USER.PROGRESS, values);
	}
}
