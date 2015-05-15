/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.ClanGiftRewardConfigRecord;

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
public class ClanGiftRewardConfig extends TableImpl<ClanGiftRewardConfigRecord> {

	private static final long serialVersionUID = -1183878848;

	/**
	 * The reference instance of <code>mobsters.clan_gift_reward_config</code>
	 */
	public static final ClanGiftRewardConfig CLAN_GIFT_REWARD_CONFIG = new ClanGiftRewardConfig();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<ClanGiftRewardConfigRecord> getRecordType() {
		return ClanGiftRewardConfigRecord.class;
	}

	/**
	 * The column <code>mobsters.clan_gift_reward_config.id</code>.
	 */
	public final TableField<ClanGiftRewardConfigRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>mobsters.clan_gift_reward_config.clan_gift_id</code>.
	 */
	public final TableField<ClanGiftRewardConfigRecord, Integer> CLAN_GIFT_ID = createField("clan_gift_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.clan_gift_reward_config.reward_id</code>.
	 */
	public final TableField<ClanGiftRewardConfigRecord, Integer> REWARD_ID = createField("reward_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.clan_gift_reward_config.chance_of_drop</code>. these all add up to a value of 1 per clan gift…unless you have serious qualms about that in which case let byron know
	 */
	public final TableField<ClanGiftRewardConfigRecord, Double> CHANCE_OF_DROP = createField("chance_of_drop", org.jooq.impl.SQLDataType.FLOAT, this, "these all add up to a value of 1 per clan gift…unless you have serious qualms about that in which case let byron know");

	/**
	 * Create a <code>mobsters.clan_gift_reward_config</code> table reference
	 */
	public ClanGiftRewardConfig() {
		this("clan_gift_reward_config", null);
	}

	/**
	 * Create an aliased <code>mobsters.clan_gift_reward_config</code> table reference
	 */
	public ClanGiftRewardConfig(String alias) {
		this(alias, CLAN_GIFT_REWARD_CONFIG);
	}

	private ClanGiftRewardConfig(String alias, Table<ClanGiftRewardConfigRecord> aliased) {
		this(alias, aliased, null);
	}

	private ClanGiftRewardConfig(String alias, Table<ClanGiftRewardConfigRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<ClanGiftRewardConfigRecord> getPrimaryKey() {
		return Keys.KEY_CLAN_GIFT_REWARD_CONFIG_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<ClanGiftRewardConfigRecord>> getKeys() {
		return Arrays.<UniqueKey<ClanGiftRewardConfigRecord>>asList(Keys.KEY_CLAN_GIFT_REWARD_CONFIG_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanGiftRewardConfig as(String alias) {
		return new ClanGiftRewardConfig(alias, this);
	}

	/**
	 * Rename this table
	 */
	public ClanGiftRewardConfig rename(String name) {
		return new ClanGiftRewardConfig(name, null);
	}
}
