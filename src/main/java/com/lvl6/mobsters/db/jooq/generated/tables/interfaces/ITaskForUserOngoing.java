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
@Table(name = "task_for_user_ongoing", schema = "mobsters")
public interface ITaskForUserOngoing extends Serializable {

	/**
	 * Setter for <code>mobsters.task_for_user_ongoing.id</code>.
	 */
	public ITaskForUserOngoing setId(String value);

	/**
	 * Getter for <code>mobsters.task_for_user_ongoing.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	public String getId();

	/**
	 * Setter for <code>mobsters.task_for_user_ongoing.user_id</code>.
	 */
	public ITaskForUserOngoing setUserId(String value);

	/**
	 * Getter for <code>mobsters.task_for_user_ongoing.user_id</code>.
	 */
	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	public String getUserId();

	/**
	 * Setter for <code>mobsters.task_for_user_ongoing.task_id</code>.
	 */
	public ITaskForUserOngoing setTaskId(Integer value);

	/**
	 * Getter for <code>mobsters.task_for_user_ongoing.task_id</code>.
	 */
	@Column(name = "task_id", nullable = false, precision = 10)
	@NotNull
	public Integer getTaskId();

	/**
	 * Setter for <code>mobsters.task_for_user_ongoing.exp_gained</code>. Experience gained from all the stages, if the user beats the dungeon.
	 */
	public ITaskForUserOngoing setExpGained(Integer value);

	/**
	 * Getter for <code>mobsters.task_for_user_ongoing.exp_gained</code>. Experience gained from all the stages, if the user beats the dungeon.
	 */
	@Column(name = "exp_gained", precision = 10)
	public Integer getExpGained();

	/**
	 * Setter for <code>mobsters.task_for_user_ongoing.cash_gained</code>. Cash gained from all the stages, if the user beats the dungeon.
	 */
	public ITaskForUserOngoing setCashGained(Integer value);

	/**
	 * Getter for <code>mobsters.task_for_user_ongoing.cash_gained</code>. Cash gained from all the stages, if the user beats the dungeon.
	 */
	@Column(name = "cash_gained", precision = 10)
	public Integer getCashGained();

	/**
	 * Setter for <code>mobsters.task_for_user_ongoing.oil_gained</code>.
	 */
	public ITaskForUserOngoing setOilGained(Integer value);

	/**
	 * Getter for <code>mobsters.task_for_user_ongoing.oil_gained</code>.
	 */
	@Column(name = "oil_gained", precision = 10)
	public Integer getOilGained();

	/**
	 * Setter for <code>mobsters.task_for_user_ongoing.num_revives</code>. Keeps track of how many times the user died and chose to revive-and-continue the current stage in the dungeon.
	 */
	public ITaskForUserOngoing setNumRevives(Integer value);

	/**
	 * Getter for <code>mobsters.task_for_user_ongoing.num_revives</code>. Keeps track of how many times the user died and chose to revive-and-continue the current stage in the dungeon.
	 */
	@Column(name = "num_revives", precision = 10)
	public Integer getNumRevives();

	/**
	 * Setter for <code>mobsters.task_for_user_ongoing.start_time</code>.
	 */
	public ITaskForUserOngoing setStartTime(Timestamp value);

	/**
	 * Getter for <code>mobsters.task_for_user_ongoing.start_time</code>.
	 */
	@Column(name = "start_time")
	public Timestamp getStartTime();

	/**
	 * Setter for <code>mobsters.task_for_user_ongoing.task_stage_id</code>.
	 */
	public ITaskForUserOngoing setTaskStageId(Integer value);

	/**
	 * Getter for <code>mobsters.task_for_user_ongoing.task_stage_id</code>.
	 */
	@Column(name = "task_stage_id", precision = 10)
	public Integer getTaskStageId();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface ITaskForUserOngoing
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ITaskForUserOngoing from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface ITaskForUserOngoing
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ITaskForUserOngoing> E into(E into);
}
