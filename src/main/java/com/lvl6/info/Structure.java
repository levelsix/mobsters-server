package com.lvl6.info;

import java.io.Serializable;

public class Structure implements Serializable {

	private static final long serialVersionUID = 3254897803372739364L;
	private int id;
	private String name;
	private int income;
	private int minutesToGain;
	private int minutesToBuild;
	private int coinPrice;
	private int diamondPrice;
	private int minLevel;
	private int xLength;
	private int yLength;
	private int instaBuildDiamondCost;
	private int imgVerticalPixelOffset;
	private int successorStructId;

	public Structure(int id, String name, int income, int minutesToGain,
			int minutesToBuild, int coinPrice, int diamondPrice, int minLevel,
			int xLength, int yLength, int instaBuildDiamondCost,
			int imgVerticalPixelOffset, int successorStructId) {
		super();
		this.id = id;
		this.name = name;
		this.income = income;
		this.minutesToGain = minutesToGain;
		this.minutesToBuild = minutesToBuild;
		this.coinPrice = coinPrice;
		this.diamondPrice = diamondPrice;
		this.minLevel = minLevel;
		this.xLength = xLength;
		this.yLength = yLength;
		this.instaBuildDiamondCost = instaBuildDiamondCost;
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

	public int getCoinPrice() {
		return coinPrice;
	}

	public void setCoinPrice(int coinPrice) {
		this.coinPrice = coinPrice;
	}

	public int getDiamondPrice() {
		return diamondPrice;
	}

	public void setDiamondPrice(int diamondPrice) {
		this.diamondPrice = diamondPrice;
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

	public int getInstaBuildDiamondCost() {
		return instaBuildDiamondCost;
	}

	public void setInstaBuildDiamondCost(int instaBuildDiamondCost) {
		this.instaBuildDiamondCost = instaBuildDiamondCost;
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
		return "Structure [id=" + id + ", name=" + name + ", income=" + income
				+ ", minutesToGain=" + minutesToGain + ", minutesToBuild="
				+ minutesToBuild + ", coinPrice=" + coinPrice + ", diamondPrice="
				+ diamondPrice + ", minLevel=" + minLevel + ", xLength=" + xLength
				+ ", yLength=" + yLength + ", instaBuildDiamondCost="
				+ instaBuildDiamondCost + ", imgVerticalPixelOffset="
				+ imgVerticalPixelOffset + ", successorStructId=" + successorStructId
				+ "]";
	}

}