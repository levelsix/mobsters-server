package com.lvl6.info;

public class StructureResearchHouse {

	public StructureResearchHouse(int structId) {
		super();
		this.structId = structId;
	}

	private int structId;

	public int getStructId() {
		return structId;
	}

	public void setStructId(int structId) {
		this.structId = structId;
	}

	@Override
	public String toString() {
		return "StructureResearchHouse [structId=" + structId + "]";
	}
	
	
	
}
