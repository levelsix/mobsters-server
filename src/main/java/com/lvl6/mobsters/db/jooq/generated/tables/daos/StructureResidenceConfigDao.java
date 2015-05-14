/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.StructureResidenceConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.records.StructureResidenceConfigRecord;

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
public class StructureResidenceConfigDao extends DAOImpl<StructureResidenceConfigRecord, com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureResidenceConfig, Integer> {

	/**
	 * Create a new StructureResidenceConfigDao without any configuration
	 */
	public StructureResidenceConfigDao() {
		super(StructureResidenceConfig.STRUCTURE_RESIDENCE_CONFIG, com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureResidenceConfig.class);
	}

	/**
	 * Create a new StructureResidenceConfigDao with an attached configuration
	 */
	public StructureResidenceConfigDao(Configuration configuration) {
		super(StructureResidenceConfig.STRUCTURE_RESIDENCE_CONFIG, com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureResidenceConfig.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer getId(com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureResidenceConfig object) {
		return object.getStructId();
	}

	/**
	 * Fetch records that have <code>struct_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureResidenceConfig> fetchByStructId(Integer... values) {
		return fetch(StructureResidenceConfig.STRUCTURE_RESIDENCE_CONFIG.STRUCT_ID, values);
	}

	/**
	 * Fetch a unique record that has <code>struct_id = value</code>
	 */
	public com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureResidenceConfig fetchOneByStructId(Integer value) {
		return fetchOne(StructureResidenceConfig.STRUCTURE_RESIDENCE_CONFIG.STRUCT_ID, value);
	}

	/**
	 * Fetch records that have <code>num_monster_slots IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureResidenceConfig> fetchByNumMonsterSlots(UInteger... values) {
		return fetch(StructureResidenceConfig.STRUCTURE_RESIDENCE_CONFIG.NUM_MONSTER_SLOTS, values);
	}

	/**
	 * Fetch records that have <code>num_bonus_monster_slots IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureResidenceConfig> fetchByNumBonusMonsterSlots(UInteger... values) {
		return fetch(StructureResidenceConfig.STRUCTURE_RESIDENCE_CONFIG.NUM_BONUS_MONSTER_SLOTS, values);
	}

	/**
	 * Fetch records that have <code>num_gems_required IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureResidenceConfig> fetchByNumGemsRequired(UInteger... values) {
		return fetch(StructureResidenceConfig.STRUCTURE_RESIDENCE_CONFIG.NUM_GEMS_REQUIRED, values);
	}

	/**
	 * Fetch records that have <code>num_accepeted_fb_invites IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureResidenceConfig> fetchByNumAccepetedFbInvites(UInteger... values) {
		return fetch(StructureResidenceConfig.STRUCTURE_RESIDENCE_CONFIG.NUM_ACCEPETED_FB_INVITES, values);
	}

	/**
	 * Fetch records that have <code>occupation_name IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureResidenceConfig> fetchByOccupationName(String... values) {
		return fetch(StructureResidenceConfig.STRUCTURE_RESIDENCE_CONFIG.OCCUPATION_NAME, values);
	}

	/**
	 * Fetch records that have <code>img_suffix IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureResidenceConfig> fetchByImgSuffix(String... values) {
		return fetch(StructureResidenceConfig.STRUCTURE_RESIDENCE_CONFIG.IMG_SUFFIX, values);
	}
}
