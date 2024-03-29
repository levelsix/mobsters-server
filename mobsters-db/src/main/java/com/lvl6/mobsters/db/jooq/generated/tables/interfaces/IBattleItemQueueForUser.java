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
@Table(name = "battle_item_queue_for_user", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"user_id", "priority"})
})
public interface IBattleItemQueueForUser extends Serializable {

	/**
	 * Setter for <code>mobsters.battle_item_queue_for_user.user_id</code>.
	 */
	public IBattleItemQueueForUser setUserId(String value);

	/**
	 * Getter for <code>mobsters.battle_item_queue_for_user.user_id</code>.
	 */
	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	public String getUserId();

	/**
	 * Setter for <code>mobsters.battle_item_queue_for_user.priority</code>.
	 */
	public IBattleItemQueueForUser setPriority(Integer value);

	/**
	 * Getter for <code>mobsters.battle_item_queue_for_user.priority</code>.
	 */
	@Column(name = "priority", nullable = false, precision = 10)
	@NotNull
	public Integer getPriority();

	/**
	 * Setter for <code>mobsters.battle_item_queue_for_user.battle_item_id</code>.
	 */
	public IBattleItemQueueForUser setBattleItemId(Integer value);

	/**
	 * Getter for <code>mobsters.battle_item_queue_for_user.battle_item_id</code>.
	 */
	@Column(name = "battle_item_id", nullable = false, precision = 10)
	@NotNull
	public Integer getBattleItemId();

	/**
	 * Setter for <code>mobsters.battle_item_queue_for_user.expected_start_time</code>.
	 */
	public IBattleItemQueueForUser setExpectedStartTime(Timestamp value);

	/**
	 * Getter for <code>mobsters.battle_item_queue_for_user.expected_start_time</code>.
	 */
	@Column(name = "expected_start_time")
	public Timestamp getExpectedStartTime();

	/**
	 * Setter for <code>mobsters.battle_item_queue_for_user.elapsed_time</code>.
	 */
	public IBattleItemQueueForUser setElapsedTime(Double value);

	/**
	 * Getter for <code>mobsters.battle_item_queue_for_user.elapsed_time</code>.
	 */
	@Column(name = "elapsed_time", nullable = false, precision = 12)
	@NotNull
	public Double getElapsedTime();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface IBattleItemQueueForUser
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IBattleItemQueueForUser from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface IBattleItemQueueForUser
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IBattleItemQueueForUser> E into(E into);
}
