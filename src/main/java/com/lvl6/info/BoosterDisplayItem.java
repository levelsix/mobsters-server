package com.lvl6.info;

import java.io.Serializable;

import com.lvl6.proto.MonsterStuffProto.MonsterProto.MonsterQuality;

public class BoosterDisplayItem implements Serializable {
	
	private static final long serialVersionUID = -7551191433683892965L;
	private int id;
	private int boosterPackId;
	private boolean isMonster;
	private boolean isComplete;
	private MonsterQuality monsterQuality;
  private int gemReward;
  private int quantity;
  
	public BoosterDisplayItem(int id, int boosterPackId, boolean isMonster,
			boolean isComplete, MonsterQuality monsterQuality, int gemReward,
			int quantity) {
		super();
		this.id = id;
		this.boosterPackId = boosterPackId;
		this.isMonster = isMonster;
		this.isComplete = isComplete;
		this.monsterQuality = monsterQuality;
		this.gemReward = gemReward;
		this.quantity = quantity;
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

	public boolean isMonster() {
		return isMonster;
	}

	public void setMonster(boolean isMonster) {
		this.isMonster = isMonster;
	}

	public boolean isComplete() {
		return isComplete;
	}

	public void setComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}

	public MonsterQuality getMonsterQuality() {
		return monsterQuality;
	}

	public void setMonsterQuality(MonsterQuality monsterQuality) {
		this.monsterQuality = monsterQuality;
	}

	public int getGemReward() {
		return gemReward;
	}

	public void setGemReward(int gemReward) {
		this.gemReward = gemReward;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "BoosterDisplayItem [id=" + id + ", boosterPackId=" + boosterPackId
				+ ", isMonster=" + isMonster + ", isComplete=" + isComplete
				+ ", monsterQuality=" + monsterQuality + ", gemReward=" + gemReward
				+ ", quantity=" + quantity + "]";
	}
	
}
