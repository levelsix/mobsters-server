/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.SkillPropertyConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.SkillPropertyConfigPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.records.SkillPropertyConfigRecord;

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
public class SkillPropertyConfigDao extends DAOImpl<SkillPropertyConfigRecord, SkillPropertyConfigPojo, Integer> {

	/**
	 * Create a new SkillPropertyConfigDao without any configuration
	 */
	public SkillPropertyConfigDao() {
		super(SkillPropertyConfig.SKILL_PROPERTY_CONFIG, SkillPropertyConfigPojo.class);
	}

	/**
	 * Create a new SkillPropertyConfigDao with an attached configuration
	 */
	public SkillPropertyConfigDao(Configuration configuration) {
		super(SkillPropertyConfig.SKILL_PROPERTY_CONFIG, SkillPropertyConfigPojo.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer getId(SkillPropertyConfigPojo object) {
		return object.getId();
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<SkillPropertyConfigPojo> fetchById(Integer... values) {
		return fetch(SkillPropertyConfig.SKILL_PROPERTY_CONFIG.ID, values);
	}

	/**
	 * Fetch a unique record that has <code>id = value</code>
	 */
	public SkillPropertyConfigPojo fetchOneById(Integer value) {
		return fetchOne(SkillPropertyConfig.SKILL_PROPERTY_CONFIG.ID, value);
	}

	/**
	 * Fetch records that have <code>name IN (values)</code>
	 */
	public List<SkillPropertyConfigPojo> fetchByName(String... values) {
		return fetch(SkillPropertyConfig.SKILL_PROPERTY_CONFIG.NAME, values);
	}

	/**
	 * Fetch records that have <code>value IN (values)</code>
	 */
	public List<SkillPropertyConfigPojo> fetchByValue(Double... values) {
		return fetch(SkillPropertyConfig.SKILL_PROPERTY_CONFIG.VALUE, values);
	}

	/**
	 * Fetch records that have <code>skill_id IN (values)</code>
	 */
	public List<SkillPropertyConfigPojo> fetchBySkillId(Integer... values) {
		return fetch(SkillPropertyConfig.SKILL_PROPERTY_CONFIG.SKILL_ID, values);
	}
}
