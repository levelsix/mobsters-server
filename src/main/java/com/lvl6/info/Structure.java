package com.lvl6.info;

import java.io.Serializable;

public class Structure implements Serializable {

	private static final long serialVersionUID = 3987603240365836181L;
	private int id;
	private String name;
	private int level;
	private int income;
	private int minutesToGain;
	private int minutesToBuild;
	private int buildPrice;
	private boolean isPremiumCurrency;
	private int sellPrice;
	private int minLevel;
	private int xLength;
	private int yLength;
	private int imgVerticalPixelOffset;
	private int successorStructId;
	private int predecessorStructId;
	
	public Structure(int id, String name, int level, int income,
			int minutesToGain, int minutesToBuild, int buildPrice,
			boolean isPremiumCurrency, int sellPrice, int minLevel, int xLength,
			int yLength, int imgVerticalPixelOffset, int successorStructId,
			int predecessorStructId) {
		super();
		this.id = id;
		this.name = name;
		this.level = level;
		this.income = income;
		this.minutesToGain = minutesToGain;
		this.minutesToBuild = minutesToBuild;
		this.buildPrice = buildPrice;
		this.isPremiumCurrency = isPremiumCurrency;
		this.sellPrice = sellPrice;
		this.minLevel = minLevel;
		this.xLength = xLength;
		this.yLength = yLength;
		this.imgVerticalPixelOffset = imgVerticalPixelOffset;
		this.successorStructId = successorStructId;
		this.predecessorStructId = predecessorStructId;
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

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getIncome() {
		return income;
	}

	public void setIncome(int income) {
		this.income = income;
	}

	public int getMinutesToGain() {
		return minutesToGain;
	}

	public void setMinutesToGain(int minutesToGain) {
		this.minutesToGain = minutesToGain;
	}

	public int getMinutesToBuild() {
		return minutesToBuild;
	}

	public void setMinutesToBuild(int minutesToBuild) {
		this.minutesToBuild = minutesToBuild;
	}

	public int getBuildPrice() {
		return buildPrice;
	}

	public void setBuildPrice(int buildPrice) {
		this.buildPrice = buildPrice;
	}

	public boolean isPremiumCurrency() {
		return isPremiumCurrency;
	}

	public void setPremiumCurrency(boolean isPremiumCurrency) {
		this.isPremiumCurrency = isPremiumCurrency;
	}

	public int getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(int sellPrice) {
		this.sellPrice = sellPrice;
	}

	public int getMinLevel() {
		return minLevel;
	}

	public void setMinLevel(int minLevel) {
		this.minLevel = minLevel;
	}

	public int getxLength() {
		return xLength;
	}

	public void setxLength(int xLength) {
		this.xLength = xLength;
	}

	public int getyLength() {
		return yLength;
	}

	public void setyLength(int yLength) {
		this.yLength = yLength;
	}

	public int getImgVerticalPixelOffset() {
		return imgVerticalPixelOffset;
	}

	public void setImgVerticalPixelOffset(int imgVerticalPixelOffset) {
		this.imgVerticalPixelOffset = imgVerticalPixelOffset;
	}

	public int getSuccessorStructId() {
		return successorStructId;
	}

	public void setSuccessorStructId(int successorStructId) {
		this.successorStructId = successorStructId;
	}

	public int getPredecessorStructId() {
		return predecessorStructId;
	}

	public void setPredecessorStructId(int predecessorStructId) {
		this.predecessorStructId = predecessorStructId;
	}

	@Override
	public String toString() {
		return "Structure [id=" + id + ", name=" + name + ", level=" + level
				+ ", income=" + income + ", minutesToGain=" + minutesToGain
				+ ", minutesToBuild=" + minutesToBuild + ", buildPrice=" + buildPrice
				+ ", isPremiumCurrency=" + isPremiumCurrency + ", sellPrice="
				+ sellPrice + ", minLevel=" + minLevel + ", xLength=" + xLength
				+ ", yLength=" + yLength + ", imgVerticalPixelOffset="
				+ imgVerticalPixelOffset + ", successorStructId=" + successorStructId
				+ ", predecessorStructId=" + predecessorStructId + "]";
	}

}