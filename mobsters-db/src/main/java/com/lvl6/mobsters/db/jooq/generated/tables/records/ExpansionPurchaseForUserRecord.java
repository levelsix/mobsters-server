/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.ExpansionPurchaseForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IExpansionPurchaseForUser;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record3;
import org.jooq.Record5;
import org.jooq.Row;
import org.jooq.Row5;
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
@Table(name = "expansion_purchase_for_user", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"user_id", "x_position", "y_position"})
})
public class ExpansionPurchaseForUserRecord extends UpdatableRecordImpl<ExpansionPurchaseForUserRecord> implements Record5<String, Integer, Integer, Byte, Timestamp>, IExpansionPurchaseForUser {

	private static final long serialVersionUID = 1093941912;

	/**
	 * Setter for <code>mobsters.expansion_purchase_for_user.user_id</code>.
	 */
	@Override
	public ExpansionPurchaseForUserRecord setUserId(String value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.expansion_purchase_for_user.user_id</code>.
	 */
	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getUserId() {
		return (String) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.expansion_purchase_for_user.x_position</code>. In relation to center square (the origin 0,0)
	 */
	@Override
	public ExpansionPurchaseForUserRecord setXPosition(Integer value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.expansion_purchase_for_user.x_position</code>. In relation to center square (the origin 0,0)
	 */
	@Column(name = "x_position", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getXPosition() {
		return (Integer) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.expansion_purchase_for_user.y_position</code>. In relation to center square (the origin 0,0)
	 */
	@Override
	public ExpansionPurchaseForUserRecord setYPosition(Integer value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.expansion_purchase_for_user.y_position</code>. In relation to center square (the origin 0,0)
	 */
	@Column(name = "y_position", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getYPosition() {
		return (Integer) getValue(2);
	}

	/**
	 * Setter for <code>mobsters.expansion_purchase_for_user.is_expanding</code>.
	 */
	@Override
	public ExpansionPurchaseForUserRecord setIsExpanding(Byte value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.expansion_purchase_for_user.is_expanding</code>.
	 */
	@Column(name = "is_expanding", precision = 3)
	@Override
	public Byte getIsExpanding() {
		return (Byte) getValue(3);
	}

	/**
	 * Setter for <code>mobsters.expansion_purchase_for_user.expand_start_time</code>.
	 */
	@Override
	public ExpansionPurchaseForUserRecord setExpandStartTime(Timestamp value) {
		setValue(4, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.expansion_purchase_for_user.expand_start_time</code>.
	 */
	@Column(name = "expand_start_time")
	@Override
	public Timestamp getExpandStartTime() {
		return (Timestamp) getValue(4);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Record3<String, Integer, Integer> key() {
		return (Record3) super.key();
	}

	// -------------------------------------------------------------------------
	// Record5 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row5<String, Integer, Integer, Byte, Timestamp> fieldsRow() {
		return (Row5) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row5<String, Integer, Integer, Byte, Timestamp> valuesRow() {
		return (Row5) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field1() {
		return ExpansionPurchaseForUser.EXPANSION_PURCHASE_FOR_USER.USER_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field2() {
		return ExpansionPurchaseForUser.EXPANSION_PURCHASE_FOR_USER.X_POSITION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field3() {
		return ExpansionPurchaseForUser.EXPANSION_PURCHASE_FOR_USER.Y_POSITION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Byte> field4() {
		return ExpansionPurchaseForUser.EXPANSION_PURCHASE_FOR_USER.IS_EXPANDING;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Timestamp> field5() {
		return ExpansionPurchaseForUser.EXPANSION_PURCHASE_FOR_USER.EXPAND_START_TIME;
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
		return getXPosition();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value3() {
		return getYPosition();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Byte value4() {
		return getIsExpanding();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timestamp value5() {
		return getExpandStartTime();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExpansionPurchaseForUserRecord value1(String value) {
		setUserId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExpansionPurchaseForUserRecord value2(Integer value) {
		setXPosition(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExpansionPurchaseForUserRecord value3(Integer value) {
		setYPosition(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExpansionPurchaseForUserRecord value4(Byte value) {
		setIsExpanding(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExpansionPurchaseForUserRecord value5(Timestamp value) {
		setExpandStartTime(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExpansionPurchaseForUserRecord values(String value1, Integer value2, Integer value3, Byte value4, Timestamp value5) {
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
		value5(value5);
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IExpansionPurchaseForUser from) {
		setUserId(from.getUserId());
		setXPosition(from.getXPosition());
		setYPosition(from.getYPosition());
		setIsExpanding(from.getIsExpanding());
		setExpandStartTime(from.getExpandStartTime());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IExpansionPurchaseForUser> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached ExpansionPurchaseForUserRecord
	 */
	public ExpansionPurchaseForUserRecord() {
		super(ExpansionPurchaseForUser.EXPANSION_PURCHASE_FOR_USER);
	}

	/**
	 * Create a detached, initialised ExpansionPurchaseForUserRecord
	 */
	public ExpansionPurchaseForUserRecord(String userId, Integer xPosition, Integer yPosition, Byte isExpanding, Timestamp expandStartTime) {
		super(ExpansionPurchaseForUser.EXPANSION_PURCHASE_FOR_USER);

		setValue(0, userId);
		setValue(1, xPosition);
		setValue(2, yPosition);
		setValue(3, isExpanding);
		setValue(4, expandStartTime);
	}
}
