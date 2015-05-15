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
@Table(name = "cepfu_raid_history", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"user_id", "time_of_entry", "clan_id"})
})
public interface ICepfuRaidHistory extends Serializable {

	/**
	 * Setter for <code>mobsters.cepfu_raid_history.user_id</code>.
	 */
	public ICepfuRaidHistory setUserId(String value);

	/**
	 * Getter for <code>mobsters.cepfu_raid_history.user_id</code>.
	 */
	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	public String getUserId();

	/**
	 * Setter for <code>mobsters.cepfu_raid_history.time_of_entry</code>.
	 */
	public ICepfuRaidHistory setTimeOfEntry(Timestamp value);

	/**
	 * Getter for <code>mobsters.cepfu_raid_history.time_of_entry</code>.
	 */
	@Column(name = "time_of_entry", nullable = false)
	@NotNull
	public Timestamp getTimeOfEntry();

	/**
	 * Setter for <code>mobsters.cepfu_raid_history.clan_id</code>.
	 */
	public ICepfuRaidHistory setClanId(String value);

	/**
	 * Getter for <code>mobsters.cepfu_raid_history.clan_id</code>.
	 */
	@Column(name = "clan_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	public String getClanId();

	/**
	 * Setter for <code>mobsters.cepfu_raid_history.clan_event_persistent_id</code>.
	 */
	public ICepfuRaidHistory setClanEventPersistentId(Integer value);

	/**
	 * Getter for <code>mobsters.cepfu_raid_history.clan_event_persistent_id</code>.
	 */
	@Column(name = "clan_event_persistent_id", precision = 10)
	public Integer getClanEventPersistentId();

	/**
	 * Setter for <code>mobsters.cepfu_raid_history.cr_id</code>.
	 */
	public ICepfuRaidHistory setCrId(Integer value);

	/**
	 * Getter for <code>mobsters.cepfu_raid_history.cr_id</code>.
	 */
	@Column(name = "cr_id", precision = 10)
	public Integer getCrId();

	/**
	 * Setter for <code>mobsters.cepfu_raid_history.cr_dmg_done</code>.
	 */
	public ICepfuRaidHistory setCrDmgDone(Integer value);

	/**
	 * Getter for <code>mobsters.cepfu_raid_history.cr_dmg_done</code>.
	 */
	@Column(name = "cr_dmg_done", precision = 10)
	public Integer getCrDmgDone();

	/**
	 * Setter for <code>mobsters.cepfu_raid_history.clan_cr_dmg</code>. how much dmg clan did in raid
	 */
	public ICepfuRaidHistory setClanCrDmg(Integer value);

	/**
	 * Getter for <code>mobsters.cepfu_raid_history.clan_cr_dmg</code>. how much dmg clan did in raid
	 */
	@Column(name = "clan_cr_dmg", precision = 10)
	public Integer getClanCrDmg();

	/**
	 * Setter for <code>mobsters.cepfu_raid_history.user_monster_id_one</code>.
	 */
	public ICepfuRaidHistory setUserMonsterIdOne(String value);

	/**
	 * Getter for <code>mobsters.cepfu_raid_history.user_monster_id_one</code>.
	 */
	@Column(name = "user_monster_id_one", length = 36)
	@Size(max = 36)
	public String getUserMonsterIdOne();

	/**
	 * Setter for <code>mobsters.cepfu_raid_history.user_monster_id_two</code>.
	 */
	public ICepfuRaidHistory setUserMonsterIdTwo(String value);

	/**
	 * Getter for <code>mobsters.cepfu_raid_history.user_monster_id_two</code>.
	 */
	@Column(name = "user_monster_id_two", length = 36)
	@Size(max = 36)
	public String getUserMonsterIdTwo();

	/**
	 * Setter for <code>mobsters.cepfu_raid_history.user_monster_id_three</code>.
	 */
	public ICepfuRaidHistory setUserMonsterIdThree(String value);

	/**
	 * Getter for <code>mobsters.cepfu_raid_history.user_monster_id_three</code>.
	 */
	@Column(name = "user_monster_id_three", length = 36)
	@Size(max = 36)
	public String getUserMonsterIdThree();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface ICepfuRaidHistory
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ICepfuRaidHistory from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface ICepfuRaidHistory
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ICepfuRaidHistory> E into(E into);
}
