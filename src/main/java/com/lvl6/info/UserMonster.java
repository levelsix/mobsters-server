package com.lvl6.info;

import java.io.Serializable;

public class UserMonster implements Serializable {

	private static final long serialVersionUID = 4791630361324859273L;
	private long id;
	private int userId;
	private int monsterId;
	private int evolutionLevel;
  private int enhancementPercentage;
  private int currentHealth;
  
	public UserMonster(long id, int userId, int monsterId, int evolutionLevel,
			int enhancementPercentage, int currentHealth) {
		super();
		this.id = id;
		this.userId = userId;
		this.monsterId = monsterId;
		this.evolutionLevel = evolutionLevel;
		this.enhancementPercentage = enhancementPercentage;
		this.currentHealth = currentHealth;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getMonsterId() {
		return monsterId;
	}

	public void setMonsterId(int monsterId) {
		this.monsterId = monsterId;
	}

	public int getEvolutionLevel() {
		return evolutionLevel;
	}

	public void setEvolutionLevel(int evolutionLevel) {
		this.evolutionLevel = evolutionLevel;
	}

	public int getEnhancementPercentage() {
		return enhancementPercentage;
	}

	public void setEnhancementPercentage(int enhancementPercentage) {
		this.enhancementPercentage = enhancementPercentage;
	}

	public int getCurrentHealth() {
		return currentHealth;
	}

	public void setCurrentHealth(int currentHealth) {
		this.currentHealth = currentHealth;
	}

	@Override
	public String toString() {
		return "UserMonster [id=" + id + ", userId=" + userId + ", monsterId="
				+ monsterId + ", evolutionLevel=" + evolutionLevel
				+ ", enhancementPercentage=" + enhancementPercentage
				+ ", currentHealth=" + currentHealth + "]";
	}

}
