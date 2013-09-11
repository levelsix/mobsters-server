package com.lvl6.info;

import java.io.Serializable;

public class UserEquip implements Serializable {

	private static final long serialVersionUID = -615566509185375627L;
	private int id;
	private int userId;
	private int equipId;
	private int level;
  private int enhancementPercentage;
  private int currentDurability;

  public UserEquip(int id, int userId, int equipId, int level,
			int enhancementPercentage, int currentDurability) {
		super();
		this.id = id;
		this.userId = userId;
		this.equipId = equipId;
		this.level = level;
		this.enhancementPercentage = enhancementPercentage;
		this.currentDurability = currentDurability;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getEquipId() {
		return equipId;
	}

	public void setEquipId(int equipId) {
		this.equipId = equipId;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getEnhancementPercentage() {
		return enhancementPercentage;
	}

	public void setEnhancementPercentage(int enhancementPercentage) {
		this.enhancementPercentage = enhancementPercentage;
	}

	public int getCurrentDurability() {
		return currentDurability;
	}

	public void setCurrentDurability(int currentDurability) {
		this.currentDurability = currentDurability;
	}

	@Override
	public String toString() {
		return "UserEquip [id=" + id + ", userId=" + userId + ", equipId="
				+ equipId + ", level=" + level + ", enhancementPercentage="
				+ enhancementPercentage + ", currentDurability=" + currentDurability
				+ "]";
	}

}
