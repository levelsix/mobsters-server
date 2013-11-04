package com.lvl6.info;

import java.io.Serializable;

public class Structure implements Serializable {

	private static final long serialVersionUID = 5890003961169632164L;
	private int id;
	private String name;
	private int level;
	private int income;
	private int minutesToGain;
	private int minutesToBuild;
	private int cashPrice;
	private int gemPrice;
	private int minLevel;
	private int xLength;
	private int yLength;
	private int instaBuildGemCost;
	private int imgVerticalPixelOffset;
	private int successorStructId;
	
	public Structure(int id, String name, int level, int income,
			int minutesToGain, int minutesToBuild, int cashPrice, int gemPrice,
			int minLevel, int xLength, int yLength, int instaBuildGemCost,
			int imgVerticalPixelOffset, int successorStructId) {
		super();
		this.id = id;
		this.name = name;
		this.level = level;
		this.income = income;
		this.minutesToGain = minutesToGain;
		this.minutesToBuild = minutesToBuild;
		this.cashPrice = cashPrice;
		this.gemPrice = gemPrice;
		this.minLevel = minLevel;
		this.xLength = xLength;
		this.yLength = yLength;
		this.instaBuildGemCost = instaBuildGemCost;
		this.imgVerticalPixelOffset = imgVerticalPixelOffset;
		this.successorStructId = successorStructId;
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

	public int getCashPrice() {
		return cashPrice;
	}

	public void setCashPrice(int cashPrice) {
		this.cashPrice = cashPrice;
	}

	public int getGemPrice() {
		return gemPrice;
	}

	public void setGemPrice(int gemPrice) {
		this.gemPrice = gemPrice;
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

	public int getInstaBuildGemCost() {
		return instaBuildGemCost;
	}

	public void setInstaBuildGemCost(int instaBuildGemCost) {
		this.instaBuildGemCost = instaBuildGemCost;
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

	@Override
	public String toString() {
		return "Structure [id=" + id + ", name=" + name + ", level=" + level
				+ ", income=" + income + ", minutesToGain=" + minutesToGain
				+ ", minutesToBuild=" + minutesToBuild + ", cashPrice=" + cashPrice
				+ ", gemPrice=" + gemPrice + ", minLevel=" + minLevel + ", xLength="
				+ xLength + ", yLength=" + yLength + ", instaBuildGemCost="
				+ instaBuildGemCost + ", imgVerticalPixelOffset="
				+ imgVerticalPixelOffset + ", successorStructId=" + successorStructId
				+ "]";
	}

}