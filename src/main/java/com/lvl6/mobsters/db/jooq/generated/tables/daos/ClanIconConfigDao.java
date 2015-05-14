/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.ClanIconConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.records.ClanIconConfigRecord;

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
public class ClanIconConfigDao extends DAOImpl<ClanIconConfigRecord, com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanIconConfig, Integer> {

	/**
	 * Create a new ClanIconConfigDao without any configuration
	 */
	public ClanIconConfigDao() {
		super(ClanIconConfig.CLAN_ICON_CONFIG, com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanIconConfig.class);
	}

	/**
	 * Create a new ClanIconConfigDao with an attached configuration
	 */
	public ClanIconConfigDao(Configuration configuration) {
		super(ClanIconConfig.CLAN_ICON_CONFIG, com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanIconConfig.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer getId(com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanIconConfig object) {
		return object.getId();
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanIconConfig> fetchById(Integer... values) {
		return fetch(ClanIconConfig.CLAN_ICON_CONFIG.ID, values);
	}

	/**
	 * Fetch a unique record that has <code>id = value</code>
	 */
	public com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanIconConfig fetchOneById(Integer value) {
		return fetchOne(ClanIconConfig.CLAN_ICON_CONFIG.ID, value);
	}

	/**
	 * Fetch records that have <code>img_name IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanIconConfig> fetchByImgName(String... values) {
		return fetch(ClanIconConfig.CLAN_ICON_CONFIG.IMG_NAME, values);
	}

	/**
	 * Fetch records that have <code>is_available IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanIconConfig> fetchByIsAvailable(Boolean... values) {
		return fetch(ClanIconConfig.CLAN_ICON_CONFIG.IS_AVAILABLE, values);
	}
}
