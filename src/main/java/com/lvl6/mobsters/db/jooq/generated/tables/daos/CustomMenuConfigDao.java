/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.CustomMenuConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.records.CustomMenuConfigRecord;

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
public class CustomMenuConfigDao extends DAOImpl<CustomMenuConfigRecord, com.lvl6.mobsters.db.jooq.generated.tables.pojos.CustomMenuConfig, Record2<Integer, Integer>> {

	/**
	 * Create a new CustomMenuConfigDao without any configuration
	 */
	public CustomMenuConfigDao() {
		super(CustomMenuConfig.CUSTOM_MENU_CONFIG, com.lvl6.mobsters.db.jooq.generated.tables.pojos.CustomMenuConfig.class);
	}

	/**
	 * Create a new CustomMenuConfigDao with an attached configuration
	 */
	public CustomMenuConfigDao(Configuration configuration) {
		super(CustomMenuConfig.CUSTOM_MENU_CONFIG, com.lvl6.mobsters.db.jooq.generated.tables.pojos.CustomMenuConfig.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Record2<Integer, Integer> getId(com.lvl6.mobsters.db.jooq.generated.tables.pojos.CustomMenuConfig object) {
		return compositeKeyRecord(object.getCustomMenuId(), object.getPositionZ());
	}

	/**
	 * Fetch records that have <code>custom_menu_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.CustomMenuConfig> fetchByCustomMenuId(Integer... values) {
		return fetch(CustomMenuConfig.CUSTOM_MENU_CONFIG.CUSTOM_MENU_ID, values);
	}

	/**
	 * Fetch records that have <code>position_x IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.CustomMenuConfig> fetchByPositionX(Integer... values) {
		return fetch(CustomMenuConfig.CUSTOM_MENU_CONFIG.POSITION_X, values);
	}

	/**
	 * Fetch records that have <code>position_y IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.CustomMenuConfig> fetchByPositionY(Integer... values) {
		return fetch(CustomMenuConfig.CUSTOM_MENU_CONFIG.POSITION_Y, values);
	}

	/**
	 * Fetch records that have <code>position_z IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.CustomMenuConfig> fetchByPositionZ(Integer... values) {
		return fetch(CustomMenuConfig.CUSTOM_MENU_CONFIG.POSITION_Z, values);
	}

	/**
	 * Fetch records that have <code>is_jiggle IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.CustomMenuConfig> fetchByIsJiggle(String... values) {
		return fetch(CustomMenuConfig.CUSTOM_MENU_CONFIG.IS_JIGGLE, values);
	}

	/**
	 * Fetch records that have <code>image_name IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.CustomMenuConfig> fetchByImageName(String... values) {
		return fetch(CustomMenuConfig.CUSTOM_MENU_CONFIG.IMAGE_NAME, values);
	}
}