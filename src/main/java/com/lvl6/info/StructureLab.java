package com.lvl6.info;

import java.io.Serializable;

public class StructureLab implements Serializable {

	private static final long serialVersionUID = 6577889949065041857L;
	
	private int structId;
	private int queueSize;
	private float pointsMultiplier;
	
	public StructureLab(int structId, int queueSize, float pointsMultiplier) {
		super();
		this.structId = structId;
		this.queueSize = queueSize;
		this.pointsMultiplier = pointsMultiplier;
	}

	public int getStructId() {
		return structId;
	}

	public void setStructId(int structId) {
		this.structId = structId;
	}

	public int getQueueSize() {
		return queueSize;
	}

	public void setQueueSize(int queueSize) {
		this.queueSize = queueSize;
	}

	public float getPointsMultiplier() {
		return pointsMultiplier;
	}

	public void setPointsMultiplier(float pointsMultiplier) {
		this.pointsMultiplier = pointsMultiplier;
	}

	@Override
	public String toString() {
		return "StructureLab [structId=" + structId + ", queueSize="
				+ queueSize + ", pointsMultiplier=" + pointsMultiplier + "]";
	}
	
}
