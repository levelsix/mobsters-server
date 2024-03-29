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
@Table(name = "mini_job_for_user_history", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"user_id", "mini_job_id", "time_completed"})
})
public interface IMiniJobForUserHistory extends Serializable {

	/**
	 * Setter for <code>mobsters.mini_job_for_user_history.user_id</code>.
	 */
	public IMiniJobForUserHistory setUserId(String value);

	/**
	 * Getter for <code>mobsters.mini_job_for_user_history.user_id</code>.
	 */
	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	public String getUserId();

	/**
	 * Setter for <code>mobsters.mini_job_for_user_history.mini_job_id</code>.
	 */
	public IMiniJobForUserHistory setMiniJobId(String value);

	/**
	 * Getter for <code>mobsters.mini_job_for_user_history.mini_job_id</code>.
	 */
	@Column(name = "mini_job_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	public String getMiniJobId();

	/**
	 * Setter for <code>mobsters.mini_job_for_user_history.time_completed</code>.
	 */
	public IMiniJobForUserHistory setTimeCompleted(Timestamp value);

	/**
	 * Getter for <code>mobsters.mini_job_for_user_history.time_completed</code>.
	 */
	@Column(name = "time_completed", nullable = false)
	@NotNull
	public Timestamp getTimeCompleted();

	/**
	 * Setter for <code>mobsters.mini_job_for_user_history.base_dmg_received</code>.
	 */
	public IMiniJobForUserHistory setBaseDmgReceived(Integer value);

	/**
	 * Getter for <code>mobsters.mini_job_for_user_history.base_dmg_received</code>.
	 */
	@Column(name = "base_dmg_received", precision = 10)
	public Integer getBaseDmgReceived();

	/**
	 * Setter for <code>mobsters.mini_job_for_user_history.time_started</code>.
	 */
	public IMiniJobForUserHistory setTimeStarted(Timestamp value);

	/**
	 * Getter for <code>mobsters.mini_job_for_user_history.time_started</code>.
	 */
	@Column(name = "time_started")
	public Timestamp getTimeStarted();

	/**
	 * Setter for <code>mobsters.mini_job_for_user_history.user_monster_ids</code>.
	 */
	public IMiniJobForUserHistory setUserMonsterIds(String value);

	/**
	 * Getter for <code>mobsters.mini_job_for_user_history.user_monster_ids</code>.
	 */
	@Column(name = "user_monster_ids", length = 511)
	@Size(max = 511)
	public String getUserMonsterIds();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface IMiniJobForUserHistory
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IMiniJobForUserHistory from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface IMiniJobForUserHistory
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IMiniJobForUserHistory> E into(E into);
}
