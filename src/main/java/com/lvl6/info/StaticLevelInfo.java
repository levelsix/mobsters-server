package com.lvl6.info;

import java.io.Serializable;

public class StaticLevelInfo implements Serializable{
	
	private static final long serialVersionUID = -8242282071455381140L;
	private int lvl;
  private int requiredExp;
  private int maxCash;
  
	public StaticLevelInfo(int lvl, int requiredExp, int maxCash) {
		super();
		this.lvl = lvl;
		this.requiredExp = requiredExp;
		this.maxCash = maxCash;
	}

	public int getLvl() {
		return lvl;
	}

	public void setLvl(int lvl) {
		this.lvl = lvl;
	}

	public int getRequiredExp() {
		return requiredExp;
	}

	public void setRequiredExp(int requiredExp) {
		this.requiredExp = requiredExp;
	}

	public int getMaxCash() {
		return maxCash;
	}

	public void setMaxCash(int maxCash) {
		this.maxCash = maxCash;
	}

	@Override
	public String toString() {
		return "StaticLevelInfo [lvl=" + lvl + ", requiredExp=" + requiredExp
				+ ", maxCash=" + maxCash + "]";
	}
  
}
