package com.lvl6.mobsters.jooq.pojos.wrapper;

import java.io.Serializable;

import com.lvl6.mobsters.db.jooq.generated.tables.pojos.SecretGiftConfigPojo;

public class SecretGiftConfigWrapper implements Serializable {

	private static final long serialVersionUID = -3203320382080499982L;

	private int secretGiftConfigId;
	private double normalizedProbability;

	public SecretGiftConfigWrapper() {
		super();
	}

	public SecretGiftConfigWrapper(SecretGiftConfigPojo sgc) {
		super();
		this.secretGiftConfigId = sgc.getId();
		this.normalizedProbability = 0;
	}

	public SecretGiftConfigWrapper(int secretGiftConfigId,
			double normalizedProbability) {
		super();
		this.secretGiftConfigId = secretGiftConfigId;
		this.normalizedProbability = normalizedProbability;
	}

	public int getSecretGiftConfigId() {
		return secretGiftConfigId;
	}

	public void setSecretGiftConfigId(int secretGiftConfigId) {
		this.secretGiftConfigId = secretGiftConfigId;
	}

	public double getNormalizedProbability() {
		return normalizedProbability;
	}

	public void setNormalizedProbability(double normalizedProbability) {
		this.normalizedProbability = normalizedProbability;
	}

	@Override
	public String toString() {
		return "SecretGiftConfigWrapper [secretGiftConfigId="
				+ secretGiftConfigId + ", normalizedProbability="
				+ normalizedProbability + "]";
	}

}
