/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.CityConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.CityConfigPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.records.CityConfigRecord;

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
public class CityConfigDao extends DAOImpl<CityConfigRecord, CityConfigPojo, Integer> {

	/**
	 * Create a new CityConfigDao without any configuration
	 */
	public CityConfigDao() {
		super(CityConfig.CITY_CONFIG, CityConfigPojo.class);
	}

	/**
	 * Create a new CityConfigDao with an attached configuration
	 */
	public CityConfigDao(Configuration configuration) {
		super(CityConfig.CITY_CONFIG, CityConfigPojo.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer getId(CityConfigPojo object) {
		return object.getId();
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<CityConfigPojo> fetchById(Integer... values) {
		return fetch(CityConfig.CITY_CONFIG.ID, values);
	}

	/**
	 * Fetch a unique record that has <code>id = value</code>
	 */
	public CityConfigPojo fetchOneById(Integer value) {
		return fetchOne(CityConfig.CITY_CONFIG.ID, value);
	}

	/**
	 * Fetch records that have <code>name IN (values)</code>
	 */
	public List<CityConfigPojo> fetchByName(String... values) {
		return fetch(CityConfig.CITY_CONFIG.NAME, values);
	}

	/**
	 * Fetch records that have <code>map_img_name IN (values)</code>
	 */
	public List<CityConfigPojo> fetchByMapImgName(String... values) {
		return fetch(CityConfig.CITY_CONFIG.MAP_IMG_NAME, values);
	}

	/**
	 * Fetch records that have <code>center_xcoord IN (values)</code>
	 */
	public List<CityConfigPojo> fetchByCenterXcoord(Double... values) {
		return fetch(CityConfig.CITY_CONFIG.CENTER_XCOORD, values);
	}

	/**
	 * Fetch records that have <code>center_ycoord IN (values)</code>
	 */
	public List<CityConfigPojo> fetchByCenterYcoord(Double... values) {
		return fetch(CityConfig.CITY_CONFIG.CENTER_YCOORD, values);
	}

	/**
	 * Fetch records that have <code>road_img_name IN (values)</code>
	 */
	public List<CityConfigPojo> fetchByRoadImgName(String... values) {
		return fetch(CityConfig.CITY_CONFIG.ROAD_IMG_NAME, values);
	}

	/**
	 * Fetch records that have <code>map_tmx_name IN (values)</code>
	 */
	public List<CityConfigPojo> fetchByMapTmxName(String... values) {
		return fetch(CityConfig.CITY_CONFIG.MAP_TMX_NAME, values);
	}

	/**
	 * Fetch records that have <code>road_img_pos_x IN (values)</code>
	 */
	public List<CityConfigPojo> fetchByRoadImgPosX(Double... values) {
		return fetch(CityConfig.CITY_CONFIG.ROAD_IMG_POS_X, values);
	}

	/**
	 * Fetch records that have <code>road_img_pos_y IN (values)</code>
	 */
	public List<CityConfigPojo> fetchByRoadImgPosY(Double... values) {
		return fetch(CityConfig.CITY_CONFIG.ROAD_IMG_POS_Y, values);
	}

	/**
	 * Fetch records that have <code>attack_map_label_img_name IN (values)</code>
	 */
	public List<CityConfigPojo> fetchByAttackMapLabelImgName(String... values) {
		return fetch(CityConfig.CITY_CONFIG.ATTACK_MAP_LABEL_IMG_NAME, values);
	}
}