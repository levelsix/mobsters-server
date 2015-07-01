/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IStructureClanHouseConfig;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;


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
public class StructureClanHouseConfigPojo implements IStructureClanHouseConfig {

	private static final long serialVersionUID = -1205720598;

	private Integer structId;
	private Integer maxHelpersPerSolicitation;
	private Integer teamDonationPowerLimit;

	public StructureClanHouseConfigPojo() {}

	public StructureClanHouseConfigPojo(StructureClanHouseConfigPojo value) {
		this.structId = value.structId;
		this.maxHelpersPerSolicitation = value.maxHelpersPerSolicitation;
		this.teamDonationPowerLimit = value.teamDonationPowerLimit;
	}

	public StructureClanHouseConfigPojo(
		Integer structId,
		Integer maxHelpersPerSolicitation,
		Integer teamDonationPowerLimit
	) {
		this.structId = structId;
		this.maxHelpersPerSolicitation = maxHelpersPerSolicitation;
		this.teamDonationPowerLimit = teamDonationPowerLimit;
	}

	@Id
	@Column(name = "struct_id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getStructId() {
		return this.structId;
	}

	@Override
	public StructureClanHouseConfigPojo setStructId(Integer structId) {
		this.structId = structId;
		return this;
	}

	@Column(name = "max_helpers_per_solicitation", precision = 10)
	@Override
	public Integer getMaxHelpersPerSolicitation() {
		return this.maxHelpersPerSolicitation;
	}

	@Override
	public StructureClanHouseConfigPojo setMaxHelpersPerSolicitation(Integer maxHelpersPerSolicitation) {
		this.maxHelpersPerSolicitation = maxHelpersPerSolicitation;
		return this;
	}

	@Column(name = "team_donation_power_limit", precision = 10)
	@Override
	public Integer getTeamDonationPowerLimit() {
		return this.teamDonationPowerLimit;
	}

	@Override
	public StructureClanHouseConfigPojo setTeamDonationPowerLimit(Integer teamDonationPowerLimit) {
		this.teamDonationPowerLimit = teamDonationPowerLimit;
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


	public String toString() {
		com.lvl6.mobsters.db.jooq.generated.tables.records.StructureClanHouseConfigRecord poop = new com.lvl6.mobsters.db.jooq.generated.tables.records.StructureClanHouseConfigRecord();
		poop.from(this);
		return "StructureClanHouseConfigPojo[" + poop.valuesRow() + "]";
	}
}
