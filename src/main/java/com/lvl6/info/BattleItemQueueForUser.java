package com.lvl6.info;

import java.io.Serializable;
import java.sql.Timestamp;

public class BattleItemQueueForUser implements Serializable {

	public BattleItemQueueForUser(int priority, String userId, int battleItemId,
			Timestamp expectedStartTime) {
		super();
		this.priority = priority;
		this.userId = userId;
		this.battleItemId = battleItemId;
		this.expectedStartTime = expectedStartTime;
	}


	private static final long serialVersionUID = -1293698119576984508L;
	
	private int priority;
	private String id;
	private String userId;
	private int battleItemId;
	private Timestamp expectedStartTime;
	
	
	public BattleItemQueueForUser()
	{
		super();
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getBattleItemId() {
		return battleItemId;
	}

	public void setBattleItemId(int battleItemId) {
		this.battleItemId = battleItemId;
	}

	public int getPriority() {
		return priority;
	}
	
	public void setPriority(int priority) {
		this.priority = priority;
	}

	public Timestamp getExpectedStartTime() {
		return expectedStartTime;
	}

	public void setExpectedStartTime(Timestamp expectedStartTime) {
		this.expectedStartTime = expectedStartTime;
	}

	@Override
	public String toString() {
		return "BattleItemQueueForUser [priority=" + priority + ", id=" + id
				+ ", userId=" + userId + ", battleItemId=" + battleItemId
				+ ", expectedStartTime=" + expectedStartTime + "]";
	}


	
	

}
