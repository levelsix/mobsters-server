/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.ClanRaidStageConfigRecord;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Identity;
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
public class ClanRaidStageConfig extends TableImpl<ClanRaidStageConfigRecord> {

	private static final long serialVersionUID = -776852935;

	/**
	 * The reference instance of <code>mobsters.clan_raid_stage_config</code>
	 */
	public static final ClanRaidStageConfig CLAN_RAID_STAGE_CONFIG = new ClanRaidStageConfig();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<ClanRaidStageConfigRecord> getRecordType() {
		return ClanRaidStageConfigRecord.class;
	}

	/**
	 * The column <code>mobsters.clan_raid_stage_config.id</code>.
	 */
	public final TableField<ClanRaidStageConfigRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>mobsters.clan_raid_stage_config.clan_raid_id</code>. multiple raid stages can point to one raid
	 */
	public final TableField<ClanRaidStageConfigRecord, Integer> CLAN_RAID_ID = createField("clan_raid_id", org.jooq.impl.SQLDataType.INTEGER.defaulted(true), this, "multiple raid stages can point to one raid");

	/**
	 * The column <code>mobsters.clan_raid_stage_config.duration_minutes</code>.
	 */
	public final TableField<ClanRaidStageConfigRecord, Integer> DURATION_MINUTES = createField("duration_minutes", org.jooq.impl.SQLDataType.INTEGER.defaulted(true), this, "");

	/**
	 * The column <code>mobsters.clan_raid_stage_config.stage_num</code>.
	 */
	public final TableField<ClanRaidStageConfigRecord, Byte> STAGE_NUM = createField("stage_num", org.jooq.impl.SQLDataType.TINYINT.defaulted(true), this, "");

	/**
	 * The column <code>mobsters.clan_raid_stage_config.name</code>.
	 */
	public final TableField<ClanRaidStageConfigRecord, String> NAME = createField("name", org.jooq.impl.SQLDataType.VARCHAR.length(45).defaulted(true), this, "");

	/**
	 * Create a <code>mobsters.clan_raid_stage_config</code> table reference
	 */
	public ClanRaidStageConfig() {
		this("clan_raid_stage_config", null);
	}

	/**
	 * Create an aliased <code>mobsters.clan_raid_stage_config</code> table reference
	 */
	public ClanRaidStageConfig(String alias) {
		this(alias, CLAN_RAID_STAGE_CONFIG);
	}

	private ClanRaidStageConfig(String alias, Table<ClanRaidStageConfigRecord> aliased) {
		this(alias, aliased, null);
	}

	private ClanRaidStageConfig(String alias, Table<ClanRaidStageConfigRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Identity<ClanRaidStageConfigRecord, Integer> getIdentity() {
		return Keys.IDENTITY_CLAN_RAID_STAGE_CONFIG;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<ClanRaidStageConfigRecord> getPrimaryKey() {
		return Keys.KEY_CLAN_RAID_STAGE_CONFIG_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<ClanRaidStageConfigRecord>> getKeys() {
		return Arrays.<UniqueKey<ClanRaidStageConfigRecord>>asList(Keys.KEY_CLAN_RAID_STAGE_CONFIG_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanRaidStageConfig as(String alias) {
		return new ClanRaidStageConfig(alias, this);
	}

	/**
	 * Rename this table
	 */
	public ClanRaidStageConfig rename(String name) {
		return new ClanRaidStageConfig(name, null);
	}
}
