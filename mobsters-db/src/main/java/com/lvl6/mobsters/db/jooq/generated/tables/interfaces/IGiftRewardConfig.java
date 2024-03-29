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
@Table(name = "gift_reward_config", schema = "mobsters")
public interface IGiftRewardConfig extends Serializable {

	/**
	 * Setter for <code>mobsters.gift_reward_config.id</code>.
	 */
	public IGiftRewardConfig setId(Integer value);

	/**
	 * Getter for <code>mobsters.gift_reward_config.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	public Integer getId();

	/**
	 * Setter for <code>mobsters.gift_reward_config.gift_id</code>.
	 */
	public IGiftRewardConfig setGiftId(Integer value);

	/**
	 * Getter for <code>mobsters.gift_reward_config.gift_id</code>.
	 */
	@Column(name = "gift_id", precision = 10)
	public Integer getGiftId();

	/**
	 * Setter for <code>mobsters.gift_reward_config.reward_id</code>.
	 */
	public IGiftRewardConfig setRewardId(Integer value);

	/**
	 * Getter for <code>mobsters.gift_reward_config.reward_id</code>.
	 */
	@Column(name = "reward_id", precision = 10)
	public Integer getRewardId();

	/**
	 * Setter for <code>mobsters.gift_reward_config.chance_of_drop</code>. these all add up to a value of 1 per clan gift…unless you have serious qualms about that in which case let byron know
	 */
	public IGiftRewardConfig setChanceOfDrop(Double value);

	/**
	 * Getter for <code>mobsters.gift_reward_config.chance_of_drop</code>. these all add up to a value of 1 per clan gift…unless you have serious qualms about that in which case let byron know
	 */
	@Column(name = "chance_of_drop", precision = 12)
	public Double getChanceOfDrop();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface IGiftRewardConfig
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IGiftRewardConfig from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface IGiftRewardConfig
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IGiftRewardConfig> E into(E into);
}
