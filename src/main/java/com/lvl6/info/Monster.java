package com.lvl6.info;

import java.io.Serializable;

import com.lvl6.proto.MonsterStuffProto.MonsterProto.MonsterElement;
import com.lvl6.proto.MonsterStuffProto.MonsterProto.MonsterQuality;

public class Monster implements Serializable {

	private static final long serialVersionUID = 3952925405581173817L;
	
	private int id;
	private String name;
	private String monsterGroup;
	private MonsterQuality quality;
	private int evolutionLevel;
	private String displayName;
	private MonsterElement element;
	private String imagePrefix;
	private int numPuzzlePieces;
	private int minutesToCombinePieces;
	private int maxLevel; //aka max enhancing level
	private int evolutionMonsterId;
	private int evolutionCatalystMonsterId;
	private int minutesToEvolve;
	private int numCatalystsRequired; //will most likely be 1
	private String carrotRecruited;
	private String carrotDefeated;
	private String carrotEvolved;
	private String description;
	private int evolutionCost; //oil not cash
	
	public Monster(int id, String name, String monsterGroup,
			MonsterQuality quality, int evolutionLevel, String displayName,
			MonsterElement element, String imagePrefix, int numPuzzlePieces,
			int minutesToCombinePieces, int maxLevel, int evolutionMonsterId,
			int evolutionCatalystMonsterId, int minutesToEvolve,
			int numCatalystsRequired, String carrotRecruited, String carrotDefeated,
			String carrotEvolved, String description, int evolutionCost) {
		super();
		this.id = id;
		this.name = name;
		this.monsterGroup = monsterGroup;
		this.quality = quality;
		this.evolutionLevel = evolutionLevel;
		this.displayName = displayName;
		this.element = element;
		this.imagePrefix = imagePrefix;
		this.numPuzzlePieces = numPuzzlePieces;
		this.minutesToCombinePieces = minutesToCombinePieces;
		this.maxLevel = maxLevel;
		this.evolutionMonsterId = evolutionMonsterId;
		this.evolutionCatalystMonsterId = evolutionCatalystMonsterId;
		this.minutesToEvolve = minutesToEvolve;
		this.numCatalystsRequired = numCatalystsRequired;
		this.carrotRecruited = carrotRecruited;
		this.carrotDefeated = carrotDefeated;
		this.carrotEvolved = carrotEvolved;
		this.description = description;
		this.evolutionCost = evolutionCost;
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

	public int getEvolutionCost() {
		return evolutionCost;
	}

	public void setEvolutionCost(int evolutionCost) {
		this.evolutionCost = evolutionCost;
	}

	@Override
	public String toString() {
		return "Monster [id=" + id + ", name=" + name + ", monsterGroup="
				+ monsterGroup + ", quality=" + quality + ", evolutionLevel="
				+ evolutionLevel + ", displayName=" + displayName + ", element="
				+ element + ", imagePrefix=" + imagePrefix + ", numPuzzlePieces="
				+ numPuzzlePieces + ", minutesToCombinePieces="
				+ minutesToCombinePieces + ", maxLevel=" + maxLevel
				+ ", evolutionMonsterId=" + evolutionMonsterId
				+ ", evolutionCatalystMonsterId=" + evolutionCatalystMonsterId
				+ ", minutesToEvolve=" + minutesToEvolve + ", numCatalystsRequired="
				+ numCatalystsRequired + ", carrotRecruited=" + carrotRecruited
				+ ", carrotDefeated=" + carrotDefeated + ", carrotEvolved="
				+ carrotEvolved + ", description=" + description + ", evolutionCost="
				+ evolutionCost + "]";
	}
	
}
