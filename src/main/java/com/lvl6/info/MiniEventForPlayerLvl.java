package com.lvl6.info;

import java.io.Serializable;

public class MiniEventForPlayerLvl implements Serializable {

	private static final long serialVersionUID = -4761009839588722898L;

	private int id;
	private int miniEventId;
	private int playerLvlMin;
	private int playerLvlMax;
	private int tierOneMinPts;
	private int tierTwoMinPts;
	private int tierThreeMinPts;

	public MiniEventForPlayerLvl(int id, int miniEventId, int playerLvlMin,
			int playerLvlMax, int tierOneMinPts, int tierTwoMinPts,
			int tierThreeMinPts) {
		super();
		this.id = id;
		this.miniEventId = miniEventId;
		this.playerLvlMin = playerLvlMin;
		this.playerLvlMax = playerLvlMax;
		this.tierOneMinPts = tierOneMinPts;
		this.tierTwoMinPts = tierTwoMinPts;
		this.tierThreeMinPts = tierThreeMinPts;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMiniEventId() {
		return miniEventId;
	}

	public void setMiniEventId(int miniEventId) {
		this.miniEventId = miniEventId;
	}

	public int getPlayerLvlMin() {
		return playerLvlMin;
	}

	public void setPlayerLvlMin(int playerLvlMin) {
		this.playerLvlMin = playerLvlMin;
	}

	public int getPlayerLvlMax() {
		return playerLvlMax;
	}

	public void setPlayerLvlMax(int playerLvlMax) {
		this.playerLvlMax = playerLvlMax;
	}

	public int getTierOneMinPts() {
		return tierOneMinPts;
	}

	public void setTierOneMinPts(int tierOneMinPts) {
		this.tierOneMinPts = tierOneMinPts;
	}

	public int getTierTwoMinPts() {
		return tierTwoMinPts;
	}

	public void setTierTwoMinPts(int tierTwoMinPts) {
		this.tierTwoMinPts = tierTwoMinPts;
	}

	public int getTierThreeMinPts() {
		return tierThreeMinPts;
	}

	public void setTierThreeMinPts(int tierThreeMinPts) {
		this.tierThreeMinPts = tierThreeMinPts;
	}

	@Override
	public String toString() {
		return "MiniEventForPlayerLvl [id=" + id + ", miniEventId="
				+ miniEventId + ", playerLvlMin=" + playerLvlMin
				+ ", playerLvlMax=" + playerLvlMax + ", tierOneMinPts="
				+ tierOneMinPts + ", tierTwoMinPts=" + tierTwoMinPts
				+ ", tierThreeMinPts=" + tierThreeMinPts + "]";
	}

}
