package com.lvl6.info;

import java.io.Serializable;

public class StaticUserLevelInfo implements Serializable{
	
	private static final long serialVersionUID = -258608581176466155L;
	private int lvl;
	private int requiredExp;
  
	public StaticUserLevelInfo(int lvl, int requiredExp) {
		super();
		this.lvl = lvl;
		this.requiredExp = requiredExp;
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

	@Override
	public String toString() {
		return "StaticUserLevelInfo [lvl=" + lvl + ", requiredExp=" + requiredExp
				+ "]";
	}
  
}
