package com.lvl6.info;

import java.io.Serializable;

public class Item implements Serializable {

	private static final long serialVersionUID = -1293698119576984508L;

	private int id;
	private String name;
	private String imgName;
	private String itemType;
	private int staticDataId;
	private int amount;
	private float secretGiftChance;
	private boolean alwaysDisplayToUser;

	//variable to assist in randomly selecting this Item
	private float normalizedSecretGiftProbability;

	public Item() {
		super();
	}

	public Item(int id, String name, String imgName, String itemType,
			int staticDataId, int amount, float secretGiftChance,
			boolean alwaysDisplayToUser) {
		super();
		this.id = id;
		this.name = name;
		this.imgName = imgName;
		this.itemType = itemType;
		this.staticDataId = staticDataId;
		this.amount = amount;
		this.secretGiftChance = secretGiftChance;
		this.alwaysDisplayToUser = alwaysDisplayToUser;
		this.normalizedSecretGiftProbability = normalizedSecretGiftProbability;
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

	public float getSecretGiftChance() {
		return secretGiftChance;
	}

	public void setSecretGiftChance(float secretGiftChance) {
		this.secretGiftChance = secretGiftChance;
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

	@Override
	public String toString() {
		return "Item [id=" + id + ", name=" + name + ", imgName=" + imgName
				+ ", itemType=" + itemType + ", staticDataId=" + staticDataId
				+ ", amount=" + amount + ", secretGiftChance="
				+ secretGiftChance + ", alwaysDisplayToUser="
				+ alwaysDisplayToUser + ", normalizedSecretGiftProbability="
				+ normalizedSecretGiftProbability + "]";
	}

}
