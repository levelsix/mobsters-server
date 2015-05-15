/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.StructureConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.records.StructureConfigRecord;

import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;
import org.jooq.types.UByte;
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
public class StructureConfigDao extends DAOImpl<StructureConfigRecord, com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureConfig, UInteger> {

	/**
	 * Create a new StructureConfigDao without any configuration
	 */
	public StructureConfigDao() {
		super(StructureConfig.STRUCTURE_CONFIG, com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureConfig.class);
	}

	/**
	 * Create a new StructureConfigDao with an attached configuration
	 */
	public StructureConfigDao(Configuration configuration) {
		super(StructureConfig.STRUCTURE_CONFIG, com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureConfig.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected UInteger getId(com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureConfig object) {
		return object.getId();
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureConfig> fetchById(UInteger... values) {
		return fetch(StructureConfig.STRUCTURE_CONFIG.ID, values);
	}

	/**
	 * Fetch a unique record that has <code>id = value</code>
	 */
	public com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureConfig fetchOneById(UInteger value) {
		return fetchOne(StructureConfig.STRUCTURE_CONFIG.ID, value);
	}

	/**
	 * Fetch records that have <code>name IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureConfig> fetchByName(String... values) {
		return fetch(StructureConfig.STRUCTURE_CONFIG.NAME, values);
	}

	/**
	 * Fetch records that have <code>level IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureConfig> fetchByLevel(UByte... values) {
		return fetch(StructureConfig.STRUCTURE_CONFIG.LEVEL, values);
	}

	/**
	 * Fetch records that have <code>struct_type IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureConfig> fetchByStructType(String... values) {
		return fetch(StructureConfig.STRUCTURE_CONFIG.STRUCT_TYPE, values);
	}

	/**
	 * Fetch records that have <code>build_resource_type IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureConfig> fetchByBuildResourceType(String... values) {
		return fetch(StructureConfig.STRUCTURE_CONFIG.BUILD_RESOURCE_TYPE, values);
	}

	/**
	 * Fetch records that have <code>build_cost IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureConfig> fetchByBuildCost(UInteger... values) {
		return fetch(StructureConfig.STRUCTURE_CONFIG.BUILD_COST, values);
	}

	/**
	 * Fetch records that have <code>minutes_to_build IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureConfig> fetchByMinutesToBuild(UInteger... values) {
		return fetch(StructureConfig.STRUCTURE_CONFIG.MINUTES_TO_BUILD, values);
	}

	/**
	 * Fetch records that have <code>width IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureConfig> fetchByWidth(UInteger... values) {
		return fetch(StructureConfig.STRUCTURE_CONFIG.WIDTH, values);
	}

	/**
	 * Fetch records that have <code>height IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureConfig> fetchByHeight(UInteger... values) {
		return fetch(StructureConfig.STRUCTURE_CONFIG.HEIGHT, values);
	}

	/**
	 * Fetch records that have <code>predecessor_struct_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureConfig> fetchByPredecessorStructId(UInteger... values) {
		return fetch(StructureConfig.STRUCTURE_CONFIG.PREDECESSOR_STRUCT_ID, values);
	}

	/**
	 * Fetch records that have <code>successor_struct_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureConfig> fetchBySuccessorStructId(UInteger... values) {
		return fetch(StructureConfig.STRUCTURE_CONFIG.SUCCESSOR_STRUCT_ID, values);
	}

	/**
	 * Fetch records that have <code>img_name IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureConfig> fetchByImgName(String... values) {
		return fetch(StructureConfig.STRUCTURE_CONFIG.IMG_NAME, values);
	}

	/**
	 * Fetch records that have <code>img_vertical_pixel_offset IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureConfig> fetchByImgVerticalPixelOffset(Double... values) {
		return fetch(StructureConfig.STRUCTURE_CONFIG.IMG_VERTICAL_PIXEL_OFFSET, values);
	}

	/**
	 * Fetch records that have <code>img_horizontal_pixel_offset IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureConfig> fetchByImgHorizontalPixelOffset(Double... values) {
		return fetch(StructureConfig.STRUCTURE_CONFIG.IMG_HORIZONTAL_PIXEL_OFFSET, values);
	}

	/**
	 * Fetch records that have <code>description IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureConfig> fetchByDescription(String... values) {
		return fetch(StructureConfig.STRUCTURE_CONFIG.DESCRIPTION, values);
	}

	/**
	 * Fetch records that have <code>short_description IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureConfig> fetchByShortDescription(String... values) {
		return fetch(StructureConfig.STRUCTURE_CONFIG.SHORT_DESCRIPTION, values);
	}

	/**
	 * Fetch records that have <code>shadow_img_name IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureConfig> fetchByShadowImgName(String... values) {
		return fetch(StructureConfig.STRUCTURE_CONFIG.SHADOW_IMG_NAME, values);
	}

	/**
	 * Fetch records that have <code>shadow_vertical_offset IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureConfig> fetchByShadowVerticalOffset(Double... values) {
		return fetch(StructureConfig.STRUCTURE_CONFIG.SHADOW_VERTICAL_OFFSET, values);
	}

	/**
	 * Fetch records that have <code>shadow_horizontal_offset IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureConfig> fetchByShadowHorizontalOffset(Double... values) {
		return fetch(StructureConfig.STRUCTURE_CONFIG.SHADOW_HORIZONTAL_OFFSET, values);
	}

	/**
	 * Fetch records that have <code>shadow_scale IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureConfig> fetchByShadowScale(Double... values) {
		return fetch(StructureConfig.STRUCTURE_CONFIG.SHADOW_SCALE, values);
	}

	/**
	 * Fetch records that have <code>exp_reward IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureConfig> fetchByExpReward(Integer... values) {
		return fetch(StructureConfig.STRUCTURE_CONFIG.EXP_REWARD, values);
	}

	/**
	 * Fetch records that have <code>strength IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureConfig> fetchByStrength(Integer... values) {
		return fetch(StructureConfig.STRUCTURE_CONFIG.STRENGTH, values);
	}
}
