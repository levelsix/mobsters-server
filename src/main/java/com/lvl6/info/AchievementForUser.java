package com.lvl6.info;

import java.io.Serializable;

public class AchievementForUser implements Serializable {
	
	private static final long serialVersionUID = -4983025581174212987L;
	
	private int userId;
	private int achievementId;
	private int progress;
	private boolean isComplete;
	private boolean isRedeemed;
	
	public AchievementForUser() {
		super();
	}

	public AchievementForUser(int userId, int achievementId, int progress,
			boolean isComplete, boolean isRedeemed) {
		super();
		this.userId = userId;
		this.achievementId = achievementId;
		this.progress = progress;
		this.isComplete = isComplete;
		this.isRedeemed = isRedeemed;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getAchievementId() {
		return achievementId;
	}

	public void setAchievementId(int achievementId) {
		this.achievementId = achievementId;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public boolean isComplete() {
		return isComplete;
	}

	public void setComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}

	public boolean isRedeemed() {
		return isRedeemed;
	}

	public void setRedeemed(boolean isRedeemed) {
		this.isRedeemed = isRedeemed;
	}

	@Override
	public String toString() {
		return "AchievementForUser [userId=" + userId + ", achievementId="
				+ achievementId + ", progress=" + progress + ", isComplete="
				+ isComplete + ", isRedeemed=" + isRedeemed + "]";
	}

}
