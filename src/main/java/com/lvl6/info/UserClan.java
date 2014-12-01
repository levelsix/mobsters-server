package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class UserClan implements Serializable{
	
	private static final long serialVersionUID = 4990554882214487889L;
	
	private String userId;
	private String clanId;
	private String status;
	private Date requestTime;

	public UserClan()
	{
		super();
	}

	public UserClan(String userId, String clanId, String status, Date requestTime) {
		super();
		this.userId = userId;
		this.clanId = clanId;
		this.status = status;
		this.requestTime = requestTime;
	}
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getClanId() {
		return clanId;
	}
	public void setClanId(String clanId) {
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
