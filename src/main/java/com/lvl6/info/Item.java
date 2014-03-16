package com.lvl6.info;

import java.io.Serializable;

public class Item implements Serializable {

	private static final long serialVersionUID = 5520727320057463579L;
	
	private int id;
	private String name;
	private String imgName;
	
	public Item(int id, String name, String imgName) {
		super();
		this.id = id;
		this.name = name;
		this.imgName = imgName;
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

	public String getImgName() {
		return imgName;
	}

	public void setImgName(String imgName) {
		this.imgName = imgName;
	}

	@Override
	public String toString() {
		return "Item [id=" + id + ", name=" + name + ", imgName=" + imgName + "]";
	}
	
}
