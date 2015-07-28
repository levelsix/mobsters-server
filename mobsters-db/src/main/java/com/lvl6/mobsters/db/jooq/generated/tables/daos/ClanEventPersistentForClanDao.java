/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.ClanEventPersistentForClan;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanEventPersistentForClanPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.records.ClanEventPersistentForClanRecord;

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
public class ClanEventPersistentForClanDao extends DAOImpl<ClanEventPersistentForClanRecord, ClanEventPersistentForClanPojo, String> {

	/**
	 * Create a new ClanEventPersistentForClanDao without any configuration
	 */
	public ClanEventPersistentForClanDao() {
		super(ClanEventPersistentForClan.CLAN_EVENT_PERSISTENT_FOR_CLAN, ClanEventPersistentForClanPojo.class);
	}

	/**
	 * Create a new ClanEventPersistentForClanDao with an attached configuration
	 */
	public ClanEventPersistentForClanDao(Configuration configuration) {
		super(ClanEventPersistentForClan.CLAN_EVENT_PERSISTENT_FOR_CLAN, ClanEventPersistentForClanPojo.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getId(ClanEventPersistentForClanPojo object) {
		return object.getClanId();
	}

	/**
	 * Fetch records that have <code>clan_id IN (values)</code>
	 */
	public List<ClanEventPersistentForClanPojo> fetchByClanId(String... values) {
		return fetch(ClanEventPersistentForClan.CLAN_EVENT_PERSISTENT_FOR_CLAN.CLAN_ID, values);
	}

	/**
	 * Fetch a unique record that has <code>clan_id = value</code>
	 */
	public ClanEventPersistentForClanPojo fetchOneByClanId(String value) {
		return fetchOne(ClanEventPersistentForClan.CLAN_EVENT_PERSISTENT_FOR_CLAN.CLAN_ID, value);
	}

	/**
	 * Fetch records that have <code>clan_event_persistent_id IN (values)</code>
	 */
	public List<ClanEventPersistentForClanPojo> fetchByClanEventPersistentId(Integer... values) {
		return fetch(ClanEventPersistentForClan.CLAN_EVENT_PERSISTENT_FOR_CLAN.CLAN_EVENT_PERSISTENT_ID, values);
	}

	/**
	 * Fetch records that have <code>cr_id IN (values)</code>
	 */
	public List<ClanEventPersistentForClanPojo> fetchByCrId(Integer... values) {
		return fetch(ClanEventPersistentForClan.CLAN_EVENT_PERSISTENT_FOR_CLAN.CR_ID, values);
	}

	/**
	 * Fetch records that have <code>crs_id IN (values)</code>
	 */
	public List<ClanEventPersistentForClanPojo> fetchByCrsId(Integer... values) {
		return fetch(ClanEventPersistentForClan.CLAN_EVENT_PERSISTENT_FOR_CLAN.CRS_ID, values);
	}

	/**
	 * Fetch records that have <code>stage_start_time IN (values)</code>
	 */
	public List<ClanEventPersistentForClanPojo> fetchByStageStartTime(Timestamp... values) {
		return fetch(ClanEventPersistentForClan.CLAN_EVENT_PERSISTENT_FOR_CLAN.STAGE_START_TIME, values);
	}

	/**
	 * Fetch records that have <code>crsm_id IN (values)</code>
	 */
	public List<ClanEventPersistentForClanPojo> fetchByCrsmId(Integer... values) {
		return fetch(ClanEventPersistentForClan.CLAN_EVENT_PERSISTENT_FOR_CLAN.CRSM_ID, values);
	}

	/**
	 * Fetch records that have <code>stage_monster_start_time IN (values)</code>
	 */
	public List<ClanEventPersistentForClanPojo> fetchByStageMonsterStartTime(Timestamp... values) {
		return fetch(ClanEventPersistentForClan.CLAN_EVENT_PERSISTENT_FOR_CLAN.STAGE_MONSTER_START_TIME, values);
	}
}