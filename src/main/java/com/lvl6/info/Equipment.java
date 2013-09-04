package com.lvl6.info;

import java.io.Serializable;

import com.lvl6.proto.InfoProto.FullEquipProto.EquipType;
import com.lvl6.proto.InfoProto.FullEquipProto.Rarity;

public class Equipment implements Serializable {
	private static final long serialVersionUID = -4898540395387646721L;

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
  
	public Equipment(int id, String name, EquipType type, String description,
			int attackBoost, int defenseBoost, int minLevel, Rarity rarity,
			float chanceOfForgeFailureBase, int minutesToAttemptForgeBase) {
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

	@Override
	public String toString() {
		return "Equipment [id=" + id + ", name=" + name + ", type=" + type
				+ ", description=" + description + ", attackBoost=" + attackBoost
				+ ", defenseBoost=" + defenseBoost + ", minLevel=" + minLevel
				+ ", rarity=" + rarity + ", chanceOfForgeFailureBase="
				+ chanceOfForgeFailureBase + ", minutesToAttemptForgeBase="
				+ minutesToAttemptForgeBase + "]";
	}

}
