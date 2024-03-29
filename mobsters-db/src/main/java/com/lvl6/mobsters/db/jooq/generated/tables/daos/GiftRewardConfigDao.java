/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.GiftRewardConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.GiftRewardConfigPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.records.GiftRewardConfigRecord;

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
public class GiftRewardConfigDao extends DAOImpl<GiftRewardConfigRecord, GiftRewardConfigPojo, Integer> {

	/**
	 * Create a new GiftRewardConfigDao without any configuration
	 */
	public GiftRewardConfigDao() {
		super(GiftRewardConfig.GIFT_REWARD_CONFIG, GiftRewardConfigPojo.class);
	}

	/**
	 * Create a new GiftRewardConfigDao with an attached configuration
	 */
	public GiftRewardConfigDao(Configuration configuration) {
		super(GiftRewardConfig.GIFT_REWARD_CONFIG, GiftRewardConfigPojo.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer getId(GiftRewardConfigPojo object) {
		return object.getId();
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<GiftRewardConfigPojo> fetchById(Integer... values) {
		return fetch(GiftRewardConfig.GIFT_REWARD_CONFIG.ID, values);
	}

	/**
	 * Fetch a unique record that has <code>id = value</code>
	 */
	public GiftRewardConfigPojo fetchOneById(Integer value) {
		return fetchOne(GiftRewardConfig.GIFT_REWARD_CONFIG.ID, value);
	}

	/**
	 * Fetch records that have <code>gift_id IN (values)</code>
	 */
	public List<GiftRewardConfigPojo> fetchByGiftId(Integer... values) {
		return fetch(GiftRewardConfig.GIFT_REWARD_CONFIG.GIFT_ID, values);
	}

	/**
	 * Fetch records that have <code>reward_id IN (values)</code>
	 */
	public List<GiftRewardConfigPojo> fetchByRewardId(Integer... values) {
		return fetch(GiftRewardConfig.GIFT_REWARD_CONFIG.REWARD_ID, values);
	}

	/**
	 * Fetch records that have <code>chance_of_drop IN (values)</code>
	 */
	public List<GiftRewardConfigPojo> fetchByChanceOfDrop(Double... values) {
		return fetch(GiftRewardConfig.GIFT_REWARD_CONFIG.CHANCE_OF_DROP, values);
	}
}
