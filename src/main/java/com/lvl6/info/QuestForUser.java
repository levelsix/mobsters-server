package com.lvl6.info;

import java.io.Serializable;

public class QuestForUser implements Serializable {

	private static final long serialVersionUID = 7901389370805472521L;
	
	private int userId;
	private int questId;
	private boolean isRedeemed;
	private boolean isComplete;
	private int progress;
	
	public QuestForUser(int userId, int questId, boolean isRedeemed,
			boolean isComplete, int progress) {
		super();
		this.userId = userId;
		this.questId = questId;
		this.isRedeemed = isRedeemed;
		this.isComplete = isComplete;
		this.progress = progress;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getQuestId() {
		return questId;
	}

	public void setQuestId(int questId) {
		this.questId = questId;
	}

	public boolean isRedeemed() {
		return isRedeemed;
	}

	public void setRedeemed(boolean isRedeemed) {
		this.isRedeemed = isRedeemed;
	}

	public boolean isComplete() {
		return isComplete;
	}

	public void setComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	@Override
	public String toString() {
		return "QuestForUser [userId=" + userId + ", questId=" + questId
				+ ", isRedeemed=" + isRedeemed + ", isComplete=" + isComplete
				+ ", progress=" + progress + "]";
	}

}
