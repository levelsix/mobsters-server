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
	
	//only one item exists for questId and monsterId
	//duple (questId, monsterId) should only appear in this table once, which
	//has the following case in mind: when user needs to collect items
	//the static data id would be that of an item and multiple monsters
	//can drop said item
	
	public QuestMonsterItem(int questId, int monsterId, int itemId,
			float itemDropRate) {
		super();
		this.questId = questId;
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
	
	
	public int getQuestId() {
		return questId;
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
