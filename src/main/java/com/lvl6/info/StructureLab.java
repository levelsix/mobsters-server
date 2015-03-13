package com.lvl6.info;

import java.io.Serializable;

public class StructureLab implements Serializable {

	private static final long serialVersionUID = -2404804141023315530L;

	private int structId;
	private int queueSize;
	private float pointsMultiplier;
	private float pointsPerSecond;

	public StructureLab(int structId, int queueSize, float pointsMultiplier,
			float pointsPerSecond) {
		super();
		this.structId = structId;
		this.queueSize = queueSize;
		this.pointsMultiplier = pointsMultiplier;
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

	public float getPointsMultiplier() {
		return pointsMultiplier;
	}

	public void setPointsMultiplier(float pointsMultiplier) {
		this.pointsMultiplier = pointsMultiplier;
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
				+ queueSize + ", pointsMultiplier=" + pointsMultiplier
				+ ", pointsPerSecond=" + pointsPerSecond + "]";
	}

}
