package com.lvl6.info;

import java.io.Serializable;

public class StructurePvpBoard implements Serializable {

	private static final long serialVersionUID = -8735632690890428866L;

	private int structId;
	private int powerLimit;

	public StructurePvpBoard(int structId, int powerLimit) {
		super();
		this.structId = structId;
		this.powerLimit = powerLimit;
	}

	public int getStructId() {
		return structId;
	}

	public void setStructId(int structId) {
		this.structId = structId;
	}

	public int getPowerLimit() {
		return powerLimit;
	}

	public void setPowerLimit(int powerLimit) {
		this.powerLimit = powerLimit;
	}

	@Override
	public String toString() {
		return "StructureTeamCenter [structId=" + structId + ", powerLimit="
				+ powerLimit + "]";
	}

}
