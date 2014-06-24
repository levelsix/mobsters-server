package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class ExpansionPurchaseForUser implements Serializable {
	
	private static final long serialVersionUID = -5045515317226423462L;
	private int userId;
	private int xPosition;
	private int yPosition;
	private boolean isExpanding;
	private Date expandStartTime; // refers to last time the user clicks the
  // upgrade button, not when the last upgrade
  // was complete
  
	public ExpansionPurchaseForUser(int userId, int xPosition, int yPosition,
			boolean isExpanding, Date expandStartTime) {
		super();
		this.userId = userId;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.isExpanding = isExpanding;
		this.expandStartTime = expandStartTime;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getxPosition() {
		return xPosition;
	}

	public void setxPosition(int xPosition) {
		this.xPosition = xPosition;
	}

	public int getyPosition() {
		return yPosition;
	}

	public void setyPosition(int yPosition) {
		this.yPosition = yPosition;
	}

	public boolean isExpanding() {
		return isExpanding;
	}

	public void setExpanding(boolean isExpanding) {
		this.isExpanding = isExpanding;
	}

	public Date getExpandStartTime() {
		return expandStartTime;
	}

	public void setExpandStartTime(Date expandStartTime) {
		this.expandStartTime = expandStartTime;
	}

	@Override
	public String toString() {
		return "ExpansionPurchaseForUser [userId=" + userId + ", xPosition="
				+ xPosition + ", yPosition=" + yPosition + ", isExpanding="
				+ isExpanding + ", expandStartTime=" + expandStartTime + "]";
	}

}
