package com.lvl6.info;

import java.io.Serializable;

public class CityElement implements Serializable {
	
	private static final long serialVersionUID = 4626120437231136146L;
	
	private int cityId;
	private int assetId;
//	private String goodName;
	private String type;
	private CoordinatePair coords;
	private float xLength;
	private float yLength;
	private String imgGood;
	private String orientation;
	private CoordinatePair spriteCoords;
	
	public CityElement(int cityId, int assetId, String type,
			CoordinatePair coords, float xLength, float yLength,
			String imgGood, String orientation, CoordinatePair spriteCoords) {
		super();
		this.cityId = cityId;
		this.assetId = assetId;
		this.type = type;
		this.coords = coords;
		this.xLength = xLength;
		this.yLength = yLength;
		this.imgGood = imgGood;
		this.orientation = orientation;
		this.spriteCoords = spriteCoords;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public int getAssetId() {
		return assetId;
	}

	public void setAssetId(int assetId) {
		this.assetId = assetId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public CoordinatePair getCoords() {
		return coords;
	}

	public void setCoords(CoordinatePair coords) {
		this.coords = coords;
	}

	public float getxLength() {
		return xLength;
	}

	public void setxLength(float xLength) {
		this.xLength = xLength;
	}

	public float getyLength() {
		return yLength;
	}

	public void setyLength(float yLength) {
		this.yLength = yLength;
	}

	public String getImgGood() {
		return imgGood;
	}

	public void setImgGood(String imgGood) {
		this.imgGood = imgGood;
	}

	public String getOrientation() {
		return orientation;
	}

	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}

	public CoordinatePair getSpriteCoords() {
		return spriteCoords;
	}

	public void setSpriteCoords(CoordinatePair spriteCoords) {
		this.spriteCoords = spriteCoords;
	}

	@Override
	public String toString() {
		return "CityElement [cityId=" + cityId + ", assetId=" + assetId
				+ ", type=" + type + ", coords=" + coords + ", xLength="
				+ xLength + ", yLength=" + yLength + ", imgGood=" + imgGood
				+ ", orientation=" + orientation + ", spriteCoords="
				+ spriteCoords + "]";
	}
	
}
