package com.lvl6.info;

import java.io.Serializable;

public class Task implements Serializable {

	private static final long serialVersionUID = 4860267481324691347L;
	private int id;
	private String goodName;
	private String description;
	private int cityId;
	private int energyCost;
	private int assetNumberWithinCity;
	

	public Task(int id, String goodName, String description, int cityId,
			int energyCost, int assetNumberWithinCity) {
		super();
		this.id = id;
		this.goodName = goodName;
		this.description = description;
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


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
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
		return "Task [id=" + id + ", goodName=" + goodName + ", description="
				+ description + ", cityId=" + cityId + ", energyCost=" + energyCost
				+ ", assetNumberWithinCity=" + assetNumberWithinCity + "]";
	}
	
}
