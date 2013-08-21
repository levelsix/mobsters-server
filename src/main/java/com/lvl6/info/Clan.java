package com.lvl6.info;

import java.util.Date;

public class Clan {
	private int id;
	private String name;
	private int ownerId;
	private Date createTime;
	private String description;
	private String tag;
	private int currentTierLevel;
	private boolean requestToJoinRequired;

	public Clan(int id, String name, int ownerId, Date createTime,
			String description, String tag, int currentTierLevel,
			boolean requestToJoinRequired) {
		super();
		this.id = id;
		this.name = name;
		this.ownerId = ownerId;
		this.createTime = createTime;
		this.description = description;
		this.tag = tag;
		this.currentTierLevel = currentTierLevel;
		this.requestToJoinRequired = requestToJoinRequired;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public int getCurrentTierLevel() {
		return currentTierLevel;
	}

	public void setCurrentTierLevel(int currentTierLevel) {
		this.currentTierLevel = currentTierLevel;
	}

	public boolean isRequestToJoinRequired() {
		return requestToJoinRequired;
	}

	public void setRequestToJoinRequired(boolean requestToJoinRequired) {
		this.requestToJoinRequired = requestToJoinRequired;
	}

	@Override
	public String toString() {
		return "Clan [id=" + id + ", name=" + name + ", ownerId=" + ownerId
				+ ", createTime=" + createTime + ", description=" + description
				+ ", tag=" + tag + ", currentTierLevel=" + currentTierLevel
				+ ", requestToJoinRequired=" + requestToJoinRequired + "]";
	}

}

