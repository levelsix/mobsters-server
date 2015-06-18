package com.lvl6.info;

import java.io.Serializable;

public class SecretGift implements Serializable {

	private static final long serialVersionUID = 5395077554511176383L;

	private int id;
	private int rewardId;
	private float chanceToBeSelected;

	//variable to assist in randomly selecting this Item
	private float normalizedSecretGiftProbability;

	public SecretGift() {
		super();
	}

	public SecretGift(int id, int rewardId, float chanceToBeSelected) {
		super();
		this.id = id;
		this.rewardId = rewardId;
		this.chanceToBeSelected = chanceToBeSelected;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRewardId() {
		return rewardId;
	}

	public void setRewardId(int rewardId) {
		this.rewardId = rewardId;
	}

	public float getChanceToBeSelected() {
		return chanceToBeSelected;
	}

	public void setChanceToBeSelected(float chanceToBeSelected) {
		this.chanceToBeSelected = chanceToBeSelected;
	}

	public float getNormalizedSecretGiftProbability() {
		return normalizedSecretGiftProbability;
	}

	public void setNormalizedSecretGiftProbability(
			float normalizedSecretGiftProbability) {
		this.normalizedSecretGiftProbability = normalizedSecretGiftProbability;
	}

	@Override
	public String toString() {
		return "SecretGift [id=" + id + ", rewardId=" + rewardId
				+ ", chanceToBeSelected=" + chanceToBeSelected
				+ ", normalizedSecretGiftProbability="
				+ normalizedSecretGiftProbability + "]";
	}

}
