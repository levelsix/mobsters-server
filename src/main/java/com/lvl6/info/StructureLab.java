package com.lvl6.info;

import java.io.Serializable;

public class StructureLab implements Serializable {

	private static final long serialVersionUID = 5038262297256522280L;
	private int structId;
	private int queueSize;
	float pointsPerSecond;
	
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
}
