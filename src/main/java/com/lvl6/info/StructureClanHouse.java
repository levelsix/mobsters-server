package com.lvl6.info;

import java.io.Serializable;

public class StructureClanHouse implements Serializable {

	private static final long serialVersionUID = 5366830636269528356L;
	
	private int structId;
	
	public StructureClanHouse(int structId) {
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
		return "StructureClanHouse [structId="
			+ structId
			+ "]";
	}

}
