/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.interfaces;


import java.io.Serializable;
import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


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
@Table(name = "cepfu_raid_stage_history", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"user_id", "crs_start_time", "clan_id"})
})
public interface ICepfuRaidStageHistory extends Serializable {

	/**
	 * Setter for <code>mobsters.cepfu_raid_stage_history.user_id</code>.
	 */
	public ICepfuRaidStageHistory setUserId(String value);

	/**
	 * Getter for <code>mobsters.cepfu_raid_stage_history.user_id</code>.
	 */
	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	public String getUserId();

	/**
	 * Setter for <code>mobsters.cepfu_raid_stage_history.crs_start_time</code>.
	 */
	public ICepfuRaidStageHistory setCrsStartTime(Timestamp value);

	/**
	 * Getter for <code>mobsters.cepfu_raid_stage_history.crs_start_time</code>.
	 */
	@Column(name = "crs_start_time", nullable = false)
	@NotNull
	public Timestamp getCrsStartTime();

	/**
	 * Setter for <code>mobsters.cepfu_raid_stage_history.clan_id</code>.
	 */
	public ICepfuRaidStageHistory setClanId(String value);

	/**
	 * Getter for <code>mobsters.cepfu_raid_stage_history.clan_id</code>.
	 */
	@Column(name = "clan_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	public String getClanId();

	/**
	 * Setter for <code>mobsters.cepfu_raid_stage_history.clan_event_persistent_id</code>.
	 */
	public ICepfuRaidStageHistory setClanEventPersistentId(Integer value);

	/**
	 * Getter for <code>mobsters.cepfu_raid_stage_history.clan_event_persistent_id</code>.
	 */
	@Column(name = "clan_event_persistent_id", precision = 10)
	public Integer getClanEventPersistentId();

	/**
	 * Setter for <code>mobsters.cepfu_raid_stage_history.cr_id</code>.
	 */
	public ICepfuRaidStageHistory setCrId(Integer value);

	/**
	 * Getter for <code>mobsters.cepfu_raid_stage_history.cr_id</code>.
	 */
	@Column(name = "cr_id", precision = 10)
	public Integer getCrId();

	/**
	 * Setter for <code>mobsters.cepfu_raid_stage_history.crs_id</code>.
	 */
	public ICepfuRaidStageHistory setCrsId(Integer value);

	/**
	 * Getter for <code>mobsters.cepfu_raid_stage_history.crs_id</code>.
	 */
	@Column(name = "crs_id", precision = 10)
	public Integer getCrsId();

	/**
	 * Setter for <code>mobsters.cepfu_raid_stage_history.crs_dmg_done</code>.
	 */
	public ICepfuRaidStageHistory setCrsDmgDone(Integer value);

	/**
	 * Getter for <code>mobsters.cepfu_raid_stage_history.crs_dmg_done</code>.
	 */
	@Column(name = "crs_dmg_done", precision = 10)
	public Integer getCrsDmgDone();

	/**
	 * Setter for <code>mobsters.cepfu_raid_stage_history.stage_health</code>. sum of all monster healths in stage
	 */
	public ICepfuRaidStageHistory setStageHealth(Integer value);

	/**
	 * Getter for <code>mobsters.cepfu_raid_stage_history.stage_health</code>. sum of all monster healths in stage
	 */
	@Column(name = "stage_health", precision = 10)
	public Integer getStageHealth();

	/**
	 * Setter for <code>mobsters.cepfu_raid_stage_history.crs_end_time</code>.
	 */
	public ICepfuRaidStageHistory setCrsEndTime(Timestamp value);

	/**
	 * Getter for <code>mobsters.cepfu_raid_stage_history.crs_end_time</code>.
	 */
	@Column(name = "crs_end_time")
	public Timestamp getCrsEndTime();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface ICepfuRaidStageHistory
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ICepfuRaidStageHistory from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface ICepfuRaidStageHistory
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ICepfuRaidStageHistory> E into(E into);
}
