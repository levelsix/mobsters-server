package com.lvl6.info;

import java.io.Serializable;

public class StructureResourceStorage implements Serializable {

/**
	 * 
	 */
	private static final long serialVersionUID = 5021640371198924904L;
//	private static final long serialVersionUID = 5021640371198924904L;
	private int structId;
	private String resourceTypeStored;
	private int capacity;
	
	public StructureResourceStorage(int structId, String resourceTypeStored, int capacity) {
		super();
		this.structId = structId;
		this.resourceTypeStored = resourceTypeStored;
		this.capacity = capacity;
	}

	public int getStructId() {
		return structId;
	}

	public void setStructId(int structId) {
		this.structId = structId;
	}

	public String getResourceTypeStored() {
		return resourceTypeStored;
	}

	public void setResourceTypeStored(String resourceTypeStored) {
		this.resourceTypeStored = resourceTypeStored;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	@Override
	public String toString() {
		return "ResourceStorage [structId=" + structId + ", resourceTypeStored="
				+ resourceTypeStored + ", capacity=" + capacity + "]";
	}
}
