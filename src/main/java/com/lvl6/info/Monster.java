package com.lvl6.info;

import java.io.Serializable;
import java.util.Random;

public class Monster implements Serializable {

	private static final long serialVersionUID = 5982658575244016351L;
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
	private String carrotDefeated;
	private String carrotRecruited;
	private String carrotEvolved;
	private int elementOneDmg;
	private int elementTwoDmg;
	private int elementThreeDmg;
	private int elementFourDmg;
	private int elementFiveDmg;
	
	private Random rand;
	
	public Monster(int id, String name, int quality, int evolutionLevel,
			String displayName, int element, int maxHp, String imageName,
			int monsterType, int expReward, int minSilverDrop, int maxSilverDrop,
			int numPuzzlePieces, float puzzlePieceDropRate, String carrotDefeated,
			String carrotRecruited, String carrotEvolved, int elementOneDmg,
			int elementTwoDmg, int elementThreeDmg, int elementFourDmg,
			int elementFiveDmg) {
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
		this.carrotDefeated = carrotDefeated;
		this.carrotRecruited = carrotRecruited;
		this.carrotEvolved = carrotEvolved;
		this.elementOneDmg = elementOneDmg;
		this.elementTwoDmg = elementTwoDmg;
		this.elementThreeDmg = elementThreeDmg;
		this.elementFourDmg = elementFourDmg;
		this.elementFiveDmg = elementFiveDmg;
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

	public String getCarrotDefeated() {
		return carrotDefeated;
	}

	public void setCarrotDefeated(String carrotDefeated) {
		this.carrotDefeated = carrotDefeated;
	}

	public String getCarrotRecruited() {
		return carrotRecruited;
	}

	public void setCarrotRecruited(String carrotRecruited) {
		this.carrotRecruited = carrotRecruited;
	}

	public String getCarrotEvolved() {
		return carrotEvolved;
	}

	public void setCarrotEvolved(String carrotEvolved) {
		this.carrotEvolved = carrotEvolved;
	}

	public int getElementOneDmg() {
		return elementOneDmg;
	}

	public void setElementOneDmg(int elementOneDmg) {
		this.elementOneDmg = elementOneDmg;
	}

	public int getElementTwoDmg() {
		return elementTwoDmg;
	}

	public void setElementTwoDmg(int elementTwoDmg) {
		this.elementTwoDmg = elementTwoDmg;
	}

	public int getElementThreeDmg() {
		return elementThreeDmg;
	}

	public void setElementThreeDmg(int elementThreeDmg) {
		this.elementThreeDmg = elementThreeDmg;
	}

	public int getElementFourDmg() {
		return elementFourDmg;
	}

	public void setElementFourDmg(int elementFourDmg) {
		this.elementFourDmg = elementFourDmg;
	}

	public int getElementFiveDmg() {
		return elementFiveDmg;
	}

	public void setElementFiveDmg(int elementFiveDmg) {
		this.elementFiveDmg = elementFiveDmg;
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
				+ ", puzzlePieceDropRate=" + puzzlePieceDropRate + ", carrotDefeated="
				+ carrotDefeated + ", carrotRecruited=" + carrotRecruited
				+ ", carrotEvolved=" + carrotEvolved + ", elementOneDmg="
				+ elementOneDmg + ", elementTwoDmg=" + elementTwoDmg
				+ ", elementThreeDmg=" + elementThreeDmg + ", elementFourDmg="
				+ elementFourDmg + ", elementFiveDmg=" + elementFiveDmg + "]";
	}

}
