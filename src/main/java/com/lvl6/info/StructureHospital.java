package com.lvl6.info;

import java.io.Serializable;

public class StructureHospital implements Serializable {

	private static final long serialVersionUID = 5189605348767310428L;

	private int structId;
	private int queueSize;
	float healthPerSecond;
	float secsToFullyHealMultiplier;

	public StructureHospital(int structId, int queueSize,
			float healthPerSecond, float secsToFullyHealMultiplier) {
		super();
		this.structId = structId;
		this.queueSize = queueSize;
		this.healthPerSecond = healthPerSecond;
		this.secsToFullyHealMultiplier = secsToFullyHealMultiplier;
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

	public float getHealthPerSecond() {
		return healthPerSecond;
	}

	public void setHealthPerSecond(float healthPerSecond) {
		this.healthPerSecond = healthPerSecond;
	}

	public float getSecsToFullyHealMultiplier() {
		return secsToFullyHealMultiplier;
	}

	public void setSecsToFullyHealMultiplier(float secsToFullyHealMultiplier) {
		this.secsToFullyHealMultiplier = secsToFullyHealMultiplier;
	}

	@Override
	public String toString() {
		return "StructureHospital [structId=" + structId + ", queueSize="
				+ queueSize + ", healthPerSecond=" + healthPerSecond
				+ ", secsToFullyHealMultiplier=" + secsToFullyHealMultiplier
				+ "]";
	}

}
