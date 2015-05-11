/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.LoginHistory;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ILoginHistory;

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
@Table(name = "login_history", schema = "mobsters")
public class LoginHistoryRecord extends UpdatableRecordImpl<LoginHistoryRecord> implements Record5<String, String, String, Timestamp, Byte>, ILoginHistory {

	private static final long serialVersionUID = -2030624541;

	/**
	 * Setter for <code>mobsters.login_history.id</code>.
	 */
	@Override
	public LoginHistoryRecord setId(String value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.login_history.id</code>.
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
	 * Setter for <code>mobsters.login_history.udid</code>.
	 */
	@Override
	public LoginHistoryRecord setUdid(String value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.login_history.udid</code>.
	 */
	@Column(name = "udid", length = 250)
	@Size(max = 250)
	@Override
	public String getUdid() {
		return (String) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.login_history.user_id</code>. this is not set if it is a new user
	 */
	@Override
	public LoginHistoryRecord setUserId(String value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.login_history.user_id</code>. this is not set if it is a new user
	 */
	@Column(name = "user_id", length = 36)
	@Size(max = 36)
	@Override
	public String getUserId() {
		return (String) getValue(2);
	}

	/**
	 * Setter for <code>mobsters.login_history.date</code>.
	 */
	@Override
	public LoginHistoryRecord setDate(Timestamp value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.login_history.date</code>.
	 */
	@Column(name = "date")
	@Override
	public Timestamp getDate() {
		return (Timestamp) getValue(3);
	}

	/**
	 * Setter for <code>mobsters.login_history.is_login</code>. 1 or true if user logged in. 0 or false if user logged out
	 */
	@Override
	public LoginHistoryRecord setIsLogin(Byte value) {
		setValue(4, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.login_history.is_login</code>. 1 or true if user logged in. 0 or false if user logged out
	 */
	@Column(name = "is_login", precision = 3)
	@Override
	public Byte getIsLogin() {
		return (Byte) getValue(4);
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
	// Record5 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row5<String, String, String, Timestamp, Byte> fieldsRow() {
		return (Row5) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row5<String, String, String, Timestamp, Byte> valuesRow() {
		return (Row5) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field1() {
		return LoginHistory.LOGIN_HISTORY.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field2() {
		return LoginHistory.LOGIN_HISTORY.UDID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field3() {
		return LoginHistory.LOGIN_HISTORY.USER_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Timestamp> field4() {
		return LoginHistory.LOGIN_HISTORY.DATE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Byte> field5() {
		return LoginHistory.LOGIN_HISTORY.IS_LOGIN;
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
		return getUdid();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value3() {
		return getUserId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timestamp value4() {
		return getDate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Byte value5() {
		return getIsLogin();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LoginHistoryRecord value1(String value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LoginHistoryRecord value2(String value) {
		setUdid(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LoginHistoryRecord value3(String value) {
		setUserId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LoginHistoryRecord value4(Timestamp value) {
		setDate(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LoginHistoryRecord value5(Byte value) {
		setIsLogin(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LoginHistoryRecord values(String value1, String value2, String value3, Timestamp value4, Byte value5) {
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
	public void from(ILoginHistory from) {
		setId(from.getId());
		setUdid(from.getUdid());
		setUserId(from.getUserId());
		setDate(from.getDate());
		setIsLogin(from.getIsLogin());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends ILoginHistory> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached LoginHistoryRecord
	 */
	public LoginHistoryRecord() {
		super(LoginHistory.LOGIN_HISTORY);
	}

	/**
	 * Create a detached, initialised LoginHistoryRecord
	 */
	public LoginHistoryRecord(String id, String udid, String userId, Timestamp date, Byte isLogin) {
		super(LoginHistory.LOGIN_HISTORY);

		setValue(0, id);
		setValue(1, udid);
		setValue(2, userId);
		setValue(3, date);
		setValue(4, isLogin);
	}
}