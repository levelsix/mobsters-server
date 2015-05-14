/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.ClanAvenge;
import com.lvl6.mobsters.db.jooq.generated.tables.records.ClanAvengeRecord;

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
public class ClanAvengeDao extends DAOImpl<ClanAvengeRecord, com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanAvenge, String> {

	/**
	 * Create a new ClanAvengeDao without any configuration
	 */
	public ClanAvengeDao() {
		super(ClanAvenge.CLAN_AVENGE, com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanAvenge.class);
	}

	/**
	 * Create a new ClanAvengeDao with an attached configuration
	 */
	public ClanAvengeDao(Configuration configuration) {
		super(ClanAvenge.CLAN_AVENGE, com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanAvenge.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getId(com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanAvenge object) {
		return object.getId();
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanAvenge> fetchById(String... values) {
		return fetch(ClanAvenge.CLAN_AVENGE.ID, values);
	}

	/**
	 * Fetch a unique record that has <code>id = value</code>
	 */
	public com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanAvenge fetchOneById(String value) {
		return fetchOne(ClanAvenge.CLAN_AVENGE.ID, value);
	}

	/**
	 * Fetch records that have <code>clan_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanAvenge> fetchByClanId(String... values) {
		return fetch(ClanAvenge.CLAN_AVENGE.CLAN_ID, values);
	}

	/**
	 * Fetch records that have <code>attacker_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanAvenge> fetchByAttackerId(String... values) {
		return fetch(ClanAvenge.CLAN_AVENGE.ATTACKER_ID, values);
	}

	/**
	 * Fetch records that have <code>defender_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanAvenge> fetchByDefenderId(String... values) {
		return fetch(ClanAvenge.CLAN_AVENGE.DEFENDER_ID, values);
	}

	/**
	 * Fetch records that have <code>battle_end_time IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanAvenge> fetchByBattleEndTime(Timestamp... values) {
		return fetch(ClanAvenge.CLAN_AVENGE.BATTLE_END_TIME, values);
	}

	/**
	 * Fetch records that have <code>avenge_request_time IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanAvenge> fetchByAvengeRequestTime(Timestamp... values) {
		return fetch(ClanAvenge.CLAN_AVENGE.AVENGE_REQUEST_TIME, values);
	}
}
