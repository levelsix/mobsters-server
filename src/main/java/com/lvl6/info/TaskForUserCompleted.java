package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class TaskForUserCompleted implements Serializable {

	private static final long serialVersionUID = 1056343725813049903L;
	private int userId;
	private int taskId;
	private Date timeOfEntry;
	
	public TaskForUserCompleted(int userId, int taskId, Date timeOfEntry) {
		super();
		this.userId = userId;
		this.taskId = taskId;
		this.timeOfEntry = timeOfEntry;
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

	public Date getTimeOfEntry() {
		return timeOfEntry;
	}

	public void setTimeOfEntry(Date timeOfEntry) {
		this.timeOfEntry = timeOfEntry;
	}

	@Override
	public String toString() {
		return "TaskForUserCompleted [userId=" + userId + ", taskId=" + taskId
				+ ", timeOfEntry=" + timeOfEntry + "]";
	}

}
