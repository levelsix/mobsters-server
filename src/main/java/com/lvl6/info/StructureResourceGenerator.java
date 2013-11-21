package com.lvl6.info;

import java.io.Serializable;

public class StructureResourceGenerator implements Serializable {

	private static final long serialVersionUID = -2371172975086740032L;
	private int structId;
	private String resourceTypeGenerated;
	//at the moment, some amount per hour
	private float productionRate;
	private int capacity;
	
	public StructureResourceGenerator(int structId, String resourceTypeGenerated,
			float productionRate, int capacity) {
		super();
		this.structId = structId;
		this.resourceTypeGenerated = resourceTypeGenerated;
		this.productionRate = productionRate;
		this.capacity = capacity;
	}

	public int getStructId() {
		return structId;
	}

	public void setStructId(int structId) {
		this.structId = structId;
	}

	public String getResourceTypeGenerated() {
		return resourceTypeGenerated;
	}

	public void setResourceTypeGenerated(String resourceTypeGenerated) {
		this.resourceTypeGenerated = resourceTypeGenerated;
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

	@Override
	public String toString() {
		return "ResourceGenerator [structId=" + structId
				+ ", resourceTypeGenerated=" + resourceTypeGenerated
				+ ", productionRate=" + productionRate + ", capacity=" + capacity + "]";
	}
	
}