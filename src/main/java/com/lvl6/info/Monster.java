package com.lvl6.info;

import java.io.Serializable;

import com.lvl6.proto.MonsterStuffProto.MonsterProto.MonsterElement;
import com.lvl6.proto.MonsterStuffProto.MonsterProto.MonsterQuality;

public class Monster implements Serializable {

	private static final long serialVersionUID = 2923324178507001754L;
	private int id;
	private String name;
	private String monsterGroup;
	private MonsterQuality quality;
	private int evolutionLevel;
	private String displayName;
	private MonsterElement element;
	private int baseHp;
	private String imageName;
	private int numPuzzlePieces;
	private int elementOneDmg;
	private int elementTwoDmg;
	private int elementThreeDmg;
	private int elementFourDmg;
  private int elementFiveDmg;
  private float hpLevelMultiplier;
  private float attackLevelMultiplier;
  private int maxLevel;
  private int evolutionMonsterId;
  private String carrotRecruited;
  private String carrotDefeated;
  private String carrotEvolved;
  private String description;
  
	public Monster(int id, String name, String monsterGroup,
			MonsterQuality quality, int evolutionLevel, String displayName,
			MonsterElement element, int baseHp, String imageName,
			int numPuzzlePieces, int elementOneDmg, int elementTwoDmg,
			int elementThreeDmg, int elementFourDmg, int elementFiveDmg,
			float hpLevelMultiplier, float attackLevelMultiplier, int maxLevel,
			int evolutionMonsterId, String carrotRecruited, String carrotDefeated,
			String carrotEvolved, String description) {
		super();
		this.id = id;
		this.name = name;
		this.monsterGroup = monsterGroup;
		this.quality = quality;
		this.evolutionLevel = evolutionLevel;
		this.displayName = displayName;
		this.element = element;
		this.baseHp = baseHp;
		this.imageName = imageName;
		this.numPuzzlePieces = numPuzzlePieces;
		this.elementOneDmg = elementOneDmg;
		this.elementTwoDmg = elementTwoDmg;
		this.elementThreeDmg = elementThreeDmg;
		this.elementFourDmg = elementFourDmg;
		this.elementFiveDmg = elementFiveDmg;
		this.hpLevelMultiplier = hpLevelMultiplier;
		this.attackLevelMultiplier = attackLevelMultiplier;
		this.maxLevel = maxLevel;
		this.evolutionMonsterId = evolutionMonsterId;
		this.carrotRecruited = carrotRecruited;
		this.carrotDefeated = carrotDefeated;
		this.carrotEvolved = carrotEvolved;
		this.description = description;
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

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public int getNumPuzzlePieces() {
		return numPuzzlePieces;
	}

	public void setNumPuzzlePieces(int numPuzzlePieces) {
		this.numPuzzlePieces = numPuzzlePieces;
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

	@Override
	public String toString() {
		return "Monster [id=" + id + ", name=" + name + ", monsterGroup="
				+ monsterGroup + ", quality=" + quality + ", evolutionLevel="
				+ evolutionLevel + ", displayName=" + displayName + ", element="
				+ element + ", baseHp=" + baseHp + ", imageName=" + imageName
				+ ", numPuzzlePieces=" + numPuzzlePieces + ", elementOneDmg="
				+ elementOneDmg + ", elementTwoDmg=" + elementTwoDmg
				+ ", elementThreeDmg=" + elementThreeDmg + ", elementFourDmg="
				+ elementFourDmg + ", elementFiveDmg=" + elementFiveDmg
				+ ", hpLevelMultiplier=" + hpLevelMultiplier
				+ ", attackLevelMultiplier=" + attackLevelMultiplier + ", maxLevel="
				+ maxLevel + ", evolutionMonsterId=" + evolutionMonsterId
				+ ", carrotRecruited=" + carrotRecruited + ", carrotDefeated="
				+ carrotDefeated + ", carrotEvolved=" + carrotEvolved
				+ ", description=" + description + "]";
	}

}
