/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.ClanBossRewardRecord;

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
public class ClanBossReward extends TableImpl<ClanBossRewardRecord> {

	private static final long serialVersionUID = 1182417349;

	/**
	 * The reference instance of <code>mobsters.clan_boss_reward</code>
	 */
	public static final ClanBossReward CLAN_BOSS_REWARD = new ClanBossReward();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<ClanBossRewardRecord> getRecordType() {
		return ClanBossRewardRecord.class;
	}

	/**
	 * The column <code>mobsters.clan_boss_reward.id</code>.
	 */
	public final TableField<ClanBossRewardRecord, String> ID = createField("id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>mobsters.clan_boss_reward.clan_boss_id</code>.
	 */
	public final TableField<ClanBossRewardRecord, Integer> CLAN_BOSS_ID = createField("clan_boss_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.clan_boss_reward.equip_id</code>.
	 */
	public final TableField<ClanBossRewardRecord, Integer> EQUIP_ID = createField("equip_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * Create a <code>mobsters.clan_boss_reward</code> table reference
	 */
	public ClanBossReward() {
		this("clan_boss_reward", null);
	}

	/**
	 * Create an aliased <code>mobsters.clan_boss_reward</code> table reference
	 */
	public ClanBossReward(String alias) {
		this(alias, CLAN_BOSS_REWARD);
	}

	private ClanBossReward(String alias, Table<ClanBossRewardRecord> aliased) {
		this(alias, aliased, null);
	}

	private ClanBossReward(String alias, Table<ClanBossRewardRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<ClanBossRewardRecord> getPrimaryKey() {
		return Keys.KEY_CLAN_BOSS_REWARD_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<ClanBossRewardRecord>> getKeys() {
		return Arrays.<UniqueKey<ClanBossRewardRecord>>asList(Keys.KEY_CLAN_BOSS_REWARD_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanBossReward as(String alias) {
		return new ClanBossReward(alias, this);
	}

	/**
	 * Rename this table
	 */
	public ClanBossReward rename(String name) {
		return new ClanBossReward(name, null);
	}
}
