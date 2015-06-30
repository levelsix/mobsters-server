/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.ClanMembersCount;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IClanMembersCount;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Row;
import org.jooq.Row2;
import org.jooq.impl.TableRecordImpl;


/**
 * VIEW
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
@Table(name = "clan_members_count", schema = "mobsters")
public class ClanMembersCountRecord extends TableRecordImpl<ClanMembersCountRecord> implements Record2<String, Long>, IClanMembersCount {

	private static final long serialVersionUID = 917576925;

	/**
	 * Setter for <code>mobsters.clan_members_count.clan_id</code>.
	 */
	@Override
	public ClanMembersCountRecord setClanId(String value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_members_count.clan_id</code>.
	 */
	@Column(name = "clan_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getClanId() {
		return (String) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.clan_members_count.members</code>.
	 */
	@Override
	public ClanMembersCountRecord setMembers(Long value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_members_count.members</code>.
	 */
	@Column(name = "members", nullable = false, precision = 19)
	@NotNull
	@Override
	public Long getMembers() {
		return (Long) getValue(1);
	}

	// -------------------------------------------------------------------------
	// Record2 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row2<String, Long> fieldsRow() {
		return (Row2) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row2<String, Long> valuesRow() {
		return (Row2) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field1() {
		return ClanMembersCount.CLAN_MEMBERS_COUNT.CLAN_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Long> field2() {
		return ClanMembersCount.CLAN_MEMBERS_COUNT.MEMBERS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value1() {
		return getClanId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long value2() {
		return getMembers();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanMembersCountRecord value1(String value) {
		setClanId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanMembersCountRecord value2(Long value) {
		setMembers(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanMembersCountRecord values(String value1, Long value2) {
		value1(value1);
		value2(value2);
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IClanMembersCount from) {
		setClanId(from.getClanId());
		setMembers(from.getMembers());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IClanMembersCount> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached ClanMembersCountRecord
	 */
	public ClanMembersCountRecord() {
		super(ClanMembersCount.CLAN_MEMBERS_COUNT);
	}

	/**
	 * Create a detached, initialised ClanMembersCountRecord
	 */
	public ClanMembersCountRecord(String clanId, Long members) {
		super(ClanMembersCount.CLAN_MEMBERS_COUNT);

		setValue(0, clanId);
		setValue(1, members);
	}
}
