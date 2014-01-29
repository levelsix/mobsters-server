package com.lvl6.info;

import java.io.Serializable;
import java.util.Random;

import com.lvl6.proto.TaskProto.TaskStageMonsterProto.MonsterType;

public class TaskStageMonster implements Serializable {

	
	private static final long serialVersionUID = -5605457666674487478L;
	private int id;
	private int stageId;
  private int monsterId;
  private MonsterType monsterType;
  private int expReward;
  private int minCashDrop;
  private int maxCashDrop;
  private float puzzlePieceDropRate;
  private int level;
  private float chanceToAppear;
  
  private Random rand;

  public TaskStageMonster(int id, int stageId, int monsterId,
      MonsterType monsterType, int expReward, int minCashDrop,
      int maxCashDrop, float puzzlePieceDropRate, int level,
      float chanceToAppear) {
    super();
    this.id = id;
    this.stageId = stageId;
    this.monsterId = monsterId;
    this.monsterType = monsterType;
    this.expReward = expReward;
    this.minCashDrop = minCashDrop;
    this.maxCashDrop = maxCashDrop;
    this.puzzlePieceDropRate = puzzlePieceDropRate;
    this.level = level;
    this.chanceToAppear = chanceToAppear;
  }

  //covenience methods--------------------------------------------------------
  public Random getRand() {
    return rand;
  }

  public void setRand(Random rand) {
    this.rand = rand;
  }
  
  public int getCashDrop() {
    //example goal: [min,max]=[5, 10], transform range to start at 0.
    //[min-min, max-min] = [0,max-min] = [0,10-5] = [0,5]
    //this means there are (10-5)+1 possible numbers
    
    int minMaxDiff = getMaxCashDrop() - getMinCashDrop();
    int randCash = rand.nextInt(minMaxDiff + 1); 

    //number generated in [0, max-min] range, but need to transform
    //back to original range [min, max]. so add min. [0+min, max-min+min]
    return randCash + getMinCashDrop();
  }

  public boolean didPuzzlePieceDrop() {
    float randFloat = getRand().nextFloat();
    
    if (randFloat < getPuzzlePieceDropRate()) {
      return true;
    } else {
      return false;
    }
  }
  //end covenience methods--------------------------------------------------------

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStageId() {
		return stageId;
	}

	public void setStageId(int stageId) {
		this.stageId = stageId;
	}

	public int getMonsterId() {
		return monsterId;
	}

	public void setMonsterId(int monsterId) {
		this.monsterId = monsterId;
	}

	public MonsterType getMonsterType() {
		return monsterType;
	}

	public void setMonsterType(MonsterType monsterType) {
		this.monsterType = monsterType;
	}

	public int getExpReward() {
		return expReward;
	}

	public void setExpReward(int expReward) {
		this.expReward = expReward;
	}

	public int getMinCashDrop() {
		return minCashDrop;
	}

	public void setMinCashDrop(int minCashDrop) {
		this.minCashDrop = minCashDrop;
	}

	public int getMaxCashDrop() {
		return maxCashDrop;
	}

	public void setMaxCashDrop(int maxCashDrop) {
		this.maxCashDrop = maxCashDrop;
	}

	public float getPuzzlePieceDropRate() {
		return puzzlePieceDropRate;
	}

	public void setPuzzlePieceDropRate(float puzzlePieceDropRate) {
		this.puzzlePieceDropRate = puzzlePieceDropRate;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public float getChanceToAppear() {
		return chanceToAppear;
	}

	public void setChanceToAppear(float chanceToAppear) {
		this.chanceToAppear = chanceToAppear;
	}

	@Override
  public String toString() {
    return "TaskStageMonster [stageId=" + stageId + ", monsterId=" + monsterId
        + ", monsterType=" + monsterType + ", expReward=" + expReward
        + ", minCashDrop=" + minCashDrop + ", maxCashDrop="
        + maxCashDrop + ", puzzlePieceDropRate=" + puzzlePieceDropRate + "]";
  }

}
