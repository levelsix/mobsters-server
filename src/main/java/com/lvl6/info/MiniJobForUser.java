package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class MiniJobForUser implements Serializable {
	
	private static final long serialVersionUID = -5431494347613930829L;
	
	private String id;
	private String userId;
	private int miniJobId;
	private int baseDmgReceived;
	private int durationSeconds;
	private Date timeStarted;
	private List<String> userMonsterIds;
	private String userMonsterIdStr;
	private Date timeCompleted;
	
	public MiniJobForUser() {
		super();
	}

	public MiniJobForUser(String id, String userId, int miniJobId,
			int baseDmgReceived, int durationSeconds, Date timeStarted,
			List<String> userMonsterIds, String userMonsterIdStr,
			Date timeCompleted) {
		super();
		this.id = id;
		this.userId = userId;
		this.miniJobId = miniJobId;
		this.baseDmgReceived = baseDmgReceived;
		this.durationSeconds = durationSeconds;
		this.timeStarted = timeStarted;
		this.userMonsterIds = userMonsterIds;
		this.userMonsterIdStr = userMonsterIdStr;
		this.timeCompleted = timeCompleted;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
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

	public int getDurationSeconds() {
		return durationSeconds;
	}

	public void setDurationSeconds(int durationSeconds) {
		this.durationSeconds = durationSeconds;
	}

	public Date getTimeStarted() {
		return timeStarted;
	}

	public void setTimeStarted(Date timeStarted) {
		this.timeStarted = timeStarted;
	}

	public List<String> getUserMonsterIds() {
		return userMonsterIds;
	}

	public void setUserMonsterIds(List<String> userMonsterIds) {
		this.userMonsterIds = userMonsterIds;
	}

	public String getUserMonsterIdStr() {
		return userMonsterIdStr;
	}

	public void setUserMonsterIdStr(String userMonsterIdStr) {
		this.userMonsterIdStr = userMonsterIdStr;
	}

	public Date getTimeCompleted() {
		return timeCompleted;
	}

	public void setTimeCompleted(Date timeCompleted) {
		this.timeCompleted = timeCompleted;
	}

	public int getDurationMinutes()
	{
		return durationSeconds / 60;
	}

	@Override
	public String toString() {
		return "MiniJobForUser [id=" + id + ", userId=" + userId
				+ ", miniJobId=" + miniJobId + ", baseDmgReceived="
				+ baseDmgReceived + ", durationSeconds=" + durationSeconds
				+ ", timeStarted=" + timeStarted + ", userMonsterIds="
				+ userMonsterIds + ", userMonsterIdStr=" + userMonsterIdStr
				+ ", timeCompleted=" + timeCompleted + "]";
	}

}
