package com.lvl6.info;

import java.io.Serializable;

public class MiniJob implements Serializable {

	private static final long serialVersionUID = -809651011006346155L;
	
	private int id;
	private int requiredStructId;
	private String name;
	private int cashReward;
	private int oilReward;
	private int gemReward;
	private int monsterIdReward;
	private String quality;
	private int maxNumMonstersAllowed;
	private float chanceToAppear;
	private int hpRequired;
	private int atkRequired;
	private int minDmgDealt;
	private int maxDmgDealt;
	
	public MiniJob(int id, int requiredStructId, String name, int cashReward,
			int oilReward, int gemReward, int monsterIdReward, String quality,
			int maxNumMonstersAllowed, float chanceToAppear, int hpRequired,
			int atkRequired, int minDmgDealt, int maxDmgDealt) {
		super();
		this.id = id;
		this.requiredStructId = requiredStructId;
		this.name = name;
		this.cashReward = cashReward;
		this.oilReward = oilReward;
		this.gemReward = gemReward;
		this.monsterIdReward = monsterIdReward;
		this.quality = quality;
		this.maxNumMonstersAllowed = maxNumMonstersAllowed;
		this.chanceToAppear = chanceToAppear;
		this.hpRequired = hpRequired;
		this.atkRequired = atkRequired;
		this.minDmgDealt = minDmgDealt;
		this.maxDmgDealt = maxDmgDealt;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRequiredStructId() {
		return requiredStructId;
	}

	public void setRequiredStructId(int requiredStructId) {
		this.requiredStructId = requiredStructId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCashReward() {
		return cashReward;
	}

	public void setCashReward(int cashReward) {
		this.cashReward = cashReward;
	}

	public int getOilReward() {
		return oilReward;
	}

	public void setOilReward(int oilReward) {
		this.oilReward = oilReward;
	}

	public int getGemReward() {
		return gemReward;
	}

	public void setGemReward(int gemReward) {
		this.gemReward = gemReward;
	}

	public int getMonsterIdReward() {
		return monsterIdReward;
	}

	public void setMonsterIdReward(int monsterIdReward) {
		this.monsterIdReward = monsterIdReward;
	}

	public String getQuality() {
		return quality;
	}

	public void setQuality(String quality) {
		this.quality = quality;
	}

	public int getMaxNumMonstersAllowed() {
		return maxNumMonstersAllowed;
	}

	public void setMaxNumMonstersAllowed(int maxNumMonstersAllowed) {
		this.maxNumMonstersAllowed = maxNumMonstersAllowed;
	}

	public float getChanceToAppear() {
		return chanceToAppear;
	}

	public void setChanceToAppear(float chanceToAppear) {
		this.chanceToAppear = chanceToAppear;
	}

	public int getHpRequired() {
		return hpRequired;
	}

	public void setHpRequired(int hpRequired) {
		this.hpRequired = hpRequired;
	}

	public int getAtkRequired() {
		return atkRequired;
	}

	public void setAtkRequired(int atkRequired) {
		this.atkRequired = atkRequired;
	}

	public int getMinDmgDealt() {
		return minDmgDealt;
	}

	public void setMinDmgDealt(int minDmgDealt) {
		this.minDmgDealt = minDmgDealt;
	}

	public int getMaxDmgDealt() {
		return maxDmgDealt;
	}

	public void setMaxDmgDealt(int maxDmgDealt) {
		this.maxDmgDealt = maxDmgDealt;
	}

	@Override
	public String toString() {
		return "MiniTask [id=" + id + ", requiredStructId=" + requiredStructId
				+ ", name=" + name + ", cashReward=" + cashReward
				+ ", oilReward=" + oilReward + ", gemReward=" + gemReward
				+ ", monsterIdReward=" + monsterIdReward + ", quality="
				+ quality + ", maxNumMonstersAllowed=" + maxNumMonstersAllowed
				+ ", chanceToAppear=" + chanceToAppear + ", hpRequired="
				+ hpRequired + ", atkRequired=" + atkRequired
				+ ", minDmgDealt=" + minDmgDealt + ", maxDmgDealt="
				+ maxDmgDealt + "]";
	}

}
