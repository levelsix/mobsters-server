/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.CepfuRaidStageHistory;
import com.lvl6.mobsters.db.jooq.generated.tables.records.CepfuRaidStageHistoryRecord;

import java.sql.Timestamp;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.Record3;
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
public class CepfuRaidStageHistoryDao extends DAOImpl<CepfuRaidStageHistoryRecord, com.lvl6.mobsters.db.jooq.generated.tables.pojos.CepfuRaidStageHistory, Record3<String, Timestamp, String>> {

	/**
	 * Create a new CepfuRaidStageHistoryDao without any configuration
	 */
	public CepfuRaidStageHistoryDao() {
		super(CepfuRaidStageHistory.CEPFU_RAID_STAGE_HISTORY, com.lvl6.mobsters.db.jooq.generated.tables.pojos.CepfuRaidStageHistory.class);
	}

	/**
	 * Create a new CepfuRaidStageHistoryDao with an attached configuration
	 */
	public CepfuRaidStageHistoryDao(Configuration configuration) {
		super(CepfuRaidStageHistory.CEPFU_RAID_STAGE_HISTORY, com.lvl6.mobsters.db.jooq.generated.tables.pojos.CepfuRaidStageHistory.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Record3<String, Timestamp, String> getId(com.lvl6.mobsters.db.jooq.generated.tables.pojos.CepfuRaidStageHistory object) {
		return compositeKeyRecord(object.getUserId(), object.getCrsStartTime(), object.getClanId());
	}

	/**
	 * Fetch records that have <code>user_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.CepfuRaidStageHistory> fetchByUserId(String... values) {
		return fetch(CepfuRaidStageHistory.CEPFU_RAID_STAGE_HISTORY.USER_ID, values);
	}

	/**
	 * Fetch records that have <code>crs_start_time IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.CepfuRaidStageHistory> fetchByCrsStartTime(Timestamp... values) {
		return fetch(CepfuRaidStageHistory.CEPFU_RAID_STAGE_HISTORY.CRS_START_TIME, values);
	}

	/**
	 * Fetch records that have <code>clan_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.CepfuRaidStageHistory> fetchByClanId(String... values) {
		return fetch(CepfuRaidStageHistory.CEPFU_RAID_STAGE_HISTORY.CLAN_ID, values);
	}

	/**
	 * Fetch records that have <code>clan_event_persistent_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.CepfuRaidStageHistory> fetchByClanEventPersistentId(Integer... values) {
		return fetch(CepfuRaidStageHistory.CEPFU_RAID_STAGE_HISTORY.CLAN_EVENT_PERSISTENT_ID, values);
	}

	/**
	 * Fetch records that have <code>cr_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.CepfuRaidStageHistory> fetchByCrId(Integer... values) {
		return fetch(CepfuRaidStageHistory.CEPFU_RAID_STAGE_HISTORY.CR_ID, values);
	}

	/**
	 * Fetch records that have <code>crs_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.CepfuRaidStageHistory> fetchByCrsId(Integer... values) {
		return fetch(CepfuRaidStageHistory.CEPFU_RAID_STAGE_HISTORY.CRS_ID, values);
	}

	/**
	 * Fetch records that have <code>crs_dmg_done IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.CepfuRaidStageHistory> fetchByCrsDmgDone(Integer... values) {
		return fetch(CepfuRaidStageHistory.CEPFU_RAID_STAGE_HISTORY.CRS_DMG_DONE, values);
	}

	/**
	 * Fetch records that have <code>stage_health IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.CepfuRaidStageHistory> fetchByStageHealth(Integer... values) {
		return fetch(CepfuRaidStageHistory.CEPFU_RAID_STAGE_HISTORY.STAGE_HEALTH, values);
	}

	/**
	 * Fetch records that have <code>crs_end_time IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.CepfuRaidStageHistory> fetchByCrsEndTime(Timestamp... values) {
		return fetch(CepfuRaidStageHistory.CEPFU_RAID_STAGE_HISTORY.CRS_END_TIME, values);
	}
}