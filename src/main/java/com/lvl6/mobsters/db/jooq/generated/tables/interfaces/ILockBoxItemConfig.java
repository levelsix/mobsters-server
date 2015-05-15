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
@Table(name = "lock_box_item_config", schema = "mobsters")
public interface ILockBoxItemConfig extends Serializable {

	/**
	 * Setter for <code>mobsters.lock_box_item_config.id</code>.
	 */
	public ILockBoxItemConfig setId(Integer value);

	/**
	 * Getter for <code>mobsters.lock_box_item_config.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	public Integer getId();

	/**
	 * Setter for <code>mobsters.lock_box_item_config.lock_box_event_id</code>.
	 */
	public ILockBoxItemConfig setLockBoxEventId(Integer value);

	/**
	 * Getter for <code>mobsters.lock_box_item_config.lock_box_event_id</code>.
	 */
	@Column(name = "lock_box_event_id", precision = 10)
	public Integer getLockBoxEventId();

	/**
	 * Setter for <code>mobsters.lock_box_item_config.chance_to_unlock</code>.
	 */
	public ILockBoxItemConfig setChanceToUnlock(Double value);

	/**
	 * Getter for <code>mobsters.lock_box_item_config.chance_to_unlock</code>.
	 */
	@Column(name = "chance_to_unlock", precision = 12)
	public Double getChanceToUnlock();

	/**
	 * Setter for <code>mobsters.lock_box_item_config.name</code>.
	 */
	public ILockBoxItemConfig setName(String value);

	/**
	 * Getter for <code>mobsters.lock_box_item_config.name</code>.
	 */
	@Column(name = "name", length = 20)
	@Size(max = 20)
	public String getName();

	/**
	 * Setter for <code>mobsters.lock_box_item_config.class_type</code>.
	 */
	public ILockBoxItemConfig setClassType(Byte value);

	/**
	 * Getter for <code>mobsters.lock_box_item_config.class_type</code>.
	 */
	@Column(name = "class_type", precision = 3)
	public Byte getClassType();

	/**
	 * Setter for <code>mobsters.lock_box_item_config.image_name</code>.
	 */
	public ILockBoxItemConfig setImageName(String value);

	/**
	 * Getter for <code>mobsters.lock_box_item_config.image_name</code>.
	 */
	@Column(name = "image_name", length = 20)
	@Size(max = 20)
	public String getImageName();

	/**
	 * Setter for <code>mobsters.lock_box_item_config.redeem_for_num_booster_items</code>.
	 */
	public ILockBoxItemConfig setRedeemForNumBoosterItems(Integer value);

	/**
	 * Getter for <code>mobsters.lock_box_item_config.redeem_for_num_booster_items</code>.
	 */
	@Column(name = "redeem_for_num_booster_items", precision = 10)
	public Integer getRedeemForNumBoosterItems();

	/**
	 * Setter for <code>mobsters.lock_box_item_config.is_gold_booster_pack</code>.
	 */
	public ILockBoxItemConfig setIsGoldBoosterPack(Byte value);

	/**
	 * Getter for <code>mobsters.lock_box_item_config.is_gold_booster_pack</code>.
	 */
	@Column(name = "is_gold_booster_pack", precision = 3)
	public Byte getIsGoldBoosterPack();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface ILockBoxItemConfig
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ILockBoxItemConfig from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface ILockBoxItemConfig
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ILockBoxItemConfig> E into(E into);
}
