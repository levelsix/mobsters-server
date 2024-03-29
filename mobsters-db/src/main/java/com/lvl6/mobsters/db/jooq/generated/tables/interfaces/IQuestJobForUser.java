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
@Table(name = "quest_job_for_user", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"user_id", "quest_id", "quest_job_id"})
})
public interface IQuestJobForUser extends Serializable {

	/**
	 * Setter for <code>mobsters.quest_job_for_user.user_id</code>.
	 */
	public IQuestJobForUser setUserId(String value);

	/**
	 * Getter for <code>mobsters.quest_job_for_user.user_id</code>.
	 */
	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	public String getUserId();

	/**
	 * Setter for <code>mobsters.quest_job_for_user.quest_id</code>.
	 */
	public IQuestJobForUser setQuestId(Integer value);

	/**
	 * Getter for <code>mobsters.quest_job_for_user.quest_id</code>.
	 */
	@Column(name = "quest_id", nullable = false, precision = 10)
	@NotNull
	public Integer getQuestId();

	/**
	 * Setter for <code>mobsters.quest_job_for_user.quest_job_id</code>.
	 */
	public IQuestJobForUser setQuestJobId(Integer value);

	/**
	 * Getter for <code>mobsters.quest_job_for_user.quest_job_id</code>.
	 */
	@Column(name = "quest_job_id", nullable = false, precision = 10)
	@NotNull
	public Integer getQuestJobId();

	/**
	 * Setter for <code>mobsters.quest_job_for_user.is_complete</code>.
	 */
	public IQuestJobForUser setIsComplete(Boolean value);

	/**
	 * Getter for <code>mobsters.quest_job_for_user.is_complete</code>.
	 */
	@Column(name = "is_complete", precision = 1)
	public Boolean getIsComplete();

	/**
	 * Setter for <code>mobsters.quest_job_for_user.progress</code>.
	 */
	public IQuestJobForUser setProgress(Integer value);

	/**
	 * Getter for <code>mobsters.quest_job_for_user.progress</code>.
	 */
	@Column(name = "progress", precision = 7)
	public Integer getProgress();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface IQuestJobForUser
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IQuestJobForUser from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface IQuestJobForUser
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IQuestJobForUser> E into(E into);
}
