package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class TaskForUser implements Serializable {

	private static final long serialVersionUID = -3612315843594872687L;
	private long id;
	private int userId;
	private int taskId;
	public int expGained;
	public int silverGained;
	public int numRevives;
	private Date startDate;
	
	public TaskForUser(long id, int userId, int taskId, int expGained,
			int silverGained, int numRevives, Date startDate) {
		super();
		this.id = id;
		this.userId = userId;
		this.taskId = taskId;
		this.expGained = expGained;
		this.silverGained = silverGained;
		this.numRevives = numRevives;
		this.startDate = startDate;
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

	public int getSilverGained() {
		return silverGained;
	}

	public void setSilverGained(int silverGained) {
		this.silverGained = silverGained;
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

	@Override
	public String toString() {
		return "TaskForUser [id=" + id + ", userId=" + userId + ", taskId="
				+ taskId + ", expGained=" + expGained + ", silverGained="
				+ silverGained + ", numRevives=" + numRevives + ", startDate="
				+ startDate + "]";
	}
	
}
