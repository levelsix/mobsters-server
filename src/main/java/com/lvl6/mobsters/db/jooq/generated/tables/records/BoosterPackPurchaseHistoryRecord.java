/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.BoosterPackPurchaseHistory;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IBoosterPackPurchaseHistory;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record12;
import org.jooq.Record3;
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
@Table(name = "booster_pack_purchase_history", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"user_id", "booster_pack_id", "time_of_purchase"})
})
public class BoosterPackPurchaseHistoryRecord extends UpdatableRecordImpl<BoosterPackPurchaseHistoryRecord> implements Record12<String, Integer, Timestamp, Integer, Integer, Byte, Boolean, Boolean, Integer, Integer, Double, String>, IBoosterPackPurchaseHistory {

	private static final long serialVersionUID = -2072071748;

	/**
	 * Setter for <code>mobsters.booster_pack_purchase_history.user_id</code>.
	 */
	@Override
	public BoosterPackPurchaseHistoryRecord setUserId(String value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.booster_pack_purchase_history.user_id</code>.
	 */
	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getUserId() {
		return (String) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.booster_pack_purchase_history.booster_pack_id</code>.
	 */
	@Override
	public BoosterPackPurchaseHistoryRecord setBoosterPackId(Integer value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.booster_pack_purchase_history.booster_pack_id</code>.
	 */
	@Column(name = "booster_pack_id", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getBoosterPackId() {
		return (Integer) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.booster_pack_purchase_history.time_of_purchase</code>.
	 */
	@Override
	public BoosterPackPurchaseHistoryRecord setTimeOfPurchase(Timestamp value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.booster_pack_purchase_history.time_of_purchase</code>.
	 */
	@Column(name = "time_of_purchase", nullable = false)
	@NotNull
	@Override
	public Timestamp getTimeOfPurchase() {
		return (Timestamp) getValue(2);
	}

	/**
	 * Setter for <code>mobsters.booster_pack_purchase_history.booster_item_id</code>.
	 */
	@Override
	public BoosterPackPurchaseHistoryRecord setBoosterItemId(Integer value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.booster_pack_purchase_history.booster_item_id</code>.
	 */
	@Column(name = "booster_item_id", precision = 10)
	@Override
	public Integer getBoosterItemId() {
		return (Integer) getValue(3);
	}

	/**
	 * Setter for <code>mobsters.booster_pack_purchase_history.monster_id</code>.
	 */
	@Override
	public BoosterPackPurchaseHistoryRecord setMonsterId(Integer value) {
		setValue(4, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.booster_pack_purchase_history.monster_id</code>.
	 */
	@Column(name = "monster_id", precision = 7)
	@Override
	public Integer getMonsterId() {
		return (Integer) getValue(4);
	}

	/**
	 * Setter for <code>mobsters.booster_pack_purchase_history.num_pieces</code>. How many pieces of the monster the user gets if a monster is rewarded.
	 */
	@Override
	public BoosterPackPurchaseHistoryRecord setNumPieces(Byte value) {
		setValue(5, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.booster_pack_purchase_history.num_pieces</code>. How many pieces of the monster the user gets if a monster is rewarded.
	 */
	@Column(name = "num_pieces", precision = 3)
	@Override
	public Byte getNumPieces() {
		return (Byte) getValue(5);
	}

	/**
	 * Setter for <code>mobsters.booster_pack_purchase_history.is_complete</code>. specifies if user can skip combine wait time and use monster after purchasing
	 */
	@Override
	public BoosterPackPurchaseHistoryRecord setIsComplete(Boolean value) {
		setValue(6, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.booster_pack_purchase_history.is_complete</code>. specifies if user can skip combine wait time and use monster after purchasing
	 */
	@Column(name = "is_complete", precision = 1)
	@Override
	public Boolean getIsComplete() {
		return (Boolean) getValue(6);
	}

	/**
	 * Setter for <code>mobsters.booster_pack_purchase_history.is_special</code>. client uses this to determine if it is displayed
	 */
	@Override
	public BoosterPackPurchaseHistoryRecord setIsSpecial(Boolean value) {
		setValue(7, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.booster_pack_purchase_history.is_special</code>. client uses this to determine if it is displayed
	 */
	@Column(name = "is_special", precision = 1)
	@Override
	public Boolean getIsSpecial() {
		return (Boolean) getValue(7);
	}

	/**
	 * Setter for <code>mobsters.booster_pack_purchase_history.gem_reward</code>.
	 */
	@Override
	public BoosterPackPurchaseHistoryRecord setGemReward(Integer value) {
		setValue(8, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.booster_pack_purchase_history.gem_reward</code>.
	 */
	@Column(name = "gem_reward", precision = 7)
	@Override
	public Integer getGemReward() {
		return (Integer) getValue(8);
	}

	/**
	 * Setter for <code>mobsters.booster_pack_purchase_history.cash_reward</code>. At the moment, unused
	 */
	@Override
	public BoosterPackPurchaseHistoryRecord setCashReward(Integer value) {
		setValue(9, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.booster_pack_purchase_history.cash_reward</code>. At the moment, unused
	 */
	@Column(name = "cash_reward", precision = 7)
	@Override
	public Integer getCashReward() {
		return (Integer) getValue(9);
	}

	/**
	 * Setter for <code>mobsters.booster_pack_purchase_history.chance_to_appear</code>.
	 */
	@Override
	public BoosterPackPurchaseHistoryRecord setChanceToAppear(Double value) {
		setValue(10, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.booster_pack_purchase_history.chance_to_appear</code>.
	 */
	@Column(name = "chance_to_appear", precision = 12)
	@Override
	public Double getChanceToAppear() {
		return (Double) getValue(10);
	}

	/**
	 * Setter for <code>mobsters.booster_pack_purchase_history.changed_monster_for_user_ids</code>.
	 */
	@Override
	public BoosterPackPurchaseHistoryRecord setChangedMonsterForUserIds(String value) {
		setValue(11, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.booster_pack_purchase_history.changed_monster_for_user_ids</code>.
	 */
	@Column(name = "changed_monster_for_user_ids", length = 511)
	@Size(max = 511)
	@Override
	public String getChangedMonsterForUserIds() {
		return (String) getValue(11);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Record3<String, Integer, Timestamp> key() {
		return (Record3) super.key();
	}

	// -------------------------------------------------------------------------
	// Record12 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row12<String, Integer, Timestamp, Integer, Integer, Byte, Boolean, Boolean, Integer, Integer, Double, String> fieldsRow() {
		return (Row12) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row12<String, Integer, Timestamp, Integer, Integer, Byte, Boolean, Boolean, Integer, Integer, Double, String> valuesRow() {
		return (Row12) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field1() {
		return BoosterPackPurchaseHistory.BOOSTER_PACK_PURCHASE_HISTORY.USER_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field2() {
		return BoosterPackPurchaseHistory.BOOSTER_PACK_PURCHASE_HISTORY.BOOSTER_PACK_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Timestamp> field3() {
		return BoosterPackPurchaseHistory.BOOSTER_PACK_PURCHASE_HISTORY.TIME_OF_PURCHASE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field4() {
		return BoosterPackPurchaseHistory.BOOSTER_PACK_PURCHASE_HISTORY.BOOSTER_ITEM_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field5() {
		return BoosterPackPurchaseHistory.BOOSTER_PACK_PURCHASE_HISTORY.MONSTER_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Byte> field6() {
		return BoosterPackPurchaseHistory.BOOSTER_PACK_PURCHASE_HISTORY.NUM_PIECES;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Boolean> field7() {
		return BoosterPackPurchaseHistory.BOOSTER_PACK_PURCHASE_HISTORY.IS_COMPLETE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Boolean> field8() {
		return BoosterPackPurchaseHistory.BOOSTER_PACK_PURCHASE_HISTORY.IS_SPECIAL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field9() {
		return BoosterPackPurchaseHistory.BOOSTER_PACK_PURCHASE_HISTORY.GEM_REWARD;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field10() {
		return BoosterPackPurchaseHistory.BOOSTER_PACK_PURCHASE_HISTORY.CASH_REWARD;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Double> field11() {
		return BoosterPackPurchaseHistory.BOOSTER_PACK_PURCHASE_HISTORY.CHANCE_TO_APPEAR;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field12() {
		return BoosterPackPurchaseHistory.BOOSTER_PACK_PURCHASE_HISTORY.CHANGED_MONSTER_FOR_USER_IDS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value1() {
		return getUserId();
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
	public Timestamp value3() {
		return getTimeOfPurchase();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value4() {
		return getBoosterItemId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value5() {
		return getMonsterId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Byte value6() {
		return getNumPieces();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean value7() {
		return getIsComplete();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean value8() {
		return getIsSpecial();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value9() {
		return getGemReward();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value10() {
		return getCashReward();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double value11() {
		return getChanceToAppear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value12() {
		return getChangedMonsterForUserIds();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoosterPackPurchaseHistoryRecord value1(String value) {
		setUserId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoosterPackPurchaseHistoryRecord value2(Integer value) {
		setBoosterPackId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoosterPackPurchaseHistoryRecord value3(Timestamp value) {
		setTimeOfPurchase(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoosterPackPurchaseHistoryRecord value4(Integer value) {
		setBoosterItemId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoosterPackPurchaseHistoryRecord value5(Integer value) {
		setMonsterId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoosterPackPurchaseHistoryRecord value6(Byte value) {
		setNumPieces(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoosterPackPurchaseHistoryRecord value7(Boolean value) {
		setIsComplete(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoosterPackPurchaseHistoryRecord value8(Boolean value) {
		setIsSpecial(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoosterPackPurchaseHistoryRecord value9(Integer value) {
		setGemReward(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoosterPackPurchaseHistoryRecord value10(Integer value) {
		setCashReward(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoosterPackPurchaseHistoryRecord value11(Double value) {
		setChanceToAppear(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoosterPackPurchaseHistoryRecord value12(String value) {
		setChangedMonsterForUserIds(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoosterPackPurchaseHistoryRecord values(String value1, Integer value2, Timestamp value3, Integer value4, Integer value5, Byte value6, Boolean value7, Boolean value8, Integer value9, Integer value10, Double value11, String value12) {
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
	public void from(IBoosterPackPurchaseHistory from) {
		setUserId(from.getUserId());
		setBoosterPackId(from.getBoosterPackId());
		setTimeOfPurchase(from.getTimeOfPurchase());
		setBoosterItemId(from.getBoosterItemId());
		setMonsterId(from.getMonsterId());
		setNumPieces(from.getNumPieces());
		setIsComplete(from.getIsComplete());
		setIsSpecial(from.getIsSpecial());
		setGemReward(from.getGemReward());
		setCashReward(from.getCashReward());
		setChanceToAppear(from.getChanceToAppear());
		setChangedMonsterForUserIds(from.getChangedMonsterForUserIds());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IBoosterPackPurchaseHistory> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached BoosterPackPurchaseHistoryRecord
	 */
	public BoosterPackPurchaseHistoryRecord() {
		super(BoosterPackPurchaseHistory.BOOSTER_PACK_PURCHASE_HISTORY);
	}

	/**
	 * Create a detached, initialised BoosterPackPurchaseHistoryRecord
	 */
	public BoosterPackPurchaseHistoryRecord(String userId, Integer boosterPackId, Timestamp timeOfPurchase, Integer boosterItemId, Integer monsterId, Byte numPieces, Boolean isComplete, Boolean isSpecial, Integer gemReward, Integer cashReward, Double chanceToAppear, String changedMonsterForUserIds) {
		super(BoosterPackPurchaseHistory.BOOSTER_PACK_PURCHASE_HISTORY);

		setValue(0, userId);
		setValue(1, boosterPackId);
		setValue(2, timeOfPurchase);
		setValue(3, boosterItemId);
		setValue(4, monsterId);
		setValue(5, numPieces);
		setValue(6, isComplete);
		setValue(7, isSpecial);
		setValue(8, gemReward);
		setValue(9, cashReward);
		setValue(10, chanceToAppear);
		setValue(11, changedMonsterForUserIds);
	}
}
