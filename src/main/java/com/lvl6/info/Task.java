package com.lvl6.info;

import java.io.Serializable;

public class Task implements Serializable {

	private static final long serialVersionUID = -3002131432760456790L;
	private int id;
	private String goodName;
	private int cityId;
	private int energyCost;
//	private int minCoinsGained;
//	private int maxCoinsGained;
//	private float chanceOfEquipFloat;
//	private List<Integer> potentialLootEquipIds;
//	private int expGained;
	private int assetNumberWithinCity;
//	private int numForCompletion;
//	private String goodProcessingText;
//	private String badProcessingText;
//	private CoordinatePair spriteLandingCoords;
//	private AnimationType animationType;
	
	public Task(int id, String goodName, int cityId, int energyCost,
			int assetNumberWithinCity) {
		super();
		this.id = id;
		this.goodName = goodName;
		this.cityId = cityId;
		this.energyCost = energyCost;
		this.assetNumberWithinCity = assetNumberWithinCity;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getGoodName() {
		return goodName;
	}

	public void setGoodName(String goodName) {
		this.goodName = goodName;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public int getEnergyCost() {
		return energyCost;
	}

	public void setEnergyCost(int energyCost) {
		this.energyCost = energyCost;
	}

	public int getAssetNumberWithinCity() {
		return assetNumberWithinCity;
	}

	public void setAssetNumberWithinCity(int assetNumberWithinCity) {
		this.assetNumberWithinCity = assetNumberWithinCity;
	}

	@Override
	public String toString() {
		return "Task [id=" + id + ", goodName=" + goodName + ", cityId=" + cityId
				+ ", energyCost=" + energyCost + ", assetNumberWithinCity="
				+ assetNumberWithinCity + "]";
	}


}
