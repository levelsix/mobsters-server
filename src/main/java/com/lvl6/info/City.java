package com.lvl6.info;

import java.io.Serializable;

public class City implements Serializable {

	private static final long serialVersionUID = 8092805006560290613L;
	private int id;
	private String name;
	private String mapImgName;
	private CoordinatePair center;
	private String roadImgName;
	private String mapTmxName;
	private CoordinatePair roadImgCoords;
	private String attackMapLabelImgName;
	
	public City(int id, String name, String mapImgName, CoordinatePair center,
			String roadImgName, String mapTmxName, CoordinatePair roadImgCoords,
			String attackMapLabelImgName) {
		super();
		this.id = id;
		this.name = name;
		this.mapImgName = mapImgName;
		this.center = center;
		this.roadImgName = roadImgName;
		this.mapTmxName = mapTmxName;
		this.roadImgCoords = roadImgCoords;
		this.attackMapLabelImgName = attackMapLabelImgName;
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

	public String getMapImgName() {
		return mapImgName;
	}

	public void setMapImgName(String mapImgName) {
		this.mapImgName = mapImgName;
	}

	public CoordinatePair getCenter() {
		return center;
	}

	public void setCenter(CoordinatePair center) {
		this.center = center;
	}

	public String getRoadImgName() {
		return roadImgName;
	}

	public void setRoadImgName(String roadImgName) {
		this.roadImgName = roadImgName;
	}

	public String getMapTmxName() {
		return mapTmxName;
	}

	public void setMapTmxName(String mapTmxName) {
		this.mapTmxName = mapTmxName;
	}

	public CoordinatePair getRoadImgCoords() {
		return roadImgCoords;
	}

	public void setRoadImgCoords(CoordinatePair roadImgCoords) {
		this.roadImgCoords = roadImgCoords;
	}

	public String getAttackMapLabelImgName() {
		return attackMapLabelImgName;
	}

	public void setAttackMapLabelImgName(String attackMapLabelImgName) {
		this.attackMapLabelImgName = attackMapLabelImgName;
	}

	@Override
	public String toString() {
		return "City [id=" + id + ", name=" + name + ", mapImgName=" + mapImgName
				+ ", center=" + center + ", roadImgName=" + roadImgName
				+ ", mapTmxName=" + mapTmxName + ", roadImgCoords=" + roadImgCoords
				+ ", attackMapLabelImgName=" + attackMapLabelImgName + "]";
	}
	
}
