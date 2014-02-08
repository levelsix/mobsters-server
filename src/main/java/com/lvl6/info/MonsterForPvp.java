package com.lvl6.info;

import java.io.Serializable;

public class MonsterForPvp implements Serializable {

	private static final long serialVersionUID = -7096228881477626798L;
	private int id;
	private int monsterId;
	private int monsterLvl;
	private int minElo;
	private int maxElo;
	
	public MonsterForPvp(int id, int monsterId, int monsterLvl, int minElo,
			int maxElo) {
		super();
		this.id = id;
		this.monsterId = monsterId;
		this.monsterLvl = monsterLvl;
		this.minElo = minElo;
		this.maxElo = maxElo;
	}

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

	public int getMinElo() {
		return minElo;
	}

	public void setMinElo(int minElo) {
		this.minElo = minElo;
	}

	public int getMaxElo() {
		return maxElo;
	}

	public void setMaxElo(int maxElo) {
		this.maxElo = maxElo;
	}

	@Override
	public String toString() {
		return "MonsterForPvp [id=" + id + ", monsterId=" + monsterId
				+ ", monsterLvl=" + monsterLvl + ", minElo=" + minElo + ", maxElo="
				+ maxElo + "]";
	}

}
