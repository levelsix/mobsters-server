package com.lvl6.info;

import java.io.Serializable;

public class ServerToggle implements Serializable {

	private static final long serialVersionUID = 9155948932873832596L;

	private int id;
	private String name;
	private boolean on;

	public ServerToggle(int id, String name, boolean on) {
		super();
		this.id = id;
		this.name = name;
		this.on = on;
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

	public boolean isOn() {
		return on;
	}

	public void setOn(boolean on) {
		this.on = on;
	}

	@Override
	public String toString() {
		return "ServerToggle [id=" + id + ", name=" + name + ", on=" + on + "]";
	}

}
