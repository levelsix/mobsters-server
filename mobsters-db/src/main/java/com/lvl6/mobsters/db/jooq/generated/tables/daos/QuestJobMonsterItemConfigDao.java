/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.QuestJobMonsterItemConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.QuestJobMonsterItemConfigPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.records.QuestJobMonsterItemConfigRecord;

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
public class QuestJobMonsterItemConfigDao extends DAOImpl<QuestJobMonsterItemConfigRecord, QuestJobMonsterItemConfigPojo, Record2<Integer, Integer>> {

	/**
	 * Create a new QuestJobMonsterItemConfigDao without any configuration
	 */
	public QuestJobMonsterItemConfigDao() {
		super(QuestJobMonsterItemConfig.QUEST_JOB_MONSTER_ITEM_CONFIG, QuestJobMonsterItemConfigPojo.class);
	}

	/**
	 * Create a new QuestJobMonsterItemConfigDao with an attached configuration
	 */
	public QuestJobMonsterItemConfigDao(Configuration configuration) {
		super(QuestJobMonsterItemConfig.QUEST_JOB_MONSTER_ITEM_CONFIG, QuestJobMonsterItemConfigPojo.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Record2<Integer, Integer> getId(QuestJobMonsterItemConfigPojo object) {
		return compositeKeyRecord(object.getQuestJobId(), object.getMonsterId());
	}

	/**
	 * Fetch records that have <code>quest_job_id IN (values)</code>
	 */
	public List<QuestJobMonsterItemConfigPojo> fetchByQuestJobId(Integer... values) {
		return fetch(QuestJobMonsterItemConfig.QUEST_JOB_MONSTER_ITEM_CONFIG.QUEST_JOB_ID, values);
	}

	/**
	 * Fetch records that have <code>monster_id IN (values)</code>
	 */
	public List<QuestJobMonsterItemConfigPojo> fetchByMonsterId(Integer... values) {
		return fetch(QuestJobMonsterItemConfig.QUEST_JOB_MONSTER_ITEM_CONFIG.MONSTER_ID, values);
	}

	/**
	 * Fetch records that have <code>item_id IN (values)</code>
	 */
	public List<QuestJobMonsterItemConfigPojo> fetchByItemId(Integer... values) {
		return fetch(QuestJobMonsterItemConfig.QUEST_JOB_MONSTER_ITEM_CONFIG.ITEM_ID, values);
	}

	/**
	 * Fetch records that have <code>item_drop_rate IN (values)</code>
	 */
	public List<QuestJobMonsterItemConfigPojo> fetchByItemDropRate(Double... values) {
		return fetch(QuestJobMonsterItemConfig.QUEST_JOB_MONSTER_ITEM_CONFIG.ITEM_DROP_RATE, values);
	}
}
