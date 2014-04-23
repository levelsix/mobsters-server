package com.lvl6.info;

import java.io.Serializable;

public class QuestJobForUser implements Serializable {

	private static final long serialVersionUID = 2975888670470703532L;
	
	private int userId;
	private int questId; //not really necessary but eh
	private int questJobId;
	private boolean isComplete;
	private int progress;
	
	public QuestJobForUser() {
		super();
	}
	
	public QuestJobForUser(int userId, int questId, int questJobId,
			boolean isComplete, int progress) {
		super();
		this.userId = userId;
		this.questId = questId;
		this.questJobId = questJobId;
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

	public int getQuestJobId() {
		return questJobId;
	}

	public void setQuestJobId(int questJobId) {
		this.questJobId = questJobId;
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
		return "QuestJobForUser [userId=" + userId + ", questId=" + questId
				+ ", questJobId=" + questJobId + ", isComplete=" + isComplete
				+ ", progress=" + progress + "]";
	}
	
}
