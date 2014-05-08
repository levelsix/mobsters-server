package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class MiniJobForUser implements Serializable {
	
	private static final long serialVersionUID = -7377306943664515125L;
	
	private long id;
	private int userId;
	private int miniJobId;
	private int baseDmgReceived;
	private int durationMinutes;
	private Date timeStarted;
	private List<Long> userMonsterIds;
	private String userMonsterIdStr;
	private Date timeCompleted;
	
	public MiniJobForUser() {
		super();
	}

	public MiniJobForUser(long id, int userId, int miniJobId,
			int baseDmgReceived, int durationMinutes, Date timeStarted,
			List<Long> userMonsterIds, String userMonsterIdStr,
			Date timeCompleted) {
		super();
		this.id = id;
		this.userId = userId;
		this.miniJobId = miniJobId;
		this.baseDmgReceived = baseDmgReceived;
		this.durationMinutes = durationMinutes;
		this.timeStarted = timeStarted;
		this.userMonsterIds = userMonsterIds;
		this.userMonsterIdStr = userMonsterIdStr;
		this.timeCompleted = timeCompleted;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public int getDurationMinutes() {
		return durationMinutes;
	}

	public void setDurationMinutes(int durationMinutes) {
		this.durationMinutes = durationMinutes;
	}

	public Date getTimeStarted() {
		return timeStarted;
	}

	public void setTimeStarted(Date timeStarted) {
		this.timeStarted = timeStarted;
	}

	public List<Long> getUserMonsterIds() {
		return userMonsterIds;
	}

	public void setUserMonsterIds(List<Long> userMonsterIds) {
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

	@Override
	public String toString() {
		return "MiniJobForUser [id=" + id + ", userId=" + userId
				+ ", miniJobId=" + miniJobId + ", baseDmgReceived="
				+ baseDmgReceived + ", durationMinutes=" + durationMinutes
				+ ", timeStarted=" + timeStarted + ", userMonsterIds="
				+ userMonsterIds + ", userMonsterIdStr=" + userMonsterIdStr
				+ ", timeCompleted=" + timeCompleted + "]";
	}

}
