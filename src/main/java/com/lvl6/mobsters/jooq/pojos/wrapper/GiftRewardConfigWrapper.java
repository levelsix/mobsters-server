package com.lvl6.mobsters.jooq.pojos.wrapper;

import java.io.Serializable;

import com.lvl6.mobsters.db.jooq.generated.tables.pojos.GiftRewardConfig;

public class GiftRewardConfigWrapper implements Serializable {

	private int giftRewardConfigId;
	private double normalizedProbability;

	public GiftRewardConfigWrapper() {
		super();
	}

	public GiftRewardConfigWrapper(GiftRewardConfig grc) {
		super();
		this.giftRewardConfigId = grc.getId();
		this.normalizedProbability = 0;
	}

	public GiftRewardConfigWrapper(int giftRewardConfigId,
			double normalizedProbability) {
		super();
		this.giftRewardConfigId = giftRewardConfigId;
		this.normalizedProbability = normalizedProbability;
	}

	public int getGiftRewardConfigId() {
		return giftRewardConfigId;
	}

	public void setGiftRewardConfigId(int giftRewardConfigId) {
		this.giftRewardConfigId = giftRewardConfigId;
	}

	public double getNormalizedProbability() {
		return normalizedProbability;
	}

	public void setNormalizedProbability(double normalizedProbability) {
		this.normalizedProbability = normalizedProbability;
	}

	@Override
	public String toString() {
		return "GiftRewardConfigWrapper [giftRewardConfigId=" + giftRewardConfigId
				+ ", normalizedProbability=" + normalizedProbability + "]";
	}

}
