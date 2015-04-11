package com.lvl6.info;

public class EloPair {

	public EloPair(int minElo, int maxElo) {
		super();
		this.minElo = minElo;
		this.maxElo = maxElo;
	}
	
	private int minElo;
	private int maxElo;
	
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
		return "EloPair [minElo=" + minElo + ", maxElo=" + maxElo + "]";
	}



}
