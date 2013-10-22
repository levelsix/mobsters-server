package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class MonsterEnhancingForUser implements Serializable {

	private static final long serialVersionUID = -8922073042885570264L;
	private int userId;
	private long monsterForUserId;
  private Date expectedStartTime;
//  private Date queuedTime;
  
  
	public MonsterEnhancingForUser(int userId, long monsterForUserId,
			Date expectedStartTime) {
		super();
		this.userId = userId;
		this.monsterForUserId = monsterForUserId;
		this.expectedStartTime = expectedStartTime;
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


	@Override
	public String toString() {
		return "MonsterHealingForUser [userId=" + userId + ", monsterForUserId="
				+ monsterForUserId + ", expectedStartTime=" + expectedStartTime + "]";
	}
  
}
