package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class MonsterHealingForUser implements Serializable {

	private static final long serialVersionUID = -4500932968776459991L;
	
	private int userId;
	private long monsterForUserId;
  private Date expectedStartTime;
  private int userStructHospitalId;
  private int healthProgress;
  private int priority;
//  private Date queuedTime;
  
	public MonsterHealingForUser(int userId, long monsterForUserId,
			Date expectedStartTime, int userStructHospitalId, int healthProgress,
			int priority) {
		super();
		this.userId = userId;
		this.monsterForUserId = monsterForUserId;
		this.expectedStartTime = expectedStartTime;
		this.userStructHospitalId = userStructHospitalId;
		this.healthProgress = healthProgress;
		this.priority = priority;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public long getMonsterForUserId() {
		return monsterForUserId;
	}

	public void setMonsterForUserId(long monsterForUserId) {
		this.monsterForUserId = monsterForUserId;
	}

	public Date getExpectedStartTime() {
		return expectedStartTime;
	}

	public void setExpectedStartTime(Date expectedStartTime) {
		this.expectedStartTime = expectedStartTime;
	}

	public int getUserStructHospitalId() {
		return userStructHospitalId;
	}

	public void setUserStructHospitalId(int userStructHospitalId) {
		this.userStructHospitalId = userStructHospitalId;
	}

	public int getHealthProgress() {
		return healthProgress;
	}

	public void setHealthProgress(int healthProgress) {
		this.healthProgress = healthProgress;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}


	@Override
	public String toString() {
		return "MonsterHealingForUser [userId=" + userId + ", monsterForUserId="
				+ monsterForUserId + ", expectedStartTime=" + expectedStartTime
				+ ", userStructHospitalId=" + userStructHospitalId
				+ ", healthProgress=" + healthProgress + ", priority=" + priority + "]";
	}
  
}
