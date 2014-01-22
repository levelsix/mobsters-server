package com.lvl6.info;

import java.io.Serializable;

public class MonsterLevelInfo implements Serializable {

	private static final long serialVersionUID = -8794232931561187784L;
	private int monsterId;
	private int level;
	private int hp;
	private int attack;
	private int curLvlRequiredExp;
	private int feederExp;
	
	public MonsterLevelInfo(int monsterId, int level, int hp, int attack,
			int curLvlRequiredExp, int feederExp) {
		super();
		this.monsterId = monsterId;
		this.level = level;
		this.hp = hp;
		this.attack = attack;
		this.curLvlRequiredExp = curLvlRequiredExp;
		this.feederExp = feederExp;
	}

	public int getMonsterId() {
		return monsterId;
	}

	public void setMonsterId(int monsterId) {
		this.monsterId = monsterId;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public int getAttack() {
		return attack;
	}

	public void setAttack(int attack) {
		this.attack = attack;
	}

	public int getCurLvlRequiredExp() {
		return curLvlRequiredExp;
	}

	public void setCurLvlRequiredExp(int curLvlRequiredExp) {
		this.curLvlRequiredExp = curLvlRequiredExp;
	}

	public int getFeederExp() {
		return feederExp;
	}

	public void setFeederExp(int feederExp) {
		this.feederExp = feederExp;
	}

	@Override
	public String toString() {
		return "MonsterLevelInfo [monsterId=" + monsterId + ", level=" + level
				+ ", hp=" + hp + ", attack=" + attack + ", curLvlRequiredExp="
				+ curLvlRequiredExp + ", feederExp=" + feederExp + "]";
	}
	
}
