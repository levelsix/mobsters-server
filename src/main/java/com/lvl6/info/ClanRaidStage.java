package com.lvl6.info;

import java.io.Serializable;

//this class is analogous to a task
public class ClanRaidStage implements Serializable {

	private static final long serialVersionUID = -4696619811182312888L;
	private int id;
	private int clanRaidId;
	private int durationMinutes;
	private int stageNum;
	
	public ClanRaidStage(int id, int clanRaidId, int durationMinutes, int stageNum) {
		super();
		this.id = id;
		this.clanRaidId = clanRaidId;
		this.durationMinutes = durationMinutes;
		this.stageNum = stageNum;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getClanRaidId() {
		return clanRaidId;
	}

	public void setClanRaidId(int clanRaidId) {
		this.clanRaidId = clanRaidId;
	}

	public int getDurationMinutes() {
		return durationMinutes;
	}

	public void setDurationMinutes(int durationMinutes) {
		this.durationMinutes = durationMinutes;
	}

	public int getStageNum() {
		return stageNum;
	}

	public void setStageNum(int stageNum) {
		this.stageNum = stageNum;
	}

	@Override
	public String toString() {
		return "ClanRaidStage [id=" + id + ", clanRaidId=" + clanRaidId
				+ ", durationMinutes=" + durationMinutes + ", stageNum=" + stageNum
				+ "]";
	}
	
}
