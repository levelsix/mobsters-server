package com.lvl6.info;

import java.io.Serializable;

public class MonsterForUser implements Serializable {

	private static final long serialVersionUID = -8922073042885570264L;
	private long id;
	private int userId;
	private int monsterId;
  private int currentExp;
  private int currentLvl;
  private int currentHealth;
  private int numPieces;
  private boolean isComplete;
  private int teamSlotNum;
  
	public MonsterForUser(long id, int userId, int monsterId, int currentExp,
			int currentLvl, int currentHealth, int numPieces, boolean isComplete,
			int teamSlotNum) {
		super();
		this.id = id;
		this.userId = userId;
		this.monsterId = monsterId;
		this.currentExp = currentExp;
		this.currentLvl = currentLvl;
		this.currentHealth = currentHealth;
		this.numPieces = numPieces;
		this.isComplete = isComplete;
		this.teamSlotNum = teamSlotNum;
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

	public int getTeamSlotNum() {
		return teamSlotNum;
	}

	public void setTeamSlotNum(int teamSlotNum) {
		this.teamSlotNum = teamSlotNum;
	}

	@Override
	public String toString() {
		return "MonsterForUser [id=" + id + ", userId=" + userId + ", monsterId="
				+ monsterId + ", currentExp=" + currentExp + ", currentLvl="
				+ currentLvl + ", currentHealth=" + currentHealth + ", numPieces="
				+ numPieces + ", isComplete=" + isComplete + ", teamSlotNum="
				+ teamSlotNum + "]";
	}
  
}
