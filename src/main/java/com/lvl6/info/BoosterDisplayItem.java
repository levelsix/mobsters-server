package com.lvl6.info;

import java.io.Serializable;

public class BoosterDisplayItem implements Serializable {

	private static final long serialVersionUID = 2771191093432628260L;

	private int id;
	private int boosterPackId;
	private int rewardId;
	
	public BoosterDisplayItem() {
		super();
	}

	public BoosterDisplayItem(int id, int boosterPackId, int rewardId) {
		super();
		this.id = id;
		this.boosterPackId = boosterPackId;
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

	public int getRewardId() {
		return rewardId;
	}

	public void setRewardId(int rewardId) {
		this.rewardId = rewardId;
	}

	@Override
	public String toString() {
		return "BoosterDisplayItem [id=" + id + ", boosterPackId="
				+ boosterPackId + ", rewardId=" + rewardId + "]";
	}

	

}
