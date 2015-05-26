package com.lvl6.info;

import java.io.Serializable;

public class ClanIcon implements Serializable {

	private static final long serialVersionUID = -3648830914482901288L;

	private int id;
	private String imgName;
	private boolean isAvailable;

	public ClanIcon() {
		super();
	}

	public ClanIcon(int id, String imgName, boolean isAvailable) {
		super();
		this.id = id;
		this.imgName = imgName;
		this.isAvailable = isAvailable;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getImgName() {
		return imgName;
	}

	public void setImgName(String imgName) {
		this.imgName = imgName;
	}

	public boolean isAvailable() {
		return isAvailable;
	}

	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	@Override
	public String toString() {
		return "ClanIcon [id=" + id + ", imgName=" + imgName + ", isAvailable="
				+ isAvailable + "]";
	}

}
