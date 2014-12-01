package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;


public class StructureForUser implements Serializable {
	
	private static final long serialVersionUID = 9028602340046219872L;
	
	private String id;
	private String userId;
	private int structId;
	private Date lastRetrieved;
	private CoordinatePair coordinates;
//	private int level;
	private Date purchaseTime;
	private boolean isComplete;
	private String orientation;
	private int fbInviteStructLvl;
	
	public StructureForUser()
	{
		super();
	}

	public StructureForUser(String id, String userId, int structId,
			Date lastRetrieved, CoordinatePair coordinates, Date purchaseTime,
			boolean isComplete, String orientation, int fbInviteStructLvl) {
		super();
		this.id = id;
		this.userId = userId;
		this.structId = structId;
		this.lastRetrieved = lastRetrieved;
		this.coordinates = coordinates;
		this.purchaseTime = purchaseTime;
		this.isComplete = isComplete;
		this.orientation = orientation;
		this.fbInviteStructLvl = fbInviteStructLvl;
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

	public int getStructId() {
		return structId;
	}

	public void setStructId(int structId) {
		this.structId = structId;
	}

	public Date getLastRetrieved() {
		return lastRetrieved;
	}

	public void setLastRetrieved(Date lastRetrieved) {
		this.lastRetrieved = lastRetrieved;
	}

	public CoordinatePair getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(CoordinatePair coordinates) {
		this.coordinates = coordinates;
	}

	public Date getPurchaseTime() {
		return purchaseTime;
	}

	public void setPurchaseTime(Date purchaseTime) {
		this.purchaseTime = purchaseTime;
	}

	public boolean isComplete() {
		return isComplete;
	}

	public void setComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}

	public String getOrientation() {
		return orientation;
	}

	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}

	public int getFbInviteStructLvl() {
		return fbInviteStructLvl;
	}

	public void setFbInviteStructLvl(int fbInviteStructLvl) {
		this.fbInviteStructLvl = fbInviteStructLvl;
	}

	@Override
	public String toString() {
		return "StructureForUser [id=" + id + ", userId=" + userId
				+ ", structId=" + structId + ", lastRetrieved=" + lastRetrieved
				+ ", coordinates=" + coordinates + ", purchaseTime="
				+ purchaseTime + ", isComplete=" + isComplete
				+ ", orientation=" + orientation + ", fbInviteStructLvl="
				+ fbInviteStructLvl + "]";
	}

}
