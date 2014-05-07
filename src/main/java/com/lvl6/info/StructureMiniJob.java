package com.lvl6.info;

import java.io.Serializable;

public class StructureMiniJob implements Serializable {

	private static final long serialVersionUID = -8203936439545654220L;
	
	private int structId;
	private int generatedJobLimit;
	private int hoursBetweenJobGeneration;
	
	public StructureMiniJob(int structId, int generatedJobLimit,
			int hoursBetweenJobGeneration) {
		super();
		this.structId = structId;
		this.generatedJobLimit = generatedJobLimit;
		this.hoursBetweenJobGeneration = hoursBetweenJobGeneration;
	}

	public int getStructId() {
		return structId;
	}

	public void setStructId(int structId) {
		this.structId = structId;
	}

	public int getGeneratedJobLimit() {
		return generatedJobLimit;
	}

	public void setGeneratedJobLimit(int generatedJobLimit) {
		this.generatedJobLimit = generatedJobLimit;
	}

	public int getHoursBetweenJobGeneration() {
		return hoursBetweenJobGeneration;
	}

	public void setHoursBetweenJobGeneration(int hoursBetweenJobGeneration) {
		this.hoursBetweenJobGeneration = hoursBetweenJobGeneration;
	}

	@Override
	public String toString() {
		return "StructureMiniJob [structId=" + structId
				+ ", generatedJobLimit=" + generatedJobLimit
				+ ", hoursBetweenJobGeneration=" + hoursBetweenJobGeneration
				+ "]";
	}
	
}
