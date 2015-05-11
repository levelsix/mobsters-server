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
import javax.validation.constraints.Size;

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
@Entity
@Table(name = "booster_display_item_config", schema = "mobsters")
public interface IBoosterDisplayItemConfig extends Serializable {

	/**
	 * Setter for <code>mobsters.booster_display_item_config.id</code>.
	 */
	public IBoosterDisplayItemConfig setId(Integer value);

	/**
	 * Getter for <code>mobsters.booster_display_item_config.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	public Integer getId();

	/**
	 * Setter for <code>mobsters.booster_display_item_config.booster_pack_id</code>.
	 */
	public IBoosterDisplayItemConfig setBoosterPackId(UInteger value);

	/**
	 * Getter for <code>mobsters.booster_display_item_config.booster_pack_id</code>.
	 */
	@Column(name = "booster_pack_id", precision = 10)
	public UInteger getBoosterPackId();

	/**
	 * Setter for <code>mobsters.booster_display_item_config.is_monster</code>.
	 */
	public IBoosterDisplayItemConfig setIsMonster(Boolean value);

	/**
	 * Getter for <code>mobsters.booster_display_item_config.is_monster</code>.
	 */
	@Column(name = "is_monster", precision = 1)
	public Boolean getIsMonster();

	/**
	 * Setter for <code>mobsters.booster_display_item_config.is_complete</code>.
	 */
	public IBoosterDisplayItemConfig setIsComplete(Boolean value);

	/**
	 * Getter for <code>mobsters.booster_display_item_config.is_complete</code>.
	 */
	@Column(name = "is_complete", precision = 1)
	public Boolean getIsComplete();

	/**
	 * Setter for <code>mobsters.booster_display_item_config.monster_quality</code>.
	 */
	public IBoosterDisplayItemConfig setMonsterQuality(String value);

	/**
	 * Getter for <code>mobsters.booster_display_item_config.monster_quality</code>.
	 */
	@Column(name = "monster_quality", length = 15)
	@Size(max = 15)
	public String getMonsterQuality();

	/**
	 * Setter for <code>mobsters.booster_display_item_config.gem_reward</code>.
	 */
	public IBoosterDisplayItemConfig setGemReward(UInteger value);

	/**
	 * Getter for <code>mobsters.booster_display_item_config.gem_reward</code>.
	 */
	@Column(name = "gem_reward", precision = 7)
	public UInteger getGemReward();

	/**
	 * Setter for <code>mobsters.booster_display_item_config.quantity</code>. how many times this booster_item appears in the spinner
	 */
	public IBoosterDisplayItemConfig setQuantity(UInteger value);

	/**
	 * Getter for <code>mobsters.booster_display_item_config.quantity</code>. how many times this booster_item appears in the spinner
	 */
	@Column(name = "quantity", precision = 7)
	public UInteger getQuantity();

	/**
	 * Setter for <code>mobsters.booster_display_item_config.item_id</code>.
	 */
	public IBoosterDisplayItemConfig setItemId(Integer value);

	/**
	 * Getter for <code>mobsters.booster_display_item_config.item_id</code>.
	 */
	@Column(name = "item_id", precision = 10)
	public Integer getItemId();

	/**
	 * Setter for <code>mobsters.booster_display_item_config.item_quantity</code>.
	 */
	public IBoosterDisplayItemConfig setItemQuantity(Integer value);

	/**
	 * Getter for <code>mobsters.booster_display_item_config.item_quantity</code>.
	 */
	@Column(name = "item_quantity", precision = 10)
	public Integer getItemQuantity();

	/**
	 * Setter for <code>mobsters.booster_display_item_config.reward_id</code>.
	 */
	public IBoosterDisplayItemConfig setRewardId(Integer value);

	/**
	 * Getter for <code>mobsters.booster_display_item_config.reward_id</code>.
	 */
	@Column(name = "reward_id", precision = 10)
	public Integer getRewardId();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface IBoosterDisplayItemConfig
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IBoosterDisplayItemConfig from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface IBoosterDisplayItemConfig
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IBoosterDisplayItemConfig> E into(E into);
}