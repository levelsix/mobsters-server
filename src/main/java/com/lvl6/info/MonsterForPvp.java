package com.lvl6.info;

import java.io.Serializable;
import java.util.Random;

public class MonsterForPvp implements Serializable {

	private static final long serialVersionUID = 4013127890023301706L;
	
	private int id;
	private int monsterId;
	private int monsterLvl;
	private int elo;
	private int minCashReward;
	private int maxCashReward;
	private int minOilReward;
	private int maxOilReward;
	
	
	//not part of the table, just for convenience
	private Random rand;
	
	public MonsterForPvp(int id, int monsterId, int monsterLvl, int elo,
			int minCashReward, int maxCashReward, int minOilReward, int maxOilReward) {
		super();
		this.id = id;
		this.monsterId = monsterId;
		this.monsterLvl = monsterLvl;
		this.elo = elo;
		this.minCashReward = minCashReward;
		this.maxCashReward = maxCashReward;
		this.minOilReward = minOilReward;
		this.maxOilReward = maxOilReward;
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
    
    int minMaxDiff = getMaxCashReward() - getMinCashReward();
    int randCash = rand.nextInt(minMaxDiff + 1); 

    //number generated in [0, max-min] range, but need to transform
    //back to original range [min, max]. so add min. [0+min, max-min+min]
    return randCash + getMinCashReward();
  }
  
  public int getOilDrop() {
    //example goal: [min,max]=[5, 10], transform range to start at 0.
    //[min-min, max-min] = [0,max-min] = [0,10-5] = [0,5]
    //this means there are (10-5)+1 possible numbers
    
    int minMaxDiff = getMaxOilReward() - getMinOilReward();
    int randCash = rand.nextInt(minMaxDiff + 1); 

    //number generated in [0, max-min] range, but need to transform
    //back to original range [min, max]. so add min. [0+min, max-min+min]
    return randCash + getMinOilReward();
  }

  //end covenience methods--------------------------------------------------------


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMonsterId() {
		return monsterId;
	}

	public void setMonsterId(int monsterId) {
		this.monsterId = monsterId;
	}

	public int getMonsterLvl() {
		return monsterLvl;
	}

	public void setMonsterLvl(int monsterLvl) {
		this.monsterLvl = monsterLvl;
	}

	public int getElo() {
		return elo;
	}

	public void setElo(int elo) {
		this.elo = elo;
	}

	public int getMinCashReward() {
		return minCashReward;
	}

	public void setMinCashReward(int minCashReward) {
		this.minCashReward = minCashReward;
	}

	public int getMaxCashReward() {
		return maxCashReward;
	}

	public void setMaxCashReward(int maxCashReward) {
		this.maxCashReward = maxCashReward;
	}

	public int getMinOilReward() {
		return minOilReward;
	}

	public void setMinOilReward(int minOilReward) {
		this.minOilReward = minOilReward;
	}

	public int getMaxOilReward() {
		return maxOilReward;
	}

	public void setMaxOilReward(int maxOilReward) {
		this.maxOilReward = maxOilReward;
	}

	@Override
	public String toString() {
		return "MonsterForPvp [id=" + id + ", monsterId=" + monsterId
				+ ", monsterLvl=" + monsterLvl + ", elo=" + elo + ", minCashReward="
				+ minCashReward + ", maxCashReward=" + maxCashReward
				+ ", minOilReward=" + minOilReward + ", maxOilReward=" + maxOilReward
				+ "]";
	}

}
