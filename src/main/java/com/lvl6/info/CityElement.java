package com.lvl6.info;

import java.io.Serializable;

import com.lvl6.proto.CityProto.CityElementProto.CityElemType;
import com.lvl6.proto.StructureProto.StructOrientation;

public class CityElement implements Serializable {
	
	private static final long serialVersionUID = 1281950874996210998L;
	private int cityId;
	private int assetId;
//	private String goodName;
	private CityElemType type;
	private CoordinatePair coords;
	private int xLength;
	private int yLength;
	private String imgGood;
	private StructOrientation orientation;
	private CoordinatePair spriteCoords;
	

	public CityElement(int cityId, int assetId, CityElemType type,
			CoordinatePair coords, int xLength, int yLength, String imgGood,
			StructOrientation orientation, CoordinatePair spriteCoords) {
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

	public CityElemType getType() {
		return type;
	}

	public void setType(CityElemType type) {
		this.type = type;
	}

	public CoordinatePair getCoords() {
		return coords;
	}

	public void setCoords(CoordinatePair coords) {
		this.coords = coords;
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

	public String getImgGood() {
		return imgGood;
	}

	public void setImgGood(String imgGood) {
		this.imgGood = imgGood;
	}

	public StructOrientation getOrientation() {
		return orientation;
	}

	public void setOrientation(StructOrientation orientation) {
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
		return "CityElement [cityId=" + cityId + ", assetId=" + assetId + ", type="
				+ type + ", coords=" + coords + ", xLength=" + xLength + ", yLength="
				+ yLength + ", imgGood=" + imgGood + ", orientation=" + orientation
				+ ", spriteCoords=" + spriteCoords + "]";
	}

}
