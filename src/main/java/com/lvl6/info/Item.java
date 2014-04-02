package com.lvl6.info;

import java.io.Serializable;

public class Item implements Serializable {

	private static final long serialVersionUID = 4660675777685331403L;
	
	private int id;
	private String name;
	private String imgName;
	private String borderImgName;
	private int blue; //colors for the border?
	private int green;
	private int red;
	
	public Item(int id, String name, String imgName, String borderImgName,
			int blue, int green, int red) {
		super();
		this.id = id;
		this.name = name;
		this.imgName = imgName;
		this.borderImgName = borderImgName;
		this.blue = blue;
		this.green = green;
		this.red = red;
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

	public String getBorderImgName() {
		return borderImgName;
	}

	public void setBorderImgName(String borderImgName) {
		this.borderImgName = borderImgName;
	}

	public int getBlue() {
		return blue;
	}

	public void setBlue(int blue) {
		this.blue = blue;
	}

	public int getGreen() {
		return green;
	}

	public void setGreen(int green) {
		this.green = green;
	}

	public int getRed() {
		return red;
	}

	public void setRed(int red) {
		this.red = red;
	}

	@Override
	public String toString() {
		return "Item [id=" + id + ", name=" + name + ", imgName=" + imgName
				+ ", borderImgName=" + borderImgName + ", blue=" + blue
				+ ", green=" + green + ", red=" + red + "]";
	}
	
}
