package com.lvl6.info;

import java.io.Serializable;

public class BoosterPack implements Serializable {
	
	private static final long serialVersionUID = -5200606492974418991L;
	private int id;
	private String name;
	private int gemPrice;
	private String listBackgroundImgName;
	private String listDescription;
	private String navBarImgName;
	private String navTitleImgName;
	private String machineImgName;
  
	public BoosterPack(int id, String name, int gemPrice,
			String listBackgroundImgName, String listDescription,
			String navBarImgName, String navTitleImgName, String machineImgName) {
		super();
		this.id = id;
		this.name = name;
		this.gemPrice = gemPrice;
		this.listBackgroundImgName = listBackgroundImgName;
		this.listDescription = listDescription;
		this.navBarImgName = navBarImgName;
		this.navTitleImgName = navTitleImgName;
		this.machineImgName = machineImgName;
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

	public int getGemPrice() {
		return gemPrice;
	}

	public void setGemPrice(int gemPrice) {
		this.gemPrice = gemPrice;
	}

	public String getListBackgroundImgName() {
		return listBackgroundImgName;
	}

	public void setListBackgroundImgName(String listBackgroundImgName) {
		this.listBackgroundImgName = listBackgroundImgName;
	}

	public String getListDescription() {
		return listDescription;
	}

	public void setListDescription(String listDescription) {
		this.listDescription = listDescription;
	}

	public String getNavBarImgName() {
		return navBarImgName;
	}

	public void setNavBarImgName(String navBarImgName) {
		this.navBarImgName = navBarImgName;
	}

	public String getNavTitleImgName() {
		return navTitleImgName;
	}

	public void setNavTitleImgName(String navTitleImgName) {
		this.navTitleImgName = navTitleImgName;
	}

	public String getMachineImgName() {
		return machineImgName;
	}

	public void setMachineImgName(String machineImgName) {
		this.machineImgName = machineImgName;
	}

	@Override
	public String toString() {
		return "BoosterPack [id=" + id + ", name=" + name + ", gemPrice="
				+ gemPrice + ", listBackgroundImgName=" + listBackgroundImgName
				+ ", listDescription=" + listDescription + ", navBarImgName="
				+ navBarImgName + ", navTitleImgName=" + navTitleImgName
				+ ", machineImgName=" + machineImgName + "]";
	}
	
}
