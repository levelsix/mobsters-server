package com.lvl6.info;

import java.io.Serializable;

import com.lvl6.proto.InfoProto.NeutralCityElementProto.NeutralCityElemType;
import com.lvl6.proto.InfoProto.StructOrientation;

public class NeutralCityElement implements Serializable {
	
	private static final long serialVersionUID = -907736005921382685L;
	private int cityId;
	private int assetId;
	private String goodName;
	private NeutralCityElemType type;
	private CoordinatePair coords;
	private int xLength;
	private int yLength;
	private String imgGood;
	private StructOrientation orientation;


	public NeutralCityElement(int cityId, int assetId, String goodName,
			NeutralCityElemType type, CoordinatePair coords, int xLength,
			int yLength, String imgGood, StructOrientation orientation) {
		super();
		this.cityId = cityId;
		this.assetId = assetId;
		this.goodName = goodName;
		this.type = type;
		this.coords = coords;
		this.xLength = xLength;
		this.yLength = yLength;
		this.imgGood = imgGood;
		this.orientation = orientation;
	}

	public int getCityId() {
		return cityId;
	}

	public int getAssetId() {
		return assetId;
	}

	public String getGoodName() {
		return goodName;
	}

	public NeutralCityElemType getType() {
		return type;
	}

	public CoordinatePair getCoords() {
		return coords;
	}

	public int getxLength() {
		return xLength;
	}

	public int getyLength() {
		return yLength;
	}

	public String getImgGood() {
		return imgGood;
	}

	public StructOrientation getOrientation() {
		return orientation;
	}

	@Override
	public String toString() {
		return "NeutralCityElement [cityId=" + cityId + ", assetId=" + assetId
				+ ", goodName=" + goodName + ", type=" + type + ", coords=" + coords
				+ ", xLength=" + xLength + ", yLength=" + yLength + ", imgGood="
				+ imgGood + ", orientation=" + orientation + "]";
	}

}
