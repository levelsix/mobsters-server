package com.lvl6.info;

public class SalesDisplayItem {

	private static final long serialVersionUID = 1549953377153488834L;

	private int id;
	private int salesPackageId;
	private int rewardId;

	public SalesDisplayItem(int id, int salesPackageId, int rewardId) {
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
		return "SalesDisplayItem [id=" + id + ", salesPackageId="
				+ salesPackageId + ", rewardId=" + rewardId + "]";
	}












}


