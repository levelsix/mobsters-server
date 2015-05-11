/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.BoosterPackConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.records.BoosterPackConfigRecord;

import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;
import org.jooq.types.UInteger;


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
public class BoosterPackConfigDao extends DAOImpl<BoosterPackConfigRecord, com.lvl6.mobsters.db.jooq.generated.tables.pojos.BoosterPackConfig, Integer> {

	/**
	 * Create a new BoosterPackConfigDao without any configuration
	 */
	public BoosterPackConfigDao() {
		super(BoosterPackConfig.BOOSTER_PACK_CONFIG, com.lvl6.mobsters.db.jooq.generated.tables.pojos.BoosterPackConfig.class);
	}

	/**
	 * Create a new BoosterPackConfigDao with an attached configuration
	 */
	public BoosterPackConfigDao(Configuration configuration) {
		super(BoosterPackConfig.BOOSTER_PACK_CONFIG, com.lvl6.mobsters.db.jooq.generated.tables.pojos.BoosterPackConfig.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer getId(com.lvl6.mobsters.db.jooq.generated.tables.pojos.BoosterPackConfig object) {
		return object.getId();
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.BoosterPackConfig> fetchById(Integer... values) {
		return fetch(BoosterPackConfig.BOOSTER_PACK_CONFIG.ID, values);
	}

	/**
	 * Fetch a unique record that has <code>id = value</code>
	 */
	public com.lvl6.mobsters.db.jooq.generated.tables.pojos.BoosterPackConfig fetchOneById(Integer value) {
		return fetchOne(BoosterPackConfig.BOOSTER_PACK_CONFIG.ID, value);
	}

	/**
	 * Fetch records that have <code>name IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.BoosterPackConfig> fetchByName(String... values) {
		return fetch(BoosterPackConfig.BOOSTER_PACK_CONFIG.NAME, values);
	}

	/**
	 * Fetch records that have <code>gem_price IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.BoosterPackConfig> fetchByGemPrice(UInteger... values) {
		return fetch(BoosterPackConfig.BOOSTER_PACK_CONFIG.GEM_PRICE, values);
	}

	/**
	 * Fetch records that have <code>gacha_credits_price IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.BoosterPackConfig> fetchByGachaCreditsPrice(Integer... values) {
		return fetch(BoosterPackConfig.BOOSTER_PACK_CONFIG.GACHA_CREDITS_PRICE, values);
	}

	/**
	 * Fetch records that have <code>list_background_img_name IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.BoosterPackConfig> fetchByListBackgroundImgName(String... values) {
		return fetch(BoosterPackConfig.BOOSTER_PACK_CONFIG.LIST_BACKGROUND_IMG_NAME, values);
	}

	/**
	 * Fetch records that have <code>list_description IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.BoosterPackConfig> fetchByListDescription(String... values) {
		return fetch(BoosterPackConfig.BOOSTER_PACK_CONFIG.LIST_DESCRIPTION, values);
	}

	/**
	 * Fetch records that have <code>nav_bar_img_name IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.BoosterPackConfig> fetchByNavBarImgName(String... values) {
		return fetch(BoosterPackConfig.BOOSTER_PACK_CONFIG.NAV_BAR_IMG_NAME, values);
	}

	/**
	 * Fetch records that have <code>nav_title_img_name IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.BoosterPackConfig> fetchByNavTitleImgName(String... values) {
		return fetch(BoosterPackConfig.BOOSTER_PACK_CONFIG.NAV_TITLE_IMG_NAME, values);
	}

	/**
	 * Fetch records that have <code>machine_img_name IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.BoosterPackConfig> fetchByMachineImgName(String... values) {
		return fetch(BoosterPackConfig.BOOSTER_PACK_CONFIG.MACHINE_IMG_NAME, values);
	}

	/**
	 * Fetch records that have <code>exp_per_item IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.BoosterPackConfig> fetchByExpPerItem(Integer... values) {
		return fetch(BoosterPackConfig.BOOSTER_PACK_CONFIG.EXP_PER_ITEM, values);
	}

	/**
	 * Fetch records that have <code>display_to_user IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.BoosterPackConfig> fetchByDisplayToUser(Boolean... values) {
		return fetch(BoosterPackConfig.BOOSTER_PACK_CONFIG.DISPLAY_TO_USER, values);
	}

	/**
	 * Fetch records that have <code>rigged_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.BoosterPackConfig> fetchByRiggedId(Integer... values) {
		return fetch(BoosterPackConfig.BOOSTER_PACK_CONFIG.RIGGED_ID, values);
	}

	/**
	 * Fetch records that have <code>type IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.BoosterPackConfig> fetchByType(String... values) {
		return fetch(BoosterPackConfig.BOOSTER_PACK_CONFIG.TYPE, values);
	}
}