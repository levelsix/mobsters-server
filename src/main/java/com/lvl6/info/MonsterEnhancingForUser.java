package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class MonsterEnhancingForUser implements Serializable {

	private String userId;
	private String monsterForUserId;
	private Date expectedStartTime;
//	private Date queuedTime;
	private int enhancingCost;
	private boolean enhancingComplete;
  
	public MonsterEnhancingForUser()
	{
		super();
	}

	public MonsterEnhancingForUser(String userId, String monsterForUserId,
		Date expectedStartTime, int enhancingCost, boolean enhancingComplete)
	{
		super();
		this.userId = userId;
		this.monsterForUserId = monsterForUserId;
		this.expectedStartTime = expectedStartTime;
		this.enhancingCost = enhancingCost;
		this.enhancingComplete = enhancingComplete;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getMonsterForUserId() {
		return monsterForUserId;
	}

	public void setMonsterForUserId(String monsterForUserId) {
		this.monsterForUserId = monsterForUserId;
	}

	public Date getExpectedStartTime() {
		return expectedStartTime;
	}

	public void setExpectedStartTime(Date expectedStartTime) {
		this.expectedStartTime = expectedStartTime;
	}

	public int getEnhancingCost() {
		return enhancingCost;
	}

	public void setEnhancingCost(int enhancingCost) {
		this.enhancingCost = enhancingCost;
	}

	public boolean isEnhancingComplete()
	{
		return enhancingComplete;
	}

	public void setEnhancingComplete( boolean enhancingComplete )
	{
		this.enhancingComplete = enhancingComplete;
	}

	@Override
	public String toString()
	{
		return "MonsterEnhancingForUser [userId="
			+ userId
			+ ", monsterForUserId="
			+ monsterForUserId
			+ ", expectedStartTime="
			+ expectedStartTime
			+ ", enhancingCost="
			+ enhancingCost
			+ ", enhancingComplete="
			+ enhancingComplete
			+ "]";
	}
	
}
