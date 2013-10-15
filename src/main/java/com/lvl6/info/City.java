package com.lvl6.info;

import java.io.Serializable;

public class City implements Serializable {

	private static final long serialVersionUID = 5469771332854604127L;
	private int id;
	private String name;
	private String mapImgName;
	private CoordinatePair center;
	
	public City(int id, String name, String mapImgName, CoordinatePair center) {
		super();
		this.id = id;
		this.name = name;
		this.mapImgName = mapImgName;
		this.center = center;
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

	@Override
	public String toString() {
		return "City [id=" + id + ", name=" + name + ", mapImgName=" + mapImgName
				+ ", center=" + center + "]";
	}
	
}
