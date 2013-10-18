package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class MonsterHealingForUser implements Serializable {

	private static final long serialVersionUID = -8922073042885570264L;
	private int userId;
	private long monsterForUserId;
  private Date expectedStartTime;
  private Date queuedTime;
  
	public MonsterHealingForUser(int userId, long monsterForUserId,
			Date expectedStartTime, Date queuedTime) {
		super();
		this.userId = userId;
		this.monsterForUserId = monsterForUserId;
		this.expectedStartTime = expectedStartTime;
		this.queuedTime = queuedTime;
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

	public Date getQueuedTime() {
		return queuedTime;
	}

	public void setQueuedTime(Date queuedTime) {
		this.queuedTime = queuedTime;
	}

	@Override
	public String toString() {
		return "MonsterHealingForUser [userId=" + userId + ", monsterForUserId="
				+ monsterForUserId + ", expectedStartTime=" + expectedStartTime
				+ ", queuedTime=" + queuedTime + "]";
	}

}
