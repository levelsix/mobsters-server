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
@Table(name = "monster_for_pvp_config", schema = "mobsters")
public interface IMonsterForPvpConfig extends Serializable {

	/**
	 * Setter for <code>mobsters.monster_for_pvp_config.id</code>.
	 */
	public IMonsterForPvpConfig setId(Integer value);

	/**
	 * Getter for <code>mobsters.monster_for_pvp_config.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	public Integer getId();

	/**
	 * Setter for <code>mobsters.monster_for_pvp_config.monster_id</code>.
	 */
	public IMonsterForPvpConfig setMonsterId(Integer value);

	/**
	 * Getter for <code>mobsters.monster_for_pvp_config.monster_id</code>.
	 */
	@Column(name = "monster_id", precision = 10)
	public Integer getMonsterId();

	/**
	 * Setter for <code>mobsters.monster_for_pvp_config.monster_lvl</code>.
	 */
	public IMonsterForPvpConfig setMonsterLvl(Byte value);

	/**
	 * Getter for <code>mobsters.monster_for_pvp_config.monster_lvl</code>.
	 */
	@Column(name = "monster_lvl", precision = 3)
	public Byte getMonsterLvl();

	/**
	 * Setter for <code>mobsters.monster_for_pvp_config.elo</code>.
	 */
	public IMonsterForPvpConfig setElo(Integer value);

	/**
	 * Getter for <code>mobsters.monster_for_pvp_config.elo</code>.
	 */
	@Column(name = "elo", precision = 10)
	public Integer getElo();

	/**
	 * Setter for <code>mobsters.monster_for_pvp_config.min_cash_reward</code>.
	 */
	public IMonsterForPvpConfig setMinCashReward(Integer value);

	/**
	 * Getter for <code>mobsters.monster_for_pvp_config.min_cash_reward</code>.
	 */
	@Column(name = "min_cash_reward", precision = 10)
	public Integer getMinCashReward();

	/**
	 * Setter for <code>mobsters.monster_for_pvp_config.max_cash_reward</code>.
	 */
	public IMonsterForPvpConfig setMaxCashReward(Integer value);

	/**
	 * Getter for <code>mobsters.monster_for_pvp_config.max_cash_reward</code>.
	 */
	@Column(name = "max_cash_reward", precision = 10)
	public Integer getMaxCashReward();

	/**
	 * Setter for <code>mobsters.monster_for_pvp_config.min_oil_reward</code>.
	 */
	public IMonsterForPvpConfig setMinOilReward(Integer value);

	/**
	 * Getter for <code>mobsters.monster_for_pvp_config.min_oil_reward</code>.
	 */
	@Column(name = "min_oil_reward", precision = 10)
	public Integer getMinOilReward();

	/**
	 * Setter for <code>mobsters.monster_for_pvp_config.max_oil_reward</code>.
	 */
	public IMonsterForPvpConfig setMaxOilReward(Integer value);

	/**
	 * Getter for <code>mobsters.monster_for_pvp_config.max_oil_reward</code>.
	 */
	@Column(name = "max_oil_reward", precision = 10)
	public Integer getMaxOilReward();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface IMonsterForPvpConfig
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IMonsterForPvpConfig from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface IMonsterForPvpConfig
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IMonsterForPvpConfig> E into(E into);
}
