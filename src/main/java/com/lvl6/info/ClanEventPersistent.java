package com.lvl6.info;

import java.io.Serializable;

public class ClanEventPersistent implements Serializable {
	
	private static final long serialVersionUID = -3881447951245558541L;
	private int id;
	private String dayOfWeek;
	private int startHour;
	private int eventDurationMinutes;
	private int clanRaidId;
	
	public ClanEventPersistent(int id, String dayOfWeek, int startHour,
			int eventDurationMinutes, int clanRaidId) {
		super();
		this.id = id;
		this.dayOfWeek = dayOfWeek;
		this.startHour = startHour;
		this.eventDurationMinutes = eventDurationMinutes;
		this.clanRaidId = clanRaidId;
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

	public int getClanRaidId() {
		return clanRaidId;
	}

	public void setClanRaidId(int clanRaidId) {
		this.clanRaidId = clanRaidId;
	}

	@Override
	public String toString() {
		return "ClanEventPersistent [id=" + id + ", dayOfWeek=" + dayOfWeek
				+ ", startHour=" + startHour + ", eventDurationMinutes="
				+ eventDurationMinutes + ", clanRaidId=" + clanRaidId + "]";
	}
	
}
