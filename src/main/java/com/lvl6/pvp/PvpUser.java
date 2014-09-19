package com.lvl6.pvp;

import java.io.Serializable;
import java.util.Date;

import com.lvl6.info.PvpLeagueForUser;

//almost the exact same as PvpLeagueForUser except the userId is a string and
//last battle notification time is not present
//reasons for using separate class instead of using PvpLeagueForUser object:
//1) userId is a string (not a big reason really)
//2) maybe data in PvpLeagueForUser is extraneous and less is needed when
//storing in hazelcast
public class PvpUser implements Serializable {
	
	private static final long serialVersionUID = 6639036676061529713L;
	
	private String userId;
	private int pvpLeagueId;
	private int rank;
	private int elo;
	private Date shieldEndTime;
	private Date inBattleEndTime;
	private int attacksWon;
	private int defensesWon;
	private int attacksLost;
	private int defensesLost;
	private float monsterDmgMultiplier;

	public PvpUser() {
		super();
		
	}
	public PvpUser(PvpLeagueForUser plfu) {
		super();
		String userId = Integer.toString(plfu.getUserId());
		this.userId = userId;
		this.pvpLeagueId = plfu.getPvpLeagueId();
		this.rank = plfu.getRank();
		this.elo = plfu.getElo();
		this.shieldEndTime = plfu.getShieldEndTime();
		this.inBattleEndTime = plfu.getInBattleShieldEndTime();
		this.attacksWon = plfu.getAttacksWon();
		this.defensesWon = plfu.getDefensesWon();
		this.attacksLost = plfu.getAttacksLost();
		this.defensesLost = plfu.getDefensesLost();
		this.monsterDmgMultiplier = plfu.getMonsterDmgMultiplier();
	}

	public PvpUser(String userId, int pvpLeagueId, int rank, int elo,
			Date shieldEndTime, Date inBattleEndTime, int attacksWon,
			int defensesWon, int attacksLost, int defensesLost,
			float monsterDmgMultiplier) {
		super();
		this.userId = userId;
		this.pvpLeagueId = pvpLeagueId;
		this.rank = rank;
		this.elo = elo;
		this.shieldEndTime = shieldEndTime;
		this.inBattleEndTime = inBattleEndTime;
		this.attacksWon = attacksWon;
		this.defensesWon = defensesWon;
		this.attacksLost = attacksLost;
		this.defensesLost = defensesLost;
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
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
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

	public Date getInBattleEndTime() {
		return inBattleEndTime;
	}

	public void setInBattleEndTime(Date inBattleEndTime) {
		this.inBattleEndTime = inBattleEndTime;
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
		return "PvpUser [userId="
			+ userId
			+ ", pvpLeagueId="
			+ pvpLeagueId
			+ ", rank="
			+ rank
			+ ", elo="
			+ elo
			+ ", shieldEndTime="
			+ shieldEndTime
			+ ", inBattleEndTime="
			+ inBattleEndTime
			+ ", attacksWon="
			+ attacksWon
			+ ", defensesWon="
			+ defensesWon
			+ ", attacksLost="
			+ attacksLost
			+ ", defensesLost="
			+ defensesLost
			+ ", monsterDmgMultiplier="
			+ monsterDmgMultiplier
			+ "]";
	}
	
}
