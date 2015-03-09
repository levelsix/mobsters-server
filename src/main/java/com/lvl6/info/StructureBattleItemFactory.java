package com.lvl6.info;

import java.io.Serializable;

public class StructureBattleItemFactory implements Serializable {

	public StructureBattleItemFactory(int structId, int powerLimit) {
		super();
		this.structId = structId;
		this.powerLimit = powerLimit;
	}
	
	private static final long serialVersionUID = -1293698119576984508L;
	
	private int structId;
	private int powerLimit;
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
		return "StructureBattleItemFactory [structId=" + structId
				+ ", powerLimit=" + powerLimit + "]";
	}
	
	
	

}
