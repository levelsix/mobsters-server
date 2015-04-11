package com.lvl6.info;

import java.io.Serializable;

public class Prerequisite implements Serializable {

	private static final long serialVersionUID = -8687369531092068694L;

	private int id;
	private String gameType;
	private int gameEntityId;
	private String prereqGameType;
	private int prereqGameEntityId;
	private int quantity;

	public Prerequisite(int id, String gameType, int gameEntityId,
			String prereqGameType, int prereqGameEntityId, int quantity) {
		super();
		this.id = id;
		this.gameType = gameType;
		this.gameEntityId = gameEntityId;
		this.prereqGameType = prereqGameType;
		this.prereqGameEntityId = prereqGameEntityId;
		this.quantity = quantity;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getGameType() {
		return gameType;
	}

	public void setGameType(String gameType) {
		this.gameType = gameType;
	}

	public int getGameEntityId() {
		return gameEntityId;
	}

	public void setGameEntityId(int gameEntityId) {
		this.gameEntityId = gameEntityId;
	}

	public String getPrereqGameType() {
		return prereqGameType;
	}

	public void setPrereqGameType(String prereqGameType) {
		this.prereqGameType = prereqGameType;
	}

	public int getPrereqGameEntityId() {
		return prereqGameEntityId;
	}

	public void setPrereqGameEntityId(int prereqGameEntityId) {
		this.prereqGameEntityId = prereqGameEntityId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "Prerequisite [id=" + id + ", gameType=" + gameType
				+ ", gameEntityId=" + gameEntityId + ", prereqGameType="
				+ prereqGameType + ", prereqGameEntityId=" + prereqGameEntityId
				+ ", quantity=" + quantity + "]";
	}

}
