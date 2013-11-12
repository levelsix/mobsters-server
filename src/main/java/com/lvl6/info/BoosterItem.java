package com.lvl6.info;

import java.io.Serializable;

public class BoosterItem implements Serializable {
	
	private static final long serialVersionUID = 6843248362650495626L;
	private int id;
  private int boosterPackId;
  private int monsterId;
  private int numPieces;
  private boolean isComplete;
  private boolean isSpecial;
  private int gemReward;
  private int cashReward;
  private float chanceToAppear;
  
	public BoosterItem(int id, int boosterPackId, int monsterId, int numPieces,
			boolean isComplete, boolean isSpecial, int gemReward, int cashReward,
			float chanceToAppear) {
		super();
		this.id = id;
		this.boosterPackId = boosterPackId;
		this.monsterId = monsterId;
		this.numPieces = numPieces;
		this.isComplete = isComplete;
		this.isSpecial = isSpecial;
		this.gemReward = gemReward;
		this.cashReward = cashReward;
		this.chanceToAppear = chanceToAppear;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getBoosterPackId() {
		return boosterPackId;
	}

	public void setBoosterPackId(int boosterPackId) {
		this.boosterPackId = boosterPackId;
	}

	public int getMonsterId() {
		return monsterId;
	}

	public void setMonsterId(int monsterId) {
		this.monsterId = monsterId;
	}

	public int getNumPieces() {
		return numPieces;
	}

	public void setNumPieces(int numPieces) {
		this.numPieces = numPieces;
	}

	public boolean isComplete() {
		return isComplete;
	}

	public void setComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}

	public boolean isSpecial() {
		return isSpecial;
	}

	public void setSpecial(boolean isSpecial) {
		this.isSpecial = isSpecial;
	}

	public int getGemReward() {
		return gemReward;
	}

	public void setGemReward(int gemReward) {
		this.gemReward = gemReward;
	}

	public int getCashReward() {
		return cashReward;
	}

	public void setCashReward(int cashReward) {
		this.cashReward = cashReward;
	}

	public float getChanceToAppear() {
		return chanceToAppear;
	}

	public void setChanceToAppear(float chanceToAppear) {
		this.chanceToAppear = chanceToAppear;
	}

	@Override
	public String toString() {
		return "BoosterItem [id=" + id + ", boosterPackId=" + boosterPackId
				+ ", monsterId=" + monsterId + ", numPieces=" + numPieces
				+ ", isComplete=" + isComplete + ", isSpecial=" + isSpecial
				+ ", gemReward=" + gemReward + ", cashReward=" + cashReward
				+ ", chanceToAppear=" + chanceToAppear + "]";
	}
  
}
