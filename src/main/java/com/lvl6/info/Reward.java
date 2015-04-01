package com.lvl6.info;

import java.io.Serializable;

public class Reward implements Serializable {

	private static final long serialVersionUID = 5271020048061832981L;

	private int id;
	private int staticDataId;
	private String type;
	private int amt;

	public Reward(int id, int staticDataId, String type, int amt) {
		super();
		this.id = id;
		this.staticDataId = staticDataId;
		this.type = type;
		this.amt = amt;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStaticDataId() {
		return staticDataId;
	}

	public void setStaticDataId(int staticDataId) {
		this.staticDataId = staticDataId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getAmt() {
		return amt;
	}

	public void setAmt(int amt) {
		this.amt = amt;
	}

	@Override
	public String toString() {
		return "Reward [id=" + id + ", staticDataId=" + staticDataId
				+ ", type=" + type + ", amt=" + amt + "]";
	}

}
