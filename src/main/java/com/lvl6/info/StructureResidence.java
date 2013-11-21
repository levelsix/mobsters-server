package com.lvl6.info;

import java.io.Serializable;

public class StructureResidence implements Serializable {

	private static final long serialVersionUID = -9179486937976948825L;
	private int structId;
	private int numMonsterSlots;
	
	public StructureResidence(int structId, int numMonsterSlots) {
		super();
		this.structId = structId;
		this.numMonsterSlots = numMonsterSlots;
	}

	public int getStructId() {
		return structId;
	}

	public void setStructId(int structId) {
		this.structId = structId;
	}

	public int getNumMonsterSlots() {
		return numMonsterSlots;
	}

	public void setNumMonsterSlots(int numMonsterSlots) {
		this.numMonsterSlots = numMonsterSlots;
	}

	@Override
	public String toString() {
		return "StructureResidence [structId=" + structId + ", numMonsterSlots="
				+ numMonsterSlots + "]";
	}
	
}
