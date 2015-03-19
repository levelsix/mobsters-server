package com.lvl6.info;

import java.io.Serializable;

public class BattleItem implements Serializable {

	public BattleItem(int id, String type, String battleItemCategory,
			String createResourceType, int createCost, String name,
			String description, int powerAmount, String imageName,
			int priority, int minutesToCreate, int inBattleGemCost, int amount) {
		super();
		this.id = id;
		this.type = type;
		this.battleItemCategory = battleItemCategory;
		this.createResourceType = createResourceType;
		this.createCost = createCost;
		this.name = name;
		this.description = description;
		this.powerAmount = powerAmount;
		this.imageName = imageName;
		this.priority = priority;
		this.minutesToCreate = minutesToCreate;
		this.inBattleGemCost = inBattleGemCost;
		this.amount = amount;
	}

	private static final long serialVersionUID = -1293698119576984508L;

	private int id;
	private String type;
	private String battleItemCategory;
	private String createResourceType;
	private int createCost;
	private String name;
	private String description;
	private int powerAmount;
	private String imageName;
	private int priority;
	private int minutesToCreate;
	private int inBattleGemCost;
	private int amount;

	public BattleItem() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBattleItemCategory() {
		return battleItemCategory;
	}

	public void setCategory(String battleItemCategory) {
		this.battleItemCategory = battleItemCategory;
	}

	public String getCreateResourceType() {
		return createResourceType;
	}

	public void setCreateResourceType(String createResourceType) {
		this.createResourceType = createResourceType;
	}

	public int getCreateCost() {
		return createCost;
	}

	public void setCreateCost(int createCost) {
		this.createCost = createCost;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getPowerAmount() {
		return powerAmount;
	}

	public void setPowerAmount(int powerAmount) {
		this.powerAmount = powerAmount;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public void setBattleItemCategory(String battleItemCategory) {
		this.battleItemCategory = battleItemCategory;
	}

	@Override
	public String toString() {
		return "BattleItem [id=" + id + ", type=" + type
				+ ", battleItemCategory=" + battleItemCategory
				+ ", createResourceType=" + createResourceType
				+ ", createCost=" + createCost + ", name=" + name
				+ ", description=" + description + ", powerAmount="
				+ powerAmount + ", imageName=" + imageName + ", priority="
				+ priority + ", minutesToCreate=" + minutesToCreate
				+ ", inBattleGemCost=" + inBattleGemCost + ", amount=" + amount
				+ "]";
	}

	public int getMinutesToCreate() {
		return minutesToCreate;
	}

	public void setMinutesToCreate(int minutesToCreate) {
		this.minutesToCreate = minutesToCreate;
	}

	public int getInBattleGemCost() {
		return inBattleGemCost;
	}

	public void setInBattleGemCost(int inBattleGemCost) {
		this.inBattleGemCost = inBattleGemCost;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

}
