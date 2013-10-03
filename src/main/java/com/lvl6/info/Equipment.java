package com.lvl6.info;

import java.io.Serializable;

import com.lvl6.proto.InfoProto.FullEquipProto.EquipType;
import com.lvl6.proto.InfoProto.FullEquipProto.Rarity;

public class Equipment implements Serializable {

	private static final long serialVersionUID = 8482312656470872356L;
	private int id;
	private String name;
	private EquipType type;
	private String description;
	private int attackBoost;
	private int defenseBoost;
	private int minLevel;
	private Rarity rarity;
	private float chanceOfForgeFailureBase;
  private int minutesToAttemptForgeBase;
  private int maxDurability;
  private int constantOne;
  private int constantTwo;
  private int constantThree;
  private int constantFour;
  private int constantFive;
  private int constantSix;
  private int constantSeven;
  private int constantEight;
  private int constantNine;
  
  //look at constant_one in equipment table in mysqlworkbench
  //(click alter_table on equipment and then click constant_one)
	public Equipment(int id, String name, EquipType type, String description,
			int attackBoost, int defenseBoost, int minLevel, Rarity rarity,
			float chanceOfForgeFailureBase, int minutesToAttemptForgeBase,
			int maxDurability, int constantOne, int constantTwo, int constantThree,
			int constantFour, int constantFive, int constantSix, int constantSeven,
			int constantEight, int constantNine) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.description = description;
		this.attackBoost = attackBoost;
		this.defenseBoost = defenseBoost;
		this.minLevel = minLevel;
		this.rarity = rarity;
		this.chanceOfForgeFailureBase = chanceOfForgeFailureBase;
		this.minutesToAttemptForgeBase = minutesToAttemptForgeBase;
		this.maxDurability = maxDurability;
		this.constantOne = constantOne;
		this.constantTwo = constantTwo;
		this.constantThree = constantThree;
		this.constantFour = constantFour;
		this.constantFive = constantFive;
		this.constantSix = constantSix;
		this.constantSeven = constantSeven;
		this.constantEight = constantEight;
		this.constantNine = constantNine;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public EquipType getType() {
		return type;
	}


	public void setType(EquipType type) {
		this.type = type;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public int getAttackBoost() {
		return attackBoost;
	}


	public void setAttackBoost(int attackBoost) {
		this.attackBoost = attackBoost;
	}


	public int getDefenseBoost() {
		return defenseBoost;
	}


	public void setDefenseBoost(int defenseBoost) {
		this.defenseBoost = defenseBoost;
	}


	public int getMinLevel() {
		return minLevel;
	}


	public void setMinLevel(int minLevel) {
		this.minLevel = minLevel;
	}


	public Rarity getRarity() {
		return rarity;
	}


	public void setRarity(Rarity rarity) {
		this.rarity = rarity;
	}


	public float getChanceOfForgeFailureBase() {
		return chanceOfForgeFailureBase;
	}


	public void setChanceOfForgeFailureBase(float chanceOfForgeFailureBase) {
		this.chanceOfForgeFailureBase = chanceOfForgeFailureBase;
	}


	public int getMinutesToAttemptForgeBase() {
		return minutesToAttemptForgeBase;
	}


	public void setMinutesToAttemptForgeBase(int minutesToAttemptForgeBase) {
		this.minutesToAttemptForgeBase = minutesToAttemptForgeBase;
	}


	public int getMaxDurability() {
		return maxDurability;
	}


	public void setMaxDurability(int maxDurability) {
		this.maxDurability = maxDurability;
	}


	public int getConstantOne() {
		return constantOne;
	}


	public void setConstantOne(int constantOne) {
		this.constantOne = constantOne;
	}


	public int getConstantTwo() {
		return constantTwo;
	}


	public void setConstantTwo(int constantTwo) {
		this.constantTwo = constantTwo;
	}


	public int getConstantThree() {
		return constantThree;
	}


	public void setConstantThree(int constantThree) {
		this.constantThree = constantThree;
	}


	public int getConstantFour() {
		return constantFour;
	}


	public void setConstantFour(int constantFour) {
		this.constantFour = constantFour;
	}


	public int getConstantFive() {
		return constantFive;
	}


	public void setConstantFive(int constantFive) {
		this.constantFive = constantFive;
	}


	public int getConstantSix() {
		return constantSix;
	}


	public void setConstantSix(int constantSix) {
		this.constantSix = constantSix;
	}


	public int getConstantSeven() {
		return constantSeven;
	}


	public void setConstantSeven(int constantSeven) {
		this.constantSeven = constantSeven;
	}


	public int getConstantEight() {
		return constantEight;
	}


	public void setConstantEight(int constantEight) {
		this.constantEight = constantEight;
	}


	public int getConstantNine() {
		return constantNine;
	}


	public void setConstantNine(int constantNine) {
		this.constantNine = constantNine;
	}


	@Override
	public String toString() {
		return "Equipment [id=" + id + ", name=" + name + ", type=" + type
				+ ", description=" + description + ", attackBoost=" + attackBoost
				+ ", defenseBoost=" + defenseBoost + ", minLevel=" + minLevel
				+ ", rarity=" + rarity + ", chanceOfForgeFailureBase="
				+ chanceOfForgeFailureBase + ", minutesToAttemptForgeBase="
				+ minutesToAttemptForgeBase + ", maxDurability=" + maxDurability
				+ ", constantOne=" + constantOne + ", constantTwo=" + constantTwo
				+ ", constantThree=" + constantThree + ", constantFour=" + constantFour
				+ ", constantFive=" + constantFive + ", constantSix=" + constantSix
				+ ", constantSeven=" + constantSeven + ", constantEight="
				+ constantEight + ", constantNine=" + constantNine + "]";
	}

}
