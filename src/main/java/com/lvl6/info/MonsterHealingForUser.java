package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class MonsterHealingForUser implements Serializable {

	private static final long serialVersionUID = -2979320446074707533L;
	private int userId;
	private long monsterForUserId;
  private Date queuedTime;
//  private int userStructHospitalId;
  private float healthProgress;
  private int priority;
  
	public MonsterHealingForUser(int userId, long monsterForUserId,
			Date queuedTime, float healthProgress, int priority) {
		super();
		this.userId = userId;
		this.monsterForUserId = monsterForUserId;
		this.queuedTime = queuedTime;
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

	public Date getQueuedTime() {
		return queuedTime;
	}

	public void setQueuedTime(Date queuedTime) {
		this.queuedTime = queuedTime;
	}

	public float getHealthProgress() {
		return healthProgress;
	}

	public void setHealthProgress(float healthProgress) {
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
				+ monsterForUserId + ", queuedTime=" + queuedTime + ", healthProgress="
				+ healthProgress + ", priority=" + priority + "]";
	}
  
}
