package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

import com.lvl6.pvp.PvpUser;

public class PvpLeagueForUser implements Serializable {
	
	private static final long serialVersionUID = 1903133289529345252L;
	
	private int userId;
	private int pvpLeagueId;
	private int rank;
	private int elo;
	private Date shieldEndTime;
	//try to prevent multiple people attacking one person. When user is attacked, he gains
	//a temporary shield that prevents him from being selected in pvp
	private Date inBattleShieldEndTime;
	private int attacksWon;
	private int defensesWon;
	private int attacksLost;
	private int defensesLost;
	private Date lastBattleNotificationTime;
	private float monsterDmgMultiplier;
	
	public PvpLeagueForUser() {
		super();
	}

	public PvpLeagueForUser(PvpUser pu) {
		super();
		this.userId = Integer.parseInt(pu.getUserId());
		this.pvpLeagueId = pu.getPvpLeagueId();
		this.rank = pu.getRank();
		this.elo = pu.getElo();
		this.shieldEndTime = pu.getShieldEndTime();
		this.inBattleShieldEndTime = pu.getInBattleEndTime();
		this.attacksWon = pu.getAttacksWon();
		this.defensesWon = pu.getDefensesWon();
		this.attacksLost = pu.getAttacksLost();
		this.defensesLost = pu.getDefensesLost();
		this.monsterDmgMultiplier = pu.getMonsterDmgMultiplier();
	}
	
	public PvpLeagueForUser(PvpLeagueForUser plfu) {
		super();
		this.userId = plfu.getUserId();
		this.pvpLeagueId = plfu.getPvpLeagueId();
		this.rank = plfu.getRank();
		this.elo = plfu.getElo();
		this.shieldEndTime = plfu.getShieldEndTime();
		this.inBattleShieldEndTime = plfu.getInBattleShieldEndTime();
		this.attacksWon = plfu.getAttacksWon();
		this.defensesWon = plfu.getDefensesWon();
		this.attacksLost = plfu.getAttacksLost();
		this.defensesLost = plfu.getDefensesLost();
		this.lastBattleNotificationTime = plfu.getLastBattleNotificationTime();
		this.monsterDmgMultiplier = plfu.getMonsterDmgMultiplier();
	}
	
	public PvpLeagueForUser(int userId, int pvpLeagueId, int rank, int elo,
			Date shieldEndTime, Date inBattleShieldEndTime, int attacksWon,
			int defensesWon, int attacksLost, int defensesLost,
			Date lastBattleNotificationTime, float monsterDmgMultiplier) {
		super();
		this.userId = userId;
		this.pvpLeagueId = pvpLeagueId;
		this.rank = rank;
		this.elo = elo;
		this.shieldEndTime = shieldEndTime;
		this.inBattleShieldEndTime = inBattleShieldEndTime;
		this.attacksWon = attacksWon;
		this.defensesWon = defensesWon;
		this.attacksLost = attacksLost;
		this.defensesLost = defensesLost;
		this.lastBattleNotificationTime = lastBattleNotificationTime;
		this.monsterDmgMultiplier = monsterDmgMultiplier;
	}


	//covenience methods------------------------------------------------------------
	public int getBattlesWon() {
		int battlesWon = getAttacksWon() + getDefensesWon();
		return battlesWon;
	}
	
	public int getBattlesLost() {
		int battlesLost = getAttacksLost() + getDefensesLost();
		return battlesLost;
	}

	public int getNumBattles() {
		int numBattles = getBattlesWon() + getBattlesLost();
		return numBattles;
	}
	//end covenience methods--------------------------------------------------------

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getPvpLeagueId() {
		return pvpLeagueId;
	}

	public void setPvpLeagueId(int pvpLeagueId) {
		this.pvpLeagueId = pvpLeagueId;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public int getElo() {
		return elo;
	}

	public void setElo(int elo) {
		this.elo = elo;
	}

	public Date getShieldEndTime() {
		return shieldEndTime;
	}

	public void setShieldEndTime(Date shieldEndTime) {
		this.shieldEndTime = shieldEndTime;
	}

	public Date getInBattleShieldEndTime() {
		return inBattleShieldEndTime;
	}

	public void setInBattleShieldEndTime(Date inBattleShieldEndTime) {
		this.inBattleShieldEndTime = inBattleShieldEndTime;
	}

	public int getAttacksWon() {
		return attacksWon;
	}

	public void setAttacksWon(int attacksWon) {
		this.attacksWon = attacksWon;
	}

	public int getDefensesWon() {
		return defensesWon;
	}

	public void setDefensesWon(int defensesWon) {
		this.defensesWon = defensesWon;
	}

	public int getAttacksLost() {
		return attacksLost;
	}

	public void setAttacksLost(int attacksLost) {
		this.attacksLost = attacksLost;
	}

	public int getDefensesLost() {
		return defensesLost;
	}

	public void setDefensesLost(int defensesLost) {
		this.defensesLost = defensesLost;
	}

	public Date getLastBattleNotificationTime() {
		return lastBattleNotificationTime;
	}

	public void setLastBattleNotificationTime(Date lastBattleNotificationTime) {
		this.lastBattleNotificationTime = lastBattleNotificationTime;
	}

	public float getMonsterDmgMultiplier()
	{
		return monsterDmgMultiplier;
	}

	public void setMonsterDmgMultiplier( float monsterDmgMultiplier )
	{
		this.monsterDmgMultiplier = monsterDmgMultiplier;
	}

	@Override
	public String toString()
	{
		return "PvpLeagueForUser [userId="
			+ userId
			+ ", pvpLeagueId="
			+ pvpLeagueId
			+ ", rank="
			+ rank
			+ ", elo="
			+ elo
			+ ", shieldEndTime="
			+ shieldEndTime
			+ ", inBattleShieldEndTime="
			+ inBattleShieldEndTime
			+ ", attacksWon="
			+ attacksWon
			+ ", defensesWon="
			+ defensesWon
			+ ", attacksLost="
			+ attacksLost
			+ ", defensesLost="
			+ defensesLost
			+ ", lastBattleNotificationTime="
			+ lastBattleNotificationTime
			+ ", monsterDmgMultiplier="
			+ monsterDmgMultiplier
			+ "]";
	}

}
