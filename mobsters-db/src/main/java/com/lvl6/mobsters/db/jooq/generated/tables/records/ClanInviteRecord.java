/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.ClanInvite;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IClanInvite;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
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
@Table(name = "clan_invite", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"user_id", "inviter_id"})
})
public class ClanInviteRecord extends UpdatableRecordImpl<ClanInviteRecord> implements Record5<String, String, String, String, Timestamp>, IClanInvite {

	private static final long serialVersionUID = -750812939;

	/**
	 * Setter for <code>mobsters.clan_invite.id</code>.
	 */
	@Override
	public ClanInviteRecord setId(String value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_invite.id</code>.
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
	 * Setter for <code>mobsters.clan_invite.user_id</code>. This and inviter_id form a unique composite key
	 */
	@Override
	public ClanInviteRecord setUserId(String value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_invite.user_id</code>. This and inviter_id form a unique composite key
	 */
	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getUserId() {
		return (String) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.clan_invite.inviter_id</code>. This and user_id form a unique key composite key
	 */
	@Override
	public ClanInviteRecord setInviterId(String value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_invite.inviter_id</code>. This and user_id form a unique key composite key
	 */
	@Column(name = "inviter_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getInviterId() {
		return (String) getValue(2);
	}

	/**
	 * Setter for <code>mobsters.clan_invite.clan_id</code>.
	 */
	@Override
	public ClanInviteRecord setClanId(String value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_invite.clan_id</code>.
	 */
	@Column(name = "clan_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getClanId() {
		return (String) getValue(3);
	}

	/**
	 * Setter for <code>mobsters.clan_invite.time_of_invite</code>.
	 */
	@Override
	public ClanInviteRecord setTimeOfInvite(Timestamp value) {
		setValue(4, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_invite.time_of_invite</code>.
	 */
	@Column(name = "time_of_invite", nullable = false)
	@NotNull
	@Override
	public Timestamp getTimeOfInvite() {
		return (Timestamp) getValue(4);
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
	public Row5<String, String, String, String, Timestamp> fieldsRow() {
		return (Row5) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row5<String, String, String, String, Timestamp> valuesRow() {
		return (Row5) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field1() {
		return ClanInvite.CLAN_INVITE.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field2() {
		return ClanInvite.CLAN_INVITE.USER_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field3() {
		return ClanInvite.CLAN_INVITE.INVITER_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field4() {
		return ClanInvite.CLAN_INVITE.CLAN_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Timestamp> field5() {
		return ClanInvite.CLAN_INVITE.TIME_OF_INVITE;
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
		return getInviterId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value4() {
		return getClanId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timestamp value5() {
		return getTimeOfInvite();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanInviteRecord value1(String value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanInviteRecord value2(String value) {
		setUserId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanInviteRecord value3(String value) {
		setInviterId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanInviteRecord value4(String value) {
		setClanId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanInviteRecord value5(Timestamp value) {
		setTimeOfInvite(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanInviteRecord values(String value1, String value2, String value3, String value4, Timestamp value5) {
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
	public void from(IClanInvite from) {
		setId(from.getId());
		setUserId(from.getUserId());
		setInviterId(from.getInviterId());
		setClanId(from.getClanId());
		setTimeOfInvite(from.getTimeOfInvite());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IClanInvite> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached ClanInviteRecord
	 */
	public ClanInviteRecord() {
		super(ClanInvite.CLAN_INVITE);
	}

	/**
	 * Create a detached, initialised ClanInviteRecord
	 */
	public ClanInviteRecord(String id, String userId, String inviterId, String clanId, Timestamp timeOfInvite) {
		super(ClanInvite.CLAN_INVITE);

		setValue(0, id);
		setValue(1, userId);
		setValue(2, inviterId);
		setValue(3, clanId);
		setValue(4, timeOfInvite);
	}
}
