package com.lvl6.info;

public class StructureResearchHouse {

	private int structId;
	private float researchSpeedMultiplier;

	public StructureResearchHouse(int structId, float researchSpeedMultiplier) {
		super();
		this.structId = structId;
		this.researchSpeedMultiplier = researchSpeedMultiplier;
	}

	public float getResearchSpeedMultiplier() {
		return researchSpeedMultiplier;
	}

	public void setResearchSpeedMultiplier(float researchSpeedMultiplier) {
		this.researchSpeedMultiplier = researchSpeedMultiplier;
	}

	public int getStructId() {
		return structId;
	}

	public void setStructId(int structId) {
		this.structId = structId;
	}

	@Override
	public String toString() {
		return "StructureResearchHouse [structId=" + structId
				+ ", researchSpeedMultiplier=" + researchSpeedMultiplier + "]";
	}

}
