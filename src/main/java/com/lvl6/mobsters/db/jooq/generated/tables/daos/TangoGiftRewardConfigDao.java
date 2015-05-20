/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.TangoGiftRewardConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.records.TangoGiftRewardConfigRecord;

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
public class TangoGiftRewardConfigDao extends DAOImpl<TangoGiftRewardConfigRecord, com.lvl6.mobsters.db.jooq.generated.tables.pojos.TangoGiftRewardConfig, Integer> {

	/**
	 * Create a new TangoGiftRewardConfigDao without any configuration
	 */
	public TangoGiftRewardConfigDao() {
		super(TangoGiftRewardConfig.TANGO_GIFT_REWARD_CONFIG, com.lvl6.mobsters.db.jooq.generated.tables.pojos.TangoGiftRewardConfig.class);
	}

	/**
	 * Create a new TangoGiftRewardConfigDao with an attached configuration
	 */
	public TangoGiftRewardConfigDao(Configuration configuration) {
		super(TangoGiftRewardConfig.TANGO_GIFT_REWARD_CONFIG, com.lvl6.mobsters.db.jooq.generated.tables.pojos.TangoGiftRewardConfig.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer getId(com.lvl6.mobsters.db.jooq.generated.tables.pojos.TangoGiftRewardConfig object) {
		return object.getId();
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.TangoGiftRewardConfig> fetchById(Integer... values) {
		return fetch(TangoGiftRewardConfig.TANGO_GIFT_REWARD_CONFIG.ID, values);
	}

	/**
	 * Fetch a unique record that has <code>id = value</code>
	 */
	public com.lvl6.mobsters.db.jooq.generated.tables.pojos.TangoGiftRewardConfig fetchOneById(Integer value) {
		return fetchOne(TangoGiftRewardConfig.TANGO_GIFT_REWARD_CONFIG.ID, value);
	}

	/**
	 * Fetch records that have <code>tango_gift_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.TangoGiftRewardConfig> fetchByTangoGiftId(Integer... values) {
		return fetch(TangoGiftRewardConfig.TANGO_GIFT_REWARD_CONFIG.TANGO_GIFT_ID, values);
	}

	/**
	 * Fetch records that have <code>reward_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.TangoGiftRewardConfig> fetchByRewardId(Integer... values) {
		return fetch(TangoGiftRewardConfig.TANGO_GIFT_REWARD_CONFIG.REWARD_ID, values);
	}

	/**
	 * Fetch records that have <code>chance_of_drop IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.TangoGiftRewardConfig> fetchByChanceOfDrop(Double... values) {
		return fetch(TangoGiftRewardConfig.TANGO_GIFT_REWARD_CONFIG.CHANCE_OF_DROP, values);
	}
}