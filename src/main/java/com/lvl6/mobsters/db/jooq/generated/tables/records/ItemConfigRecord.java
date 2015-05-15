/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.ItemConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IItemConfig;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record11;
import org.jooq.Row;
import org.jooq.Row11;
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
@Table(name = "item_config", schema = "mobsters")
public class ItemConfigRecord extends UpdatableRecordImpl<ItemConfigRecord> implements Record11<Integer, String, String, String, String, Integer, Integer, Double, Boolean, String, String>, IItemConfig {

	private static final long serialVersionUID = 816307939;

	/**
	 * Setter for <code>mobsters.item_config.id</code>.
	 */
	@Override
	public ItemConfigRecord setId(Integer value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.item_config.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getId() {
		return (Integer) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.item_config.name</code>.
	 */
	@Override
	public ItemConfigRecord setName(String value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.item_config.name</code>.
	 */
	@Column(name = "name", length = 45)
	@Size(max = 45)
	@Override
	public String getName() {
		return (String) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.item_config.short_name</code>.
	 */
	@Override
	public ItemConfigRecord setShortName(String value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.item_config.short_name</code>.
	 */
	@Column(name = "short_name", length = 45)
	@Size(max = 45)
	@Override
	public String getShortName() {
		return (String) getValue(2);
	}

	/**
	 * Setter for <code>mobsters.item_config.img_name</code>.
	 */
	@Override
	public ItemConfigRecord setImgName(String value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.item_config.img_name</code>.
	 */
	@Column(name = "img_name", length = 100)
	@Size(max = 100)
	@Override
	public String getImgName() {
		return (String) getValue(3);
	}

	/**
	 * Setter for <code>mobsters.item_config.item_type</code>. Specifies pointer to a table: BOOSTER_PACK type is booster_pack table, MONSTER is monster table
	 */
	@Override
	public ItemConfigRecord setItemType(String value) {
		setValue(4, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.item_config.item_type</code>. Specifies pointer to a table: BOOSTER_PACK type is booster_pack table, MONSTER is monster table
	 */
	@Column(name = "item_type", length = 45)
	@Size(max = 45)
	@Override
	public String getItemType() {
		return (String) getValue(4);
	}

	/**
	 * Setter for <code>mobsters.item_config.static_data_id</code>. refers to id in a table, e.g. booster_pack, monster. 
	 */
	@Override
	public ItemConfigRecord setStaticDataId(Integer value) {
		setValue(5, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.item_config.static_data_id</code>. refers to id in a table, e.g. booster_pack, monster. 
	 */
	@Column(name = "static_data_id", precision = 10)
	@Override
	public Integer getStaticDataId() {
		return (Integer) getValue(5);
	}

	/**
	 * Setter for <code>mobsters.item_config.amount</code>.
	 */
	@Override
	public ItemConfigRecord setAmount(Integer value) {
		setValue(6, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.item_config.amount</code>.
	 */
	@Column(name = "amount", precision = 10)
	@Override
	public Integer getAmount() {
		return (Integer) getValue(6);
	}

	/**
	 * Setter for <code>mobsters.item_config.secret_gift_chance</code>.
	 */
	@Override
	public ItemConfigRecord setSecretGiftChance(Double value) {
		setValue(7, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.item_config.secret_gift_chance</code>.
	 */
	@Column(name = "secret_gift_chance", precision = 12)
	@Override
	public Double getSecretGiftChance() {
		return (Double) getValue(7);
	}

	/**
	 * Setter for <code>mobsters.item_config.always_display_to_user</code>.
	 */
	@Override
	public ItemConfigRecord setAlwaysDisplayToUser(Boolean value) {
		setValue(8, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.item_config.always_display_to_user</code>.
	 */
	@Column(name = "always_display_to_user", precision = 1)
	@Override
	public Boolean getAlwaysDisplayToUser() {
		return (Boolean) getValue(8);
	}

	/**
	 * Setter for <code>mobsters.item_config.action_game_type</code>.
	 */
	@Override
	public ItemConfigRecord setActionGameType(String value) {
		setValue(9, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.item_config.action_game_type</code>.
	 */
	@Column(name = "action_game_type", length = 45)
	@Size(max = 45)
	@Override
	public String getActionGameType() {
		return (String) getValue(9);
	}

	/**
	 * Setter for <code>mobsters.item_config.quality</code>.
	 */
	@Override
	public ItemConfigRecord setQuality(String value) {
		setValue(10, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.item_config.quality</code>.
	 */
	@Column(name = "quality", length = 15)
	@Size(max = 15)
	@Override
	public String getQuality() {
		return (String) getValue(10);
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
	// Record11 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row11<Integer, String, String, String, String, Integer, Integer, Double, Boolean, String, String> fieldsRow() {
		return (Row11) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row11<Integer, String, String, String, String, Integer, Integer, Double, Boolean, String, String> valuesRow() {
		return (Row11) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field1() {
		return ItemConfig.ITEM_CONFIG.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field2() {
		return ItemConfig.ITEM_CONFIG.NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field3() {
		return ItemConfig.ITEM_CONFIG.SHORT_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field4() {
		return ItemConfig.ITEM_CONFIG.IMG_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field5() {
		return ItemConfig.ITEM_CONFIG.ITEM_TYPE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field6() {
		return ItemConfig.ITEM_CONFIG.STATIC_DATA_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field7() {
		return ItemConfig.ITEM_CONFIG.AMOUNT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Double> field8() {
		return ItemConfig.ITEM_CONFIG.SECRET_GIFT_CHANCE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Boolean> field9() {
		return ItemConfig.ITEM_CONFIG.ALWAYS_DISPLAY_TO_USER;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field10() {
		return ItemConfig.ITEM_CONFIG.ACTION_GAME_TYPE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field11() {
		return ItemConfig.ITEM_CONFIG.QUALITY;
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
		return getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value3() {
		return getShortName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value4() {
		return getImgName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value5() {
		return getItemType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value6() {
		return getStaticDataId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value7() {
		return getAmount();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double value8() {
		return getSecretGiftChance();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean value9() {
		return getAlwaysDisplayToUser();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value10() {
		return getActionGameType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value11() {
		return getQuality();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ItemConfigRecord value1(Integer value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ItemConfigRecord value2(String value) {
		setName(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ItemConfigRecord value3(String value) {
		setShortName(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ItemConfigRecord value4(String value) {
		setImgName(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ItemConfigRecord value5(String value) {
		setItemType(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ItemConfigRecord value6(Integer value) {
		setStaticDataId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ItemConfigRecord value7(Integer value) {
		setAmount(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ItemConfigRecord value8(Double value) {
		setSecretGiftChance(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ItemConfigRecord value9(Boolean value) {
		setAlwaysDisplayToUser(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ItemConfigRecord value10(String value) {
		setActionGameType(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ItemConfigRecord value11(String value) {
		setQuality(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ItemConfigRecord values(Integer value1, String value2, String value3, String value4, String value5, Integer value6, Integer value7, Double value8, Boolean value9, String value10, String value11) {
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
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IItemConfig from) {
		setId(from.getId());
		setName(from.getName());
		setShortName(from.getShortName());
		setImgName(from.getImgName());
		setItemType(from.getItemType());
		setStaticDataId(from.getStaticDataId());
		setAmount(from.getAmount());
		setSecretGiftChance(from.getSecretGiftChance());
		setAlwaysDisplayToUser(from.getAlwaysDisplayToUser());
		setActionGameType(from.getActionGameType());
		setQuality(from.getQuality());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IItemConfig> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached ItemConfigRecord
	 */
	public ItemConfigRecord() {
		super(ItemConfig.ITEM_CONFIG);
	}

	/**
	 * Create a detached, initialised ItemConfigRecord
	 */
	public ItemConfigRecord(Integer id, String name, String shortName, String imgName, String itemType, Integer staticDataId, Integer amount, Double secretGiftChance, Boolean alwaysDisplayToUser, String actionGameType, String quality) {
		super(ItemConfig.ITEM_CONFIG);

		setValue(0, id);
		setValue(1, name);
		setValue(2, shortName);
		setValue(3, imgName);
		setValue(4, itemType);
		setValue(5, staticDataId);
		setValue(6, amount);
		setValue(7, secretGiftChance);
		setValue(8, alwaysDisplayToUser);
		setValue(9, actionGameType);
		setValue(10, quality);
	}
}
