package com.lvl6.info;

import java.io.Serializable;

public class PvpBoardObstacleForUser implements Serializable {

	private static final long serialVersionUID = 7948431947926085118L;

	private int id;
	private String userId;
	private int obstacleId;
	private int posX;
	private int posY;

	public PvpBoardObstacleForUser() {
		super();
	}

	public PvpBoardObstacleForUser(int id, String userId, int obstacleId,
			int posX, int posY) {
		super();
		this.id = id;
		this.userId = userId;
		this.obstacleId = obstacleId;
		this.posX = posX;
		this.posY = posY;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
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

	public int getPosX() {
		return posX;
	}

	public void setPosX(int posX) {
		this.posX = posX;
	}

	public int getPosY() {
		return posY;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}

	@Override
	public String toString() {
		return "PvpBoardObstacleForUser [id=" + id + ", userId=" + userId
				+ ", obstacleId=" + obstacleId + ", posX=" + posX + ", posY="
				+ posY + "]";
	}

}
