package com.lvl6.pvp;

import java.io.Serializable;
import java.util.Date;


public class PvpUser implements Serializable {
	
	private static final long serialVersionUID = -7603419475310848855L;
	private String userId;
	private int elo;
	private Date shieldEndTime;
	private Date inBattleEndTime;
	
	public PvpUser() {
		super();
		
	}

	public PvpUser(String userId, int elo, Date shieldEndTime,
			Date inBattleEndTime) {
		super();
		this.userId = userId;
		this.elo = elo;
		this.shieldEndTime = shieldEndTime;
		this.inBattleEndTime = inBattleEndTime;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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

	@Override
	public String toString() {
		return "PvpUser [userId=" + userId + ", elo=" + elo
				+ ", shieldEndTime=" + shieldEndTime + ", inBattleEndTime="
				+ inBattleEndTime + "]";
	}
	
}