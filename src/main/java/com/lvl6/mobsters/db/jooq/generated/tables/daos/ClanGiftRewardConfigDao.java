/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.ClanGiftRewardConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.records.ClanGiftRewardConfigRecord;

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
public class ClanGiftRewardConfigDao extends DAOImpl<ClanGiftRewardConfigRecord, com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanGiftRewardConfig, Integer> {

	/**
	 * Create a new ClanGiftRewardConfigDao without any configuration
	 */
	public ClanGiftRewardConfigDao() {
		super(ClanGiftRewardConfig.CLAN_GIFT_REWARD_CONFIG, com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanGiftRewardConfig.class);
	}

	/**
	 * Create a new ClanGiftRewardConfigDao with an attached configuration
	 */
	public ClanGiftRewardConfigDao(Configuration configuration) {
		super(ClanGiftRewardConfig.CLAN_GIFT_REWARD_CONFIG, com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanGiftRewardConfig.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer getId(com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanGiftRewardConfig object) {
		return object.getId();
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanGiftRewardConfig> fetchById(Integer... values) {
		return fetch(ClanGiftRewardConfig.CLAN_GIFT_REWARD_CONFIG.ID, values);
	}

	/**
	 * Fetch a unique record that has <code>id = value</code>
	 */
	public com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanGiftRewardConfig fetchOneById(Integer value) {
		return fetchOne(ClanGiftRewardConfig.CLAN_GIFT_REWARD_CONFIG.ID, value);
	}

	/**
	 * Fetch records that have <code>clan_gift_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanGiftRewardConfig> fetchByClanGiftId(Integer... values) {
		return fetch(ClanGiftRewardConfig.CLAN_GIFT_REWARD_CONFIG.CLAN_GIFT_ID, values);
	}

	/**
	 * Fetch records that have <code>reward_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanGiftRewardConfig> fetchByRewardId(Integer... values) {
		return fetch(ClanGiftRewardConfig.CLAN_GIFT_REWARD_CONFIG.REWARD_ID, values);
	}

	/**
	 * Fetch records that have <code>chance_of_drop IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanGiftRewardConfig> fetchByChanceOfDrop(Double... values) {
		return fetch(ClanGiftRewardConfig.CLAN_GIFT_REWARD_CONFIG.CHANCE_OF_DROP, values);
	}
}
