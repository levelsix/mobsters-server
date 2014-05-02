package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class MiniJobForUser implements Serializable {
	
	private static final long serialVersionUID = 8744099139117830254L;
	
	private int userId;
	private int miniJobId;
	private int baseDmgReceived;
	private Date timeStarted;
	private List<Integer> userMonsterIds;
	
	public MiniJobForUser() {
		super();
	}
	
	public MiniJobForUser(int userId, int miniJobId, int baseDmgReceived,
			Date timeStarted, List<Integer> userMonsterIds) {
		super();
		this.userId = userId;
		this.miniJobId = miniJobId;
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

	public int getMiniJobId() {
		return miniJobId;
	}

	public void setMiniJobId(int miniJobId) {
		this.miniJobId = miniJobId;
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
		return "MiniJobForUser [userId=" + userId + ", miniJobId="
				+ miniJobId + ", baseDmgReceived=" + baseDmgReceived
				+ ", timeStarted=" + timeStarted + ", userMonsterIds="
				+ userMonsterIds + "]";
	}
	
}
