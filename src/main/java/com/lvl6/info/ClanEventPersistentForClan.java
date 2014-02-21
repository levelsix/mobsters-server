package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class ClanEventPersistentForClan implements Serializable {
	
	private static final long serialVersionUID = 3321531217790714106L;
	private int clanId;
  private int eventPersistentId;
  private Date timeOfEntry; // refers to time clan started a daily event
  
	public ClanEventPersistentForClan(int clanId, int eventPersistentId,
			Date timeOfEntry) {
		super();
		this.clanId = clanId;
		this.eventPersistentId = eventPersistentId;
		this.timeOfEntry = timeOfEntry;
	}

	public int getClanId() {
		return clanId;
	}

	public void setClanId(int clanId) {
		this.clanId = clanId;
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
		return "ClanEventPersistentForClan [clanId=" + clanId
				+ ", eventPersistentId=" + eventPersistentId + ", timeOfEntry="
				+ timeOfEntry + "]";
	}
 
}
