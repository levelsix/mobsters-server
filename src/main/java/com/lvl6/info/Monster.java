package com.lvl6.info;

import java.io.Serializable;
import java.util.Random;

public class Monster implements Serializable {

	private static final long serialVersionUID = 1721484895968046994L;
	private int id;
	private String name;
	private int quality;
	private int evolutionLevel;
	private String displayName;
	private int element;
	private int maxHp;
	private String imageName;
	private int monsterType;
	private int expReward;
	private int minSilverDrop;
	private int maxSilverDrop;
	private int numPuzzlePieces;
	private float puzzlePieceDropRate;
	
	private Random rand;


	public Monster(int id, String name, int quality, int evolutionLevel,
			String displayName, int element, int maxHp, String imageName,
			int monsterType, int expReward, int minSilverDrop, int maxSilverDrop,
			int numPuzzlePieces, float puzzlePieceDropRate) {
		super();
		this.id = id;
		this.name = name;
		this.quality = quality;
		this.evolutionLevel = evolutionLevel;
		this.displayName = displayName;
		this.element = element;
		this.maxHp = maxHp;
		this.imageName = imageName;
		this.monsterType = monsterType;
		this.expReward = expReward;
		this.minSilverDrop = minSilverDrop;
		this.maxSilverDrop = maxSilverDrop;
		this.numPuzzlePieces = numPuzzlePieces;
		this.puzzlePieceDropRate = puzzlePieceDropRate;
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


	public int getQuality() {
		return quality;
	}


	public void setQuality(int quality) {
		this.quality = quality;
	}


	public int getEvolutionLevel() {
		return evolutionLevel;
	}


	public void setEvolutionLevel(int evolutionLevel) {
		this.evolutionLevel = evolutionLevel;
	}


	public String getDisplayName() {
		return displayName;
	}


	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}


	public int getElement() {
		return element;
	}


	public void setElement(int element) {
		this.element = element;
	}


	public int getMaxHp() {
		return maxHp;
	}


	public void setMaxHp(int maxHp) {
		this.maxHp = maxHp;
	}


	public String getImageName() {
		return imageName;
	}


	public void setImageName(String imageName) {
		this.imageName = imageName;
	}


	public int getMonsterType() {
		return monsterType;
	}


	public void setMonsterType(int monsterType) {
		this.monsterType = monsterType;
	}


	public int getExpReward() {
		return expReward;
	}


	public void setExpReward(int expReward) {
		this.expReward = expReward;
	}


	public int getMinSilverDrop() {
		return minSilverDrop;
	}


	public void setMinSilverDrop(int minSilverDrop) {
		this.minSilverDrop = minSilverDrop;
	}


	public int getMaxSilverDrop() {
		return maxSilverDrop;
	}


	public void setMaxSilverDrop(int maxSilverDrop) {
		this.maxSilverDrop = maxSilverDrop;
	}


	public int getNumPuzzlePieces() {
		return numPuzzlePieces;
	}


	public void setNumPuzzlePieces(int numPuzzlePieces) {
		this.numPuzzlePieces = numPuzzlePieces;
	}


	public float getPuzzlePieceDropRate() {
		return puzzlePieceDropRate;
	}


	public void setPuzzlePieceDropRate(float puzzlePieceDropRate) {
		this.puzzlePieceDropRate = puzzlePieceDropRate;
	}


	public Random getRand() {
		return rand;
	}

	public void setRand(Random rand) {
		this.rand = rand;
	}
	
	public int getSilverDrop() {
		//example goal: [min,max]=[5, 10], transform range to start at 0.
		//[min-min, max-min] = [0,max-min] = [0,10-5] = [0,5]
		//this means there are (10-5)+1 possible numbers
		
		int minMaxDiff = getMaxSilverDrop() - getMinSilverDrop();
		int randSilver = rand.nextInt(minMaxDiff + 1); 

		//number generated in [0, max-min] range, but need to transform
		//back to original range [min, max]. so add min. [0+min, max-min+min]
		return randSilver + getMinSilverDrop();
	}

	public boolean didPuzzlePieceDrop() {
		float randFloat = this.rand.nextFloat();
		
		if (randFloat < this.puzzlePieceDropRate) {
			return true;
		} else {
			return false;
		}
	}


	@Override
	public String toString() {
		return "Monster [id=" + id + ", name=" + name + ", quality=" + quality
				+ ", evolutionLevel=" + evolutionLevel + ", displayName=" + displayName
				+ ", element=" + element + ", maxHp=" + maxHp + ", imageName="
				+ imageName + ", monsterType=" + monsterType + ", expReward="
				+ expReward + ", minSilverDrop=" + minSilverDrop + ", maxSilverDrop="
				+ maxSilverDrop + ", numPuzzlePieces=" + numPuzzlePieces
				+ ", puzzlePieceDropRate=" + puzzlePieceDropRate + "]";
	}

}
