package com.lvl6.info;

import java.io.Serializable;

public class EventPersistent implements Serializable {

	private static final long serialVersionUID = 1511718617355339151L;
	private int id;
	private String dayOfWeek;
	private int startHour;
	private int eventDurationMinutes;
	private int taskId;
	private int cooldownMinutes;
	private String eventType;
	private String monsterElement;

	public EventPersistent(int id, String dayOfWeek, int startHour,
			int eventDurationMinutes, int taskId, int cooldownMinutes,
			String eventType, String monsterElement) {
		super();
		this.id = id;
		this.dayOfWeek = dayOfWeek;
		this.startHour = startHour;
		this.eventDurationMinutes = eventDurationMinutes;
		this.taskId = taskId;
		this.cooldownMinutes = cooldownMinutes;
		this.eventType = eventType;
		this.monsterElement = monsterElement;
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

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getMonsterElement() {
		return monsterElement;
	}

	public void setMonsterElement(String monsterElement) {
		this.monsterElement = monsterElement;
	}

	@Override
	public String toString() {
		return "EventPersistent [id=" + id + ", dayOfWeek=" + dayOfWeek
				+ ", startHour=" + startHour + ", eventDurationMinutes="
				+ eventDurationMinutes + ", taskId=" + taskId
				+ ", cooldownMinutes=" + cooldownMinutes + ", eventType="
				+ eventType + ", monsterElement=" + monsterElement + "]";
	}

}
