package com.lvl6.info;

import java.io.Serializable;

public class Obstacle implements Serializable {

	private int id;
	private String name;
	private String removalCostType;
	private int cost;
	private int secondsToRemove;
	private int width; //how many tiles it covers in the map
	private int height; //how many tiles it covers in the map
	private String imgName;
	private float imgVerticalPixelOffset;
	private String description;
	private float chanceToAppear;
	
	public Obstacle(int id, String name, String removalCostType, int cost,
			int secondsToRemove, int width, int height, String imgName,
			float imgVerticalPixelOffset, String description, float chanceToAppear) {
		super();
		this.id = id;
		this.name = name;
		this.removalCostType = removalCostType;
		this.cost = cost;
		this.secondsToRemove = secondsToRemove;
		this.width = width;
		this.height = height;
		this.imgName = imgName;
		this.imgVerticalPixelOffset = imgVerticalPixelOffset;
		this.description = description;
		this.chanceToAppear = chanceToAppear;
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

	public String getRemovalCostType() {
		return removalCostType;
	}

	public void setRemovalCostType(String removalCostType) {
		this.removalCostType = removalCostType;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public int getSecondsToRemove() {
		return secondsToRemove;
	}

	public void setSecondsToRemove(int secondsToRemove) {
		this.secondsToRemove = secondsToRemove;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getImgName() {
		return imgName;
	}

	public void setImgName(String imgName) {
		this.imgName = imgName;
	}

	public float getImgVerticalPixelOffset() {
		return imgVerticalPixelOffset;
	}

	public void setImgVerticalPixelOffset(float imgVerticalPixelOffset) {
		this.imgVerticalPixelOffset = imgVerticalPixelOffset;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public float getChanceToAppear() {
		return chanceToAppear;
	}

	public void setChanceToAppear(float chanceToAppear) {
		this.chanceToAppear = chanceToAppear;
	}

	@Override
	public String toString() {
		return "Obstacle [id=" + id + ", name=" + name + ", removalCostType="
				+ removalCostType + ", cost=" + cost + ", secondsToRemove="
				+ secondsToRemove + ", width=" + width + ", height=" + height
				+ ", imgName=" + imgName + ", imgVerticalPixelOffset="
				+ imgVerticalPixelOffset + ", description=" + description
				+ ", chanceToAppear=" + chanceToAppear + "]";
	}
	
}
