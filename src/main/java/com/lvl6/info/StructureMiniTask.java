package com.lvl6.info;

import java.io.Serializable;

public class StructureMiniTask implements Serializable {

	private static final long serialVersionUID = 7776699359323284334L;
	
	private int structId;
	private int generatedTaskLimit;
	private int hoursBetweenTaskGeneration;
	
	public StructureMiniTask(int structId, int generatedTaskLimit,
			int hoursBetweenTaskGeneration) {
		super();
		this.structId = structId;
		this.generatedTaskLimit = generatedTaskLimit;
		this.hoursBetweenTaskGeneration = hoursBetweenTaskGeneration;
	}

	public int getStructId() {
		return structId;
	}

	public void setStructId(int structId) {
		this.structId = structId;
	}

	public int getGeneratedTaskLimit() {
		return generatedTaskLimit;
	}

	public void setGeneratedTaskLimit(int generatedTaskLimit) {
		this.generatedTaskLimit = generatedTaskLimit;
	}

	public int getHoursBetweenTaskGeneration() {
		return hoursBetweenTaskGeneration;
	}

	public void setHoursBetweenTaskGeneration(int hoursBetweenTaskGeneration) {
		this.hoursBetweenTaskGeneration = hoursBetweenTaskGeneration;
	}

	@Override
	public String toString() {
		return "StructureMiniTask [structId=" + structId
				+ ", generatedTaskLimit=" + generatedTaskLimit
				+ ", hoursBetweenTaskGeneration=" + hoursBetweenTaskGeneration
				+ "]";
	}
	
}
