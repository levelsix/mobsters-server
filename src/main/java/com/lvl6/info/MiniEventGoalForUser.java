package com.lvl6.info;

import java.io.Serializable;

public class MiniEventGoalForUser implements Serializable {

	private static final long serialVersionUID = -1602557594551918965L;

	private String userId;
	private int miniEventGoalId;
	private int progress;

	public MiniEventGoalForUser() {
		super();
	}

	public MiniEventGoalForUser(String userId, int miniEventGoalId,
			int progress) {
		super();
		this.userId = userId;
		this.miniEventGoalId = miniEventGoalId;
		this.progress = progress;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getMiniEventGoalId() {
		return miniEventGoalId;
	}

	public void setMiniEventGoalId(int miniEventGoalId) {
		this.miniEventGoalId = miniEventGoalId;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	@Override
	public String toString() {
		return "MiniEventGoalForUser [userId=" + userId
				+ ", miniEventGoalId=" + miniEventGoalId + ", progress="
				+ progress + "]";
	}

}
