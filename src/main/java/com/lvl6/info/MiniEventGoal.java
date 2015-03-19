package com.lvl6.info;

import java.io.Serializable;

public class MiniEventGoal implements Serializable {

	private static final long serialVersionUID = -4410520036714178767L;
	
	private int id;
	private int miniEventId;
	private String type;
	private int amt;
	private String desc;
	private int ptsReward;

	public MiniEventGoal(int id, int miniEventId, String type, int amt,
			String desc, int ptsReward)
	{
		super();
		this.id = id;
		this.miniEventId = miniEventId;
		this.type = type;
		this.amt = amt;
		this.desc = desc;
		this.ptsReward = ptsReward;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMiniEventId() {
		return miniEventId;
	}

	public void setMiniEventId(int miniEventId) {
		this.miniEventId = miniEventId;
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

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getPtsReward() {
		return ptsReward;
	}

	public void setPtsReward(int ptsReward) {
		this.ptsReward = ptsReward;
	}

	@Override
	public String toString() {
		return "MiniEventGoal [id=" + id + ", miniEventId=" + miniEventId
				+ ", type=" + type + ", amt=" + amt + ", desc=" + desc
				+ ", ptsReward=" + ptsReward + "]";
	}

}
