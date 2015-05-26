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
@Table(name = "tango_gift_reward_config", schema = "mobsters")
public interface ITangoGiftRewardConfig extends Serializable {

	/**
	 * Setter for <code>mobsters.tango_gift_reward_config.id</code>.
	 */
	public ITangoGiftRewardConfig setId(Integer value);

	/**
	 * Getter for <code>mobsters.tango_gift_reward_config.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	public Integer getId();

	/**
	 * Setter for <code>mobsters.tango_gift_reward_config.tango_gift_id</code>.
	 */
	public ITangoGiftRewardConfig setTangoGiftId(Integer value);

	/**
	 * Getter for <code>mobsters.tango_gift_reward_config.tango_gift_id</code>.
	 */
	@Column(name = "tango_gift_id", precision = 10)
	public Integer getTangoGiftId();

	/**
	 * Setter for <code>mobsters.tango_gift_reward_config.reward_id</code>.
	 */
	public ITangoGiftRewardConfig setRewardId(Integer value);

	/**
	 * Getter for <code>mobsters.tango_gift_reward_config.reward_id</code>.
	 */
	@Column(name = "reward_id", precision = 10)
	public Integer getRewardId();

	/**
	 * Setter for <code>mobsters.tango_gift_reward_config.chance_of_drop</code>.
	 */
	public ITangoGiftRewardConfig setChanceOfDrop(Double value);

	/**
	 * Getter for <code>mobsters.tango_gift_reward_config.chance_of_drop</code>.
	 */
	@Column(name = "chance_of_drop", precision = 12)
	public Double getChanceOfDrop();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface ITangoGiftRewardConfig
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ITangoGiftRewardConfig from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface ITangoGiftRewardConfig
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ITangoGiftRewardConfig> E into(E into);
}
