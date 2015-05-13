package com.lvl6.info;

public class StrengthLeaderBoard {

	private int rank;
	private String userId;
	private long strength;
	
	public StrengthLeaderBoard(int rank, String userId, long strength) {
		super();
		this.rank = rank;
		this.userId = userId;
		this.strength = strength;
	}
	
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public long getStrength() {
		return strength;
	}
	public void setStrength(long strength) {
		this.strength = strength;
	}
	
	@Override
	public String toString() {
		return "StrengthLeaderBoard [rank=" + rank + ", userId=" + userId
				+ ", strength=" + strength + "]";
	}
	
	
	
}
