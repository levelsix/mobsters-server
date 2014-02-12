package com.lvl6.pvp;

import java.io.Serializable;
import java.util.Date;


public class OfflinePvpUser implements Serializable {
	
	private static final long serialVersionUID = 6723168586598615893L;
	private String userId;
	private int elo;
	private Date shieldEndTime;
	private Date inBattleShieldEndTime;
	
	public OfflinePvpUser(String userId, int elo, Date shieldEndTime,
			Date inBattleShieldEndTime) {
		super();
		this.userId = userId;
		this.elo = elo;
		this.shieldEndTime = shieldEndTime;
		this.inBattleShieldEndTime = inBattleShieldEndTime;
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

	public Date getInBattleShieldEndTime() {
		return inBattleShieldEndTime;
	}

	public void setInBattleShieldEndTime(Date inBattleShieldEndTime) {
		this.inBattleShieldEndTime = inBattleShieldEndTime;
	}

	@Override
	public String toString() {
		return "OfflinePvpUser [userId=" + userId + ", elo=" + elo
				+ ", shieldEndTime=" + shieldEndTime + ", inBattleShieldEndTime="
				+ inBattleShieldEndTime + "]";
	}
	
}