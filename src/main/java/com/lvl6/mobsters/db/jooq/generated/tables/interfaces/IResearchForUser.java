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
@Table(name = "research_for_user", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"id", "user_id"})
})
public interface IResearchForUser extends Serializable {

	/**
	 * Setter for <code>mobsters.research_for_user.id</code>.
	 */
	public IResearchForUser setId(String value);

	/**
	 * Getter for <code>mobsters.research_for_user.id</code>.
	 */
	@Column(name = "id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	public String getId();

	/**
	 * Setter for <code>mobsters.research_for_user.user_id</code>.
	 */
	public IResearchForUser setUserId(String value);

	/**
	 * Getter for <code>mobsters.research_for_user.user_id</code>.
	 */
	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	public String getUserId();

	/**
	 * Setter for <code>mobsters.research_for_user.research_id</code>.
	 */
	public IResearchForUser setResearchId(Integer value);

	/**
	 * Getter for <code>mobsters.research_for_user.research_id</code>.
	 */
	@Column(name = "research_id", precision = 10)
	public Integer getResearchId();

	/**
	 * Setter for <code>mobsters.research_for_user.time_purchased</code>.
	 */
	public IResearchForUser setTimePurchased(Timestamp value);

	/**
	 * Getter for <code>mobsters.research_for_user.time_purchased</code>.
	 */
	@Column(name = "time_purchased")
	public Timestamp getTimePurchased();

	/**
	 * Setter for <code>mobsters.research_for_user.is_complete</code>.
	 */
	public IResearchForUser setIsComplete(Boolean value);

	/**
	 * Getter for <code>mobsters.research_for_user.is_complete</code>.
	 */
	@Column(name = "is_complete", precision = 1)
	public Boolean getIsComplete();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface IResearchForUser
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IResearchForUser from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface IResearchForUser
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IResearchForUser> E into(E into);
}