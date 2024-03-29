/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ITaskForUserCompleted;

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
@Table(name = "task_for_user_completed", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"user_id", "task_id"})
})
public class TaskForUserCompletedPojo implements ITaskForUserCompleted {

	private static final long serialVersionUID = -1645137828;

	private String    userId;
	private Integer   taskId;
	private Timestamp timeOfEntry;
	private Integer   unclaimedCash;
	private Integer   unclaimedOil;

	public TaskForUserCompletedPojo() {}

	public TaskForUserCompletedPojo(TaskForUserCompletedPojo value) {
		this.userId = value.userId;
		this.taskId = value.taskId;
		this.timeOfEntry = value.timeOfEntry;
		this.unclaimedCash = value.unclaimedCash;
		this.unclaimedOil = value.unclaimedOil;
	}

	public TaskForUserCompletedPojo(
		String    userId,
		Integer   taskId,
		Timestamp timeOfEntry,
		Integer   unclaimedCash,
		Integer   unclaimedOil
	) {
		this.userId = userId;
		this.taskId = taskId;
		this.timeOfEntry = timeOfEntry;
		this.unclaimedCash = unclaimedCash;
		this.unclaimedOil = unclaimedOil;
	}

	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getUserId() {
		return this.userId;
	}

	@Override
	public TaskForUserCompletedPojo setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	@Column(name = "task_id", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getTaskId() {
		return this.taskId;
	}

	@Override
	public TaskForUserCompletedPojo setTaskId(Integer taskId) {
		this.taskId = taskId;
		return this;
	}

	@Column(name = "time_of_entry")
	@Override
	public Timestamp getTimeOfEntry() {
		return this.timeOfEntry;
	}

	@Override
	public TaskForUserCompletedPojo setTimeOfEntry(Timestamp timeOfEntry) {
		this.timeOfEntry = timeOfEntry;
		return this;
	}

	@Column(name = "unclaimed_cash", precision = 10)
	@Override
	public Integer getUnclaimedCash() {
		return this.unclaimedCash;
	}

	@Override
	public TaskForUserCompletedPojo setUnclaimedCash(Integer unclaimedCash) {
		this.unclaimedCash = unclaimedCash;
		return this;
	}

	@Column(name = "unclaimed_oil", precision = 10)
	@Override
	public Integer getUnclaimedOil() {
		return this.unclaimedOil;
	}

	@Override
	public TaskForUserCompletedPojo setUnclaimedOil(Integer unclaimedOil) {
		this.unclaimedOil = unclaimedOil;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(ITaskForUserCompleted from) {
		setUserId(from.getUserId());
		setTaskId(from.getTaskId());
		setTimeOfEntry(from.getTimeOfEntry());
		setUnclaimedCash(from.getUnclaimedCash());
		setUnclaimedOil(from.getUnclaimedOil());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends ITaskForUserCompleted> E into(E into) {
		into.from(this);
		return into;
	}


	public String toString() {
		com.lvl6.mobsters.db.jooq.generated.tables.records.TaskForUserCompletedRecord poop = new com.lvl6.mobsters.db.jooq.generated.tables.records.TaskForUserCompletedRecord();
		poop.from(this);
		return "TaskForUserCompletedPojo[" + poop.valuesRow() + "]";
	}
}
