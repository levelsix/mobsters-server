package com.lvl6.info;

public class ClanGiftRewards {

	private int id;
	private int clanGiftId;
	private int rewardId;
	private float chanceOfDrop;
	public ClanGiftRewards() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ClanGiftRewards(int id, int clanGiftId, int rewardId,
			float chanceOfDrop) {
		super();
		this.id = id;
		this.clanGiftId = clanGiftId;
		this.rewardId = rewardId;
		this.chanceOfDrop = chanceOfDrop;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getClanGiftId() {
		return clanGiftId;
	}
	public void setClanGiftId(int clanGiftId) {
		this.clanGiftId = clanGiftId;
	}
	public int getRewardId() {
		return rewardId;
	}
	public void setRewardId(int rewardId) {
		this.rewardId = rewardId;
	}
	public float getChanceOfDrop() {
		return chanceOfDrop;
	}
	public void setChanceOfDrop(float chanceOfDrop) {
		this.chanceOfDrop = chanceOfDrop;
	}
	@Override
	public String toString() {
		return "ClanGiftRewards [id=" + id + ", clanGiftId=" + clanGiftId
				+ ", rewardId=" + rewardId + ", chanceOfDrop=" + chanceOfDrop
				+ "]";
	}



}
