package com.lvl6.info;

import java.io.Serializable;

public class ClanRaidStageReward implements Serializable {
	//only oil, cash, or monster stuff may be set at on time. The three are mutually
	//exclusive
	
	private static final long serialVersionUID = -4986817946989780266L;
	private int id;
	private int clanRaidStageId;
	private int minOilReward;
	private int maxOilReward;
	private int minCashReward;
	private int maxCashReward;
	private int monsterId;
	private int expectedMonsterRewardQuantity;
	
	public ClanRaidStageReward(int id, int clanRaidStageId, int minOilReward,
			int maxOilReward, int minCashReward, int maxCashReward, int monsterId,
			int expectedMonsterRewardQuantity) {
		super();
		this.id = id;
		this.clanRaidStageId = clanRaidStageId;
		this.minOilReward = minOilReward;
		this.maxOilReward = maxOilReward;
		this.minCashReward = minCashReward;
		this.maxCashReward = maxCashReward;
		this.monsterId = monsterId;
		this.expectedMonsterRewardQuantity = expectedMonsterRewardQuantity;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getClanRaidStageId() {
		return clanRaidStageId;
	}

	public void setClanRaidStageId(int clanRaidStageId) {
		this.clanRaidStageId = clanRaidStageId;
	}

	public int getMinOilReward() {
		return minOilReward;
	}

	public void setMinOilReward(int minOilReward) {
		this.minOilReward = minOilReward;
	}

	public int getMaxOilReward() {
		return maxOilReward;
	}

	public void setMaxOilReward(int maxOilReward) {
		this.maxOilReward = maxOilReward;
	}

	public int getMinCashReward() {
		return minCashReward;
	}

	public void setMinCashReward(int minCashReward) {
		this.minCashReward = minCashReward;
	}

	public int getMaxCashReward() {
		return maxCashReward;
	}

	public void setMaxCashReward(int maxCashReward) {
		this.maxCashReward = maxCashReward;
	}

	public int getMonsterId() {
		return monsterId;
	}

	public void setMonsterId(int monsterId) {
		this.monsterId = monsterId;
	}

	public int getExpectedMonsterRewardQuantity() {
		return expectedMonsterRewardQuantity;
	}

	public void setExpectedMonsterRewardQuantity(int expectedMonsterRewardQuantity) {
		this.expectedMonsterRewardQuantity = expectedMonsterRewardQuantity;
	}

	@Override
	public String toString() {
		return "ClanRaidStageReward [id=" + id + ", clanRaidStageId="
				+ clanRaidStageId + ", minOilReward=" + minOilReward
				+ ", maxOilReward=" + maxOilReward + ", minCashReward=" + minCashReward
				+ ", maxCashReward=" + maxCashReward + ", monsterId=" + monsterId
				+ ", expectedMonsterRewardQuantity=" + expectedMonsterRewardQuantity
				+ "]";
	}
	
}
