/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.StructureClanHouseConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IStructureClanHouseConfig;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Row;
import org.jooq.Row3;
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
@Table(name = "structure_clan_house_config", schema = "mobsters")
public class StructureClanHouseConfigRecord extends UpdatableRecordImpl<StructureClanHouseConfigRecord> implements Record3<Integer, Integer, Integer>, IStructureClanHouseConfig {

	private static final long serialVersionUID = 269793167;

	/**
	 * Setter for <code>mobsters.structure_clan_house_config.struct_id</code>.
	 */
	@Override
	public StructureClanHouseConfigRecord setStructId(Integer value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_clan_house_config.struct_id</code>.
	 */
	@Id
	@Column(name = "struct_id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getStructId() {
		return (Integer) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.structure_clan_house_config.max_helpers_per_solicitation</code>.
	 */
	@Override
	public StructureClanHouseConfigRecord setMaxHelpersPerSolicitation(Integer value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_clan_house_config.max_helpers_per_solicitation</code>.
	 */
	@Column(name = "max_helpers_per_solicitation", precision = 10)
	@Override
	public Integer getMaxHelpersPerSolicitation() {
		return (Integer) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.structure_clan_house_config.team_donation_power_limit</code>.
	 */
	@Override
	public StructureClanHouseConfigRecord setTeamDonationPowerLimit(Integer value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_clan_house_config.team_donation_power_limit</code>.
	 */
	@Column(name = "team_donation_power_limit", precision = 10)
	@Override
	public Integer getTeamDonationPowerLimit() {
		return (Integer) getValue(2);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Record1<Integer> key() {
		return (Record1) super.key();
	}

	// -------------------------------------------------------------------------
	// Record3 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row3<Integer, Integer, Integer> fieldsRow() {
		return (Row3) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row3<Integer, Integer, Integer> valuesRow() {
		return (Row3) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field1() {
		return StructureClanHouseConfig.STRUCTURE_CLAN_HOUSE_CONFIG.STRUCT_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field2() {
		return StructureClanHouseConfig.STRUCTURE_CLAN_HOUSE_CONFIG.MAX_HELPERS_PER_SOLICITATION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field3() {
		return StructureClanHouseConfig.STRUCTURE_CLAN_HOUSE_CONFIG.TEAM_DONATION_POWER_LIMIT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value1() {
		return getStructId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value2() {
		return getMaxHelpersPerSolicitation();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value3() {
		return getTeamDonationPowerLimit();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureClanHouseConfigRecord value1(Integer value) {
		setStructId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureClanHouseConfigRecord value2(Integer value) {
		setMaxHelpersPerSolicitation(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureClanHouseConfigRecord value3(Integer value) {
		setTeamDonationPowerLimit(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureClanHouseConfigRecord values(Integer value1, Integer value2, Integer value3) {
		value1(value1);
		value2(value2);
		value3(value3);
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IStructureClanHouseConfig from) {
		setStructId(from.getStructId());
		setMaxHelpersPerSolicitation(from.getMaxHelpersPerSolicitation());
		setTeamDonationPowerLimit(from.getTeamDonationPowerLimit());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IStructureClanHouseConfig> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached StructureClanHouseConfigRecord
	 */
	public StructureClanHouseConfigRecord() {
		super(StructureClanHouseConfig.STRUCTURE_CLAN_HOUSE_CONFIG);
	}

	/**
	 * Create a detached, initialised StructureClanHouseConfigRecord
	 */
	public StructureClanHouseConfigRecord(Integer structId, Integer maxHelpersPerSolicitation, Integer teamDonationPowerLimit) {
		super(StructureClanHouseConfig.STRUCTURE_CLAN_HOUSE_CONFIG);

		setValue(0, structId);
		setValue(1, maxHelpersPerSolicitation);
		setValue(2, teamDonationPowerLimit);
	}
}