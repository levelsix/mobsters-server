package com.lvl6.info;

import java.io.Serializable;

public class Item implements Serializable {

	private static final long serialVersionUID = 8650746165009271972L;

	private int id;
	private String name;
	private String shortName;
	private String imgName;
	private String itemType;
	private int staticDataId;
	private int amount;
	private boolean alwaysDisplayToUser;
	private String gameActionType;
	private String quality;

	//variable to assist in randomly selecting this Item
	private float normalizedSecretGiftProbability;

	public Item() {
		super();
	}

	public Item(int id, String name, String shortName, String imgName,
			String itemType, int staticDataId, int amount,
			boolean alwaysDisplayToUser,
			String gameActionType, String quality)
 	{
		super();
		this.id = id;
		this.name = name;
		this.shortName = shortName;
		this.imgName = imgName;
		this.itemType = itemType;
		this.staticDataId = staticDataId;
		this.amount = amount;
		this.alwaysDisplayToUser = alwaysDisplayToUser;
		this.gameActionType = gameActionType;
		this.quality = quality;

		//this.normalizedSecretGiftProbability = normalizedSecretGiftProbability;
	}





	public String getGameActionType() {
		return gameActionType;
	}

	public void setGameActionType(String gameActionType) {
		this.gameActionType = gameActionType;
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

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public int getStaticDataId() {
		return staticDataId;
	}

	public void setStaticDataId(int staticDataId) {
		this.staticDataId = staticDataId;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public float getNormalizedSecretGiftProbability() {
		return normalizedSecretGiftProbability;
	}

	public void setNormalizedSecretGiftProbability(
			float normalizedSecretGiftProbability) {
		this.normalizedSecretGiftProbability = normalizedSecretGiftProbability;
	}

	public boolean isAlwaysDisplayToUser() {
		return alwaysDisplayToUser;
	}

	public void setAlwaysDisplayToUser(boolean alwaysDisplayToUser) {
		this.alwaysDisplayToUser = alwaysDisplayToUser;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getQuality() {
		return quality;
	}

	public void setQuality(String quality) {
		this.quality = quality;
	}

	@Override
	public String toString() {
		return "Item [id=" + id + ", name=" + name + ", shortName=" + shortName
				+ ", imgName=" + imgName + ", itemType=" + itemType
				+ ", staticDataId=" + staticDataId + ", amount=" + amount
				+ ", alwaysDisplayToUser=" + alwaysDisplayToUser
				+ ", gameActionType=" + gameActionType
				+ ", normalizedSecretGiftProbability="
				+ normalizedSecretGiftProbability + "]";
	}

}
