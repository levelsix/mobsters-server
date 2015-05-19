/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.interfaces;


import java.io.Serializable;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;


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
@Entity
@Table(name = "clan_raid_stage_reward_config", schema = "mobsters")
public interface IClanRaidStageRewardConfig extends Serializable {

	/**
	 * Setter for <code>mobsters.clan_raid_stage_reward_config.id</code>.
	 */
	public IClanRaidStageRewardConfig setId(Integer value);

	/**
	 * Getter for <code>mobsters.clan_raid_stage_reward_config.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	public Integer getId();

	/**
	 * Setter for <code>mobsters.clan_raid_stage_reward_config.clan_raid_stage_id</code>.
	 */
	public IClanRaidStageRewardConfig setClanRaidStageId(Integer value);

	/**
	 * Getter for <code>mobsters.clan_raid_stage_reward_config.clan_raid_stage_id</code>.
	 */
	@Column(name = "clan_raid_stage_id", nullable = false, precision = 10)
	@NotNull
	public Integer getClanRaidStageId();

	/**
	 * Setter for <code>mobsters.clan_raid_stage_reward_config.min_oil_reward</code>.
	 */
	public IClanRaidStageRewardConfig setMinOilReward(Integer value);

	/**
	 * Getter for <code>mobsters.clan_raid_stage_reward_config.min_oil_reward</code>.
	 */
	@Column(name = "min_oil_reward", nullable = false, precision = 10)
	@NotNull
	public Integer getMinOilReward();

	/**
	 * Setter for <code>mobsters.clan_raid_stage_reward_config.max_oil_reward</code>. This along with the other columns can be set. Rewards are not mutually exclusive.
	 */
	public IClanRaidStageRewardConfig setMaxOilReward(Integer value);

	/**
	 * Getter for <code>mobsters.clan_raid_stage_reward_config.max_oil_reward</code>. This along with the other columns can be set. Rewards are not mutually exclusive.
	 */
	@Column(name = "max_oil_reward", nullable = false, precision = 10)
	@NotNull
	public Integer getMaxOilReward();

	/**
	 * Setter for <code>mobsters.clan_raid_stage_reward_config.min_cash_reward</code>.
	 */
	public IClanRaidStageRewardConfig setMinCashReward(Integer value);

	/**
	 * Getter for <code>mobsters.clan_raid_stage_reward_config.min_cash_reward</code>.
	 */
	@Column(name = "min_cash_reward", nullable = false, precision = 10)
	@NotNull
	public Integer getMinCashReward();

	/**
	 * Setter for <code>mobsters.clan_raid_stage_reward_config.max_cash_reward</code>. This along with the other columns can be set. Rewards are not mutually exclusive.
	 */
	public IClanRaidStageRewardConfig setMaxCashReward(Integer value);

	/**
	 * Getter for <code>mobsters.clan_raid_stage_reward_config.max_cash_reward</code>. This along with the other columns can be set. Rewards are not mutually exclusive.
	 */
	@Column(name = "max_cash_reward", nullable = false, precision = 10)
	@NotNull
	public Integer getMaxCashReward();

	/**
	 * Setter for <code>mobsters.clan_raid_stage_reward_config.monster_id</code>. This along with the other columns can be set. Rewards are not mutually exclusive.
	 */
	public IClanRaidStageRewardConfig setMonsterId(Integer value);

	/**
	 * Getter for <code>mobsters.clan_raid_stage_reward_config.monster_id</code>. This along with the other columns can be set. Rewards are not mutually exclusive.
	 */
	@Column(name = "monster_id", nullable = false, precision = 10)
	@NotNull
	public Integer getMonsterId();

	/**
	 * Setter for <code>mobsters.clan_raid_stage_reward_config.expected_monster_reward_quantity</code>. more of a drop rate multiplier
	 */
	public IClanRaidStageRewardConfig setExpectedMonsterRewardQuantity(Byte value);

	/**
	 * Getter for <code>mobsters.clan_raid_stage_reward_config.expected_monster_reward_quantity</code>. more of a drop rate multiplier
	 */
	@Column(name = "expected_monster_reward_quantity", nullable = false, precision = 3)
	@NotNull
	public Byte getExpectedMonsterRewardQuantity();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface IClanRaidStageRewardConfig
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IClanRaidStageRewardConfig from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface IClanRaidStageRewardConfig
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IClanRaidStageRewardConfig> E into(E into);
}
