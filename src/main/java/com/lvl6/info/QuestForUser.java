package com.lvl6.info;

import java.io.Serializable;

public class QuestForUser implements Serializable {

	private String userId;
	private int questId;
	private boolean isRedeemed;
	private boolean isComplete;
	
	public QuestForUser()
	{
		super();
	}
	
	public QuestForUser(String userId, int questId, boolean isRedeemed,
			boolean isComplete) {
		super();
		this.userId = userId;
		this.questId = questId;
		this.isRedeemed = isRedeemed;
		this.isComplete = isComplete;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
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

	@Override
	public String toString() {
		return "QuestForUser [userId=" + userId + ", questId=" + questId
				+ ", isRedeemed=" + isRedeemed + ", isComplete=" + isComplete
				+ "]";
	}
	
}
