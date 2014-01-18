package com.lvl6.info;

import java.io.Serializable;

import com.lvl6.proto.MonsterStuffProto.MonsterProto.MonsterElement;
import com.lvl6.proto.MonsterStuffProto.MonsterProto.MonsterQuality;

public class Monster implements Serializable {

	private static final long serialVersionUID = -3068933333874383134L;
	private int id;
	private String name;
	private String monsterGroup;
	private MonsterQuality quality;
	private int evolutionLevel;
	private String displayName;
	private MonsterElement element;
	private int baseHp;
	private String imagePrefix;
	private int numPuzzlePieces;
	private int minutesToCombinePieces;
	private int elementOneDmg;
	private int elementTwoDmg;
	private int elementThreeDmg;
	private int elementFourDmg;
	private int elementFiveDmg;
	private int elementSixDmg;
	private float hpLevelMultiplier;
	private float attackLevelMultiplier;
	private int maxLevel;
	private int evolutionMonsterId;
	private String carrotRecruited;
	private String carrotDefeated;
	private String carrotEvolved;
	private String description;
	private int evolutionCatalystMonsterId;
	private int minutesToEvolve;
	private int numCatalystsRequired;
	
	public Monster(int id, String name, String monsterGroup,
			MonsterQuality quality, int evolutionLevel, String displayName,
			MonsterElement element, int baseHp, String imagePrefix,
			int numPuzzlePieces, int minutesToCombinePieces, int elementOneDmg,
			int elementTwoDmg, int elementThreeDmg, int elementFourDmg,
			int elementFiveDmg, int elementSixDmg, float hpLevelMultiplier,
			float attackLevelMultiplier, int maxLevel, int evolutionMonsterId,
			String carrotRecruited, String carrotDefeated, String carrotEvolved,
			String description, int evolutionCatalystMonsterId, int minutesToEvolve,
			int numCatalystsRequired) {
		super();
		this.id = id;
		this.name = name;
		this.monsterGroup = monsterGroup;
		this.quality = quality;
		this.evolutionLevel = evolutionLevel;
		this.displayName = displayName;
		this.element = element;
		this.baseHp = baseHp;
		this.imagePrefix = imagePrefix;
		this.numPuzzlePieces = numPuzzlePieces;
		this.minutesToCombinePieces = minutesToCombinePieces;
		this.elementOneDmg = elementOneDmg;
		this.elementTwoDmg = elementTwoDmg;
		this.elementThreeDmg = elementThreeDmg;
		this.elementFourDmg = elementFourDmg;
		this.elementFiveDmg = elementFiveDmg;
		this.elementSixDmg = elementSixDmg;
		this.hpLevelMultiplier = hpLevelMultiplier;
		this.attackLevelMultiplier = attackLevelMultiplier;
		this.maxLevel = maxLevel;
		this.evolutionMonsterId = evolutionMonsterId;
		this.carrotRecruited = carrotRecruited;
		this.carrotDefeated = carrotDefeated;
		this.carrotEvolved = carrotEvolved;
		this.description = description;
		this.evolutionCatalystMonsterId = evolutionCatalystMonsterId;
		this.minutesToEvolve = minutesToEvolve;
		this.numCatalystsRequired = numCatalystsRequired;
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

	public String getMonsterGroup() {
		return monsterGroup;
	}

	public void setMonsterGroup(String monsterGroup) {
		this.monsterGroup = monsterGroup;
	}

	public MonsterQuality getQuality() {
		return quality;
	}

	public void setQuality(MonsterQuality quality) {
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

	public MonsterElement getElement() {
		return element;
	}

	public void setElement(MonsterElement element) {
		this.element = element;
	}

	public int getBaseHp() {
		return baseHp;
	}

	public void setBaseHp(int baseHp) {
		this.baseHp = baseHp;
	}

	public String getImagePrefix() {
		return imagePrefix;
	}

	public void setImagePrefix(String imagePrefix) {
		this.imagePrefix = imagePrefix;
	}

	public int getNumPuzzlePieces() {
		return numPuzzlePieces;
	}

	public void setNumPuzzlePieces(int numPuzzlePieces) {
		this.numPuzzlePieces = numPuzzlePieces;
	}

	public int getMinutesToCombinePieces() {
		return minutesToCombinePieces;
	}

	public void setMinutesToCombinePieces(int minutesToCombinePieces) {
		this.minutesToCombinePieces = minutesToCombinePieces;
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

	public int getElementSixDmg() {
		return elementSixDmg;
	}

	public void setElementSixDmg(int elementSixDmg) {
		this.elementSixDmg = elementSixDmg;
	}

	public float getHpLevelMultiplier() {
		return hpLevelMultiplier;
	}

	public void setHpLevelMultiplier(float hpLevelMultiplier) {
		this.hpLevelMultiplier = hpLevelMultiplier;
	}

	public float getAttackLevelMultiplier() {
		return attackLevelMultiplier;
	}

	public void setAttackLevelMultiplier(float attackLevelMultiplier) {
		this.attackLevelMultiplier = attackLevelMultiplier;
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}

	public int getEvolutionMonsterId() {
		return evolutionMonsterId;
	}

	public void setEvolutionMonsterId(int evolutionMonsterId) {
		this.evolutionMonsterId = evolutionMonsterId;
	}

	public String getCarrotRecruited() {
		return carrotRecruited;
	}

	public void setCarrotRecruited(String carrotRecruited) {
		this.carrotRecruited = carrotRecruited;
	}

	public String getCarrotDefeated() {
		return carrotDefeated;
	}

	public void setCarrotDefeated(String carrotDefeated) {
		this.carrotDefeated = carrotDefeated;
	}

	public String getCarrotEvolved() {
		return carrotEvolved;
	}

	public void setCarrotEvolved(String carrotEvolved) {
		this.carrotEvolved = carrotEvolved;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getEvolutionCatalystMonsterId() {
		return evolutionCatalystMonsterId;
	}

	public void setEvolutionCatalystMonsterId(int evolutionCatalystMonsterId) {
		this.evolutionCatalystMonsterId = evolutionCatalystMonsterId;
	}

	public int getMinutesToEvolve() {
		return minutesToEvolve;
	}

	public void setMinutesToEvolve(int minutesToEvolve) {
		this.minutesToEvolve = minutesToEvolve;
	}

	public int getNumCatalystsRequired() {
		return numCatalystsRequired;
	}

	public void setNumCatalystsRequired(int numCatalystsRequired) {
		this.numCatalystsRequired = numCatalystsRequired;
	}

	@Override
	public String toString() {
		return "Monster [id=" + id + ", name=" + name + ", monsterGroup="
				+ monsterGroup + ", quality=" + quality + ", evolutionLevel="
				+ evolutionLevel + ", displayName=" + displayName + ", element="
				+ element + ", baseHp=" + baseHp + ", imagePrefix=" + imagePrefix
				+ ", numPuzzlePieces=" + numPuzzlePieces + ", minutesToCombinePieces="
				+ minutesToCombinePieces + ", elementOneDmg=" + elementOneDmg
				+ ", elementTwoDmg=" + elementTwoDmg + ", elementThreeDmg="
				+ elementThreeDmg + ", elementFourDmg=" + elementFourDmg
				+ ", elementFiveDmg=" + elementFiveDmg + ", elementSixDmg="
				+ elementSixDmg + ", hpLevelMultiplier=" + hpLevelMultiplier
				+ ", attackLevelMultiplier=" + attackLevelMultiplier + ", maxLevel="
				+ maxLevel + ", evolutionMonsterId=" + evolutionMonsterId
				+ ", carrotRecruited=" + carrotRecruited + ", carrotDefeated="
				+ carrotDefeated + ", carrotEvolved=" + carrotEvolved
				+ ", description=" + description + ", evolutionCatalystMonsterId="
				+ evolutionCatalystMonsterId + ", minutesToEvolve=" + minutesToEvolve
				+ ", numCatalystsRequired=" + numCatalystsRequired + "]";
	}

}
