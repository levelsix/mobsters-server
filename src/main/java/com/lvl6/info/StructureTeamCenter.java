package com.lvl6.info;

import java.io.Serializable;

public class StructureTeamCenter implements Serializable {

	private static final long serialVersionUID = -8562432276610351451L;
	
	private int structId;
	
	public StructureTeamCenter(int structId) {
		super();
		this.structId = structId;
	}

	public int getStructId() {
		return structId;
	}

	public void setStructId(int structId) {
		this.structId = structId;
	}

	@Override
	public String toString()
	{
		return "StructureTeamCenter [structId="
			+ structId
			+ "]";
	}

}
