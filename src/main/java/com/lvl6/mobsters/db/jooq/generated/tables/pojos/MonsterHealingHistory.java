/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IMonsterHealingHistory;

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
@Table(name = "monster_healing_history", schema = "mobsters")
public class MonsterHealingHistory implements IMonsterHealingHistory {

	private static final long serialVersionUID = -1181835501;

	private String    id;
	private String    userId;
	private String    monsterForUserId;
	private Timestamp queuedTime;
	private Timestamp unqueuedTime;
	private Boolean   finishedHealing;

	public MonsterHealingHistory() {}

	public MonsterHealingHistory(MonsterHealingHistory value) {
		this.id = value.id;
		this.userId = value.userId;
		this.monsterForUserId = value.monsterForUserId;
		this.queuedTime = value.queuedTime;
		this.unqueuedTime = value.unqueuedTime;
		this.finishedHealing = value.finishedHealing;
	}

	public MonsterHealingHistory(
		String    id,
		String    userId,
		String    monsterForUserId,
		Timestamp queuedTime,
		Timestamp unqueuedTime,
		Boolean   finishedHealing
	) {
		this.id = id;
		this.userId = userId;
		this.monsterForUserId = monsterForUserId;
		this.queuedTime = queuedTime;
		this.unqueuedTime = unqueuedTime;
		this.finishedHealing = finishedHealing;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public MonsterHealingHistory setId(String id) {
		this.id = id;
		return this;
	}

	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getUserId() {
		return this.userId;
	}

	@Override
	public MonsterHealingHistory setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	@Column(name = "monster_for_user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getMonsterForUserId() {
		return this.monsterForUserId;
	}

	@Override
	public MonsterHealingHistory setMonsterForUserId(String monsterForUserId) {
		this.monsterForUserId = monsterForUserId;
		return this;
	}

	@Column(name = "queued_time")
	@Override
	public Timestamp getQueuedTime() {
		return this.queuedTime;
	}

	@Override
	public MonsterHealingHistory setQueuedTime(Timestamp queuedTime) {
		this.queuedTime = queuedTime;
		return this;
	}

	@Column(name = "unqueued_time")
	@Override
	public Timestamp getUnqueuedTime() {
		return this.unqueuedTime;
	}

	@Override
	public MonsterHealingHistory setUnqueuedTime(Timestamp unqueuedTime) {
		this.unqueuedTime = unqueuedTime;
		return this;
	}

	@Column(name = "finished_healing", precision = 1)
	@Override
	public Boolean getFinishedHealing() {
		return this.finishedHealing;
	}

	@Override
	public MonsterHealingHistory setFinishedHealing(Boolean finishedHealing) {
		this.finishedHealing = finishedHealing;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IMonsterHealingHistory from) {
		setId(from.getId());
		setUserId(from.getUserId());
		setMonsterForUserId(from.getMonsterForUserId());
		setQueuedTime(from.getQueuedTime());
		setUnqueuedTime(from.getUnqueuedTime());
		setFinishedHealing(from.getFinishedHealing());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IMonsterHealingHistory> E into(E into) {
		into.from(this);
		return into;
	}
}
