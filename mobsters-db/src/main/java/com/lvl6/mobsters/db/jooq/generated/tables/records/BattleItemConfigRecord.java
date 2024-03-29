/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.BattleItemConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IBattleItemConfig;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record13;
import org.jooq.Row;
import org.jooq.Row13;
import org.jooq.impl.UpdatableRecordImpl;


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
@Table(name = "battle_item_config", schema = "mobsters")
public class BattleItemConfigRecord extends UpdatableRecordImpl<BattleItemConfigRecord> implements Record13<Integer, String, String, String, Integer, String, String, Integer, String, Integer, Integer, Integer, Integer>, IBattleItemConfig {

	private static final long serialVersionUID = -1141270463;

	/**
	 * Setter for <code>mobsters.battle_item_config.id</code>.
	 */
	@Override
	public BattleItemConfigRecord setId(Integer value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.battle_item_config.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getId() {
		return (Integer) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.battle_item_config.type</code>.
	 */
	@Override
	public BattleItemConfigRecord setType(String value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.battle_item_config.type</code>.
	 */
	@Column(name = "type", nullable = false, length = 45)
	@NotNull
	@Size(max = 45)
	@Override
	public String getType() {
		return (String) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.battle_item_config.battle_item_category</code>.
	 */
	@Override
	public BattleItemConfigRecord setBattleItemCategory(String value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.battle_item_config.battle_item_category</code>.
	 */
	@Column(name = "battle_item_category", nullable = false, length = 45)
	@NotNull
	@Size(max = 45)
	@Override
	public String getBattleItemCategory() {
		return (String) getValue(2);
	}

	/**
	 * Setter for <code>mobsters.battle_item_config.create_resource_type</code>.
	 */
	@Override
	public BattleItemConfigRecord setCreateResourceType(String value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.battle_item_config.create_resource_type</code>.
	 */
	@Column(name = "create_resource_type", nullable = false, length = 45)
	@NotNull
	@Size(max = 45)
	@Override
	public String getCreateResourceType() {
		return (String) getValue(3);
	}

	/**
	 * Setter for <code>mobsters.battle_item_config.create_cost</code>.
	 */
	@Override
	public BattleItemConfigRecord setCreateCost(Integer value) {
		setValue(4, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.battle_item_config.create_cost</code>.
	 */
	@Column(name = "create_cost", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getCreateCost() {
		return (Integer) getValue(4);
	}

	/**
	 * Setter for <code>mobsters.battle_item_config.name</code>.
	 */
	@Override
	public BattleItemConfigRecord setName(String value) {
		setValue(5, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.battle_item_config.name</code>.
	 */
	@Column(name = "name", nullable = false, length = 45)
	@NotNull
	@Size(max = 45)
	@Override
	public String getName() {
		return (String) getValue(5);
	}

	/**
	 * Setter for <code>mobsters.battle_item_config.description</code>.
	 */
	@Override
	public BattleItemConfigRecord setDescription(String value) {
		setValue(6, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.battle_item_config.description</code>.
	 */
	@Column(name = "description", length = 65535)
	@Size(max = 65535)
	@Override
	public String getDescription() {
		return (String) getValue(6);
	}

	/**
	 * Setter for <code>mobsters.battle_item_config.power_amount</code>.
	 */
	@Override
	public BattleItemConfigRecord setPowerAmount(Integer value) {
		setValue(7, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.battle_item_config.power_amount</code>.
	 */
	@Column(name = "power_amount", precision = 10)
	@Override
	public Integer getPowerAmount() {
		return (Integer) getValue(7);
	}

	/**
	 * Setter for <code>mobsters.battle_item_config.image_name</code>.
	 */
	@Override
	public BattleItemConfigRecord setImageName(String value) {
		setValue(8, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.battle_item_config.image_name</code>.
	 */
	@Column(name = "image_name", length = 45)
	@Size(max = 45)
	@Override
	public String getImageName() {
		return (String) getValue(8);
	}

	/**
	 * Setter for <code>mobsters.battle_item_config.priority</code>.
	 */
	@Override
	public BattleItemConfigRecord setPriority(Integer value) {
		setValue(9, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.battle_item_config.priority</code>.
	 */
	@Column(name = "priority", precision = 10)
	@Override
	public Integer getPriority() {
		return (Integer) getValue(9);
	}

	/**
	 * Setter for <code>mobsters.battle_item_config.minutes_to_create</code>.
	 */
	@Override
	public BattleItemConfigRecord setMinutesToCreate(Integer value) {
		setValue(10, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.battle_item_config.minutes_to_create</code>.
	 */
	@Column(name = "minutes_to_create", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getMinutesToCreate() {
		return (Integer) getValue(10);
	}

	/**
	 * Setter for <code>mobsters.battle_item_config.in_battle_gem_cost</code>.
	 */
	@Override
	public BattleItemConfigRecord setInBattleGemCost(Integer value) {
		setValue(11, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.battle_item_config.in_battle_gem_cost</code>.
	 */
	@Column(name = "in_battle_gem_cost", precision = 10)
	@Override
	public Integer getInBattleGemCost() {
		return (Integer) getValue(11);
	}

	/**
	 * Setter for <code>mobsters.battle_item_config.amount</code>. this is what rain does, like how much a potion heals etc
	 */
	@Override
	public BattleItemConfigRecord setAmount(Integer value) {
		setValue(12, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.battle_item_config.amount</code>. this is what rain does, like how much a potion heals etc
	 */
	@Column(name = "amount", precision = 10)
	@Override
	public Integer getAmount() {
		return (Integer) getValue(12);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Record1<Integer> key() {
		return (Record1) super.key();
	}

	// -------------------------------------------------------------------------
	// Record13 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row13<Integer, String, String, String, Integer, String, String, Integer, String, Integer, Integer, Integer, Integer> fieldsRow() {
		return (Row13) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row13<Integer, String, String, String, Integer, String, String, Integer, String, Integer, Integer, Integer, Integer> valuesRow() {
		return (Row13) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field1() {
		return BattleItemConfig.BATTLE_ITEM_CONFIG.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field2() {
		return BattleItemConfig.BATTLE_ITEM_CONFIG.TYPE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field3() {
		return BattleItemConfig.BATTLE_ITEM_CONFIG.BATTLE_ITEM_CATEGORY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field4() {
		return BattleItemConfig.BATTLE_ITEM_CONFIG.CREATE_RESOURCE_TYPE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field5() {
		return BattleItemConfig.BATTLE_ITEM_CONFIG.CREATE_COST;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field6() {
		return BattleItemConfig.BATTLE_ITEM_CONFIG.NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field7() {
		return BattleItemConfig.BATTLE_ITEM_CONFIG.DESCRIPTION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field8() {
		return BattleItemConfig.BATTLE_ITEM_CONFIG.POWER_AMOUNT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field9() {
		return BattleItemConfig.BATTLE_ITEM_CONFIG.IMAGE_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field10() {
		return BattleItemConfig.BATTLE_ITEM_CONFIG.PRIORITY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field11() {
		return BattleItemConfig.BATTLE_ITEM_CONFIG.MINUTES_TO_CREATE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field12() {
		return BattleItemConfig.BATTLE_ITEM_CONFIG.IN_BATTLE_GEM_COST;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field13() {
		return BattleItemConfig.BATTLE_ITEM_CONFIG.AMOUNT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value1() {
		return getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value2() {
		return getType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value3() {
		return getBattleItemCategory();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value4() {
		return getCreateResourceType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value5() {
		return getCreateCost();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value6() {
		return getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value7() {
		return getDescription();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value8() {
		return getPowerAmount();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value9() {
		return getImageName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value10() {
		return getPriority();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value11() {
		return getMinutesToCreate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value12() {
		return getInBattleGemCost();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value13() {
		return getAmount();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BattleItemConfigRecord value1(Integer value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BattleItemConfigRecord value2(String value) {
		setType(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BattleItemConfigRecord value3(String value) {
		setBattleItemCategory(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BattleItemConfigRecord value4(String value) {
		setCreateResourceType(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BattleItemConfigRecord value5(Integer value) {
		setCreateCost(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BattleItemConfigRecord value6(String value) {
		setName(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BattleItemConfigRecord value7(String value) {
		setDescription(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BattleItemConfigRecord value8(Integer value) {
		setPowerAmount(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BattleItemConfigRecord value9(String value) {
		setImageName(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BattleItemConfigRecord value10(Integer value) {
		setPriority(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BattleItemConfigRecord value11(Integer value) {
		setMinutesToCreate(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BattleItemConfigRecord value12(Integer value) {
		setInBattleGemCost(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BattleItemConfigRecord value13(Integer value) {
		setAmount(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BattleItemConfigRecord values(Integer value1, String value2, String value3, String value4, Integer value5, String value6, String value7, Integer value8, String value9, Integer value10, Integer value11, Integer value12, Integer value13) {
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
		value5(value5);
		value6(value6);
		value7(value7);
		value8(value8);
		value9(value9);
		value10(value10);
		value11(value11);
		value12(value12);
		value13(value13);
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IBattleItemConfig from) {
		setId(from.getId());
		setType(from.getType());
		setBattleItemCategory(from.getBattleItemCategory());
		setCreateResourceType(from.getCreateResourceType());
		setCreateCost(from.getCreateCost());
		setName(from.getName());
		setDescription(from.getDescription());
		setPowerAmount(from.getPowerAmount());
		setImageName(from.getImageName());
		setPriority(from.getPriority());
		setMinutesToCreate(from.getMinutesToCreate());
		setInBattleGemCost(from.getInBattleGemCost());
		setAmount(from.getAmount());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IBattleItemConfig> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached BattleItemConfigRecord
	 */
	public BattleItemConfigRecord() {
		super(BattleItemConfig.BATTLE_ITEM_CONFIG);
	}

	/**
	 * Create a detached, initialised BattleItemConfigRecord
	 */
	public BattleItemConfigRecord(Integer id, String type, String battleItemCategory, String createResourceType, Integer createCost, String name, String description, Integer powerAmount, String imageName, Integer priority, Integer minutesToCreate, Integer inBattleGemCost, Integer amount) {
		super(BattleItemConfig.BATTLE_ITEM_CONFIG);

		setValue(0, id);
		setValue(1, type);
		setValue(2, battleItemCategory);
		setValue(3, createResourceType);
		setValue(4, createCost);
		setValue(5, name);
		setValue(6, description);
		setValue(7, powerAmount);
		setValue(8, imageName);
		setValue(9, priority);
		setValue(10, minutesToCreate);
		setValue(11, inBattleGemCost);
		setValue(12, amount);
	}
}
