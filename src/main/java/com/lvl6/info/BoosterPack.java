package com.lvl6.info;

import java.io.Serializable;

public class BoosterPack implements Serializable {

	private static final long serialVersionUID = 1549953377153488834L;

	private int id;
	private String name;
	private int gemPrice;//prices for all booster packs should be same
	private int gachaCreditsPrice;
	private String listBackgroundImgName;
	private String listDescription;
	private String navBarImgName;
	private String navTitleImgName;
	private String machineImgName;
	private int expPerItem;
	private boolean displayToUser;
	private int riggedId; //if pack is/isn't rigged then this is/isn't set
	private String type;

	public BoosterPack(int id, String name, int gemPrice, int gachaCreditsPrice,
			String listBackgroundImgName, String listDescription,
			String navBarImgName, String navTitleImgName,
			String machineImgName, int expPerItem, boolean displayToUser,
			int riggedId, String type) {
		super();
		this.id = id;
		this.name = name;
		this.gemPrice = gemPrice;
		this.gachaCreditsPrice = gachaCreditsPrice;
		this.listBackgroundImgName = listBackgroundImgName;
		this.listDescription = listDescription;
		this.navBarImgName = navBarImgName;
		this.navTitleImgName = navTitleImgName;
		this.machineImgName = machineImgName;
		this.expPerItem = expPerItem;
		this.displayToUser = displayToUser;
		this.riggedId = riggedId;
		this.type = type;
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

	public int getExpPerItem() {
		return expPerItem;
	}

	public void setExpPerItem(int expPerItem) {
		this.expPerItem = expPerItem;
	}

	public boolean isDisplayToUser() {
		return displayToUser;
	}

	public void setDisplayToUser(boolean displayToUser) {
		this.displayToUser = displayToUser;
	}

	public int getRiggedId() {
		return riggedId;
	}

	public void setRiggedId(int riggedId) {
		this.riggedId = riggedId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getGachaCreditsPrice() {
		return gachaCreditsPrice;
	}

	public void setGachaCreditsPrice(int gachaCreditsPrice) {
		this.gachaCreditsPrice = gachaCreditsPrice;
	}

	@Override
	public String toString() {
		return "BoosterPack [id=" + id + ", name=" + name + ", gemPrice="
				+ gemPrice + ", gachaCreditsPrice=" + gachaCreditsPrice
				+ ", listBackgroundImgName=" + listBackgroundImgName
				+ ", listDescription=" + listDescription + ", navBarImgName="
				+ navBarImgName + ", navTitleImgName=" + navTitleImgName
				+ ", machineImgName=" + machineImgName + ", expPerItem="
				+ expPerItem + ", displayToUser=" + displayToUser
				+ ", riggedId=" + riggedId + ", type=" + type + "]";
	}

}
