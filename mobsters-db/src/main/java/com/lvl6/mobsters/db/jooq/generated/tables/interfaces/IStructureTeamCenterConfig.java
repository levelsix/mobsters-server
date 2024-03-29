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
@Table(name = "structure_team_center_config", schema = "mobsters")
public interface IStructureTeamCenterConfig extends Serializable {

	/**
	 * Setter for <code>mobsters.structure_team_center_config.struct_id</code>.
	 */
	public IStructureTeamCenterConfig setStructId(Integer value);

	/**
	 * Getter for <code>mobsters.structure_team_center_config.struct_id</code>.
	 */
	@Id
	@Column(name = "struct_id", unique = true, nullable = false, precision = 10)
	@NotNull
	public Integer getStructId();

	/**
	 * Setter for <code>mobsters.structure_team_center_config.team_cost_limit</code>.
	 */
	public IStructureTeamCenterConfig setTeamCostLimit(Integer value);

	/**
	 * Getter for <code>mobsters.structure_team_center_config.team_cost_limit</code>.
	 */
	@Column(name = "team_cost_limit", precision = 10)
	public Integer getTeamCostLimit();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface IStructureTeamCenterConfig
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IStructureTeamCenterConfig from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface IStructureTeamCenterConfig
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IStructureTeamCenterConfig> E into(E into);
}
