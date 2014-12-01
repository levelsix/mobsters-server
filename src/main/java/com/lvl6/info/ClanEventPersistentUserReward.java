package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

//user can have multiple of these (different clanIds)
public class ClanEventPersistentUserReward implements Serializable {
	
	private static final long serialVersionUID = -6109900992140941207L;
	
	private String id;
	private String userId;
	private Date crsStartTime;
	private int crsId;
	private Date crsEndTime;
	private String resourceType;
	private int staticDataId;
	private int quantity;
	private int clanEventPersistentId;
	private Date timeRedeemed;
	
	public ClanEventPersistentUserReward()
	{
		super();
	}

	public ClanEventPersistentUserReward(
		String id,
		String userId,
		Date crsStartTime,
		int crsId,
		Date crsEndTime,
		String resourceType,
		int staticDataId,
		int quantity,
		int clanEventPersistentId,
		Date timeRedeemed )
	{
		super();
		this.id = id;
		this.userId = userId;
		this.crsStartTime = crsStartTime;
		this.crsId = crsId;
		this.crsEndTime = crsEndTime;
		this.resourceType = resourceType;
		this.staticDataId = staticDataId;
		this.quantity = quantity;
		this.clanEventPersistentId = clanEventPersistentId;
		this.timeRedeemed = timeRedeemed;
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

	public Date getCrsStartTime() {
		return crsStartTime;
	}

	public void setCrsStartTime(Date crsStartTime) {
		this.crsStartTime = crsStartTime;
	}

	public int getCrsId() {
		return crsId;
	}

	public void setCrsId(int crsId) {
		this.crsId = crsId;
	}

	public Date getCrsEndTime() {
		return crsEndTime;
	}

	public void setCrsEndTime(Date crsEndTime) {
		this.crsEndTime = crsEndTime;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public int getStaticDataId() {
		return staticDataId;
	}

	public void setStaticDataId(int staticDataId) {
		this.staticDataId = staticDataId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getClanEventPersistentId() {
		return clanEventPersistentId;
	}

	public void setClanEventPersistentId(int clanEventPersistentId) {
		this.clanEventPersistentId = clanEventPersistentId;
	}

	public Date getTimeRedeemed() {
		return timeRedeemed;
	}

	public void setTimeRedeemed(Date timeRedeemed) {
		this.timeRedeemed = timeRedeemed;
	}

	@Override
	public String toString() {
		return "ClanEventPersistentUserReward [id=" + id + ", userId=" + userId
				+ ", crsStartTime=" + crsStartTime + ", crsId=" + crsId
				+ ", crsEndTime=" + crsEndTime + ", resourceType=" + resourceType
				+ ", staticDataId=" + staticDataId + ", quantity=" + quantity
				+ ", clanEventPersistentId=" + clanEventPersistentId
				+ ", timeRedeemed=" + timeRedeemed + "]";
	}
	
}
