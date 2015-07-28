/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.ResearchConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.ResearchConfigPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.records.ResearchConfigRecord;

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
public class ResearchConfigDao extends DAOImpl<ResearchConfigRecord, ResearchConfigPojo, Integer> {

	/**
	 * Create a new ResearchConfigDao without any configuration
	 */
	public ResearchConfigDao() {
		super(ResearchConfig.RESEARCH_CONFIG, ResearchConfigPojo.class);
	}

	/**
	 * Create a new ResearchConfigDao with an attached configuration
	 */
	public ResearchConfigDao(Configuration configuration) {
		super(ResearchConfig.RESEARCH_CONFIG, ResearchConfigPojo.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer getId(ResearchConfigPojo object) {
		return object.getId();
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<ResearchConfigPojo> fetchById(Integer... values) {
		return fetch(ResearchConfig.RESEARCH_CONFIG.ID, values);
	}

	/**
	 * Fetch a unique record that has <code>id = value</code>
	 */
	public ResearchConfigPojo fetchOneById(Integer value) {
		return fetchOne(ResearchConfig.RESEARCH_CONFIG.ID, value);
	}

	/**
	 * Fetch records that have <code>research_type IN (values)</code>
	 */
	public List<ResearchConfigPojo> fetchByResearchType(String... values) {
		return fetch(ResearchConfig.RESEARCH_CONFIG.RESEARCH_TYPE, values);
	}

	/**
	 * Fetch records that have <code>research_domain IN (values)</code>
	 */
	public List<ResearchConfigPojo> fetchByResearchDomain(String... values) {
		return fetch(ResearchConfig.RESEARCH_CONFIG.RESEARCH_DOMAIN, values);
	}

	/**
	 * Fetch records that have <code>icon_img_name IN (values)</code>
	 */
	public List<ResearchConfigPojo> fetchByIconImgName(String... values) {
		return fetch(ResearchConfig.RESEARCH_CONFIG.ICON_IMG_NAME, values);
	}

	/**
	 * Fetch records that have <code>name IN (values)</code>
	 */
	public List<ResearchConfigPojo> fetchByName(String... values) {
		return fetch(ResearchConfig.RESEARCH_CONFIG.NAME, values);
	}

	/**
	 * Fetch records that have <code>pred_id IN (values)</code>
	 */
	public List<ResearchConfigPojo> fetchByPredId(Integer... values) {
		return fetch(ResearchConfig.RESEARCH_CONFIG.PRED_ID, values);
	}

	/**
	 * Fetch records that have <code>succ_id IN (values)</code>
	 */
	public List<ResearchConfigPojo> fetchBySuccId(Integer... values) {
		return fetch(ResearchConfig.RESEARCH_CONFIG.SUCC_ID, values);
	}

	/**
	 * Fetch records that have <code>desc IN (values)</code>
	 */
	public List<ResearchConfigPojo> fetchByDesc(String... values) {
		return fetch(ResearchConfig.RESEARCH_CONFIG.DESC, values);
	}

	/**
	 * Fetch records that have <code>duration_min IN (values)</code>
	 */
	public List<ResearchConfigPojo> fetchByDurationMin(Integer... values) {
		return fetch(ResearchConfig.RESEARCH_CONFIG.DURATION_MIN, values);
	}

	/**
	 * Fetch records that have <code>cost_amt IN (values)</code>
	 */
	public List<ResearchConfigPojo> fetchByCostAmt(Integer... values) {
		return fetch(ResearchConfig.RESEARCH_CONFIG.COST_AMT, values);
	}

	/**
	 * Fetch records that have <code>cost_type IN (values)</code>
	 */
	public List<ResearchConfigPojo> fetchByCostType(String... values) {
		return fetch(ResearchConfig.RESEARCH_CONFIG.COST_TYPE, values);
	}

	/**
	 * Fetch records that have <code>level IN (values)</code>
	 */
	public List<ResearchConfigPojo> fetchByLevel(Integer... values) {
		return fetch(ResearchConfig.RESEARCH_CONFIG.LEVEL, values);
	}

	/**
	 * Fetch records that have <code>priority IN (values)</code>
	 */
	public List<ResearchConfigPojo> fetchByPriority(Double... values) {
		return fetch(ResearchConfig.RESEARCH_CONFIG.PRIORITY, values);
	}

	/**
	 * Fetch records that have <code>tier IN (values)</code>
	 */
	public List<ResearchConfigPojo> fetchByTier(Integer... values) {
		return fetch(ResearchConfig.RESEARCH_CONFIG.TIER, values);
	}

	/**
	 * Fetch records that have <code>strength IN (values)</code>
	 */
	public List<ResearchConfigPojo> fetchByStrength(Integer... values) {
		return fetch(ResearchConfig.RESEARCH_CONFIG.STRENGTH, values);
	}
}