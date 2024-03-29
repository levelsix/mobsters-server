/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ITaskHistory;

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
@Table(name = "task_history", schema = "mobsters")
public class TaskHistoryPojo implements ITaskHistory {

	private static final long serialVersionUID = 2074084933;

	private String    taskForUserId;
	private String    userId;
	private Integer   taskId;
	private Integer   expGained;
	private Integer   cashGained;
	private Integer   oilGained;
	private Integer   numRevives;
	private Timestamp startTime;
	private Timestamp endTime;
	private Boolean   userWon;
	private Boolean   cancelled;
	private Integer   taskStageId;

	public TaskHistoryPojo() {}

	public TaskHistoryPojo(TaskHistoryPojo value) {
		this.taskForUserId = value.taskForUserId;
		this.userId = value.userId;
		this.taskId = value.taskId;
		this.expGained = value.expGained;
		this.cashGained = value.cashGained;
		this.oilGained = value.oilGained;
		this.numRevives = value.numRevives;
		this.startTime = value.startTime;
		this.endTime = value.endTime;
		this.userWon = value.userWon;
		this.cancelled = value.cancelled;
		this.taskStageId = value.taskStageId;
	}

	public TaskHistoryPojo(
		String    taskForUserId,
		String    userId,
		Integer   taskId,
		Integer   expGained,
		Integer   cashGained,
		Integer   oilGained,
		Integer   numRevives,
		Timestamp startTime,
		Timestamp endTime,
		Boolean   userWon,
		Boolean   cancelled,
		Integer   taskStageId
	) {
		this.taskForUserId = taskForUserId;
		this.userId = userId;
		this.taskId = taskId;
		this.expGained = expGained;
		this.cashGained = cashGained;
		this.oilGained = oilGained;
		this.numRevives = numRevives;
		this.startTime = startTime;
		this.endTime = endTime;
		this.userWon = userWon;
		this.cancelled = cancelled;
		this.taskStageId = taskStageId;
	}

	@Id
	@Column(name = "task_for_user_id", unique = true, nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getTaskForUserId() {
		return this.taskForUserId;
	}

	@Override
	public TaskHistoryPojo setTaskForUserId(String taskForUserId) {
		this.taskForUserId = taskForUserId;
		return this;
	}

	@Column(name = "user_id", length = 36)
	@Size(max = 36)
	@Override
	public String getUserId() {
		return this.userId;
	}

	@Override
	public TaskHistoryPojo setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	@Column(name = "task_id", precision = 10)
	@Override
	public Integer getTaskId() {
		return this.taskId;
	}

	@Override
	public TaskHistoryPojo setTaskId(Integer taskId) {
		this.taskId = taskId;
		return this;
	}

	@Column(name = "exp_gained", precision = 10)
	@Override
	public Integer getExpGained() {
		return this.expGained;
	}

	@Override
	public TaskHistoryPojo setExpGained(Integer expGained) {
		this.expGained = expGained;
		return this;
	}

	@Column(name = "cash_gained", precision = 10)
	@Override
	public Integer getCashGained() {
		return this.cashGained;
	}

	@Override
	public TaskHistoryPojo setCashGained(Integer cashGained) {
		this.cashGained = cashGained;
		return this;
	}

	@Column(name = "oil_gained", precision = 10)
	@Override
	public Integer getOilGained() {
		return this.oilGained;
	}

	@Override
	public TaskHistoryPojo setOilGained(Integer oilGained) {
		this.oilGained = oilGained;
		return this;
	}

	@Column(name = "num_revives", precision = 10)
	@Override
	public Integer getNumRevives() {
		return this.numRevives;
	}

	@Override
	public TaskHistoryPojo setNumRevives(Integer numRevives) {
		this.numRevives = numRevives;
		return this;
	}

	@Column(name = "start_time")
	@Override
	public Timestamp getStartTime() {
		return this.startTime;
	}

	@Override
	public TaskHistoryPojo setStartTime(Timestamp startTime) {
		this.startTime = startTime;
		return this;
	}

	@Column(name = "end_time")
	@Override
	public Timestamp getEndTime() {
		return this.endTime;
	}

	@Override
	public TaskHistoryPojo setEndTime(Timestamp endTime) {
		this.endTime = endTime;
		return this;
	}

	@Column(name = "user_won", precision = 1)
	@Override
	public Boolean getUserWon() {
		return this.userWon;
	}

	@Override
	public TaskHistoryPojo setUserWon(Boolean userWon) {
		this.userWon = userWon;
		return this;
	}

	@Column(name = "cancelled", precision = 1)
	@Override
	public Boolean getCancelled() {
		return this.cancelled;
	}

	@Override
	public TaskHistoryPojo setCancelled(Boolean cancelled) {
		this.cancelled = cancelled;
		return this;
	}

	@Column(name = "task_stage_id", precision = 10)
	@Override
	public Integer getTaskStageId() {
		return this.taskStageId;
	}

	@Override
	public TaskHistoryPojo setTaskStageId(Integer taskStageId) {
		this.taskStageId = taskStageId;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(ITaskHistory from) {
		setTaskForUserId(from.getTaskForUserId());
		setUserId(from.getUserId());
		setTaskId(from.getTaskId());
		setExpGained(from.getExpGained());
		setCashGained(from.getCashGained());
		setOilGained(from.getOilGained());
		setNumRevives(from.getNumRevives());
		setStartTime(from.getStartTime());
		setEndTime(from.getEndTime());
		setUserWon(from.getUserWon());
		setCancelled(from.getCancelled());
		setTaskStageId(from.getTaskStageId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends ITaskHistory> E into(E into) {
		into.from(this);
		return into;
	}


	public String toString() {
		com.lvl6.mobsters.db.jooq.generated.tables.records.TaskHistoryRecord poop = new com.lvl6.mobsters.db.jooq.generated.tables.records.TaskHistoryRecord();
		poop.from(this);
		return "TaskHistoryPojo[" + poop.valuesRow() + "]";
	}
}
