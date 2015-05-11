/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.ClanMemberTeamDonation;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IClanMemberTeamDonation;

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
import org.jooq.Record7;
import org.jooq.Row;
import org.jooq.Row7;
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
@Table(name = "clan_member_team_donation", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"id", "user_id", "clan_id"}),
	@UniqueConstraint(columnNames = {"clan_id", "user_id"})
})
public class ClanMemberTeamDonationRecord extends UpdatableRecordImpl<ClanMemberTeamDonationRecord> implements Record7<String, String, String, Integer, Boolean, String, Timestamp>, IClanMemberTeamDonation {

	private static final long serialVersionUID = -1844062146;

	/**
	 * Setter for <code>mobsters.clan_member_team_donation.id</code>.
	 */
	@Override
	public ClanMemberTeamDonationRecord setId(String value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_member_team_donation.id</code>.
	 */
	@Column(name = "id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getId() {
		return (String) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.clan_member_team_donation.user_id</code>.
	 */
	@Override
	public ClanMemberTeamDonationRecord setUserId(String value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_member_team_donation.user_id</code>.
	 */
	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getUserId() {
		return (String) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.clan_member_team_donation.clan_id</code>.
	 */
	@Override
	public ClanMemberTeamDonationRecord setClanId(String value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_member_team_donation.clan_id</code>.
	 */
	@Column(name = "clan_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getClanId() {
		return (String) getValue(2);
	}

	/**
	 * Setter for <code>mobsters.clan_member_team_donation.power_limit</code>.
	 */
	@Override
	public ClanMemberTeamDonationRecord setPowerLimit(Integer value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_member_team_donation.power_limit</code>.
	 */
	@Column(name = "power_limit", precision = 10)
	@Override
	public Integer getPowerLimit() {
		return (Integer) getValue(3);
	}

	/**
	 * Setter for <code>mobsters.clan_member_team_donation.fulfilled</code>.
	 */
	@Override
	public ClanMemberTeamDonationRecord setFulfilled(Boolean value) {
		setValue(4, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_member_team_donation.fulfilled</code>.
	 */
	@Column(name = "fulfilled", precision = 1)
	@Override
	public Boolean getFulfilled() {
		return (Boolean) getValue(4);
	}

	/**
	 * Setter for <code>mobsters.clan_member_team_donation.msg</code>.
	 */
	@Override
	public ClanMemberTeamDonationRecord setMsg(String value) {
		setValue(5, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_member_team_donation.msg</code>.
	 */
	@Column(name = "msg", length = 105)
	@Size(max = 105)
	@Override
	public String getMsg() {
		return (String) getValue(5);
	}

	/**
	 * Setter for <code>mobsters.clan_member_team_donation.time_of_solicitation</code>.
	 */
	@Override
	public ClanMemberTeamDonationRecord setTimeOfSolicitation(Timestamp value) {
		setValue(6, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_member_team_donation.time_of_solicitation</code>.
	 */
	@Column(name = "time_of_solicitation")
	@Override
	public Timestamp getTimeOfSolicitation() {
		return (Timestamp) getValue(6);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Record3<String, String, String> key() {
		return (Record3) super.key();
	}

	// -------------------------------------------------------------------------
	// Record7 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row7<String, String, String, Integer, Boolean, String, Timestamp> fieldsRow() {
		return (Row7) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row7<String, String, String, Integer, Boolean, String, Timestamp> valuesRow() {
		return (Row7) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field1() {
		return ClanMemberTeamDonation.CLAN_MEMBER_TEAM_DONATION.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field2() {
		return ClanMemberTeamDonation.CLAN_MEMBER_TEAM_DONATION.USER_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field3() {
		return ClanMemberTeamDonation.CLAN_MEMBER_TEAM_DONATION.CLAN_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field4() {
		return ClanMemberTeamDonation.CLAN_MEMBER_TEAM_DONATION.POWER_LIMIT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Boolean> field5() {
		return ClanMemberTeamDonation.CLAN_MEMBER_TEAM_DONATION.FULFILLED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field6() {
		return ClanMemberTeamDonation.CLAN_MEMBER_TEAM_DONATION.MSG;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Timestamp> field7() {
		return ClanMemberTeamDonation.CLAN_MEMBER_TEAM_DONATION.TIME_OF_SOLICITATION;
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
	public String value3() {
		return getClanId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value4() {
		return getPowerLimit();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean value5() {
		return getFulfilled();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value6() {
		return getMsg();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timestamp value7() {
		return getTimeOfSolicitation();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanMemberTeamDonationRecord value1(String value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanMemberTeamDonationRecord value2(String value) {
		setUserId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanMemberTeamDonationRecord value3(String value) {
		setClanId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanMemberTeamDonationRecord value4(Integer value) {
		setPowerLimit(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanMemberTeamDonationRecord value5(Boolean value) {
		setFulfilled(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanMemberTeamDonationRecord value6(String value) {
		setMsg(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanMemberTeamDonationRecord value7(Timestamp value) {
		setTimeOfSolicitation(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanMemberTeamDonationRecord values(String value1, String value2, String value3, Integer value4, Boolean value5, String value6, Timestamp value7) {
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
		value5(value5);
		value6(value6);
		value7(value7);
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IClanMemberTeamDonation from) {
		setId(from.getId());
		setUserId(from.getUserId());
		setClanId(from.getClanId());
		setPowerLimit(from.getPowerLimit());
		setFulfilled(from.getFulfilled());
		setMsg(from.getMsg());
		setTimeOfSolicitation(from.getTimeOfSolicitation());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IClanMemberTeamDonation> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached ClanMemberTeamDonationRecord
	 */
	public ClanMemberTeamDonationRecord() {
		super(ClanMemberTeamDonation.CLAN_MEMBER_TEAM_DONATION);
	}

	/**
	 * Create a detached, initialised ClanMemberTeamDonationRecord
	 */
	public ClanMemberTeamDonationRecord(String id, String userId, String clanId, Integer powerLimit, Boolean fulfilled, String msg, Timestamp timeOfSolicitation) {
		super(ClanMemberTeamDonation.CLAN_MEMBER_TEAM_DONATION);

		setValue(0, id);
		setValue(1, userId);
		setValue(2, clanId);
		setValue(3, powerLimit);
		setValue(4, fulfilled);
		setValue(5, msg);
		setValue(6, timeOfSolicitation);
	}
}