package com.lvl6.info;

import java.io.Serializable;

public class BoosterItem implements Serializable {

	private static final long serialVersionUID = 2455005376363185782L;

	private int id;
	private int boosterPackId;
	private boolean isSpecial;
	private float chanceToAppear;
	private int rewardId;

	public BoosterItem() {
		super();
	}

	public BoosterItem(int id, int boosterPackId, boolean isSpecial, float chanceToAppear, 
			int rewardId) {
		super();
		this.id = id;
		this.boosterPackId = boosterPackId;
		this.isSpecial = isSpecial;
		this.chanceToAppear = chanceToAppear;
		this.rewardId = rewardId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getBoosterPackId() {
		return boosterPackId;
	}

	public void setBoosterPackId(int boosterPackId) {
		this.boosterPackId = boosterPackId;
	}

	public boolean isSpecial() {
		return isSpecial;
	}

	public void setSpecial(boolean isSpecial) {
		this.isSpecial = isSpecial;
	}

	public float getChanceToAppear() {
		return chanceToAppear;
	}

	public void setChanceToAppear(float chanceToAppear) {
		this.chanceToAppear = chanceToAppear;
	}

	public int getRewardId() {
		return rewardId;
	}

	public void setRewardId(int rewardId) {
		this.rewardId = rewardId;
	}

	public int getRewardId() {
		return rewardId;
	}

	public void setRewardId(int rewardId) {
		this.rewardId = rewardId;
	}

	@Override
	public String toString() {
		return "BoosterItem [id=" + id + ", boosterPackId=" + boosterPackId
				+ ", isSpecial=" + isSpecial + ", chanceToAppear="
				+ chanceToAppear + ", rewardId=" + rewardId + "]";
	}

}
