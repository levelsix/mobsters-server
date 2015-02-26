package com.lvl6.info;

import java.io.Serializable;

public class BoardObstacle implements Serializable {
	
	private static final long serialVersionUID = 3719473963420857938L;
	
	private int id;
	private String name;
	private String type;
	private int powerAmt;
	private boolean initAvailable;
	
	public BoardObstacle(int id, String name, String type, int powerAmt,
			boolean initAvailable) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.powerAmt = powerAmt;
		this.initAvailable = initAvailable;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getPowerAmt() {
		return powerAmt;
	}

	public void setPowerAmt(int powerAmt) {
		this.powerAmt = powerAmt;
	}

	public boolean getInitAvailable() {
		return initAvailable;
	}

	public void setInitAvailable(boolean initAvailable) {
		this.initAvailable = initAvailable;
	}

	@Override
	public String toString() {
		return "BoardObstacle [id=" + id + ", name=" + name + ", type=" + type
				+ ", powerAmt=" + powerAmt + ", initAvailable=" + initAvailable
				+ "]";
	}

}
