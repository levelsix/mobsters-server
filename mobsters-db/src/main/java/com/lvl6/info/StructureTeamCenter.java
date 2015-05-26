package com.lvl6.info;

import java.io.Serializable;

public class StructureTeamCenter implements Serializable {

	private static final long serialVersionUID = 1745393383940150048L;

	private int structId;
	private int teamCostLimit;

	public StructureTeamCenter(int structId, int teamCostLimit) {
		super();
		this.structId = structId;
		this.teamCostLimit = teamCostLimit;
	}

	public int getStructId() {
		return structId;
	}

	public void setStructId(int structId) {
		this.structId = structId;
	}

	public int getTeamCostLimit() {
		return teamCostLimit;
	}

	public void setTeamCostLimit(int teamCostLimit) {
		this.teamCostLimit = teamCostLimit;
	}

	@Override
	public String toString() {
		return "StructureTeamCenter [structId=" + structId + ", teamCostLimit="
				+ teamCostLimit + "]";
	}

}
