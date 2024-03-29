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
@Table(name = "monster_enhancing_for_user", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"user_id", "monster_for_user_id"})
})
public interface IMonsterEnhancingForUser extends Serializable {

	/**
	 * Setter for <code>mobsters.monster_enhancing_for_user.user_id</code>.
	 */
	public IMonsterEnhancingForUser setUserId(String value);

	/**
	 * Getter for <code>mobsters.monster_enhancing_for_user.user_id</code>.
	 */
	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	public String getUserId();

	/**
	 * Setter for <code>mobsters.monster_enhancing_for_user.monster_for_user_id</code>.
	 */
	public IMonsterEnhancingForUser setMonsterForUserId(String value);

	/**
	 * Getter for <code>mobsters.monster_enhancing_for_user.monster_for_user_id</code>.
	 */
	@Column(name = "monster_for_user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	public String getMonsterForUserId();

	/**
	 * Setter for <code>mobsters.monster_enhancing_for_user.expected_start_time</code>.
	 */
	public IMonsterEnhancingForUser setExpectedStartTime(Timestamp value);

	/**
	 * Getter for <code>mobsters.monster_enhancing_for_user.expected_start_time</code>.
	 */
	@Column(name = "expected_start_time")
	public Timestamp getExpectedStartTime();

	/**
	 * Setter for <code>mobsters.monster_enhancing_for_user.enhancing_cost</code>.
	 */
	public IMonsterEnhancingForUser setEnhancingCost(Integer value);

	/**
	 * Getter for <code>mobsters.monster_enhancing_for_user.enhancing_cost</code>.
	 */
	@Column(name = "enhancing_cost", precision = 10)
	public Integer getEnhancingCost();

	/**
	 * Setter for <code>mobsters.monster_enhancing_for_user.enhancing_complete</code>.
	 */
	public IMonsterEnhancingForUser setEnhancingComplete(Boolean value);

	/**
	 * Getter for <code>mobsters.monster_enhancing_for_user.enhancing_complete</code>.
	 */
	@Column(name = "enhancing_complete", precision = 1)
	public Boolean getEnhancingComplete();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface IMonsterEnhancingForUser
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IMonsterEnhancingForUser from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface IMonsterEnhancingForUser
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IMonsterEnhancingForUser> E into(E into);
}
