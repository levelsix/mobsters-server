/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IBattleItemQueueForUser;

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
public class BattleItemQueueForUserPojo implements IBattleItemQueueForUser {

	private static final long serialVersionUID = -27390159;

	private String    userId;
	private Integer   priority;
	private Integer   battleItemId;
	private Timestamp expectedStartTime;
	private Double    elapsedTime;

	public BattleItemQueueForUserPojo() {}

	public BattleItemQueueForUserPojo(BattleItemQueueForUserPojo value) {
		this.userId = value.userId;
		this.priority = value.priority;
		this.battleItemId = value.battleItemId;
		this.expectedStartTime = value.expectedStartTime;
		this.elapsedTime = value.elapsedTime;
	}

	public BattleItemQueueForUserPojo(
		String    userId,
		Integer   priority,
		Integer   battleItemId,
		Timestamp expectedStartTime,
		Double    elapsedTime
	) {
		this.userId = userId;
		this.priority = priority;
		this.battleItemId = battleItemId;
		this.expectedStartTime = expectedStartTime;
		this.elapsedTime = elapsedTime;
	}

	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getUserId() {
		return this.userId;
	}

	@Override
	public BattleItemQueueForUserPojo setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	@Column(name = "priority", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getPriority() {
		return this.priority;
	}

	@Override
	public BattleItemQueueForUserPojo setPriority(Integer priority) {
		this.priority = priority;
		return this;
	}

	@Column(name = "battle_item_id", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getBattleItemId() {
		return this.battleItemId;
	}

	@Override
	public BattleItemQueueForUserPojo setBattleItemId(Integer battleItemId) {
		this.battleItemId = battleItemId;
		return this;
	}

	@Column(name = "expected_start_time")
	@Override
	public Timestamp getExpectedStartTime() {
		return this.expectedStartTime;
	}

	@Override
	public BattleItemQueueForUserPojo setExpectedStartTime(Timestamp expectedStartTime) {
		this.expectedStartTime = expectedStartTime;
		return this;
	}

	@Column(name = "elapsed_time", nullable = false, precision = 12)
	@NotNull
	@Override
	public Double getElapsedTime() {
		return this.elapsedTime;
	}

	@Override
	public BattleItemQueueForUserPojo setElapsedTime(Double elapsedTime) {
		this.elapsedTime = elapsedTime;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IBattleItemQueueForUser from) {
		setUserId(from.getUserId());
		setPriority(from.getPriority());
		setBattleItemId(from.getBattleItemId());
		setExpectedStartTime(from.getExpectedStartTime());
		setElapsedTime(from.getElapsedTime());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IBattleItemQueueForUser> E into(E into) {
		into.from(this);
		return into;
	}


	public String toString() {
		com.lvl6.mobsters.db.jooq.generated.tables.records.BattleItemQueueForUserRecord poop = new com.lvl6.mobsters.db.jooq.generated.tables.records.BattleItemQueueForUserRecord();
		poop.from(this);
		return "BattleItemQueueForUserPojo[" + poop.valuesRow() + "]";
	}
}
