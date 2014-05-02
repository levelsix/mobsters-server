package com.lvl6.info;

import java.io.Serializable;

public class StructureLab implements Serializable {

	private static final long serialVersionUID = 1709205982691094635L;
	
	private int structId;
	private int queueSize;
	private float pointsPerSecond;
	
	public StructureLab(int structId, int queueSize, float pointsPerSecond) {
		super();
		this.structId = structId;
		this.queueSize = queueSize;
		this.pointsPerSecond = pointsPerSecond;
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

	public float getPointsPerSecond() {
		return pointsPerSecond;
	}

	public void setPointsPerSecond(float pointsPerSecond) {
		this.pointsPerSecond = pointsPerSecond;
	}

	@Override
	public String toString() {
		return "StructureLab [structId=" + structId + ", queueSize="
				+ queueSize + ", pointsPerSecond=" + pointsPerSecond + "]";
	}
	
}
