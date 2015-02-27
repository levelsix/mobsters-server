package com.lvl6.info;

import java.io.Serializable;
import java.sql.Timestamp;

public class BattleItemQueueForUser implements Serializable {

	public BattleItemQueueForUser(String userId, int battleItemId,
			Timestamp queuedTime, int priority) {
		super();
		this.userId = userId;
		this.battleItemId = battleItemId;
		this.queuedTime = queuedTime;
		this.priority = priority;
	}


	private static final long serialVersionUID = -1293698119576984508L;
	
	private String userId;
	private int battleItemId;
	private Timestamp queuedTime;
	private int priority;
	
	
	public BattleItemQueueForUser()
	{
		super();
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


	public Timestamp getQueuedTime() {
		return queuedTime;
	}


	public void setQueuedTime(Timestamp queuedTime) {
		this.queuedTime = queuedTime;
	}


	public int getPriority() {
		return priority;
	}


	public void setPriority(int priority) {
		this.priority = priority;
	}


	@Override
	public String toString() {
		return "BattleItemQueueForUser [userId=" + userId + ", battleItemId="
				+ battleItemId + ", queuedTime=" + queuedTime + ", priority="
				+ priority + "]";
	}

	
	
	

}
