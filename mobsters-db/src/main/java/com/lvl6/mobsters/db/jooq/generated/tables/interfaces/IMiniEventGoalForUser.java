/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.interfaces;


import java.io.Serializable;

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
@Table(name = "mini_event_goal_for_user", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"user_id", "mini_event_timetable_id", "mini_event_goal_id"})
})
public interface IMiniEventGoalForUser extends Serializable {

	/**
	 * Setter for <code>mobsters.mini_event_goal_for_user.user_id</code>.
	 */
	public IMiniEventGoalForUser setUserId(String value);

	/**
	 * Getter for <code>mobsters.mini_event_goal_for_user.user_id</code>.
	 */
	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	public String getUserId();

	/**
	 * Setter for <code>mobsters.mini_event_goal_for_user.mini_event_timetable_id</code>.
	 */
	public IMiniEventGoalForUser setMiniEventTimetableId(Integer value);

	/**
	 * Getter for <code>mobsters.mini_event_goal_for_user.mini_event_timetable_id</code>.
	 */
	@Column(name = "mini_event_timetable_id", nullable = false, precision = 10)
	@NotNull
	public Integer getMiniEventTimetableId();

	/**
	 * Setter for <code>mobsters.mini_event_goal_for_user.mini_event_goal_id</code>.
	 */
	public IMiniEventGoalForUser setMiniEventGoalId(Integer value);

	/**
	 * Getter for <code>mobsters.mini_event_goal_for_user.mini_event_goal_id</code>.
	 */
	@Column(name = "mini_event_goal_id", nullable = false, precision = 10)
	@NotNull
	public Integer getMiniEventGoalId();

	/**
	 * Setter for <code>mobsters.mini_event_goal_for_user.progress</code>.
	 */
	public IMiniEventGoalForUser setProgress(Integer value);

	/**
	 * Getter for <code>mobsters.mini_event_goal_for_user.progress</code>.
	 */
	@Column(name = "progress", precision = 10)
	public Integer getProgress();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface IMiniEventGoalForUser
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IMiniEventGoalForUser from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface IMiniEventGoalForUser
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IMiniEventGoalForUser> E into(E into);
}
