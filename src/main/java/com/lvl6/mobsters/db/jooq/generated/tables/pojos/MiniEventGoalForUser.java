/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IMiniEventGoalForUser;

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
	@UniqueConstraint(columnNames = {"user_id", "mini_event_goal_id"})
})
public class MiniEventGoalForUser implements IMiniEventGoalForUser {

	private static final long serialVersionUID = 985848477;

	private String  userId;
	private Integer miniEventGoalId;
	private Integer progress;

	public MiniEventGoalForUser() {}

	public MiniEventGoalForUser(MiniEventGoalForUser value) {
		this.userId = value.userId;
		this.miniEventGoalId = value.miniEventGoalId;
		this.progress = value.progress;
	}

	public MiniEventGoalForUser(
		String  userId,
		Integer miniEventGoalId,
		Integer progress
	) {
		this.userId = userId;
		this.miniEventGoalId = miniEventGoalId;
		this.progress = progress;
	}

	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getUserId() {
		return this.userId;
	}

	@Override
	public MiniEventGoalForUser setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	@Column(name = "mini_event_goal_id", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getMiniEventGoalId() {
		return this.miniEventGoalId;
	}

	@Override
	public MiniEventGoalForUser setMiniEventGoalId(Integer miniEventGoalId) {
		this.miniEventGoalId = miniEventGoalId;
		return this;
	}

	@Column(name = "progress", precision = 10)
	@Override
	public Integer getProgress() {
		return this.progress;
	}

	@Override
	public MiniEventGoalForUser setProgress(Integer progress) {
		this.progress = progress;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IMiniEventGoalForUser from) {
		setUserId(from.getUserId());
		setMiniEventGoalId(from.getMiniEventGoalId());
		setProgress(from.getProgress());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IMiniEventGoalForUser> E into(E into) {
		into.from(this);
		return into;
	}
}
