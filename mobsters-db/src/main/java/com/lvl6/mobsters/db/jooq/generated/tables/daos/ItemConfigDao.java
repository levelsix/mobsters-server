/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.ItemConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.ItemConfigPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.records.ItemConfigRecord;

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
public class ItemConfigDao extends DAOImpl<ItemConfigRecord, ItemConfigPojo, Integer> {

	/**
	 * Create a new ItemConfigDao without any configuration
	 */
	public ItemConfigDao() {
		super(ItemConfig.ITEM_CONFIG, ItemConfigPojo.class);
	}

	/**
	 * Create a new ItemConfigDao with an attached configuration
	 */
	public ItemConfigDao(Configuration configuration) {
		super(ItemConfig.ITEM_CONFIG, ItemConfigPojo.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer getId(ItemConfigPojo object) {
		return object.getId();
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<ItemConfigPojo> fetchById(Integer... values) {
		return fetch(ItemConfig.ITEM_CONFIG.ID, values);
	}

	/**
	 * Fetch a unique record that has <code>id = value</code>
	 */
	public ItemConfigPojo fetchOneById(Integer value) {
		return fetchOne(ItemConfig.ITEM_CONFIG.ID, value);
	}

	/**
	 * Fetch records that have <code>name IN (values)</code>
	 */
	public List<ItemConfigPojo> fetchByName(String... values) {
		return fetch(ItemConfig.ITEM_CONFIG.NAME, values);
	}

	/**
	 * Fetch records that have <code>short_name IN (values)</code>
	 */
	public List<ItemConfigPojo> fetchByShortName(String... values) {
		return fetch(ItemConfig.ITEM_CONFIG.SHORT_NAME, values);
	}

	/**
	 * Fetch records that have <code>img_name IN (values)</code>
	 */
	public List<ItemConfigPojo> fetchByImgName(String... values) {
		return fetch(ItemConfig.ITEM_CONFIG.IMG_NAME, values);
	}

	/**
	 * Fetch records that have <code>item_type IN (values)</code>
	 */
	public List<ItemConfigPojo> fetchByItemType(String... values) {
		return fetch(ItemConfig.ITEM_CONFIG.ITEM_TYPE, values);
	}

	/**
	 * Fetch records that have <code>static_data_id IN (values)</code>
	 */
	public List<ItemConfigPojo> fetchByStaticDataId(Integer... values) {
		return fetch(ItemConfig.ITEM_CONFIG.STATIC_DATA_ID, values);
	}

	/**
	 * Fetch records that have <code>amount IN (values)</code>
	 */
	public List<ItemConfigPojo> fetchByAmount(Integer... values) {
		return fetch(ItemConfig.ITEM_CONFIG.AMOUNT, values);
	}

	/**
	 * Fetch records that have <code>always_display_to_user IN (values)</code>
	 */
	public List<ItemConfigPojo> fetchByAlwaysDisplayToUser(Boolean... values) {
		return fetch(ItemConfig.ITEM_CONFIG.ALWAYS_DISPLAY_TO_USER, values);
	}

	/**
	 * Fetch records that have <code>action_game_type IN (values)</code>
	 */
	public List<ItemConfigPojo> fetchByActionGameType(String... values) {
		return fetch(ItemConfig.ITEM_CONFIG.ACTION_GAME_TYPE, values);
	}

	/**
	 * Fetch records that have <code>quality IN (values)</code>
	 */
	public List<ItemConfigPojo> fetchByQuality(String... values) {
		return fetch(ItemConfig.ITEM_CONFIG.QUALITY, values);
	}
}
