package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class ClanEventPersistentForClan implements Serializable {
	
	private static final long serialVersionUID = -8806794346949184851L;
	private int clanId;
  private int clanEventPersistentId; //not really needed, but oh well
  private int crId; //clan raid id
  private int crsId; //clan raid stage id
  private Date stageStartTime; // refers to time clan started a daily event
  private int crsmId; //clan raid stage monster id
  private Date stageMonsterStartTime; //differentiate attacks across different stage monsters
  
	public ClanEventPersistentForClan(int clanId, int clanEventPersistentId,
			int crId, int crsId, Date stageStartTime, int crsmId,
			Date stageMonsterStartTime) {
		super();
		this.clanId = clanId;
		this.clanEventPersistentId = clanEventPersistentId;
		this.crId = crId;
		this.crsId = crsId;
		this.stageStartTime = stageStartTime;
		this.crsmId = crsmId;
		this.stageMonsterStartTime = stageMonsterStartTime;
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

	public Date getStageStartTime() {
		return stageStartTime;
	}

	public void setStageStartTime(Date stageStartTime) {
		this.stageStartTime = stageStartTime;
	}

	public int getCrsmId() {
		return crsmId;
	}

	public void setCrsmId(int crsmId) {
		this.crsmId = crsmId;
	}

	public Date getStageMonsterStartTime() {
		return stageMonsterStartTime;
	}

	public void setStageMonsterStartTime(Date stageMonsterStartTime) {
		this.stageMonsterStartTime = stageMonsterStartTime;
	}

	@Override
	public String toString() {
		return "ClanEventPersistentForClan [clanId=" + clanId
				+ ", clanEventPersistentId=" + clanEventPersistentId + ", crId=" + crId
				+ ", crsId=" + crsId + ", stageStartTime=" + stageStartTime
				+ ", crsmId=" + crsmId + ", stageMonsterStartTime="
				+ stageMonsterStartTime + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClanEventPersistentForClan other = (ClanEventPersistentForClan) obj;
		if (clanEventPersistentId != other.clanEventPersistentId)
			return false;
		if (clanId != other.clanId)
			return false;
		if (crId != other.crId)
			return false;
		if (crsId != other.crsId)
			return false;
		if (crsmId != other.crsmId)
			return false;
		if (stageMonsterStartTime == null) {
			if (other.stageMonsterStartTime != null)
				return false;
		} else if (!stageMonsterStartTime.equals(other.stageMonsterStartTime))
			return false;
		if (stageStartTime == null) {
			if (other.stageStartTime != null)
				return false;
		} else if (!stageStartTime.equals(other.stageStartTime))
			return false;
		return true;
	}
	
}
