/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.GoldSaleConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.GoldSaleConfigPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.records.GoldSaleConfigRecord;

import java.sql.Timestamp;
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
public class GoldSaleConfigDao extends DAOImpl<GoldSaleConfigRecord, GoldSaleConfigPojo, Integer> {

	/**
	 * Create a new GoldSaleConfigDao without any configuration
	 */
	public GoldSaleConfigDao() {
		super(GoldSaleConfig.GOLD_SALE_CONFIG, GoldSaleConfigPojo.class);
	}

	/**
	 * Create a new GoldSaleConfigDao with an attached configuration
	 */
	public GoldSaleConfigDao(Configuration configuration) {
		super(GoldSaleConfig.GOLD_SALE_CONFIG, GoldSaleConfigPojo.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer getId(GoldSaleConfigPojo object) {
		return object.getId();
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<GoldSaleConfigPojo> fetchById(Integer... values) {
		return fetch(GoldSaleConfig.GOLD_SALE_CONFIG.ID, values);
	}

	/**
	 * Fetch a unique record that has <code>id = value</code>
	 */
	public GoldSaleConfigPojo fetchOneById(Integer value) {
		return fetchOne(GoldSaleConfig.GOLD_SALE_CONFIG.ID, value);
	}

	/**
	 * Fetch records that have <code>start_time IN (values)</code>
	 */
	public List<GoldSaleConfigPojo> fetchByStartTime(Timestamp... values) {
		return fetch(GoldSaleConfig.GOLD_SALE_CONFIG.START_TIME, values);
	}

	/**
	 * Fetch records that have <code>end_time IN (values)</code>
	 */
	public List<GoldSaleConfigPojo> fetchByEndTime(Timestamp... values) {
		return fetch(GoldSaleConfig.GOLD_SALE_CONFIG.END_TIME, values);
	}

	/**
	 * Fetch records that have <code>gold_shoppe_image_name IN (values)</code>
	 */
	public List<GoldSaleConfigPojo> fetchByGoldShoppeImageName(String... values) {
		return fetch(GoldSaleConfig.GOLD_SALE_CONFIG.GOLD_SHOPPE_IMAGE_NAME, values);
	}

	/**
	 * Fetch records that have <code>gold_bar_image_name IN (values)</code>
	 */
	public List<GoldSaleConfigPojo> fetchByGoldBarImageName(String... values) {
		return fetch(GoldSaleConfig.GOLD_SALE_CONFIG.GOLD_BAR_IMAGE_NAME, values);
	}

	/**
	 * Fetch records that have <code>package1_sale IN (values)</code>
	 */
	public List<GoldSaleConfigPojo> fetchByPackage1Sale(String... values) {
		return fetch(GoldSaleConfig.GOLD_SALE_CONFIG.PACKAGE1_SALE, values);
	}

	/**
	 * Fetch records that have <code>package2_sale IN (values)</code>
	 */
	public List<GoldSaleConfigPojo> fetchByPackage2Sale(String... values) {
		return fetch(GoldSaleConfig.GOLD_SALE_CONFIG.PACKAGE2_SALE, values);
	}

	/**
	 * Fetch records that have <code>package3_sale IN (values)</code>
	 */
	public List<GoldSaleConfigPojo> fetchByPackage3Sale(String... values) {
		return fetch(GoldSaleConfig.GOLD_SALE_CONFIG.PACKAGE3_SALE, values);
	}

	/**
	 * Fetch records that have <code>package4_sale IN (values)</code>
	 */
	public List<GoldSaleConfigPojo> fetchByPackage4Sale(String... values) {
		return fetch(GoldSaleConfig.GOLD_SALE_CONFIG.PACKAGE4_SALE, values);
	}

	/**
	 * Fetch records that have <code>package5_sale IN (values)</code>
	 */
	public List<GoldSaleConfigPojo> fetchByPackage5Sale(String... values) {
		return fetch(GoldSaleConfig.GOLD_SALE_CONFIG.PACKAGE5_SALE, values);
	}

	/**
	 * Fetch records that have <code>packageS1_sale IN (values)</code>
	 */
	public List<GoldSaleConfigPojo> fetchByPackages1Sale(String... values) {
		return fetch(GoldSaleConfig.GOLD_SALE_CONFIG.PACKAGES1_SALE, values);
	}

	/**
	 * Fetch records that have <code>packageS2_sale IN (values)</code>
	 */
	public List<GoldSaleConfigPojo> fetchByPackages2Sale(String... values) {
		return fetch(GoldSaleConfig.GOLD_SALE_CONFIG.PACKAGES2_SALE, values);
	}

	/**
	 * Fetch records that have <code>packageS3_sale IN (values)</code>
	 */
	public List<GoldSaleConfigPojo> fetchByPackages3Sale(String... values) {
		return fetch(GoldSaleConfig.GOLD_SALE_CONFIG.PACKAGES3_SALE, values);
	}

	/**
	 * Fetch records that have <code>packageS4_sale IN (values)</code>
	 */
	public List<GoldSaleConfigPojo> fetchByPackages4Sale(String... values) {
		return fetch(GoldSaleConfig.GOLD_SALE_CONFIG.PACKAGES4_SALE, values);
	}

	/**
	 * Fetch records that have <code>packageS5_sale IN (values)</code>
	 */
	public List<GoldSaleConfigPojo> fetchByPackages5Sale(String... values) {
		return fetch(GoldSaleConfig.GOLD_SALE_CONFIG.PACKAGES5_SALE, values);
	}
}
