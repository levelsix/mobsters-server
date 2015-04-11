package com.lvl6.info;

import java.io.Serializable;

//this class is analogous to a task
public class ClanRaidStage implements Serializable {

	private static final long serialVersionUID = 4353447583250770652L;
	private int id;
	private int clanRaidId;
	private int durationMinutes;
	private int stageNum;
	private String name;

	//sum of all monster healths for this stage
	//not actually a column in the table
	private int stageHealth;

	public ClanRaidStage(int id, int clanRaidId, int durationMinutes,
			int stageNum, String name) {
		super();
		this.id = id;
		this.clanRaidId = clanRaidId;
		this.durationMinutes = durationMinutes;
		this.stageNum = stageNum;
		this.name = name;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStageHealth() {
		return stageHealth;
	}

	public void setStageHealth(int stageHealth) {
		this.stageHealth = stageHealth;
	}

	@Override
	public String toString() {
		return "ClanRaidStage [id=" + id + ", clanRaidId=" + clanRaidId
				+ ", durationMinutes=" + durationMinutes + ", stageNum="
				+ stageNum + ", name=" + name + ", stageHealth=" + stageHealth
				+ "]";
	}

}
