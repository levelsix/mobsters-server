package com.lvl6.pvp;

import java.io.Serializable;
import java.util.Date;


public class OfflinePvpUser implements Serializable {
	
	private static final long serialVersionUID = 3459023237592127885L;
	private String userId;
	private int elo;
	private Date shieldEndTime;
	
	public OfflinePvpUser(String userId, int elo, Date shieldEndTime) {
		super();
		this.userId = userId;
		this.elo = elo;
		this.shieldEndTime = shieldEndTime;
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

	@Override
	public String toString() {
		return "OfflinePvpUser [userId=" + userId + ", elo=" + elo
				+ ", shieldEndTime=" + shieldEndTime + "]";
	}
	
}