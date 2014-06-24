package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class MonsterForUser implements Serializable {

	private static final long serialVersionUID = -6261386860780660263L;
	private long id;
	private int userId;
	private int monsterId;
	private int currentExp;
	private int currentLvl;
	private int currentHealth;
	private int numPieces;
	private boolean isComplete;
	private Date combineStartTime;
	private int teamSlotNum;
	private String sourceOfPieces;
  
	public MonsterForUser(long id, int userId, int monsterId, int currentExp,
			int currentLvl, int currentHealth, int numPieces, boolean isComplete,
			Date combineStartTime, int teamSlotNum, String sourceOfPieces) {
		super();
		this.id = id;
		this.userId = userId;
		this.monsterId = monsterId;
		this.currentExp = currentExp;
		this.currentLvl = currentLvl;
		this.currentHealth = currentHealth;
		this.numPieces = numPieces;
		this.isComplete = isComplete;
		this.combineStartTime = combineStartTime;
		this.teamSlotNum = teamSlotNum;
		this.sourceOfPieces = sourceOfPieces;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getMonsterId() {
		return monsterId;
	}

	public void setMonsterId(int monsterId) {
		this.monsterId = monsterId;
	}

	public int getCurrentExp() {
		return currentExp;
	}

	public void setCurrentExp(int currentExp) {
		this.currentExp = currentExp;
	}

	public int getCurrentLvl() {
		return currentLvl;
	}

	public void setCurrentLvl(int currentLvl) {
		this.currentLvl = currentLvl;
	}

	public int getCurrentHealth() {
		return currentHealth;
	}

	public void setCurrentHealth(int currentHealth) {
		this.currentHealth = currentHealth;
	}

	public int getNumPieces() {
		return numPieces;
	}

	public void setNumPieces(int numPieces) {
		this.numPieces = numPieces;
	}

	public boolean isComplete() {
		return isComplete;
	}

	public void setComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}

	public Date getCombineStartTime() {
		return combineStartTime;
	}

	public void setCombineStartTime(Date combineStartTime) {
		this.combineStartTime = combineStartTime;
	}

	public int getTeamSlotNum() {
		return teamSlotNum;
	}

	public void setTeamSlotNum(int teamSlotNum) {
		this.teamSlotNum = teamSlotNum;
	}

	public String getSourceOfPieces() {
		return sourceOfPieces;
	}

	public void setSourceOfPieces(String sourceOfPieces) {
		this.sourceOfPieces = sourceOfPieces;
	}

	@Override
	public String toString() {
		return "MonsterForUser [id=" + id + ", userId=" + userId + ", monsterId="
				+ monsterId + ", currentExp=" + currentExp + ", currentLvl="
				+ currentLvl + ", currentHealth=" + currentHealth + ", numPieces="
				+ numPieces + ", isComplete=" + isComplete + ", combineStartTime="
				+ combineStartTime + ", teamSlotNum=" + teamSlotNum
				+ ", sourceOfPieces=" + sourceOfPieces + "]";
	}
  
}
