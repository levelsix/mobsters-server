package com.lvl6.info;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ClanEventPersistentForUser implements Serializable {
	
	private static final long serialVersionUID = -2606973933800935109L;
	private int userId;
	private int clanId;
	private int crId;
	private int crDmgDone;
	private int crsId;
	private int crsDmgDone;
	private int crsmId;//primary key in clan raid stage monster
	private int crsmDmgDone;
	private long userMonsterIdOne;
	private long userMonsterIdTwo;
	private long userMonsterIdThree;
	
	public ClanEventPersistentForUser(int userId, int clanId, int crId,
			int crDmgDone, int crsId, int crsDmgDone, int crsmId, int crsmDmgDone,
			long userMonsterIdOne, long userMonsterIdTwo, long userMonsterIdThree) {
		super();
		this.userId = userId;
		this.clanId = clanId;
		this.crId = crId;
		this.crDmgDone = crDmgDone;
		this.crsId = crsId;
		this.crsDmgDone = crsDmgDone;
		this.crsmId = crsmId;
		this.crsmDmgDone = crsmDmgDone;
		this.userMonsterIdOne = userMonsterIdOne;
		this.userMonsterIdTwo = userMonsterIdTwo;
		this.userMonsterIdThree = userMonsterIdThree;
	}
	
	//convenience methods
	public List<Long> getUserMonsterIds() {
		List<Long> userMonsterIds = new ArrayList<Long>();
		
		if (userMonsterIdOne > 0) {
			userMonsterIds.add(userMonsterIdOne);
		}
		if (userMonsterIdTwo > 0) {
			userMonsterIds.add(userMonsterIdTwo);
		}
		if (userMonsterIdThree > 0) {
			userMonsterIds.add(userMonsterIdThree);
		}
		
		return userMonsterIds;
	}
	//---------------------------

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getClanId() {
		return clanId;
	}

	public void setClanId(int clanId) {
		this.clanId = clanId;
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

	public int getCrsmId() {
		return crsmId;
	}

	public void setCrsmId(int crsmId) {
		this.crsmId = crsmId;
	}

	public int getCrsmDmgDone() {
		return crsmDmgDone;
	}

	public void setCrsmDmgDone(int crsmDmgDone) {
		this.crsmDmgDone = crsmDmgDone;
	}

	public long getUserMonsterIdOne() {
		return userMonsterIdOne;
	}

	public void setUserMonsterIdOne(int userMonsterIdOne) {
		this.userMonsterIdOne = userMonsterIdOne;
	}

	public long getUserMonsterIdTwo() {
		return userMonsterIdTwo;
	}

	public void setUserMonsterIdTwo(int userMonsterIdTwo) {
		this.userMonsterIdTwo = userMonsterIdTwo;
	}

	public long getUserMonsterIdThree() {
		return userMonsterIdThree;
	}

	public void setUserMonsterIdThree(int userMonsterIdThree) {
		this.userMonsterIdThree = userMonsterIdThree;
	}

	@Override
	public String toString() {
		return "ClanEventPersistentForUser [userId=" + userId + ", clanId="
				+ clanId + ", crId=" + crId + ", crDmgDone=" + crDmgDone + ", crsId="
				+ crsId + ", crsDmgDone=" + crsDmgDone + ", crsmId=" + crsmId
				+ ", crsmDmgDone=" + crsmDmgDone + ", userMonsterIdOne="
				+ userMonsterIdOne + ", userMonsterIdTwo=" + userMonsterIdTwo
				+ ", userMonsterIdThree=" + userMonsterIdThree + "]";
	}
  
}
