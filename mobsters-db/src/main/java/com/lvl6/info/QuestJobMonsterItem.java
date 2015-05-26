package com.lvl6.info;

import java.io.Serializable;
import java.util.Random;

public class QuestJobMonsterItem implements Serializable {

	private static final long serialVersionUID = 9159046998370704510L;

	private int questJobId;
	private int monsterId;
	private int itemId;
	private float itemDropRate;

	//convenience object
	private Random rand;

	public QuestJobMonsterItem(int questJobId, int monsterId, int itemId,
			float itemDropRate) {
		super();
		this.questJobId = questJobId;
		this.monsterId = monsterId;
		this.itemId = itemId;
		this.itemDropRate = itemDropRate;
	}

	//covenience methods--------------------------------------------------------
	public Random getRand() {
		return rand;
	}

	public void setRand(Random rand) {
		this.rand = rand;
	}

	public boolean didItemDrop() {
		float randFloat = getRand().nextFloat();

		if (randFloat < getItemDropRate()) {
			return true;
		} else {
			return false;
		}
	}

	//end covenience methods--------------------------------------------------------

	public int getQuestJobId() {
		return questJobId;
	}

	public void setQuestJobId(int questJobId) {
		this.questJobId = questJobId;
	}

	public int getMonsterId() {
		return monsterId;
	}

	public void setMonsterId(int monsterId) {
		this.monsterId = monsterId;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public float getItemDropRate() {
		return itemDropRate;
	}

	public void setItemDropRate(float itemDropRate) {
		this.itemDropRate = itemDropRate;
	}

	@Override
	public String toString() {
		return "QuestJobMonsterItem [questJobId=" + questJobId + ", monsterId="
				+ monsterId + ", itemId=" + itemId + ", itemDropRate="
				+ itemDropRate + ", rand=" + rand + "]";
	}

}
