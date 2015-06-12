package com.lvl6.info;

import java.io.Serializable;

public class CustomMenu implements Serializable {

	private static final long serialVersionUID = 6088649625609749363L;

	private int id;
	private int positionX;
	private int positionY;
	private int positionZ;
	private boolean isJiggle;
	private String imageName;
	private int ipadPositionX;
	private int ipadPositionY;

	public CustomMenu(int id, int positionX, int positionY, int positionZ,
			boolean isJiggle, String imageName, int ipadPositionX,
			int ipadPositionY) {
		super();
		this.id = id;
		this.positionX = positionX;
		this.positionY = positionY;
		this.positionZ = positionZ;
		this.isJiggle = isJiggle;
		this.imageName = imageName;
		this.ipadPositionX = ipadPositionX;
		this.ipadPositionY = ipadPositionY;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPositionX() {
		return positionX;
	}
	public void setPositionX(int positionX) {
		this.positionX = positionX;
	}
	public int getPositionY() {
		return positionY;
	}
	public void setPositionY(int positionY) {
		this.positionY = positionY;
	}
	public int getPositionZ() {
		return positionZ;
	}
	public void setPositionZ(int positionZ) {
		this.positionZ = positionZ;
	}
	public boolean isJiggle() {
		return isJiggle;
	}
	public void setJiggle(boolean isJiggle) {
		this.isJiggle = isJiggle;
	}

	public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public int getIpadPositionX() {
		return ipadPositionX;
	}

	public void setIpadPositionX(int ipadPositionX) {
		this.ipadPositionX = ipadPositionX;
	}

	public int getIpadPositionY() {
		return ipadPositionY;
	}

	public void setIpadPositionY(int ipadPositionY) {
		this.ipadPositionY = ipadPositionY;
	}

	@Override
	public String toString() {
		return "CustomMenu [id=" + id + ", positionX=" + positionX
				+ ", positionY=" + positionY + ", positionZ=" + positionZ
				+ ", isJiggle=" + isJiggle + ", imageName=" + imageName
				+ ", ipadPositionX=" + ipadPositionX + ", ipadPositionY="
				+ ipadPositionY + "]";
	}

}
