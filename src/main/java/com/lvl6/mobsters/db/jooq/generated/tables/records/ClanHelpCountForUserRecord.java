/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.ClanHelpCountForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IClanHelpCountForUser;

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
@Table(name = "clan_help_count_for_user", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"user_id", "clan_id", "date"})
})
public class ClanHelpCountForUserRecord extends UpdatableRecordImpl<ClanHelpCountForUserRecord> implements Record5<String, String, Timestamp, Integer, Integer>, IClanHelpCountForUser {

	private static final long serialVersionUID = -1773581578;

	/**
	 * Setter for <code>mobsters.clan_help_count_for_user.user_id</code>.
	 */
	@Override
	public ClanHelpCountForUserRecord setUserId(String value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_help_count_for_user.user_id</code>.
	 */
	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getUserId() {
		return (String) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.clan_help_count_for_user.clan_id</code>.
	 */
	@Override
	public ClanHelpCountForUserRecord setClanId(String value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_help_count_for_user.clan_id</code>.
	 */
	@Column(name = "clan_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getClanId() {
		return (String) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.clan_help_count_for_user.date</code>. year, month, day
	 */
	@Override
	public ClanHelpCountForUserRecord setDate(Timestamp value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_help_count_for_user.date</code>. year, month, day
	 */
	@Column(name = "date", nullable = false)
	@NotNull
	@Override
	public Timestamp getDate() {
		return (Timestamp) getValue(2);
	}

	/**
	 * Setter for <code>mobsters.clan_help_count_for_user.solicited</code>.
	 */
	@Override
	public ClanHelpCountForUserRecord setSolicited(Integer value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_help_count_for_user.solicited</code>.
	 */
	@Column(name = "solicited", precision = 10)
	@Override
	public Integer getSolicited() {
		return (Integer) getValue(3);
	}

	/**
	 * Setter for <code>mobsters.clan_help_count_for_user.given</code>.
	 */
	@Override
	public ClanHelpCountForUserRecord setGiven(Integer value) {
		setValue(4, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_help_count_for_user.given</code>.
	 */
	@Column(name = "given", precision = 10)
	@Override
	public Integer getGiven() {
		return (Integer) getValue(4);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Record3<String, String, Timestamp> key() {
		return (Record3) super.key();
	}

	// -------------------------------------------------------------------------
	// Record5 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row5<String, String, Timestamp, Integer, Integer> fieldsRow() {
		return (Row5) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row5<String, String, Timestamp, Integer, Integer> valuesRow() {
		return (Row5) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field1() {
		return ClanHelpCountForUser.CLAN_HELP_COUNT_FOR_USER.USER_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field2() {
		return ClanHelpCountForUser.CLAN_HELP_COUNT_FOR_USER.CLAN_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Timestamp> field3() {
		return ClanHelpCountForUser.CLAN_HELP_COUNT_FOR_USER.DATE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field4() {
		return ClanHelpCountForUser.CLAN_HELP_COUNT_FOR_USER.SOLICITED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field5() {
		return ClanHelpCountForUser.CLAN_HELP_COUNT_FOR_USER.GIVEN;
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
	public String value2() {
		return getClanId();
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
	public Integer value4() {
		return getSolicited();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value5() {
		return getGiven();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanHelpCountForUserRecord value1(String value) {
		setUserId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanHelpCountForUserRecord value2(String value) {
		setClanId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanHelpCountForUserRecord value3(Timestamp value) {
		setDate(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanHelpCountForUserRecord value4(Integer value) {
		setSolicited(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanHelpCountForUserRecord value5(Integer value) {
		setGiven(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanHelpCountForUserRecord values(String value1, String value2, Timestamp value3, Integer value4, Integer value5) {
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
	public void from(IClanHelpCountForUser from) {
		setUserId(from.getUserId());
		setClanId(from.getClanId());
		setDate(from.getDate());
		setSolicited(from.getSolicited());
		setGiven(from.getGiven());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IClanHelpCountForUser> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached ClanHelpCountForUserRecord
	 */
	public ClanHelpCountForUserRecord() {
		super(ClanHelpCountForUser.CLAN_HELP_COUNT_FOR_USER);
	}

	/**
	 * Create a detached, initialised ClanHelpCountForUserRecord
	 */
	public ClanHelpCountForUserRecord(String userId, String clanId, Timestamp date, Integer solicited, Integer given) {
		super(ClanHelpCountForUser.CLAN_HELP_COUNT_FOR_USER);

		setValue(0, userId);
		setValue(1, clanId);
		setValue(2, date);
		setValue(3, solicited);
		setValue(4, given);
	}
}