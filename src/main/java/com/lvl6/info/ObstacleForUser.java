package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class ObstacleForUser implements Serializable {
	
	private static final long serialVersionUID = -2897503606027274504L;
	
	private String id;
	private String userId;
	private int obstacleId;
	private int xcoord;
	private int ycoord;
	private Date removalTime;
	private String orientation;
	
	public ObstacleForUser() {
		super();
	}

	public ObstacleForUser(String id, String userId, int obstacleId, int xcoord,
			int ycoord, Date removalTime, String orientation) {
		super();
		this.id = id;
		this.userId = userId;
		this.obstacleId = obstacleId;
		this.xcoord = xcoord;
		this.ycoord = ycoord;
		this.removalTime = removalTime;
		this.orientation = orientation;
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

	public int getObstacleId() {
		return obstacleId;
	}

	public void setObstacleId(int obstacleId) {
		this.obstacleId = obstacleId;
	}

	public int getXcoord() {
		return xcoord;
	}

	public void setXcoord(int xcoord) {
		this.xcoord = xcoord;
	}

	public int getYcoord() {
		return ycoord;
	}

	public void setYcoord(int ycoord) {
		this.ycoord = ycoord;
	}

	public Date getRemovalTime() {
		return removalTime;
	}

	public void setRemovalTime(Date removalTime) {
		this.removalTime = removalTime;
	}

	public String getOrientation() {
		return orientation;
	}

	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}

	@Override
	public String toString() {
		return "ObstacleForUser [id=" + id + ", userId=" + userId
				+ ", obstacleId=" + obstacleId + ", xcoord=" + xcoord
				+ ", ycoord=" + ycoord + ", removalTime=" + removalTime
				+ ", orientation=" + orientation + "]";
	}

}
