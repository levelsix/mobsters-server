/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.CepfuRaidStageMonsterHistoryRecord;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;


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
public class CepfuRaidStageMonsterHistory extends TableImpl<CepfuRaidStageMonsterHistoryRecord> {

	private static final long serialVersionUID = 1560140493;

	/**
	 * The reference instance of <code>mobsters.cepfu_raid_stage_monster_history</code>
	 */
	public static final CepfuRaidStageMonsterHistory CEPFU_RAID_STAGE_MONSTER_HISTORY = new CepfuRaidStageMonsterHistory();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<CepfuRaidStageMonsterHistoryRecord> getRecordType() {
		return CepfuRaidStageMonsterHistoryRecord.class;
	}

	/**
	 * The column <code>mobsters.cepfu_raid_stage_monster_history.user_id</code>.
	 */
	public final TableField<CepfuRaidStageMonsterHistoryRecord, String> USER_ID = createField("user_id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>mobsters.cepfu_raid_stage_monster_history.crsm_start_time</code>.
	 */
	public final TableField<CepfuRaidStageMonsterHistoryRecord, Timestamp> CRSM_START_TIME = createField("crsm_start_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>mobsters.cepfu_raid_stage_monster_history.clan_id</code>.
	 */
	public final TableField<CepfuRaidStageMonsterHistoryRecord, String> CLAN_ID = createField("clan_id", org.jooq.impl.SQLDataType.CHAR.length(36), this, "");

	/**
	 * The column <code>mobsters.cepfu_raid_stage_monster_history.clan_event_persistent_id</code>.
	 */
	public final TableField<CepfuRaidStageMonsterHistoryRecord, Integer> CLAN_EVENT_PERSISTENT_ID = createField("clan_event_persistent_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.cepfu_raid_stage_monster_history.crs_id</code>.
	 */
	public final TableField<CepfuRaidStageMonsterHistoryRecord, Integer> CRS_ID = createField("crs_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.cepfu_raid_stage_monster_history.crsm_id</code>.
	 */
	public final TableField<CepfuRaidStageMonsterHistoryRecord, Integer> CRSM_ID = createField("crsm_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.cepfu_raid_stage_monster_history.crsm_dmg_done</code>.
	 */
	public final TableField<CepfuRaidStageMonsterHistoryRecord, Integer> CRSM_DMG_DONE = createField("crsm_dmg_done", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.cepfu_raid_stage_monster_history.crsm_end_time</code>. when user killed monster
	 */
	public final TableField<CepfuRaidStageMonsterHistoryRecord, Timestamp> CRSM_END_TIME = createField("crsm_end_time", org.jooq.impl.SQLDataType.TIMESTAMP, this, "when user killed monster");

	/**
	 * Create a <code>mobsters.cepfu_raid_stage_monster_history</code> table reference
	 */
	public CepfuRaidStageMonsterHistory() {
		this("cepfu_raid_stage_monster_history", null);
	}

	/**
	 * Create an aliased <code>mobsters.cepfu_raid_stage_monster_history</code> table reference
	 */
	public CepfuRaidStageMonsterHistory(String alias) {
		this(alias, CEPFU_RAID_STAGE_MONSTER_HISTORY);
	}

	private CepfuRaidStageMonsterHistory(String alias, Table<CepfuRaidStageMonsterHistoryRecord> aliased) {
		this(alias, aliased, null);
	}

	private CepfuRaidStageMonsterHistory(String alias, Table<CepfuRaidStageMonsterHistoryRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<CepfuRaidStageMonsterHistoryRecord> getPrimaryKey() {
		return Keys.KEY_CEPFU_RAID_STAGE_MONSTER_HISTORY_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<CepfuRaidStageMonsterHistoryRecord>> getKeys() {
		return Arrays.<UniqueKey<CepfuRaidStageMonsterHistoryRecord>>asList(Keys.KEY_CEPFU_RAID_STAGE_MONSTER_HISTORY_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CepfuRaidStageMonsterHistory as(String alias) {
		return new CepfuRaidStageMonsterHistory(alias, this);
	}

	/**
	 * Rename this table
	 */
	public CepfuRaidStageMonsterHistory rename(String name) {
		return new CepfuRaidStageMonsterHistory(name, null);
	}
}