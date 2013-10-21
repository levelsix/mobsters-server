package com.lvl6.info;

import java.io.Serializable;
import java.util.Random;

import com.lvl6.proto.TaskProto.TaskStageMonsterProto.MonsterType;

public class TaskStageMonster implements Serializable {

	private static final long serialVersionUID = 6187452147997498630L;
  private int id;
	private int stageId;
  private int monsterId;
  private MonsterType monsterType;
  private int expReward;
  private int minSilverDrop;
  private int maxSilverDrop;
  private float puzzlePieceDropRate;
  private int level;
  private float chanceToAppear;
  
  private Random rand;

  public TaskStageMonster(int id, int stageId, int monsterId,
      MonsterType monsterType, int expReward, int minSilverDrop,
      int maxSilverDrop, float puzzlePieceDropRate, int level,
      float chanceToAppear) {
    super();
    this.id = id;
    this.stageId = stageId;
    this.monsterId = monsterId;
    this.monsterType = monsterType;
    this.expReward = expReward;
    this.minSilverDrop = minSilverDrop;
    this.maxSilverDrop = maxSilverDrop;
    this.puzzlePieceDropRate = puzzlePieceDropRate;
    this.level = level;
    this.chanceToAppear = chanceToAppear;
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

  public Random getRand() {
    return rand;
  }

  public void setRand(Random rand) {
    this.rand = rand;
  }
  
  public int getSilverDrop() {
    //example goal: [min,max]=[5, 10], transform range to start at 0.
    //[min-min, max-min] = [0,max-min] = [0,10-5] = [0,5]
    //this means there are (10-5)+1 possible numbers
    
    int minMaxDiff = getMaxSilverDrop() - getMinSilverDrop();
    int randSilver = rand.nextInt(minMaxDiff + 1); 

    //number generated in [0, max-min] range, but need to transform
    //back to original range [min, max]. so add min. [0+min, max-min+min]
    return randSilver + getMinSilverDrop();
  }

  public boolean didPuzzlePieceDrop() {
    float randFloat = this.rand.nextFloat();
    
    if (randFloat < this.puzzlePieceDropRate) {
      return true;
    } else {
      return false;
    }
  }

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

  public int getMinSilverDrop() {
    return minSilverDrop;
  }

  public void setMinSilverDrop(int minSilverDrop) {
    this.minSilverDrop = minSilverDrop;
  }

  public int getMaxSilverDrop() {
    return maxSilverDrop;
  }

  public void setMaxSilverDrop(int maxSilverDrop) {
    this.maxSilverDrop = maxSilverDrop;
  }

  public float getPuzzlePieceDropRate() {
    return puzzlePieceDropRate;
  }

  public void setPuzzlePieceDropRate(float puzzlePieceDropRate) {
    this.puzzlePieceDropRate = puzzlePieceDropRate;
  }

	@Override
  public String toString() {
    return "TaskStageMonster [stageId=" + stageId + ", monsterId=" + monsterId
        + ", monsterType=" + monsterType + ", expReward=" + expReward
        + ", minSilverDrop=" + minSilverDrop + ", maxSilverDrop="
        + maxSilverDrop + ", puzzlePieceDropRate=" + puzzlePieceDropRate + "]";
  }

}
