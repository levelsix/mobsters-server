package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

//user can have multiple of these (different clanIds)
public class CepfuRaidHistory implements Serializable {
	
	private int userId;
	private Date timeOfEntry;
	private int clanId;
	private int clanEventPersistentId;
	private int crId;
	private int crDmgDone;
	private int clanCrDmg;
	private long userMonsterIdOne;
	private long userMonsterIdTwo;
	private long userMonsterIdThree;
	
	public CepfuRaidHistory(int userId, Date timeOfEntry, int clanId,
			int clanEventPersistentId, int crId, int crDmgDone, int clanCrDmg,
			long userMonsterIdOne, long userMonsterIdTwo, long userMonsterIdThree) {
		super();
		this.userId = userId;
		this.timeOfEntry = timeOfEntry;
		this.clanId = clanId;
		this.clanEventPersistentId = clanEventPersistentId;
		this.crId = crId;
		this.crDmgDone = crDmgDone;
		this.clanCrDmg = clanCrDmg;
		this.userMonsterIdOne = userMonsterIdOne;
		this.userMonsterIdTwo = userMonsterIdTwo;
		this.userMonsterIdThree = userMonsterIdThree;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Date getTimeOfEntry() {
		return timeOfEntry;
	}

	public void setTimeOfEntry(Date timeOfEntry) {
		this.timeOfEntry = timeOfEntry;
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

	public int getCrDmgDone() {
		return crDmgDone;
	}

	public void setCrDmgDone(int crDmgDone) {
		this.crDmgDone = crDmgDone;
	}

	public int getClanCrDmg() {
		return clanCrDmg;
	}

	public void setClanCrDmg(int clanCrDmg) {
		this.clanCrDmg = clanCrDmg;
	}

	public long getUserMonsterIdOne() {
		return userMonsterIdOne;
	}

	public void setUserMonsterIdOne(long userMonsterIdOne) {
		this.userMonsterIdOne = userMonsterIdOne;
	}

	public long getUserMonsterIdTwo() {
		return userMonsterIdTwo;
	}

	public void setUserMonsterIdTwo(long userMonsterIdTwo) {
		this.userMonsterIdTwo = userMonsterIdTwo;
	}

	public long getUserMonsterIdThree() {
		return userMonsterIdThree;
	}

	public void setUserMonsterIdThree(long userMonsterIdThree) {
		this.userMonsterIdThree = userMonsterIdThree;
	}

	@Override
	public String toString() {
		return "CepfuRaidHistory [userId=" + userId + ", timeOfEntry="
				+ timeOfEntry + ", clanId=" + clanId + ", clanEventPersistentId="
				+ clanEventPersistentId + ", crId=" + crId + ", crDmgDone=" + crDmgDone
				+ ", clanCrDmg=" + clanCrDmg + ", userMonsterIdOne=" + userMonsterIdOne
				+ ", userMonsterIdTwo=" + userMonsterIdTwo + ", userMonsterIdThree="
				+ userMonsterIdThree + "]";
	}
	
}
