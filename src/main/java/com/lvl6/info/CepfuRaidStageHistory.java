package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

//user can have multiple of these (different clanIds)
public class CepfuRaidStageHistory implements Serializable {
	
	private static final long serialVersionUID = -3437234734957993022L;
	private int userId;
	private Date crsStartTime;
	private int clanId;
	private int clanEventPersistentId;
	private int crId;
	private int crsId;
	private int crsDmgDone;
	private int stageHealth;
	private Date crsEndTime;
	
	public CepfuRaidStageHistory(int userId, Date crsStartTime, int clanId,
			int clanEventPersistentId, int crId, int crsId, int crsDmgDone,
			int stageHealth, Date crsEndTime) {
		super();
		this.userId = userId;
		this.crsStartTime = crsStartTime;
		this.clanId = clanId;
		this.clanEventPersistentId = clanEventPersistentId;
		this.crId = crId;
		this.crsId = crsId;
		this.crsDmgDone = crsDmgDone;
		this.stageHealth = stageHealth;
		this.crsEndTime = crsEndTime;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Date getCrsStartTime() {
		return crsStartTime;
	}

	public void setCrsStartTime(Date crsStartTime) {
		this.crsStartTime = crsStartTime;
	}

	public int getClanId() {
		return clanId;
	}

	public void setClanId(int clanId) {
		this.clanId = clanId;
	}

	public int getClanEventPersistentId() {
		return clanEventPersistentId;
	}

	public void setClanEventPersistentId(int clanEventPersistentId) {
		this.clanEventPersistentId = clanEventPersistentId;
	}

	public int getCrId() {
		return crId;
	}

	public void setCrId(int crId) {
		this.crId = crId;
	}

	public int getCrsId() {
		return crsId;
	}

	public void setCrsId(int crsId) {
		this.crsId = crsId;
	}

	public int getCrsDmgDone() {
		return crsDmgDone;
	}

	public void setCrsDmgDone(int crsDmgDone) {
		this.crsDmgDone = crsDmgDone;
	}

	public int getStageHealth() {
		return stageHealth;
	}

	public void setStageHealth(int stageHealth) {
		this.stageHealth = stageHealth;
	}

	public Date getCrsEndTime() {
		return crsEndTime;
	}

	public void setCrsEndTime(Date crsEndTime) {
		this.crsEndTime = crsEndTime;
	}

	@Override
	public String toString() {
		return "CepfuRaidStageHistory [userId=" + userId + ", crsStartTime="
				+ crsStartTime + ", clanId=" + clanId + ", clanEventPersistentId="
				+ clanEventPersistentId + ", crId=" + crId + ", crsId=" + crsId
				+ ", crsDmgDone=" + crsDmgDone + ", stageHealth=" + stageHealth
				+ ", crsEndTime=" + crsEndTime + "]";
		
	}
}
