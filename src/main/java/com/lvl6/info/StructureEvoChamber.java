package com.lvl6.info;

import java.io.Serializable;

public class StructureEvoChamber implements Serializable {

	private static final long serialVersionUID = -1713126465250585021L;
	
	private int structId;
	
	public StructureEvoChamber(int structId) {
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
		return "StructureEvoChamber [structId="
			+ structId
			+ "]";
	}

}
