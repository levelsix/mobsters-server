package com.lvl6.info;

import java.io.Serializable;
import java.util.Random;

public class QuestMonsterItem implements Serializable {

	private static final long serialVersionUID = -9041249361685192938L;
	private int questId;
	private int monsterId;
	private int itemId;
	private float itemDropRate;
	
	
	//convenience object
	private Random rand;
	
	//only one item exists for questId 
	//maybe a monsterId can have multiple items associated with it
	//questId should only appear in this table once
	
	public QuestMonsterItem(int questId, int monsterId, int itemId,
			float itemDropRate) {
		super();
		this.questId = questId;
		this.monsterId = monsterId;
		this.itemId = itemId;
		this.itemDropRate = itemDropRate;
	}
//covenience methods--------------------------------------------------------
	public int getQuestId() {
		return questId;
	}
	
	public Random getRand() {
		return rand;
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

	public void setRand(Random rand) {
		this.rand = rand;
	}

	public void setQuestId(int questId) {
		this.questId = questId;
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
		return "QuestMonsterItem [questId=" + questId + ", monsterId=" + monsterId
				+ ", itemId=" + itemId + ", itemDropRate=" + itemDropRate + "]";
	}

}
