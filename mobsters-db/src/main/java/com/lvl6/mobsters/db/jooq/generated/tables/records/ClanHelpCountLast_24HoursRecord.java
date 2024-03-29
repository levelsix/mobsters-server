/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.ClanHelpCountLast_24Hours;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IClanHelpCountLast_24Hours;

import java.math.BigInteger;

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
@Table(name = "clan_help_count_last_24_hours", schema = "mobsters")
public class ClanHelpCountLast_24HoursRecord extends TableRecordImpl<ClanHelpCountLast_24HoursRecord> implements Record2<String, BigInteger>, IClanHelpCountLast_24Hours {

	private static final long serialVersionUID = -881206060;

	/**
	 * Setter for <code>mobsters.clan_help_count_last_24_hours.clan_id</code>.
	 */
	@Override
	public ClanHelpCountLast_24HoursRecord setClanId(String value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_help_count_last_24_hours.clan_id</code>.
	 */
	@Column(name = "clan_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getClanId() {
		return (String) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.clan_help_count_last_24_hours.helps_given</code>.
	 */
	@Override
	public ClanHelpCountLast_24HoursRecord setHelpsGiven(BigInteger value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_help_count_last_24_hours.helps_given</code>.
	 */
	@Column(name = "helps_given", precision = 32)
	@Override
	public BigInteger getHelpsGiven() {
		return (BigInteger) getValue(1);
	}

	// -------------------------------------------------------------------------
	// Record2 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row2<String, BigInteger> fieldsRow() {
		return (Row2) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row2<String, BigInteger> valuesRow() {
		return (Row2) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field1() {
		return ClanHelpCountLast_24Hours.CLAN_HELP_COUNT_LAST_24_HOURS.CLAN_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<BigInteger> field2() {
		return ClanHelpCountLast_24Hours.CLAN_HELP_COUNT_LAST_24_HOURS.HELPS_GIVEN;
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
	public BigInteger value2() {
		return getHelpsGiven();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanHelpCountLast_24HoursRecord value1(String value) {
		setClanId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanHelpCountLast_24HoursRecord value2(BigInteger value) {
		setHelpsGiven(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanHelpCountLast_24HoursRecord values(String value1, BigInteger value2) {
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
	public void from(IClanHelpCountLast_24Hours from) {
		setClanId(from.getClanId());
		setHelpsGiven(from.getHelpsGiven());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IClanHelpCountLast_24Hours> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached ClanHelpCountLast_24HoursRecord
	 */
	public ClanHelpCountLast_24HoursRecord() {
		super(ClanHelpCountLast_24Hours.CLAN_HELP_COUNT_LAST_24_HOURS);
	}

	/**
	 * Create a detached, initialised ClanHelpCountLast_24HoursRecord
	 */
	public ClanHelpCountLast_24HoursRecord(String clanId, BigInteger helpsGiven) {
		super(ClanHelpCountLast_24Hours.CLAN_HELP_COUNT_LAST_24_HOURS);

		setValue(0, clanId);
		setValue(1, helpsGiven);
	}
}
