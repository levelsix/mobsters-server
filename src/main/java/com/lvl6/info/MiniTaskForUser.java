package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class MiniTaskForUser implements Serializable {
	
	private static final long serialVersionUID = 8744099139117830254L;
	
	private int userId;
	private int miniTaskId;
	private int baseDmgReceived;
	private Date timeStarted;
	private List<Integer> userMonsterIds;
	
	public MiniTaskForUser() {
		super();
	}
	
	public MiniTaskForUser(int userId, int miniTaskId, int baseDmgReceived,
			Date timeStarted, List<Integer> userMonsterIds) {
		super();
		this.userId = userId;
		this.miniTaskId = miniTaskId;
		this.baseDmgReceived = baseDmgReceived;
		this.timeStarted = timeStarted;
		this.userMonsterIds = userMonsterIds;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getMiniTaskId() {
		return miniTaskId;
	}

	public void setMiniTaskId(int miniTaskId) {
		this.miniTaskId = miniTaskId;
	}

	public int getBaseDmgReceived() {
		return baseDmgReceived;
	}

	public void setBaseDmgReceived(int baseDmgReceived) {
		this.baseDmgReceived = baseDmgReceived;
	}

	public Date getTimeStarted() {
		return timeStarted;
	}

	public void setTimeStarted(Date timeStarted) {
		this.timeStarted = timeStarted;
	}

	public List<Integer> getUserMonsterIds() {
		return userMonsterIds;
	}

	public void setUserMonsterIds(List<Integer> userMonsterIds) {
		this.userMonsterIds = userMonsterIds;
	}

	@Override
	public String toString() {
		return "MiniTaskForUser [userId=" + userId + ", miniTaskId="
				+ miniTaskId + ", baseDmgReceived=" + baseDmgReceived
				+ ", timeStarted=" + timeStarted + ", userMonsterIds="
				+ userMonsterIds + "]";
	}
	
}
