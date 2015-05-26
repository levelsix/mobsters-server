package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class MonsterForUser implements Serializable {

	private static final long serialVersionUID = -8786371005426860510L;

	private String id;
	private String userId;
	private int monsterId;
	private int currentExp;
	private int currentLvl;
	private int currentHealth;
	private int numPieces;
	private boolean hasAllPieces;
	private boolean isComplete;
	private Date combineStartTime;
	private int teamSlotNum;
	private String sourceOfPieces;
	private boolean restricted;
	private int offensiveSkillId;
	private int defensiveSkillId;

	public MonsterForUser() {
		super();
	}

	public MonsterForUser(String id, String userId, int monsterId,
			int currentExp, int currentLvl, int currentHealth, int numPieces,
			boolean hasAllPieces, boolean isComplete, Date combineStartTime,
			int teamSlotNum, String sourceOfPieces, boolean restricted,
			int offensiveSkillId, int defensiveSkillId) {
		super();
		this.id = id;
		this.userId = userId;
		this.monsterId = monsterId;
		this.currentExp = currentExp;
		this.currentLvl = currentLvl;
		this.currentHealth = currentHealth;
		this.numPieces = numPieces;
		this.hasAllPieces = hasAllPieces;
		this.isComplete = isComplete;
		this.combineStartTime = combineStartTime;
		this.teamSlotNum = teamSlotNum;
		this.sourceOfPieces = sourceOfPieces;
		this.restricted = restricted;
		this.offensiveSkillId = offensiveSkillId;
		this.defensiveSkillId = defensiveSkillId;
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

	public boolean isHasAllPieces() {
		return hasAllPieces;
	}

	public void setHasAllPieces(boolean hasAllPieces) {
		this.hasAllPieces = hasAllPieces;
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

	public boolean isRestricted() {
		return restricted;
	}

	public void setRestricted(boolean restricted) {
		this.restricted = restricted;
	}

	public int getOffensiveSkillId() {
		return offensiveSkillId;
	}

	public void setOffensiveSkillId(int offensiveSkillId) {
		this.offensiveSkillId = offensiveSkillId;
	}

	public int getDefensiveSkillId() {
		return defensiveSkillId;
	}

	public void setDefensiveSkillId(int defensiveSkillId) {
		this.defensiveSkillId = defensiveSkillId;
	}

	@Override
	public String toString() {
		return "MonsterForUser [id=" + id + ", userId=" + userId
				+ ", monsterId=" + monsterId + ", currentExp=" + currentExp
				+ ", currentLvl=" + currentLvl + ", currentHealth="
				+ currentHealth + ", numPieces=" + numPieces
				+ ", hasAllPieces=" + hasAllPieces + ", isComplete="
				+ isComplete + ", combineStartTime=" + combineStartTime
				+ ", teamSlotNum=" + teamSlotNum + ", sourceOfPieces="
				+ sourceOfPieces + ", restricted=" + restricted
				+ ", offensiveSkillId=" + offensiveSkillId
				+ ", defensiveSkillId=" + defensiveSkillId + "]";
	}

}
