/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IMiniJobForUserHistory;

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
public class MiniJobForUserHistory implements IMiniJobForUserHistory {

	private static final long serialVersionUID = -369744599;

	private String    userId;
	private String    miniJobId;
	private Timestamp timeCompleted;
	private Integer   baseDmgReceived;
	private Timestamp timeStarted;
	private String    userMonsterIds;

	public MiniJobForUserHistory() {}

	public MiniJobForUserHistory(MiniJobForUserHistory value) {
		this.userId = value.userId;
		this.miniJobId = value.miniJobId;
		this.timeCompleted = value.timeCompleted;
		this.baseDmgReceived = value.baseDmgReceived;
		this.timeStarted = value.timeStarted;
		this.userMonsterIds = value.userMonsterIds;
	}

	public MiniJobForUserHistory(
		String    userId,
		String    miniJobId,
		Timestamp timeCompleted,
		Integer   baseDmgReceived,
		Timestamp timeStarted,
		String    userMonsterIds
	) {
		this.userId = userId;
		this.miniJobId = miniJobId;
		this.timeCompleted = timeCompleted;
		this.baseDmgReceived = baseDmgReceived;
		this.timeStarted = timeStarted;
		this.userMonsterIds = userMonsterIds;
	}

	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getUserId() {
		return this.userId;
	}

	@Override
	public MiniJobForUserHistory setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	@Column(name = "mini_job_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getMiniJobId() {
		return this.miniJobId;
	}

	@Override
	public MiniJobForUserHistory setMiniJobId(String miniJobId) {
		this.miniJobId = miniJobId;
		return this;
	}

	@Column(name = "time_completed", nullable = false)
	@NotNull
	@Override
	public Timestamp getTimeCompleted() {
		return this.timeCompleted;
	}

	@Override
	public MiniJobForUserHistory setTimeCompleted(Timestamp timeCompleted) {
		this.timeCompleted = timeCompleted;
		return this;
	}

	@Column(name = "base_dmg_received", precision = 10)
	@Override
	public Integer getBaseDmgReceived() {
		return this.baseDmgReceived;
	}

	@Override
	public MiniJobForUserHistory setBaseDmgReceived(Integer baseDmgReceived) {
		this.baseDmgReceived = baseDmgReceived;
		return this;
	}

	@Column(name = "time_started")
	@Override
	public Timestamp getTimeStarted() {
		return this.timeStarted;
	}

	@Override
	public MiniJobForUserHistory setTimeStarted(Timestamp timeStarted) {
		this.timeStarted = timeStarted;
		return this;
	}

	@Column(name = "user_monster_ids", length = 511)
	@Size(max = 511)
	@Override
	public String getUserMonsterIds() {
		return this.userMonsterIds;
	}

	@Override
	public MiniJobForUserHistory setUserMonsterIds(String userMonsterIds) {
		this.userMonsterIds = userMonsterIds;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IMiniJobForUserHistory from) {
		setUserId(from.getUserId());
		setMiniJobId(from.getMiniJobId());
		setTimeCompleted(from.getTimeCompleted());
		setBaseDmgReceived(from.getBaseDmgReceived());
		setTimeStarted(from.getTimeStarted());
		setUserMonsterIds(from.getUserMonsterIds());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IMiniJobForUserHistory> E into(E into) {
		into.from(this);
		return into;
	}
}
