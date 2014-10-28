package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class MonsterEnhancingForUser implements Serializable {

	private static final long serialVersionUID = -8422312968703378208L;
	
	private int userId;
	private long monsterForUserId;
	private Date expectedStartTime;
//	private Date queuedTime;
	private int enhancingCost;
	private boolean enhancingComplete;
  
	public MonsterEnhancingForUser(int userId, long monsterForUserId,
		Date expectedStartTime, int enhancingCost, boolean enhancingComplete)
	{
		super();
		this.userId = userId;
		this.monsterForUserId = monsterForUserId;
		this.expectedStartTime = expectedStartTime;
		this.enhancingCost = enhancingCost;
		this.enhancingComplete = enhancingComplete;
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
