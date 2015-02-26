package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class EventPersistentForUser implements Serializable {
	
	private static final long serialVersionUID = 385081006377018102L;
	
	private String userId;
	private int eventPersistentId;
	private Date timeOfEntry; // refers to time user started a daily event

	public EventPersistentForUser()
	{
		super();
	}

	public EventPersistentForUser(String userId, int eventPersistentId,
			Date timeOfEntry) {
		super();
		this.userId = userId;
		this.eventPersistentId = eventPersistentId;
		this.timeOfEntry = timeOfEntry;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getEventPersistentId() {
		return eventPersistentId;
	}

	public void setEventPersistentId(int eventPersistentId) {
		this.eventPersistentId = eventPersistentId;
	}

	public Date getTimeOfEntry() {
		return timeOfEntry;
	}

	public void setTimeOfEntry(Date timeOfEntry) {
		this.timeOfEntry = timeOfEntry;
	}

	@Override
	public String toString() {
		return "EventPersistentForUser [userId=" + userId + ", eventPersistentId="
				+ eventPersistentId + ", timeOfEntry=" + timeOfEntry + "]";
	}
 
}
