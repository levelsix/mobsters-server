/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.MiniJobConfigRecord;

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
public class MiniJobConfig extends TableImpl<MiniJobConfigRecord> {

	private static final long serialVersionUID = -347550219;

	/**
	 * The reference instance of <code>mobsters.mini_job_config</code>
	 */
	public static final MiniJobConfig MINI_JOB_CONFIG = new MiniJobConfig();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<MiniJobConfigRecord> getRecordType() {
		return MiniJobConfigRecord.class;
	}

	/**
	 * The column <code>mobsters.mini_job_config.id</code>.
	 */
	public final TableField<MiniJobConfigRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>mobsters.mini_job_config.required_struct_id</code>.
	 */
	public final TableField<MiniJobConfigRecord, Integer> REQUIRED_STRUCT_ID = createField("required_struct_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.mini_job_config.name</code>.
	 */
	public final TableField<MiniJobConfigRecord, String> NAME = createField("name", org.jooq.impl.SQLDataType.VARCHAR.length(45).defaulted(true), this, "");

	/**
	 * The column <code>mobsters.mini_job_config.cash_reward</code>.
	 */
	public final TableField<MiniJobConfigRecord, Integer> CASH_REWARD = createField("cash_reward", org.jooq.impl.SQLDataType.INTEGER.defaulted(true), this, "");

	/**
	 * The column <code>mobsters.mini_job_config.oil_reward</code>.
	 */
	public final TableField<MiniJobConfigRecord, Integer> OIL_REWARD = createField("oil_reward", org.jooq.impl.SQLDataType.INTEGER.defaulted(true), this, "");

	/**
	 * The column <code>mobsters.mini_job_config.gem_reward</code>.
	 */
	public final TableField<MiniJobConfigRecord, Integer> GEM_REWARD = createField("gem_reward", org.jooq.impl.SQLDataType.INTEGER.defaulted(true), this, "");

	/**
	 * The column <code>mobsters.mini_job_config.monster_id_reward</code>.
	 */
	public final TableField<MiniJobConfigRecord, Integer> MONSTER_ID_REWARD = createField("monster_id_reward", org.jooq.impl.SQLDataType.INTEGER.defaulted(true), this, "");

	/**
	 * The column <code>mobsters.mini_job_config.item_id_reward</code>.
	 */
	public final TableField<MiniJobConfigRecord, Integer> ITEM_ID_REWARD = createField("item_id_reward", org.jooq.impl.SQLDataType.INTEGER.defaulted(true), this, "");

	/**
	 * The column <code>mobsters.mini_job_config.item_reward_quantity</code>.
	 */
	public final TableField<MiniJobConfigRecord, Integer> ITEM_REWARD_QUANTITY = createField("item_reward_quantity", org.jooq.impl.SQLDataType.INTEGER.defaulted(true), this, "");

	/**
	 * The column <code>mobsters.mini_job_config.second_item_id_reward</code>.
	 */
	public final TableField<MiniJobConfigRecord, Integer> SECOND_ITEM_ID_REWARD = createField("second_item_id_reward", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.mini_job_config.second_item_reward_quantity</code>.
	 */
	public final TableField<MiniJobConfigRecord, Integer> SECOND_ITEM_REWARD_QUANTITY = createField("second_item_reward_quantity", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.mini_job_config.quality</code>.
	 */
	public final TableField<MiniJobConfigRecord, String> QUALITY = createField("quality", org.jooq.impl.SQLDataType.VARCHAR.length(45).defaulted(true), this, "");

	/**
	 * The column <code>mobsters.mini_job_config.max_num_monsters_allowed</code>.
	 */
	public final TableField<MiniJobConfigRecord, Byte> MAX_NUM_MONSTERS_ALLOWED = createField("max_num_monsters_allowed", org.jooq.impl.SQLDataType.TINYINT.defaulted(true), this, "");

	/**
	 * The column <code>mobsters.mini_job_config.chance_to_appear</code>.
	 */
	public final TableField<MiniJobConfigRecord, Double> CHANCE_TO_APPEAR = createField("chance_to_appear", org.jooq.impl.SQLDataType.FLOAT.defaulted(true), this, "");

	/**
	 * The column <code>mobsters.mini_job_config.hp_required</code>.
	 */
	public final TableField<MiniJobConfigRecord, Integer> HP_REQUIRED = createField("hp_required", org.jooq.impl.SQLDataType.INTEGER.defaulted(true), this, "");

	/**
	 * The column <code>mobsters.mini_job_config.atk_required</code>.
	 */
	public final TableField<MiniJobConfigRecord, Integer> ATK_REQUIRED = createField("atk_required", org.jooq.impl.SQLDataType.INTEGER.defaulted(true), this, "");

	/**
	 * The column <code>mobsters.mini_job_config.min_dmg</code>.
	 */
	public final TableField<MiniJobConfigRecord, Integer> MIN_DMG = createField("min_dmg", org.jooq.impl.SQLDataType.INTEGER.defaulted(true), this, "");

	/**
	 * The column <code>mobsters.mini_job_config.max_dmg</code>.
	 */
	public final TableField<MiniJobConfigRecord, Integer> MAX_DMG = createField("max_dmg", org.jooq.impl.SQLDataType.INTEGER.defaulted(true), this, "");

	/**
	 * The column <code>mobsters.mini_job_config.duration_min_minutes</code>.
	 */
	public final TableField<MiniJobConfigRecord, Integer> DURATION_MIN_MINUTES = createField("duration_min_minutes", org.jooq.impl.SQLDataType.INTEGER.defaulted(true), this, "");

	/**
	 * The column <code>mobsters.mini_job_config.duration_max_minutes</code>.
	 */
	public final TableField<MiniJobConfigRecord, Integer> DURATION_MAX_MINUTES = createField("duration_max_minutes", org.jooq.impl.SQLDataType.INTEGER.defaulted(true), this, "");

	/**
	 * The column <code>mobsters.mini_job_config.is_valid</code>.
	 */
	public final TableField<MiniJobConfigRecord, Boolean> IS_VALID = createField("is_valid", org.jooq.impl.SQLDataType.BIT.defaulted(true), this, "");

	/**
	 * The column <code>mobsters.mini_job_config.exp_reward</code>.
	 */
	public final TableField<MiniJobConfigRecord, Integer> EXP_REWARD = createField("exp_reward", org.jooq.impl.SQLDataType.INTEGER.defaulted(true), this, "");

	/**
	 * The column <code>mobsters.mini_job_config.reward_id_one</code>.
	 */
	public final TableField<MiniJobConfigRecord, Integer> REWARD_ID_ONE = createField("reward_id_one", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.mini_job_config.reward_id_two</code>.
	 */
	public final TableField<MiniJobConfigRecord, Integer> REWARD_ID_TWO = createField("reward_id_two", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.mini_job_config.reward_id_three</code>.
	 */
	public final TableField<MiniJobConfigRecord, Integer> REWARD_ID_THREE = createField("reward_id_three", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * Create a <code>mobsters.mini_job_config</code> table reference
	 */
	public MiniJobConfig() {
		this("mini_job_config", null);
	}

	/**
	 * Create an aliased <code>mobsters.mini_job_config</code> table reference
	 */
	public MiniJobConfig(String alias) {
		this(alias, MINI_JOB_CONFIG);
	}

	private MiniJobConfig(String alias, Table<MiniJobConfigRecord> aliased) {
		this(alias, aliased, null);
	}

	private MiniJobConfig(String alias, Table<MiniJobConfigRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Identity<MiniJobConfigRecord, Integer> getIdentity() {
		return Keys.IDENTITY_MINI_JOB_CONFIG;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<MiniJobConfigRecord> getPrimaryKey() {
		return Keys.KEY_MINI_JOB_CONFIG_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<MiniJobConfigRecord>> getKeys() {
		return Arrays.<UniqueKey<MiniJobConfigRecord>>asList(Keys.KEY_MINI_JOB_CONFIG_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MiniJobConfig as(String alias) {
		return new MiniJobConfig(alias, this);
	}

	/**
	 * Rename this table
	 */
	public MiniJobConfig rename(String name) {
		return new MiniJobConfig(name, null);
	}
}
