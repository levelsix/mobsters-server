package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

//user can have multiple of these (different clanIds)
public class CepfuRaidHistory implements Serializable {

	private static final long serialVersionUID = 139464147937696023L;

	private String userId;
	private Date timeOfEntry;
	private String clanId;
	private int clanEventPersistentId;
	private int crId;
	private int crDmgDone;
	private int clanCrDmg;
	private String userMonsterIdOne;
	private String userMonsterIdTwo;
	private String userMonsterIdThree;

	public CepfuRaidHistory() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CepfuRaidHistory(String userId, Date timeOfEntry, String clanId,
			int clanEventPersistentId, int crId, int crDmgDone, int clanCrDmg,
			String userMonsterIdOne, String userMonsterIdTwo,
			String userMonsterIdThree) {
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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getTimeOfEntry() {
		return timeOfEntry;
	}

	public void setTimeOfEntry(Date timeOfEntry) {
		this.timeOfEntry = timeOfEntry;
	}

	public String getClanId() {
		return clanId;
	}

	public void setClanId(String clanId) {
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

	public String getUserMonsterIdOne() {
		return userMonsterIdOne;
	}

	public void setUserMonsterIdOne(String userMonsterIdOne) {
		this.userMonsterIdOne = userMonsterIdOne;
	}

	public String getUserMonsterIdTwo() {
		return userMonsterIdTwo;
	}

	public void setUserMonsterIdTwo(String userMonsterIdTwo) {
		this.userMonsterIdTwo = userMonsterIdTwo;
	}

	public String getUserMonsterIdThree() {
		return userMonsterIdThree;
	}

	public void setUserMonsterIdThree(String userMonsterIdThree) {
		this.userMonsterIdThree = userMonsterIdThree;
	}

	@Override
	public String toString() {
		return "CepfuRaidHistory [userId=" + userId + ", timeOfEntry="
				+ timeOfEntry + ", clanId=" + clanId
				+ ", clanEventPersistentId=" + clanEventPersistentId
				+ ", crId=" + crId + ", crDmgDone=" + crDmgDone
				+ ", clanCrDmg=" + clanCrDmg + ", userMonsterIdOne="
				+ userMonsterIdOne + ", userMonsterIdTwo=" + userMonsterIdTwo
				+ ", userMonsterIdThree=" + userMonsterIdThree + "]";
	}

}
