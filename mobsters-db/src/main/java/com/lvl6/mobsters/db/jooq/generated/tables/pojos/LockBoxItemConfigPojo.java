/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ILockBoxItemConfig;

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
public class LockBoxItemConfigPojo implements ILockBoxItemConfig {

	private static final long serialVersionUID = 1087574168;

	private Integer id;
	private Integer lockBoxEventId;
	private Double  chanceToUnlock;
	private String  name;
	private Byte    classType;
	private String  imageName;
	private Integer redeemForNumBoosterItems;
	private Byte    isGoldBoosterPack;

	public LockBoxItemConfigPojo() {}

	public LockBoxItemConfigPojo(LockBoxItemConfigPojo value) {
		this.id = value.id;
		this.lockBoxEventId = value.lockBoxEventId;
		this.chanceToUnlock = value.chanceToUnlock;
		this.name = value.name;
		this.classType = value.classType;
		this.imageName = value.imageName;
		this.redeemForNumBoosterItems = value.redeemForNumBoosterItems;
		this.isGoldBoosterPack = value.isGoldBoosterPack;
	}

	public LockBoxItemConfigPojo(
		Integer id,
		Integer lockBoxEventId,
		Double  chanceToUnlock,
		String  name,
		Byte    classType,
		String  imageName,
		Integer redeemForNumBoosterItems,
		Byte    isGoldBoosterPack
	) {
		this.id = id;
		this.lockBoxEventId = lockBoxEventId;
		this.chanceToUnlock = chanceToUnlock;
		this.name = name;
		this.classType = classType;
		this.imageName = imageName;
		this.redeemForNumBoosterItems = redeemForNumBoosterItems;
		this.isGoldBoosterPack = isGoldBoosterPack;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public LockBoxItemConfigPojo setId(Integer id) {
		this.id = id;
		return this;
	}

	@Column(name = "lock_box_event_id", precision = 10)
	@Override
	public Integer getLockBoxEventId() {
		return this.lockBoxEventId;
	}

	@Override
	public LockBoxItemConfigPojo setLockBoxEventId(Integer lockBoxEventId) {
		this.lockBoxEventId = lockBoxEventId;
		return this;
	}

	@Column(name = "chance_to_unlock", precision = 12)
	@Override
	public Double getChanceToUnlock() {
		return this.chanceToUnlock;
	}

	@Override
	public LockBoxItemConfigPojo setChanceToUnlock(Double chanceToUnlock) {
		this.chanceToUnlock = chanceToUnlock;
		return this;
	}

	@Column(name = "name", length = 20)
	@Size(max = 20)
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public LockBoxItemConfigPojo setName(String name) {
		this.name = name;
		return this;
	}

	@Column(name = "class_type", precision = 3)
	@Override
	public Byte getClassType() {
		return this.classType;
	}

	@Override
	public LockBoxItemConfigPojo setClassType(Byte classType) {
		this.classType = classType;
		return this;
	}

	@Column(name = "image_name", length = 20)
	@Size(max = 20)
	@Override
	public String getImageName() {
		return this.imageName;
	}

	@Override
	public LockBoxItemConfigPojo setImageName(String imageName) {
		this.imageName = imageName;
		return this;
	}

	@Column(name = "redeem_for_num_booster_items", precision = 10)
	@Override
	public Integer getRedeemForNumBoosterItems() {
		return this.redeemForNumBoosterItems;
	}

	@Override
	public LockBoxItemConfigPojo setRedeemForNumBoosterItems(Integer redeemForNumBoosterItems) {
		this.redeemForNumBoosterItems = redeemForNumBoosterItems;
		return this;
	}

	@Column(name = "is_gold_booster_pack", precision = 3)
	@Override
	public Byte getIsGoldBoosterPack() {
		return this.isGoldBoosterPack;
	}

	@Override
	public LockBoxItemConfigPojo setIsGoldBoosterPack(Byte isGoldBoosterPack) {
		this.isGoldBoosterPack = isGoldBoosterPack;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(ILockBoxItemConfig from) {
		setId(from.getId());
		setLockBoxEventId(from.getLockBoxEventId());
		setChanceToUnlock(from.getChanceToUnlock());
		setName(from.getName());
		setClassType(from.getClassType());
		setImageName(from.getImageName());
		setRedeemForNumBoosterItems(from.getRedeemForNumBoosterItems());
		setIsGoldBoosterPack(from.getIsGoldBoosterPack());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends ILockBoxItemConfig> E into(E into) {
		into.from(this);
		return into;
	}


	public String toString() {
		com.lvl6.mobsters.db.jooq.generated.tables.records.LockBoxItemConfigRecord poop = new com.lvl6.mobsters.db.jooq.generated.tables.records.LockBoxItemConfigRecord();
		poop.from(this);
		return "LockBoxItemConfigPojo[" + poop.valuesRow() + "]";
	}
}
