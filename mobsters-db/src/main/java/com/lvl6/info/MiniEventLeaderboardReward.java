package com.lvl6.info;

import java.io.Serializable;

public class MiniEventLeaderboardReward implements Serializable {

	private static final long serialVersionUID = 1392573884684000781L;

	private int id;
	private int miniEventId;
	private int rewardId;
	private int leaderboardPos;

	public MiniEventLeaderboardReward(int id, int miniEventId, int rewardId,
			int leaderboardPos) {
		super();
		this.id = id;
		this.miniEventId = miniEventId;
		this.rewardId = rewardId;
		this.leaderboardPos = leaderboardPos;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMiniEventId() {
		return miniEventId;
	}

	public void setMiniEventId(int miniEventId) {
		this.miniEventId = miniEventId;
	}

	public int getRewardId() {
		return rewardId;
	}

	public void setRewardId(int rewardId) {
		this.rewardId = rewardId;
	}

	public int getLeaderboardPos() {
		return leaderboardPos;
	}

	public void setLeaderboardPos(int leaderboardPos) {
		this.leaderboardPos = leaderboardPos;
	}

	@Override
	public String toString() {
		return "MiniEventLeaderboardReward [id=" + id + ", miniEventId="
				+ miniEventId + ", rewardId=" + rewardId + ", leaderboardPos="
				+ leaderboardPos + "]";
	}

}
