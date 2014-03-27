package com.lvl6.info;

import java.io.Serializable;

public class PvpLeague implements Serializable {

	private static final long serialVersionUID = 4071892938955324752L;
	
	private int id;
	private String leagueName;
	private String imgPrefix;
	private int numRanks;
	private String description;
	private int minElo;
	private int maxElo;
	
	public PvpLeague(int id, String leagueName, String imgPrefix, int numRanks,
			String description, int minElo, int maxElo) {
		super();
		this.id = id;
		this.leagueName = leagueName;
		this.imgPrefix = imgPrefix;
		this.numRanks = numRanks;
		this.description = description;
		this.minElo = minElo;
		this.maxElo = maxElo;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLeagueName() {
		return leagueName;
	}

	public void setLeagueName(String leagueName) {
		this.leagueName = leagueName;
	}

	public String getImgPrefix() {
		return imgPrefix;
	}

	public void setImgPrefix(String imgPrefix) {
		this.imgPrefix = imgPrefix;
	}

	public int getNumRanks() {
		return numRanks;
	}

	public void setNumRanks(int numRanks) {
		this.numRanks = numRanks;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getMinElo() {
		return minElo;
	}

	public void setMinElo(int minElo) {
		this.minElo = minElo;
	}

	public int getMaxElo() {
		return maxElo;
	}

	public void setMaxElo(int maxElo) {
		this.maxElo = maxElo;
	}

	@Override
	public String toString() {
		return "PvpLeague [id=" + id + ", leagueName=" + leagueName
				+ ", imgPrefix=" + imgPrefix + ", numRanks=" + numRanks
				+ ", description=" + description + ", minElo=" + minElo
				+ ", maxElo=" + maxElo + "]";
	}
	
}
