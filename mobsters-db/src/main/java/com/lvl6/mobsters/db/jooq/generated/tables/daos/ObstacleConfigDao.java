/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.ObstacleConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.ObstacleConfigPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.records.ObstacleConfigRecord;

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
public class ObstacleConfigDao extends DAOImpl<ObstacleConfigRecord, ObstacleConfigPojo, Integer> {

	/**
	 * Create a new ObstacleConfigDao without any configuration
	 */
	public ObstacleConfigDao() {
		super(ObstacleConfig.OBSTACLE_CONFIG, ObstacleConfigPojo.class);
	}

	/**
	 * Create a new ObstacleConfigDao with an attached configuration
	 */
	public ObstacleConfigDao(Configuration configuration) {
		super(ObstacleConfig.OBSTACLE_CONFIG, ObstacleConfigPojo.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer getId(ObstacleConfigPojo object) {
		return object.getId();
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<ObstacleConfigPojo> fetchById(Integer... values) {
		return fetch(ObstacleConfig.OBSTACLE_CONFIG.ID, values);
	}

	/**
	 * Fetch a unique record that has <code>id = value</code>
	 */
	public ObstacleConfigPojo fetchOneById(Integer value) {
		return fetchOne(ObstacleConfig.OBSTACLE_CONFIG.ID, value);
	}

	/**
	 * Fetch records that have <code>name IN (values)</code>
	 */
	public List<ObstacleConfigPojo> fetchByName(String... values) {
		return fetch(ObstacleConfig.OBSTACLE_CONFIG.NAME, values);
	}

	/**
	 * Fetch records that have <code>removal_cost_type IN (values)</code>
	 */
	public List<ObstacleConfigPojo> fetchByRemovalCostType(String... values) {
		return fetch(ObstacleConfig.OBSTACLE_CONFIG.REMOVAL_COST_TYPE, values);
	}

	/**
	 * Fetch records that have <code>cost IN (values)</code>
	 */
	public List<ObstacleConfigPojo> fetchByCost(Integer... values) {
		return fetch(ObstacleConfig.OBSTACLE_CONFIG.COST, values);
	}

	/**
	 * Fetch records that have <code>seconds_to_remove IN (values)</code>
	 */
	public List<ObstacleConfigPojo> fetchBySecondsToRemove(Integer... values) {
		return fetch(ObstacleConfig.OBSTACLE_CONFIG.SECONDS_TO_REMOVE, values);
	}

	/**
	 * Fetch records that have <code>width IN (values)</code>
	 */
	public List<ObstacleConfigPojo> fetchByWidth(Byte... values) {
		return fetch(ObstacleConfig.OBSTACLE_CONFIG.WIDTH, values);
	}

	/**
	 * Fetch records that have <code>height IN (values)</code>
	 */
	public List<ObstacleConfigPojo> fetchByHeight(Byte... values) {
		return fetch(ObstacleConfig.OBSTACLE_CONFIG.HEIGHT, values);
	}

	/**
	 * Fetch records that have <code>img_name IN (values)</code>
	 */
	public List<ObstacleConfigPojo> fetchByImgName(String... values) {
		return fetch(ObstacleConfig.OBSTACLE_CONFIG.IMG_NAME, values);
	}

	/**
	 * Fetch records that have <code>img_vertical_pixel_offset IN (values)</code>
	 */
	public List<ObstacleConfigPojo> fetchByImgVerticalPixelOffset(Double... values) {
		return fetch(ObstacleConfig.OBSTACLE_CONFIG.IMG_VERTICAL_PIXEL_OFFSET, values);
	}

	/**
	 * Fetch records that have <code>description IN (values)</code>
	 */
	public List<ObstacleConfigPojo> fetchByDescription(String... values) {
		return fetch(ObstacleConfig.OBSTACLE_CONFIG.DESCRIPTION, values);
	}

	/**
	 * Fetch records that have <code>chance_to_appear IN (values)</code>
	 */
	public List<ObstacleConfigPojo> fetchByChanceToAppear(Double... values) {
		return fetch(ObstacleConfig.OBSTACLE_CONFIG.CHANCE_TO_APPEAR, values);
	}

	/**
	 * Fetch records that have <code>shadow_img_name IN (values)</code>
	 */
	public List<ObstacleConfigPojo> fetchByShadowImgName(String... values) {
		return fetch(ObstacleConfig.OBSTACLE_CONFIG.SHADOW_IMG_NAME, values);
	}

	/**
	 * Fetch records that have <code>shadow_vertical_offset IN (values)</code>
	 */
	public List<ObstacleConfigPojo> fetchByShadowVerticalOffset(Double... values) {
		return fetch(ObstacleConfig.OBSTACLE_CONFIG.SHADOW_VERTICAL_OFFSET, values);
	}

	/**
	 * Fetch records that have <code>shadow_horizontal_offset IN (values)</code>
	 */
	public List<ObstacleConfigPojo> fetchByShadowHorizontalOffset(Double... values) {
		return fetch(ObstacleConfig.OBSTACLE_CONFIG.SHADOW_HORIZONTAL_OFFSET, values);
	}
}
