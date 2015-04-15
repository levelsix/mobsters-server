package com.lvl6.info;

public class CustomMenu {

	private int id;
	private int positionX;
	private int positionY;
	private int positionZ;
	private boolean isJiggle;

	public CustomMenu(int id, int positionX, int positionY, int positionZ,
			boolean isJiggle) {
		super();
		this.id = id;
		this.positionX = positionX;
		this.positionY = positionY;
		this.positionZ = positionZ;
		this.isJiggle = isJiggle;
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

	@Override
	public String toString() {
		return "CustomMenu [id=" + id + ", positionX=" + positionX
				+ ", positionY=" + positionY + ", positionZ=" + positionZ
				+ ", isJiggle=" + isJiggle + "]";
	}

}
