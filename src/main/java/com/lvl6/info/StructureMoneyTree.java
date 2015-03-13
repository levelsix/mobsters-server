package com.lvl6.info;

import java.io.Serializable;

public class StructureMoneyTree implements Serializable {

	private static final long serialVersionUID = -2371172975086740032L;

	private int structId;
	private float productionRate;
	private int capacity;
	private int daysOfDuration;
	private int daysForRenewal;
	private String iapProductId;
	private String fakeIAPProductId;

	public StructureMoneyTree() {
		super();
	}

	public StructureMoneyTree(int structId, float productionRate, int capacity,
			int daysOfDuration, int daysForRenewal, String iapProductId,
			String fakeIAPProductId) {
		super();
		this.structId = structId;
		this.productionRate = productionRate;
		this.capacity = capacity;
		this.daysOfDuration = daysOfDuration;
		this.daysForRenewal = daysForRenewal;
		this.iapProductId = iapProductId;
		this.fakeIAPProductId = fakeIAPProductId;
	}

	public int getStructId() {
		return structId;
	}

	public void setStructId(int structId) {
		this.structId = structId;
	}

	public float getProductionRate() {
		return productionRate;
	}

	public void setProductionRate(float productionRate) {
		this.productionRate = productionRate;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getDaysOfDuration() {
		return daysOfDuration;
	}

	public void setDaysOfDuration(int daysOfDuration) {
		this.daysOfDuration = daysOfDuration;
	}

	public int getDaysForRenewal() {
		return daysForRenewal;
	}

	public void setDaysForRenewal(int daysForRenewal) {
		this.daysForRenewal = daysForRenewal;
	}

	public String getIapProductId() {
		return iapProductId;
	}

	public void setIapProductId(String iapProductId) {
		this.iapProductId = iapProductId;
	}

	@Override
	public String toString() {
		return "StructureMoneyTree [structId=" + structId + ", productionRate="
				+ productionRate + ", capacity=" + capacity
				+ ", daysOfDuration=" + daysOfDuration + ", daysForRenewal="
				+ daysForRenewal + ", iapProductId=" + iapProductId
				+ ", fakeIAPProductId=" + fakeIAPProductId + "]";
	}

	public String getFakeIAPProductId() {
		return fakeIAPProductId;
	}

	public void setFakeIAPProductId(String fakeIAPProductId) {
		this.fakeIAPProductId = fakeIAPProductId;
	}

}