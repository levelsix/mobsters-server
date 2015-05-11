/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.BattleItemForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IBattleItemForUser;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Record4;
import org.jooq.Row;
import org.jooq.Row4;
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
@Table(name = "battle_item_for_user", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"id", "user_id"})
})
public class BattleItemForUserRecord extends UpdatableRecordImpl<BattleItemForUserRecord> implements Record4<String, String, Integer, Integer>, IBattleItemForUser {

	private static final long serialVersionUID = 1522031110;

	/**
	 * Setter for <code>mobsters.battle_item_for_user.id</code>.
	 */
	@Override
	public BattleItemForUserRecord setId(String value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.battle_item_for_user.id</code>.
	 */
	@Column(name = "id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getId() {
		return (String) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.battle_item_for_user.user_id</code>.
	 */
	@Override
	public BattleItemForUserRecord setUserId(String value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.battle_item_for_user.user_id</code>.
	 */
	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getUserId() {
		return (String) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.battle_item_for_user.battle_item_id</code>.
	 */
	@Override
	public BattleItemForUserRecord setBattleItemId(Integer value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.battle_item_for_user.battle_item_id</code>.
	 */
	@Column(name = "battle_item_id", precision = 10)
	@Override
	public Integer getBattleItemId() {
		return (Integer) getValue(2);
	}

	/**
	 * Setter for <code>mobsters.battle_item_for_user.quantity</code>.
	 */
	@Override
	public BattleItemForUserRecord setQuantity(Integer value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.battle_item_for_user.quantity</code>.
	 */
	@Column(name = "quantity", precision = 10)
	@Override
	public Integer getQuantity() {
		return (Integer) getValue(3);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Record2<String, String> key() {
		return (Record2) super.key();
	}

	// -------------------------------------------------------------------------
	// Record4 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row4<String, String, Integer, Integer> fieldsRow() {
		return (Row4) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row4<String, String, Integer, Integer> valuesRow() {
		return (Row4) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field1() {
		return BattleItemForUser.BATTLE_ITEM_FOR_USER.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field2() {
		return BattleItemForUser.BATTLE_ITEM_FOR_USER.USER_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field3() {
		return BattleItemForUser.BATTLE_ITEM_FOR_USER.BATTLE_ITEM_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field4() {
		return BattleItemForUser.BATTLE_ITEM_FOR_USER.QUANTITY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value1() {
		return getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value2() {
		return getUserId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value3() {
		return getBattleItemId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value4() {
		return getQuantity();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BattleItemForUserRecord value1(String value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BattleItemForUserRecord value2(String value) {
		setUserId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BattleItemForUserRecord value3(Integer value) {
		setBattleItemId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BattleItemForUserRecord value4(Integer value) {
		setQuantity(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BattleItemForUserRecord values(String value1, String value2, Integer value3, Integer value4) {
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IBattleItemForUser from) {
		setId(from.getId());
		setUserId(from.getUserId());
		setBattleItemId(from.getBattleItemId());
		setQuantity(from.getQuantity());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IBattleItemForUser> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached BattleItemForUserRecord
	 */
	public BattleItemForUserRecord() {
		super(BattleItemForUser.BATTLE_ITEM_FOR_USER);
	}

	/**
	 * Create a detached, initialised BattleItemForUserRecord
	 */
	public BattleItemForUserRecord(String id, String userId, Integer battleItemId, Integer quantity) {
		super(BattleItemForUser.BATTLE_ITEM_FOR_USER);

		setValue(0, id);
		setValue(1, userId);
		setValue(2, battleItemId);
		setValue(3, quantity);
	}
}