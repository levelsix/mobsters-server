/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.ClanRaidStageRewardConfigRecord;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;


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
public class ClanRaidStageRewardConfig extends TableImpl<ClanRaidStageRewardConfigRecord> {

	private static final long serialVersionUID = -856797391;

	/**
	 * The reference instance of <code>mobsters.clan_raid_stage_reward_config</code>
	 */
	public static final ClanRaidStageRewardConfig CLAN_RAID_STAGE_REWARD_CONFIG = new ClanRaidStageRewardConfig();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<ClanRaidStageRewardConfigRecord> getRecordType() {
		return ClanRaidStageRewardConfigRecord.class;
	}

	/**
	 * The column <code>mobsters.clan_raid_stage_reward_config.id</code>.
	 */
	public final TableField<ClanRaidStageRewardConfigRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>mobsters.clan_raid_stage_reward_config.clan_raid_stage_id</code>.
	 */
	public final TableField<ClanRaidStageRewardConfigRecord, UInteger> CLAN_RAID_STAGE_ID = createField("clan_raid_stage_id", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>mobsters.clan_raid_stage_reward_config.min_oil_reward</code>.
	 */
	public final TableField<ClanRaidStageRewardConfigRecord, UInteger> MIN_OIL_REWARD = createField("min_oil_reward", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>mobsters.clan_raid_stage_reward_config.max_oil_reward</code>. This along with the other columns can be set. Rewards are not mutually exclusive.
	 */
	public final TableField<ClanRaidStageRewardConfigRecord, UInteger> MAX_OIL_REWARD = createField("max_oil_reward", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false).defaulted(true), this, "This along with the other columns can be set. Rewards are not mutually exclusive.");

	/**
	 * The column <code>mobsters.clan_raid_stage_reward_config.min_cash_reward</code>.
	 */
	public final TableField<ClanRaidStageRewardConfigRecord, UInteger> MIN_CASH_REWARD = createField("min_cash_reward", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>mobsters.clan_raid_stage_reward_config.max_cash_reward</code>. This along with the other columns can be set. Rewards are not mutually exclusive.
	 */
	public final TableField<ClanRaidStageRewardConfigRecord, UInteger> MAX_CASH_REWARD = createField("max_cash_reward", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false).defaulted(true), this, "This along with the other columns can be set. Rewards are not mutually exclusive.");

	/**
	 * The column <code>mobsters.clan_raid_stage_reward_config.monster_id</code>. This along with the other columns can be set. Rewards are not mutually exclusive.
	 */
	public final TableField<ClanRaidStageRewardConfigRecord, UInteger> MONSTER_ID = createField("monster_id", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false).defaulted(true), this, "This along with the other columns can be set. Rewards are not mutually exclusive.");

	/**
	 * The column <code>mobsters.clan_raid_stage_reward_config.expected_monster_reward_quantity</code>. more of a drop rate multiplier
	 */
	public final TableField<ClanRaidStageRewardConfigRecord, UByte> EXPECTED_MONSTER_REWARD_QUANTITY = createField("expected_monster_reward_quantity", org.jooq.impl.SQLDataType.TINYINTUNSIGNED.nullable(false).defaulted(true), this, "more of a drop rate multiplier");

	/**
	 * Create a <code>mobsters.clan_raid_stage_reward_config</code> table reference
	 */
	public ClanRaidStageRewardConfig() {
		this("clan_raid_stage_reward_config", null);
	}

	/**
	 * Create an aliased <code>mobsters.clan_raid_stage_reward_config</code> table reference
	 */
	public ClanRaidStageRewardConfig(String alias) {
		this(alias, CLAN_RAID_STAGE_REWARD_CONFIG);
	}

	private ClanRaidStageRewardConfig(String alias, Table<ClanRaidStageRewardConfigRecord> aliased) {
		this(alias, aliased, null);
	}

	private ClanRaidStageRewardConfig(String alias, Table<ClanRaidStageRewardConfigRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<ClanRaidStageRewardConfigRecord> getPrimaryKey() {
		return Keys.KEY_CLAN_RAID_STAGE_REWARD_CONFIG_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<ClanRaidStageRewardConfigRecord>> getKeys() {
		return Arrays.<UniqueKey<ClanRaidStageRewardConfigRecord>>asList(Keys.KEY_CLAN_RAID_STAGE_REWARD_CONFIG_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanRaidStageRewardConfig as(String alias) {
		return new ClanRaidStageRewardConfig(alias, this);
	}

	/**
	 * Rename this table
	 */
	public ClanRaidStageRewardConfig rename(String name) {
		return new ClanRaidStageRewardConfig(name, null);
	}
}
