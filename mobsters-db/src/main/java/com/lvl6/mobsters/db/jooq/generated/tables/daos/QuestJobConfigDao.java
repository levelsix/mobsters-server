/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.QuestJobConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.QuestJobConfigPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.records.QuestJobConfigRecord;

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
public class QuestJobConfigDao extends DAOImpl<QuestJobConfigRecord, QuestJobConfigPojo, Integer> {

	/**
	 * Create a new QuestJobConfigDao without any configuration
	 */
	public QuestJobConfigDao() {
		super(QuestJobConfig.QUEST_JOB_CONFIG, QuestJobConfigPojo.class);
	}

	/**
	 * Create a new QuestJobConfigDao with an attached configuration
	 */
	public QuestJobConfigDao(Configuration configuration) {
		super(QuestJobConfig.QUEST_JOB_CONFIG, QuestJobConfigPojo.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer getId(QuestJobConfigPojo object) {
		return object.getId();
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<QuestJobConfigPojo> fetchById(Integer... values) {
		return fetch(QuestJobConfig.QUEST_JOB_CONFIG.ID, values);
	}

	/**
	 * Fetch a unique record that has <code>id = value</code>
	 */
	public QuestJobConfigPojo fetchOneById(Integer value) {
		return fetchOne(QuestJobConfig.QUEST_JOB_CONFIG.ID, value);
	}

	/**
	 * Fetch records that have <code>quest_id IN (values)</code>
	 */
	public List<QuestJobConfigPojo> fetchByQuestId(Integer... values) {
		return fetch(QuestJobConfig.QUEST_JOB_CONFIG.QUEST_ID, values);
	}

	/**
	 * Fetch records that have <code>quest_job_type IN (values)</code>
	 */
	public List<QuestJobConfigPojo> fetchByQuestJobType(String... values) {
		return fetch(QuestJobConfig.QUEST_JOB_CONFIG.QUEST_JOB_TYPE, values);
	}

	/**
	 * Fetch records that have <code>description IN (values)</code>
	 */
	public List<QuestJobConfigPojo> fetchByDescription(String... values) {
		return fetch(QuestJobConfig.QUEST_JOB_CONFIG.DESCRIPTION, values);
	}

	/**
	 * Fetch records that have <code>static_data_id IN (values)</code>
	 */
	public List<QuestJobConfigPojo> fetchByStaticDataId(Integer... values) {
		return fetch(QuestJobConfig.QUEST_JOB_CONFIG.STATIC_DATA_ID, values);
	}

	/**
	 * Fetch records that have <code>quantity IN (values)</code>
	 */
	public List<QuestJobConfigPojo> fetchByQuantity(Integer... values) {
		return fetch(QuestJobConfig.QUEST_JOB_CONFIG.QUANTITY, values);
	}

	/**
	 * Fetch records that have <code>priority IN (values)</code>
	 */
	public List<QuestJobConfigPojo> fetchByPriority(Integer... values) {
		return fetch(QuestJobConfig.QUEST_JOB_CONFIG.PRIORITY, values);
	}

	/**
	 * Fetch records that have <code>city_id IN (values)</code>
	 */
	public List<QuestJobConfigPojo> fetchByCityId(Integer... values) {
		return fetch(QuestJobConfig.QUEST_JOB_CONFIG.CITY_ID, values);
	}

	/**
	 * Fetch records that have <code>city_asset_num IN (values)</code>
	 */
	public List<QuestJobConfigPojo> fetchByCityAssetNum(Integer... values) {
		return fetch(QuestJobConfig.QUEST_JOB_CONFIG.CITY_ASSET_NUM, values);
	}
}