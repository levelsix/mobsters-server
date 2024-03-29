/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.interfaces;


import java.io.Serializable;
import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
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
@Table(name = "monster_enhancing_history", schema = "mobsters")
public interface IMonsterEnhancingHistory extends Serializable {

	/**
	 * Setter for <code>mobsters.monster_enhancing_history.id</code>.
	 */
	public IMonsterEnhancingHistory setId(String value);

	/**
	 * Getter for <code>mobsters.monster_enhancing_history.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	public String getId();

	/**
	 * Setter for <code>mobsters.monster_enhancing_history.user_id</code>.
	 */
	public IMonsterEnhancingHistory setUserId(String value);

	/**
	 * Getter for <code>mobsters.monster_enhancing_history.user_id</code>.
	 */
	@Column(name = "user_id", length = 36)
	@Size(max = 36)
	public String getUserId();

	/**
	 * Setter for <code>mobsters.monster_enhancing_history.mfu_id_being_enhanced</code>.
	 */
	public IMonsterEnhancingHistory setMfuIdBeingEnhanced(String value);

	/**
	 * Getter for <code>mobsters.monster_enhancing_history.mfu_id_being_enhanced</code>.
	 */
	@Column(name = "mfu_id_being_enhanced", length = 36)
	@Size(max = 36)
	public String getMfuIdBeingEnhanced();

	/**
	 * Setter for <code>mobsters.monster_enhancing_history.feeder_mfu_id</code>.
	 */
	public IMonsterEnhancingHistory setFeederMfuId(String value);

	/**
	 * Getter for <code>mobsters.monster_enhancing_history.feeder_mfu_id</code>.
	 */
	@Column(name = "feeder_mfu_id", length = 36)
	@Size(max = 36)
	public String getFeederMfuId();

	/**
	 * Setter for <code>mobsters.monster_enhancing_history.current_experience</code>.
	 */
	public IMonsterEnhancingHistory setCurrentExperience(Integer value);

	/**
	 * Getter for <code>mobsters.monster_enhancing_history.current_experience</code>.
	 */
	@Column(name = "current_experience", precision = 10)
	public Integer getCurrentExperience();

	/**
	 * Setter for <code>mobsters.monster_enhancing_history.previous_experience</code>.
	 */
	public IMonsterEnhancingHistory setPreviousExperience(Integer value);

	/**
	 * Getter for <code>mobsters.monster_enhancing_history.previous_experience</code>.
	 */
	@Column(name = "previous_experience", precision = 10)
	public Integer getPreviousExperience();

	/**
	 * Setter for <code>mobsters.monster_enhancing_history.enhancing_start_time</code>.
	 */
	public IMonsterEnhancingHistory setEnhancingStartTime(Timestamp value);

	/**
	 * Getter for <code>mobsters.monster_enhancing_history.enhancing_start_time</code>.
	 */
	@Column(name = "enhancing_start_time")
	public Timestamp getEnhancingStartTime();

	/**
	 * Setter for <code>mobsters.monster_enhancing_history.time_of_entry</code>.
	 */
	public IMonsterEnhancingHistory setTimeOfEntry(Timestamp value);

	/**
	 * Getter for <code>mobsters.monster_enhancing_history.time_of_entry</code>.
	 */
	@Column(name = "time_of_entry")
	public Timestamp getTimeOfEntry();

	/**
	 * Setter for <code>mobsters.monster_enhancing_history.enhancing_cost</code>.
	 */
	public IMonsterEnhancingHistory setEnhancingCost(Integer value);

	/**
	 * Getter for <code>mobsters.monster_enhancing_history.enhancing_cost</code>.
	 */
	@Column(name = "enhancing_cost", precision = 10)
	public Integer getEnhancingCost();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface IMonsterEnhancingHistory
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IMonsterEnhancingHistory from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface IMonsterEnhancingHistory
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IMonsterEnhancingHistory> E into(E into);
}
