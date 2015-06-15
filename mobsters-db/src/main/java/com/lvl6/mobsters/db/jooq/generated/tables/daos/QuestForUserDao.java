/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.QuestForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.records.QuestForUserRecord;

import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.Record2;
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
public class QuestForUserDao extends DAOImpl<QuestForUserRecord, com.lvl6.mobsters.db.jooq.generated.tables.pojos.QuestForUser, Record2<String, Integer>> {

	/**
	 * Create a new QuestForUserDao without any configuration
	 */
	public QuestForUserDao() {
		super(QuestForUser.QUEST_FOR_USER, com.lvl6.mobsters.db.jooq.generated.tables.pojos.QuestForUser.class);
	}

	/**
	 * Create a new QuestForUserDao with an attached configuration
	 */
	public QuestForUserDao(Configuration configuration) {
		super(QuestForUser.QUEST_FOR_USER, com.lvl6.mobsters.db.jooq.generated.tables.pojos.QuestForUser.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Record2<String, Integer> getId(com.lvl6.mobsters.db.jooq.generated.tables.pojos.QuestForUser object) {
		return compositeKeyRecord(object.getUserId(), object.getQuestId());
	}

	/**
	 * Fetch records that have <code>user_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.QuestForUser> fetchByUserId(String... values) {
		return fetch(QuestForUser.QUEST_FOR_USER.USER_ID, values);
	}

	/**
	 * Fetch records that have <code>quest_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.QuestForUser> fetchByQuestId(Integer... values) {
		return fetch(QuestForUser.QUEST_FOR_USER.QUEST_ID, values);
	}

	/**
	 * Fetch records that have <code>is_redeemed IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.QuestForUser> fetchByIsRedeemed(Byte... values) {
		return fetch(QuestForUser.QUEST_FOR_USER.IS_REDEEMED, values);
	}

	/**
	 * Fetch records that have <code>is_complete IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.QuestForUser> fetchByIsComplete(Byte... values) {
		return fetch(QuestForUser.QUEST_FOR_USER.IS_COMPLETE, values);
	}
}