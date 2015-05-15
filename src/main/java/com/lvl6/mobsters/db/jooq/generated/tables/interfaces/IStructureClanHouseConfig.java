/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.interfaces;


import java.io.Serializable;

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
public interface IStructureClanHouseConfig extends Serializable {

	/**
	 * Setter for <code>mobsters.structure_clan_house_config.struct_id</code>.
	 */
	public IStructureClanHouseConfig setStructId(Integer value);

	/**
	 * Getter for <code>mobsters.structure_clan_house_config.struct_id</code>.
	 */
	@Id
	@Column(name = "struct_id", unique = true, nullable = false, precision = 10)
	@NotNull
	public Integer getStructId();

	/**
	 * Setter for <code>mobsters.structure_clan_house_config.max_helpers_per_solicitation</code>.
	 */
	public IStructureClanHouseConfig setMaxHelpersPerSolicitation(Integer value);

	/**
	 * Getter for <code>mobsters.structure_clan_house_config.max_helpers_per_solicitation</code>.
	 */
	@Column(name = "max_helpers_per_solicitation", precision = 10)
	public Integer getMaxHelpersPerSolicitation();

	/**
	 * Setter for <code>mobsters.structure_clan_house_config.team_donation_power_limit</code>.
	 */
	public IStructureClanHouseConfig setTeamDonationPowerLimit(Integer value);

	/**
	 * Getter for <code>mobsters.structure_clan_house_config.team_donation_power_limit</code>.
	 */
	@Column(name = "team_donation_power_limit", precision = 10)
	public Integer getTeamDonationPowerLimit();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface IStructureClanHouseConfig
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IStructureClanHouseConfig from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface IStructureClanHouseConfig
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IStructureClanHouseConfig> E into(E into);
}
