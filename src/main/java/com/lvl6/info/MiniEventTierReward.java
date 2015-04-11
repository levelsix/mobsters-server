package com.lvl6.info;

import java.io.Serializable;

public class MiniEventTierReward implements Serializable {

	private static final long serialVersionUID = -7556383801118858541L;

	private int id;
	private int miniEventForPlayerLvlId;
	private int rewardId;
	private int rewardTier;

	public MiniEventTierReward(int id, int miniEventForPlayerLvlId,
			int rewardId, int rewardTier) {
		super();
		this.id = id;
		this.miniEventForPlayerLvlId = miniEventForPlayerLvlId;
		this.rewardId = rewardId;
		this.rewardTier = rewardTier;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMiniEventForPlayerLvlId() {
		return miniEventForPlayerLvlId;
	}

	public void setMiniEventForPlayerLvlId(int miniEventForPlayerLvlId) {
		this.miniEventForPlayerLvlId = miniEventForPlayerLvlId;
	}

	public int getRewardId() {
		return rewardId;
	}

	public void setRewardId(int rewardId) {
		this.rewardId = rewardId;
	}

	public int getRewardTier() {
		return rewardTier;
	}

	public void setRewardTier(int rewardTier) {
		this.rewardTier = rewardTier;
	}

	@Override
	public String toString() {
		return "MiniEventTierReward [id=" + id + ", miniEventForPlayerLvlId="
				+ miniEventForPlayerLvlId + ", rewardId=" + rewardId
				+ ", rewardTierLvl=" + rewardTier + "]";
	}

}
