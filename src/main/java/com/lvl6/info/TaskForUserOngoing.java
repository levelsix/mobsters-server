package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class TaskForUserOngoing implements Serializable {
	
	private static final long serialVersionUID = 8030638296503679697L;
	
	private long id;
	private int userId;
	private int taskId;
	public int expGained;
	public int cashGained;
	public int oilGained;
	public int numRevives;
	private Date startDate;
	private int taskStageId;
	
	public TaskForUserOngoing(long id, int userId, int taskId, int expGained,
			int cashGained, int oilGained, int numRevives, Date startDate,
			int taskStageId) {
		super();
		this.id = id;
		this.userId = userId;
		this.taskId = taskId;
		this.expGained = expGained;
		this.cashGained = cashGained;
		this.oilGained = oilGained;
		this.numRevives = numRevives;
		this.startDate = startDate;
		this.taskStageId = taskStageId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public int getExpGained() {
		return expGained;
	}

	public void setExpGained(int expGained) {
		this.expGained = expGained;
	}

	public int getCashGained() {
		return cashGained;
	}

	public void setCashGained(int cashGained) {
		this.cashGained = cashGained;
	}

	public int getOilGained() {
		return oilGained;
	}

	public void setOilGained(int oilGained) {
		this.oilGained = oilGained;
	}

	public int getNumRevives() {
		return numRevives;
	}

	public void setNumRevives(int numRevives) {
		this.numRevives = numRevives;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public int getTaskStageId() {
		return taskStageId;
	}

	public void setTaskStageId(int taskStageId) {
		this.taskStageId = taskStageId;
	}

	@Override
	public String toString() {
		return "TaskForUserOngoing [id=" + id + ", userId=" + userId
				+ ", taskId=" + taskId + ", expGained=" + expGained
				+ ", cashGained=" + cashGained + ", oilGained=" + oilGained
				+ ", numRevives=" + numRevives + ", startDate=" + startDate
				+ ", taskStageId=" + taskStageId + "]";
	}

}
