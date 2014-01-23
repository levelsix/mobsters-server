package com.lvl6.info;

import java.io.Serializable;

public class MonsterLevelInfo implements Serializable {

	private static final long serialVersionUID = -259260813112191865L;
	private int monsterId;
	private int level;
	private int hp;
	private int curLvlRequiredExp;
	private int feederExp;
	private int fireDmg;
	private int grassDmg;
	private int waterDmg;
	private int lightningDmg;
	private int darknessDmg;
	private int rockDmg;
	
	public MonsterLevelInfo(int monsterId, int level, int hp,
			int curLvlRequiredExp, int feederExp, int fireDmg, int grassDmg,
			int waterDmg, int lightningDmg, int darknessDmg, int rockDmg) {
		super();
		this.monsterId = monsterId;
		this.level = level;
		this.hp = hp;
		this.curLvlRequiredExp = curLvlRequiredExp;
		this.feederExp = feederExp;
		this.fireDmg = fireDmg;
		this.grassDmg = grassDmg;
		this.waterDmg = waterDmg;
		this.lightningDmg = lightningDmg;
		this.darknessDmg = darknessDmg;
		this.rockDmg = rockDmg;
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

	public int getFireDmg() {
		return fireDmg;
	}

	public void setFireDmg(int fireDmg) {
		this.fireDmg = fireDmg;
	}

	public int getGrassDmg() {
		return grassDmg;
	}

	public void setGrassDmg(int grassDmg) {
		this.grassDmg = grassDmg;
	}

	public int getWaterDmg() {
		return waterDmg;
	}

	public void setWaterDmg(int waterDmg) {
		this.waterDmg = waterDmg;
	}

	public int getLightningDmg() {
		return lightningDmg;
	}

	public void setLightningDmg(int lightningDmg) {
		this.lightningDmg = lightningDmg;
	}

	public int getDarknessDmg() {
		return darknessDmg;
	}

	public void setDarknessDmg(int darknessDmg) {
		this.darknessDmg = darknessDmg;
	}

	public int getRockDmg() {
		return rockDmg;
	}

	public void setRockDmg(int rockDmg) {
		this.rockDmg = rockDmg;
	}

	@Override
	public String toString() {
		return "MonsterLevelInfo [monsterId=" + monsterId + ", level=" + level
				+ ", hp=" + hp + ", curLvlRequiredExp=" + curLvlRequiredExp
				+ ", feederExp=" + feederExp + ", fireDmg=" + fireDmg + ", grassDmg="
				+ grassDmg + ", waterDmg=" + waterDmg + ", lightningDmg="
				+ lightningDmg + ", darknessDmg=" + darknessDmg + ", rockDmg="
				+ rockDmg + "]";
	}
	
}
