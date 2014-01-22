package com.lvl6.info;

import java.io.Serializable;

public class EventPersistent implements Serializable {
	
	private static final long serialVersionUID = 9544541492861697L;
	private int id;
	private String dayOfWeek;
	private int startHour;
	private int eventDurationMinutes;
	private int taskId;
	private int cooldownMinutes;
	
	public EventPersistent(int id, String dayOfWeek, int startHour,
			int eventDurationMinutes, int taskId, int cooldownMinutes) {
		super();
		this.id = id;
		this.dayOfWeek = dayOfWeek;
		this.startHour = startHour;
		this.eventDurationMinutes = eventDurationMinutes;
		this.taskId = taskId;
		this.cooldownMinutes = cooldownMinutes;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public int getStartHour() {
		return startHour;
	}

	public void setStartHour(int startHour) {
		this.startHour = startHour;
	}

	public int getEventDurationMinutes() {
		return eventDurationMinutes;
	}

	public void setEventDurationMinutes(int eventDurationMinutes) {
		this.eventDurationMinutes = eventDurationMinutes;
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public int getCooldownMinutes() {
		return cooldownMinutes;
	}

	public void setCooldownMinutes(int cooldownMinutes) {
		this.cooldownMinutes = cooldownMinutes;
	}

	@Override
	public String toString() {
		return "EventPersistent [id=" + id + ", dayOfWeek=" + dayOfWeek
				+ ", startHour=" + startHour + ", eventDurationMinutes="
				+ eventDurationMinutes + ", taskId=" + taskId + ", cooldownMinutes="
				+ cooldownMinutes + "]";
	}
	
	//makes it easier to indicate what type of event this is
//	private String eventType;
		
}
