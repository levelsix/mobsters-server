package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class UserClan implements Serializable{
	
	private static final long serialVersionUID = 4400616881122130880L;
	
	private int userId;
	private int clanId;
	private String status;
	private Date requestTime;

	public UserClan(int userId, int clanId, String status, Date requestTime) {
		super();
		this.userId = userId;
		this.clanId = clanId;
		this.status = status;
		this.requestTime = requestTime;
	}
	
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getClanId() {
		return clanId;
	}
	public void setClanId(int clanId) {
		this.clanId = clanId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getRequestTime() {
		return requestTime;
	}
	public void setRequestTime(Date requestTime) {
		this.requestTime = requestTime;
	}
	
	@Override
	public String toString() {
		return "UserClan [userId=" + userId + ", clanId=" + clanId
				+ ", status=" + status + ", requestTime=" + requestTime + "]";
	}

}
