package com.lvl6.info;

import java.io.Serializable;

public class PvpLeague implements Serializable {

	private static final long serialVersionUID = -4851584695578561184L;

	private int id;
	private String leagueName;
	private String imgPrefix;
	private String description;
	private int minElo;
	private int maxElo;
	private int minRank;
	private int maxRank;
	private int predecessorLeagueId;
	private int successorLeagueId;

	public PvpLeague(int id, String leagueName, String imgPrefix,
			String description, int minElo, int maxElo, int minRank,
			int maxRank, int predecessorLeagueId, int successorLeagueId) {
		super();
		this.id = id;
		this.leagueName = leagueName;
		this.imgPrefix = imgPrefix;
		this.description = description;
		this.minElo = minElo;
		this.maxElo = maxElo;
		this.minRank = minRank;
		this.maxRank = maxRank;
		this.predecessorLeagueId = predecessorLeagueId;
		this.successorLeagueId = successorLeagueId;
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

	public int getMinRank() {
		return minRank;
	}

	public void setMinRank(int minRank) {
		this.minRank = minRank;
	}

	public int getMaxRank() {
		return maxRank;
	}

	public void setMaxRank(int maxRank) {
		this.maxRank = maxRank;
	}

	public int getPredecessorLeagueId() {
		return predecessorLeagueId;
	}

	public void setPredecessorLeagueId(int predecessorLeagueId) {
		this.predecessorLeagueId = predecessorLeagueId;
	}

	public int getSuccessorLeagueId() {
		return successorLeagueId;
	}

	public void setSuccessorLeagueId(int successorLeagueId) {
		this.successorLeagueId = successorLeagueId;
	}

	@Override
	public String toString() {
		return "PvpLeague [id=" + id + ", leagueName=" + leagueName
				+ ", imgPrefix=" + imgPrefix + ", description=" + description
				+ ", minElo=" + minElo + ", maxElo=" + maxElo + ", minRank="
				+ minRank + ", maxRank=" + maxRank + ", predecessorLeagueId="
				+ predecessorLeagueId + ", successorLeagueId="
				+ successorLeagueId + "]";
	}

}
