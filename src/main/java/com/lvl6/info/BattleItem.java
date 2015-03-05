package com.lvl6.info;

import java.io.Serializable;

public class BattleItem implements Serializable {

	public BattleItem(int id, String type, String battleItemCategory,
			String createResourceType, int createCost, String name,
			String description, int powerAmount, String imageName) {
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
	
	public BattleItem()
	{
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

	@Override
	public String toString() {
		return "BattleItem [id=" + id + ", type=" + type
				+ ", battleItemCategory=" + battleItemCategory
				+ ", createResourceType=" + createResourceType
				+ ", createCost=" + createCost + ", name=" + name
				+ ", description=" + description + ", powerAmount="
				+ powerAmount + ", imageName=" + imageName + "]";
	}

	
	
	

}
