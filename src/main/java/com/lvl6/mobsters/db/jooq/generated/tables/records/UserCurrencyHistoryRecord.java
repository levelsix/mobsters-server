/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.UserCurrencyHistory;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IUserCurrencyHistory;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record9;
import org.jooq.Row;
import org.jooq.Row9;
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
@Table(name = "user_currency_history", schema = "mobsters")
public class UserCurrencyHistoryRecord extends UpdatableRecordImpl<UserCurrencyHistoryRecord> implements Record9<String, String, Timestamp, String, Integer, Integer, Integer, String, String>, IUserCurrencyHistory {

	private static final long serialVersionUID = 737866098;

	/**
	 * Setter for <code>mobsters.user_currency_history.id</code>.
	 */
	@Override
	public UserCurrencyHistoryRecord setId(String value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user_currency_history.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getId() {
		return (String) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.user_currency_history.user_id</code>.
	 */
	@Override
	public UserCurrencyHistoryRecord setUserId(String value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user_currency_history.user_id</code>.
	 */
	@Column(name = "user_id", length = 36)
	@Size(max = 36)
	@Override
	public String getUserId() {
		return (String) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.user_currency_history.date</code>.
	 */
	@Override
	public UserCurrencyHistoryRecord setDate(Timestamp value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user_currency_history.date</code>.
	 */
	@Column(name = "date")
	@Override
	public Timestamp getDate() {
		return (Timestamp) getValue(2);
	}

	/**
	 * Setter for <code>mobsters.user_currency_history.resource_type</code>.
	 */
	@Override
	public UserCurrencyHistoryRecord setResourceType(String value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user_currency_history.resource_type</code>.
	 */
	@Column(name = "resource_type", length = 45)
	@Size(max = 45)
	@Override
	public String getResourceType() {
		return (String) getValue(3);
	}

	/**
	 * Setter for <code>mobsters.user_currency_history.currency_change</code>.
	 */
	@Override
	public UserCurrencyHistoryRecord setCurrencyChange(Integer value) {
		setValue(4, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user_currency_history.currency_change</code>.
	 */
	@Column(name = "currency_change", precision = 10)
	@Override
	public Integer getCurrencyChange() {
		return (Integer) getValue(4);
	}

	/**
	 * Setter for <code>mobsters.user_currency_history.currency_before_change</code>.
	 */
	@Override
	public UserCurrencyHistoryRecord setCurrencyBeforeChange(Integer value) {
		setValue(5, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user_currency_history.currency_before_change</code>.
	 */
	@Column(name = "currency_before_change", precision = 10)
	@Override
	public Integer getCurrencyBeforeChange() {
		return (Integer) getValue(5);
	}

	/**
	 * Setter for <code>mobsters.user_currency_history.currency_after_change</code>.
	 */
	@Override
	public UserCurrencyHistoryRecord setCurrencyAfterChange(Integer value) {
		setValue(6, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user_currency_history.currency_after_change</code>.
	 */
	@Column(name = "currency_after_change", precision = 10)
	@Override
	public Integer getCurrencyAfterChange() {
		return (Integer) getValue(6);
	}

	/**
	 * Setter for <code>mobsters.user_currency_history.reason_for_change</code>.
	 */
	@Override
	public UserCurrencyHistoryRecord setReasonForChange(String value) {
		setValue(7, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user_currency_history.reason_for_change</code>.
	 */
	@Column(name = "reason_for_change", length = 50)
	@Size(max = 50)
	@Override
	public String getReasonForChange() {
		return (String) getValue(7);
	}

	/**
	 * Setter for <code>mobsters.user_currency_history.details</code>.
	 */
	@Override
	public UserCurrencyHistoryRecord setDetails(String value) {
		setValue(8, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user_currency_history.details</code>.
	 */
	@Column(name = "details", length = 65535)
	@Size(max = 65535)
	@Override
	public String getDetails() {
		return (String) getValue(8);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Record1<String> key() {
		return (Record1) super.key();
	}

	// -------------------------------------------------------------------------
	// Record9 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row9<String, String, Timestamp, String, Integer, Integer, Integer, String, String> fieldsRow() {
		return (Row9) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row9<String, String, Timestamp, String, Integer, Integer, Integer, String, String> valuesRow() {
		return (Row9) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field1() {
		return UserCurrencyHistory.USER_CURRENCY_HISTORY.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field2() {
		return UserCurrencyHistory.USER_CURRENCY_HISTORY.USER_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Timestamp> field3() {
		return UserCurrencyHistory.USER_CURRENCY_HISTORY.DATE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field4() {
		return UserCurrencyHistory.USER_CURRENCY_HISTORY.RESOURCE_TYPE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field5() {
		return UserCurrencyHistory.USER_CURRENCY_HISTORY.CURRENCY_CHANGE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field6() {
		return UserCurrencyHistory.USER_CURRENCY_HISTORY.CURRENCY_BEFORE_CHANGE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field7() {
		return UserCurrencyHistory.USER_CURRENCY_HISTORY.CURRENCY_AFTER_CHANGE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field8() {
		return UserCurrencyHistory.USER_CURRENCY_HISTORY.REASON_FOR_CHANGE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field9() {
		return UserCurrencyHistory.USER_CURRENCY_HISTORY.DETAILS;
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
	public Timestamp value3() {
		return getDate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value4() {
		return getResourceType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value5() {
		return getCurrencyChange();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value6() {
		return getCurrencyBeforeChange();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value7() {
		return getCurrencyAfterChange();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value8() {
		return getReasonForChange();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value9() {
		return getDetails();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserCurrencyHistoryRecord value1(String value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserCurrencyHistoryRecord value2(String value) {
		setUserId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserCurrencyHistoryRecord value3(Timestamp value) {
		setDate(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserCurrencyHistoryRecord value4(String value) {
		setResourceType(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserCurrencyHistoryRecord value5(Integer value) {
		setCurrencyChange(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserCurrencyHistoryRecord value6(Integer value) {
		setCurrencyBeforeChange(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserCurrencyHistoryRecord value7(Integer value) {
		setCurrencyAfterChange(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserCurrencyHistoryRecord value8(String value) {
		setReasonForChange(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserCurrencyHistoryRecord value9(String value) {
		setDetails(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserCurrencyHistoryRecord values(String value1, String value2, Timestamp value3, String value4, Integer value5, Integer value6, Integer value7, String value8, String value9) {
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
		value5(value5);
		value6(value6);
		value7(value7);
		value8(value8);
		value9(value9);
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IUserCurrencyHistory from) {
		setId(from.getId());
		setUserId(from.getUserId());
		setDate(from.getDate());
		setResourceType(from.getResourceType());
		setCurrencyChange(from.getCurrencyChange());
		setCurrencyBeforeChange(from.getCurrencyBeforeChange());
		setCurrencyAfterChange(from.getCurrencyAfterChange());
		setReasonForChange(from.getReasonForChange());
		setDetails(from.getDetails());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IUserCurrencyHistory> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached UserCurrencyHistoryRecord
	 */
	public UserCurrencyHistoryRecord() {
		super(UserCurrencyHistory.USER_CURRENCY_HISTORY);
	}

	/**
	 * Create a detached, initialised UserCurrencyHistoryRecord
	 */
	public UserCurrencyHistoryRecord(String id, String userId, Timestamp date, String resourceType, Integer currencyChange, Integer currencyBeforeChange, Integer currencyAfterChange, String reasonForChange, String details) {
		super(UserCurrencyHistory.USER_CURRENCY_HISTORY);

		setValue(0, id);
		setValue(1, userId);
		setValue(2, date);
		setValue(3, resourceType);
		setValue(4, currencyChange);
		setValue(5, currencyBeforeChange);
		setValue(6, currencyAfterChange);
		setValue(7, reasonForChange);
		setValue(8, details);
	}
}