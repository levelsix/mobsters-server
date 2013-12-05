package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

import com.lvl6.proto.StructureProto.StructOrientation;

public class StructureForUser implements Serializable {
	//any change in this class should also change the UpdateUtils.java method updateUserStructsLastretrieved()
	
	private static final long serialVersionUID = -5978986902991070169L;
	private int id;
	private int userId;
	private int structId;
	private Date lastRetrieved;
	private CoordinatePair coordinates;
//	private int level;
	private Date purchaseTime;
	private boolean isComplete;
	private StructOrientation orientation;
	private int fbInviteStructLvl;
	

	public StructureForUser(int id, int userId, int structId, Date lastRetrieved,
			CoordinatePair coordinates, Date purchaseTime, boolean isComplete,
			StructOrientation orientation, int fbInviteStructLvl) {
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
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

	public StructOrientation getOrientation() {
		return orientation;
	}

	public void setOrientation(StructOrientation orientation) {
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
		return "StructureForUser [id=" + id + ", userId=" + userId + ", structId="
				+ structId + ", lastRetrieved=" + lastRetrieved + ", coordinates="
				+ coordinates + ", purchaseTime=" + purchaseTime + ", isComplete="
				+ isComplete + ", orientation=" + orientation + ", fbInviteStructLvl="
				+ fbInviteStructLvl + "]";
	}

}
