/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.ClanRaidStageMonsterConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanRaidStageMonsterConfigPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.records.ClanRaidStageMonsterConfigRecord;

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
public class ClanRaidStageMonsterConfigDao extends DAOImpl<ClanRaidStageMonsterConfigRecord, ClanRaidStageMonsterConfigPojo, Integer> {

	/**
	 * Create a new ClanRaidStageMonsterConfigDao without any configuration
	 */
	public ClanRaidStageMonsterConfigDao() {
		super(ClanRaidStageMonsterConfig.CLAN_RAID_STAGE_MONSTER_CONFIG, ClanRaidStageMonsterConfigPojo.class);
	}

	/**
	 * Create a new ClanRaidStageMonsterConfigDao with an attached configuration
	 */
	public ClanRaidStageMonsterConfigDao(Configuration configuration) {
		super(ClanRaidStageMonsterConfig.CLAN_RAID_STAGE_MONSTER_CONFIG, ClanRaidStageMonsterConfigPojo.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer getId(ClanRaidStageMonsterConfigPojo object) {
		return object.getId();
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<ClanRaidStageMonsterConfigPojo> fetchById(Integer... values) {
		return fetch(ClanRaidStageMonsterConfig.CLAN_RAID_STAGE_MONSTER_CONFIG.ID, values);
	}

	/**
	 * Fetch a unique record that has <code>id = value</code>
	 */
	public ClanRaidStageMonsterConfigPojo fetchOneById(Integer value) {
		return fetchOne(ClanRaidStageMonsterConfig.CLAN_RAID_STAGE_MONSTER_CONFIG.ID, value);
	}

	/**
	 * Fetch records that have <code>clan_raid_stage_id IN (values)</code>
	 */
	public List<ClanRaidStageMonsterConfigPojo> fetchByClanRaidStageId(Integer... values) {
		return fetch(ClanRaidStageMonsterConfig.CLAN_RAID_STAGE_MONSTER_CONFIG.CLAN_RAID_STAGE_ID, values);
	}

	/**
	 * Fetch records that have <code>monster_id IN (values)</code>
	 */
	public List<ClanRaidStageMonsterConfigPojo> fetchByMonsterId(Integer... values) {
		return fetch(ClanRaidStageMonsterConfig.CLAN_RAID_STAGE_MONSTER_CONFIG.MONSTER_ID, values);
	}

	/**
	 * Fetch records that have <code>monster_hp IN (values)</code>
	 */
	public List<ClanRaidStageMonsterConfigPojo> fetchByMonsterHp(Integer... values) {
		return fetch(ClanRaidStageMonsterConfig.CLAN_RAID_STAGE_MONSTER_CONFIG.MONSTER_HP, values);
	}

	/**
	 * Fetch records that have <code>min_dmg IN (values)</code>
	 */
	public List<ClanRaidStageMonsterConfigPojo> fetchByMinDmg(Integer... values) {
		return fetch(ClanRaidStageMonsterConfig.CLAN_RAID_STAGE_MONSTER_CONFIG.MIN_DMG, values);
	}

	/**
	 * Fetch records that have <code>max_dmg IN (values)</code>
	 */
	public List<ClanRaidStageMonsterConfigPojo> fetchByMaxDmg(Integer... values) {
		return fetch(ClanRaidStageMonsterConfig.CLAN_RAID_STAGE_MONSTER_CONFIG.MAX_DMG, values);
	}

	/**
	 * Fetch records that have <code>monster_num IN (values)</code>
	 */
	public List<ClanRaidStageMonsterConfigPojo> fetchByMonsterNum(Integer... values) {
		return fetch(ClanRaidStageMonsterConfig.CLAN_RAID_STAGE_MONSTER_CONFIG.MONSTER_NUM, values);
	}
}
