package com.lvl6.info;

import java.io.Serializable;

public class PvpLeagueForUser implements Serializable {
	
	private static final long serialVersionUID = -6224873308191642535L;
	
	private int userId;
	private int pvpLeagueId;
	private int rank;
	private int elo;
	
	public PvpLeagueForUser() {
		super();
	}

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

	@Override
	public String toString() {
		return "PvpLeagueForUser [userId=" + userId + ", pvpLeagueId="
				+ pvpLeagueId + ", rank=" + rank + ", elo=" + elo + "]";
	}

}
