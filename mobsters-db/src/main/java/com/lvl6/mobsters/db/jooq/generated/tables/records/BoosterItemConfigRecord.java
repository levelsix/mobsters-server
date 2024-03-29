/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.BoosterItemConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IBoosterItemConfig;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record12;
import org.jooq.Row;
import org.jooq.Row12;
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
@Table(name = "booster_item_config", schema = "mobsters")
public class BoosterItemConfigRecord extends UpdatableRecordImpl<BoosterItemConfigRecord> implements Record12<Integer, Integer, Integer, Byte, Boolean, Boolean, Integer, Integer, Double, Integer, Integer, Integer>, IBoosterItemConfig {

	private static final long serialVersionUID = -141762629;

	/**
	 * Setter for <code>mobsters.booster_item_config.id</code>.
	 */
	@Override
	public BoosterItemConfigRecord setId(Integer value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.booster_item_config.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getId() {
		return (Integer) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.booster_item_config.booster_pack_id</code>.
	 */
	@Override
	public BoosterItemConfigRecord setBoosterPackId(Integer value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.booster_item_config.booster_pack_id</code>.
	 */
	@Column(name = "booster_pack_id", precision = 10)
	@Override
	public Integer getBoosterPackId() {
		return (Integer) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.booster_item_config.monster_id</code>.
	 */
	@Override
	public BoosterItemConfigRecord setMonsterId(Integer value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.booster_item_config.monster_id</code>.
	 */
	@Column(name = "monster_id", precision = 7)
	@Override
	public Integer getMonsterId() {
		return (Integer) getValue(2);
	}

	/**
	 * Setter for <code>mobsters.booster_item_config.num_pieces</code>.
	 */
	@Override
	public BoosterItemConfigRecord setNumPieces(Byte value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.booster_item_config.num_pieces</code>.
	 */
	@Column(name = "num_pieces", precision = 3)
	@Override
	public Byte getNumPieces() {
		return (Byte) getValue(3);
	}

	/**
	 * Setter for <code>mobsters.booster_item_config.is_complete</code>.
	 */
	@Override
	public BoosterItemConfigRecord setIsComplete(Boolean value) {
		setValue(4, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.booster_item_config.is_complete</code>.
	 */
	@Column(name = "is_complete", precision = 1)
	@Override
	public Boolean getIsComplete() {
		return (Boolean) getValue(4);
	}

	/**
	 * Setter for <code>mobsters.booster_item_config.is_special</code>. This value is for the client to determine whether or not this will be displayed on the shelf or not. Server only passes this value along to the client.
	 */
	@Override
	public BoosterItemConfigRecord setIsSpecial(Boolean value) {
		setValue(5, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.booster_item_config.is_special</code>. This value is for the client to determine whether or not this will be displayed on the shelf or not. Server only passes this value along to the client.
	 */
	@Column(name = "is_special", nullable = false, precision = 1)
	@NotNull
	@Override
	public Boolean getIsSpecial() {
		return (Boolean) getValue(5);
	}

	/**
	 * Setter for <code>mobsters.booster_item_config.gem_reward</code>.
	 */
	@Override
	public BoosterItemConfigRecord setGemReward(Integer value) {
		setValue(6, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.booster_item_config.gem_reward</code>.
	 */
	@Column(name = "gem_reward", precision = 7)
	@Override
	public Integer getGemReward() {
		return (Integer) getValue(6);
	}

	/**
	 * Setter for <code>mobsters.booster_item_config.cash_reward</code>.
	 */
	@Override
	public BoosterItemConfigRecord setCashReward(Integer value) {
		setValue(7, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.booster_item_config.cash_reward</code>.
	 */
	@Column(name = "cash_reward", precision = 10)
	@Override
	public Integer getCashReward() {
		return (Integer) getValue(7);
	}

	/**
	 * Setter for <code>mobsters.booster_item_config.chance_to_appear</code>.
	 */
	@Override
	public BoosterItemConfigRecord setChanceToAppear(Double value) {
		setValue(8, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.booster_item_config.chance_to_appear</code>.
	 */
	@Column(name = "chance_to_appear", precision = 12)
	@Override
	public Double getChanceToAppear() {
		return (Double) getValue(8);
	}

	/**
	 * Setter for <code>mobsters.booster_item_config.item_id</code>.
	 */
	@Override
	public BoosterItemConfigRecord setItemId(Integer value) {
		setValue(9, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.booster_item_config.item_id</code>.
	 */
	@Column(name = "item_id", precision = 10)
	@Override
	public Integer getItemId() {
		return (Integer) getValue(9);
	}

	/**
	 * Setter for <code>mobsters.booster_item_config.item_quantity</code>.
	 */
	@Override
	public BoosterItemConfigRecord setItemQuantity(Integer value) {
		setValue(10, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.booster_item_config.item_quantity</code>.
	 */
	@Column(name = "item_quantity", precision = 10)
	@Override
	public Integer getItemQuantity() {
		return (Integer) getValue(10);
	}

	/**
	 * Setter for <code>mobsters.booster_item_config.reward_id</code>.
	 */
	@Override
	public BoosterItemConfigRecord setRewardId(Integer value) {
		setValue(11, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.booster_item_config.reward_id</code>.
	 */
	@Column(name = "reward_id", precision = 10)
	@Override
	public Integer getRewardId() {
		return (Integer) getValue(11);
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
	// Record12 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row12<Integer, Integer, Integer, Byte, Boolean, Boolean, Integer, Integer, Double, Integer, Integer, Integer> fieldsRow() {
		return (Row12) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row12<Integer, Integer, Integer, Byte, Boolean, Boolean, Integer, Integer, Double, Integer, Integer, Integer> valuesRow() {
		return (Row12) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field1() {
		return BoosterItemConfig.BOOSTER_ITEM_CONFIG.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field2() {
		return BoosterItemConfig.BOOSTER_ITEM_CONFIG.BOOSTER_PACK_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field3() {
		return BoosterItemConfig.BOOSTER_ITEM_CONFIG.MONSTER_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Byte> field4() {
		return BoosterItemConfig.BOOSTER_ITEM_CONFIG.NUM_PIECES;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Boolean> field5() {
		return BoosterItemConfig.BOOSTER_ITEM_CONFIG.IS_COMPLETE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Boolean> field6() {
		return BoosterItemConfig.BOOSTER_ITEM_CONFIG.IS_SPECIAL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field7() {
		return BoosterItemConfig.BOOSTER_ITEM_CONFIG.GEM_REWARD;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field8() {
		return BoosterItemConfig.BOOSTER_ITEM_CONFIG.CASH_REWARD;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Double> field9() {
		return BoosterItemConfig.BOOSTER_ITEM_CONFIG.CHANCE_TO_APPEAR;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field10() {
		return BoosterItemConfig.BOOSTER_ITEM_CONFIG.ITEM_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field11() {
		return BoosterItemConfig.BOOSTER_ITEM_CONFIG.ITEM_QUANTITY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field12() {
		return BoosterItemConfig.BOOSTER_ITEM_CONFIG.REWARD_ID;
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
	public Integer value2() {
		return getBoosterPackId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value3() {
		return getMonsterId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Byte value4() {
		return getNumPieces();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean value5() {
		return getIsComplete();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean value6() {
		return getIsSpecial();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value7() {
		return getGemReward();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value8() {
		return getCashReward();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double value9() {
		return getChanceToAppear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value10() {
		return getItemId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value11() {
		return getItemQuantity();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value12() {
		return getRewardId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoosterItemConfigRecord value1(Integer value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoosterItemConfigRecord value2(Integer value) {
		setBoosterPackId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoosterItemConfigRecord value3(Integer value) {
		setMonsterId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoosterItemConfigRecord value4(Byte value) {
		setNumPieces(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoosterItemConfigRecord value5(Boolean value) {
		setIsComplete(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoosterItemConfigRecord value6(Boolean value) {
		setIsSpecial(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoosterItemConfigRecord value7(Integer value) {
		setGemReward(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoosterItemConfigRecord value8(Integer value) {
		setCashReward(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoosterItemConfigRecord value9(Double value) {
		setChanceToAppear(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoosterItemConfigRecord value10(Integer value) {
		setItemId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoosterItemConfigRecord value11(Integer value) {
		setItemQuantity(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoosterItemConfigRecord value12(Integer value) {
		setRewardId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoosterItemConfigRecord values(Integer value1, Integer value2, Integer value3, Byte value4, Boolean value5, Boolean value6, Integer value7, Integer value8, Double value9, Integer value10, Integer value11, Integer value12) {
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
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IBoosterItemConfig from) {
		setId(from.getId());
		setBoosterPackId(from.getBoosterPackId());
		setMonsterId(from.getMonsterId());
		setNumPieces(from.getNumPieces());
		setIsComplete(from.getIsComplete());
		setIsSpecial(from.getIsSpecial());
		setGemReward(from.getGemReward());
		setCashReward(from.getCashReward());
		setChanceToAppear(from.getChanceToAppear());
		setItemId(from.getItemId());
		setItemQuantity(from.getItemQuantity());
		setRewardId(from.getRewardId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IBoosterItemConfig> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached BoosterItemConfigRecord
	 */
	public BoosterItemConfigRecord() {
		super(BoosterItemConfig.BOOSTER_ITEM_CONFIG);
	}

	/**
	 * Create a detached, initialised BoosterItemConfigRecord
	 */
	public BoosterItemConfigRecord(Integer id, Integer boosterPackId, Integer monsterId, Byte numPieces, Boolean isComplete, Boolean isSpecial, Integer gemReward, Integer cashReward, Double chanceToAppear, Integer itemId, Integer itemQuantity, Integer rewardId) {
		super(BoosterItemConfig.BOOSTER_ITEM_CONFIG);

		setValue(0, id);
		setValue(1, boosterPackId);
		setValue(2, monsterId);
		setValue(3, numPieces);
		setValue(4, isComplete);
		setValue(5, isSpecial);
		setValue(6, gemReward);
		setValue(7, cashReward);
		setValue(8, chanceToAppear);
		setValue(9, itemId);
		setValue(10, itemQuantity);
		setValue(11, rewardId);
	}
}
