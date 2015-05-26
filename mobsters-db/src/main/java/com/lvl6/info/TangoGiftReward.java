package com.lvl6.info;

import java.io.Serializable;

public class TangoGiftReward implements Serializable {

	private int id;
	private int tangoGiftId;
	private int rewardId;
	private float chanceOfDrop;

	//variable to assist in randomly selecting this Item
	private float normalizedProbability;

	public TangoGiftReward() {
		super();
	}

	public TangoGiftReward(int id, int tangoGiftId, int rewardId,
			float chanceOfDrop) {
		super();
		this.id = id;
		this.tangoGiftId = tangoGiftId;
		this.rewardId = rewardId;
		this.chanceOfDrop = chanceOfDrop;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTangoGiftId() {
		return tangoGiftId;
	}

	public void setTangoGiftId(int tangoGiftId) {
		this.tangoGiftId = tangoGiftId;
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

	public float getNormalizedProbability() {
		return normalizedProbability;
	}

	public void setNormalizedProbability(float normalizedProbability) {
		this.normalizedProbability = normalizedProbability;
	}

	@Override
	public String toString() {
		return "TangoGiftReward [id=" + id + ", tangoGiftId=" + tangoGiftId
				+ ", rewardId=" + rewardId + ", chanceOfDrop=" + chanceOfDrop
				+ ", normalizedProbability=" + normalizedProbability + "]";
	}

}
