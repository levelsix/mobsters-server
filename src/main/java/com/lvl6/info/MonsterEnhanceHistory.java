package com.lvl6.info;

import java.sql.Timestamp;
import java.util.List;

public class MonsterEnhanceHistory {

	public MonsterEnhanceHistory(String userId,
			String monsterForUserIdBeingEnhanced,
			List<String> feederMonsterForUserId, int currExp, int prevExp,
			List<Timestamp> enhancingStartTime, Timestamp timeOfEntry,
			int enhancingCost) {
		super();
		this.userId = userId;
		this.monsterForUserIdBeingEnhanced = monsterForUserIdBeingEnhanced;
		this.feederMonsterForUserId = feederMonsterForUserId;
		this.currExp = currExp;
		this.prevExp = prevExp;
		this.enhancingStartTime = enhancingStartTime;
		this.timeOfEntry = timeOfEntry;
		this.enhancingCost = enhancingCost;
	}

	private String userId;
	private String monsterForUserIdBeingEnhanced;
	private List<String> feederMonsterForUserId;
	private int currExp;
	private int prevExp; 
	private List<Timestamp> enhancingStartTime; 
	private Timestamp timeOfEntry;
	private int enhancingCost;

	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getMonsterForUserIdBeingEnhanced() {
		return monsterForUserIdBeingEnhanced;
	}
	
	public void setMonsterForUserIdBeingEnhanced(
			String monsterForUserIdBeingEnhanced) {
		this.monsterForUserIdBeingEnhanced = monsterForUserIdBeingEnhanced;
	}
	
	public List<String> getFeederMonsterForUserId() {
		return feederMonsterForUserId;
	}
	
	public void setFeederMonsterForUserId(List<String> feederMonsterForUserId) {
		this.feederMonsterForUserId = feederMonsterForUserId;
	}
	
	public int getCurrExp() {
		return currExp;
	}
	
	public void setCurrExp(int currExp) {
		this.currExp = currExp;
	}
	
	public int getPrevExp() {
		return prevExp;
	}
	
	public void setPrevExp(int prevExp) {
		this.prevExp = prevExp;
	}
	
	public Timestamp getTimeOfEntry() {
		return timeOfEntry;
	}
	
	public void setTimeOfEntry(Timestamp timeOfEntry) {
		this.timeOfEntry = timeOfEntry;
	}
	
	public int getEnhancingCost() {
		return enhancingCost;
	}
	
	public void setEnhancingCost(int enhancingCost) {
		this.enhancingCost = enhancingCost;
	}
	
	@Override
	public String toString() {
		return "MonsterEnhanceHistory [userId=" + userId
				+ ", monsterForUserIdBeingEnhanced="
				+ monsterForUserIdBeingEnhanced + ", feederMonsterForUserId="
				+ feederMonsterForUserId + ", currExp=" + currExp
				+ ", prevExp=" + prevExp + ", enhancingStartTime="
				+ enhancingStartTime + ", timeOfEntry=" + timeOfEntry
				+ ", enhancingCost=" + enhancingCost + "]";
	}

	public List<Timestamp> getEnhancingStartTime() {
		return enhancingStartTime;
	}

	public void setEnhancingStartTime(List<Timestamp> enhancingStartTime) {
		this.enhancingStartTime = enhancingStartTime;
	}






}
