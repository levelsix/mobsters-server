package com.lvl6.info;

import java.io.Serializable;

public class SalesItem implements Serializable {

	private static final long serialVersionUID = 8732007664237765170L;

	private int id;
	private int salesPackageId;
	private int rewardId;

	public SalesItem() {
		super();
	}

	public SalesItem(int id, int salesPackageId, int rewardId) {
		super();
		this.id = id;
		this.salesPackageId = salesPackageId;
		this.rewardId = rewardId;

	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSalesPackageId() {
		return salesPackageId;
	}
	public void setSalesPackageId(int salesPackageId) {
		this.salesPackageId = salesPackageId;
	}
	public int getRewardId() {
		return rewardId;
	}
	public void setRewardId(int rewardId) {
		this.rewardId = rewardId;
	}

	@Override
	public String toString() {
		return "SalesItem [id=" + id + ", salesPackageId=" + salesPackageId
				+ ", rewardId=" + rewardId + "]";
	}



}
