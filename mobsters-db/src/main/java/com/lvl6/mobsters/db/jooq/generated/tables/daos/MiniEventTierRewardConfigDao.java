/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.MiniEventTierRewardConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.MiniEventTierRewardConfigPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.records.MiniEventTierRewardConfigRecord;

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
public class MiniEventTierRewardConfigDao extends DAOImpl<MiniEventTierRewardConfigRecord, MiniEventTierRewardConfigPojo, Integer> {

	/**
	 * Create a new MiniEventTierRewardConfigDao without any configuration
	 */
	public MiniEventTierRewardConfigDao() {
		super(MiniEventTierRewardConfig.MINI_EVENT_TIER_REWARD_CONFIG, MiniEventTierRewardConfigPojo.class);
	}

	/**
	 * Create a new MiniEventTierRewardConfigDao with an attached configuration
	 */
	public MiniEventTierRewardConfigDao(Configuration configuration) {
		super(MiniEventTierRewardConfig.MINI_EVENT_TIER_REWARD_CONFIG, MiniEventTierRewardConfigPojo.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer getId(MiniEventTierRewardConfigPojo object) {
		return object.getId();
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<MiniEventTierRewardConfigPojo> fetchById(Integer... values) {
		return fetch(MiniEventTierRewardConfig.MINI_EVENT_TIER_REWARD_CONFIG.ID, values);
	}

	/**
	 * Fetch a unique record that has <code>id = value</code>
	 */
	public MiniEventTierRewardConfigPojo fetchOneById(Integer value) {
		return fetchOne(MiniEventTierRewardConfig.MINI_EVENT_TIER_REWARD_CONFIG.ID, value);
	}

	/**
	 * Fetch records that have <code>mini_event_for_player_lvl_id IN (values)</code>
	 */
	public List<MiniEventTierRewardConfigPojo> fetchByMiniEventForPlayerLvlId(Integer... values) {
		return fetch(MiniEventTierRewardConfig.MINI_EVENT_TIER_REWARD_CONFIG.MINI_EVENT_FOR_PLAYER_LVL_ID, values);
	}

	/**
	 * Fetch records that have <code>reward_id IN (values)</code>
	 */
	public List<MiniEventTierRewardConfigPojo> fetchByRewardId(Integer... values) {
		return fetch(MiniEventTierRewardConfig.MINI_EVENT_TIER_REWARD_CONFIG.REWARD_ID, values);
	}

	/**
	 * Fetch records that have <code>reward_tier IN (values)</code>
	 */
	public List<MiniEventTierRewardConfigPojo> fetchByRewardTier(Integer... values) {
		return fetch(MiniEventTierRewardConfig.MINI_EVENT_TIER_REWARD_CONFIG.REWARD_TIER, values);
	}
}
