package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class MonsterForUser implements Serializable {

	private static final long serialVersionUID = 1035959159590035087L;
	
	private long id;
	private int userId;
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
  
	public MonsterForUser(long id, int userId, int monsterId, int currentExp,
			int currentLvl, int currentHealth, int numPieces, boolean hasAllPieces,
			boolean isComplete, Date combineStartTime, int teamSlotNum,
			String sourceOfPieces, boolean restricted) {
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

	public boolean isHasAllPieces()
	{
		return hasAllPieces;
	}

	public void setHasAllPieces( boolean hasAllPieces )
	{
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

	public boolean isRestricted()
	{
		return restricted;
	}

	public void setRestricted( boolean restricted )
	{
		this.restricted = restricted;
	}

	@Override
	public String toString()
	{
		return "MonsterForUser [id="
			+ id
			+ ", userId="
			+ userId
			+ ", monsterId="
			+ monsterId
			+ ", currentExp="
			+ currentExp
			+ ", currentLvl="
			+ currentLvl
			+ ", currentHealth="
			+ currentHealth
			+ ", numPieces="
			+ numPieces
			+ ", hasAllPieces="
			+ hasAllPieces
			+ ", isComplete="
			+ isComplete
			+ ", combineStartTime="
			+ combineStartTime
			+ ", teamSlotNum="
			+ teamSlotNum
			+ ", sourceOfPieces="
			+ sourceOfPieces
			+ ", restricted="
			+ restricted
			+ "]";
	}

}
