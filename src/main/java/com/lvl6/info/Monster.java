package com.lvl6.info;

import java.io.Serializable;

public class Monster implements Serializable {
	
	private static final long serialVersionUID = -2038969910987427104L;
	
	private int id;
	private String evolutionGroup;
	private String monsterGroup;
	private String quality;
	private int evolutionLevel;
	private String displayName;
	private String element;
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
	private String animationType;
	private int verticalPixelOffset;
	private String atkSoundFile;
	private int atkSoundAnimationFrame;
	private int atkAnimationRepeatedFramesStart;
	private int atkAnimationRepeatedFramesEnd;
	private String shorterName;
	private float shadowScaleFactor;
	private int baseOffensiveSkillId;
	private int baseDefensiveSkillId;
	private int pvpMonsterDropId;

	public Monster(int id, String evolutionGroup, String monsterGroup,
			String quality, int evolutionLevel, String displayName,
			String element, String imagePrefix, int numPuzzlePieces,
			int minutesToCombinePieces, int maxLevel, int evolutionMonsterId,
			int evolutionCatalystMonsterId, int minutesToEvolve,
			int numCatalystsRequired, String carrotRecruited,
			String carrotDefeated, String carrotEvolved, String description,
			int evolutionCost, String animationType, int verticalPixelOffset,
			String atkSoundFile, int atkSoundAnimationFrame,
			int atkAnimationRepeatedFramesStart,
			int atkAnimationRepeatedFramesEnd, String shorterName,
			float shadowScaleFactor, int baseOffensiveSkillId,
			int baseDefensiveSkillId, int pvpMonsterDropId) {
		super();
		this.id = id;
		this.evolutionGroup = evolutionGroup;
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
		this.animationType = animationType;
		this.verticalPixelOffset = verticalPixelOffset;
		this.atkSoundFile = atkSoundFile;
		this.atkSoundAnimationFrame = atkSoundAnimationFrame;
		this.atkAnimationRepeatedFramesStart = atkAnimationRepeatedFramesStart;
		this.atkAnimationRepeatedFramesEnd = atkAnimationRepeatedFramesEnd;
		this.shorterName = shorterName;
		this.shadowScaleFactor = shadowScaleFactor;
		this.baseOffensiveSkillId = baseOffensiveSkillId;
		this.baseDefensiveSkillId = baseDefensiveSkillId;
		this.pvpMonsterDropId = pvpMonsterDropId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEvolutionGroup() {
		return evolutionGroup;
	}

	public void setEvolutionGroup(String evolutionGroup) {
		this.evolutionGroup = evolutionGroup;
	}

	public String getMonsterGroup() {
		return monsterGroup;
	}

	public void setMonsterGroup(String monsterGroup) {
		this.monsterGroup = monsterGroup;
	}

	public String getQuality() {
		return quality;
	}

	public void setQuality(String quality) {
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

	public String getElement() {
		return element;
	}

	public void setElement(String element) {
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

	public String getAnimationType() {
		return animationType;
	}

	public void setAnimationType(String animationType) {
		this.animationType = animationType;
	}

	public int getVerticalPixelOffset() {
		return verticalPixelOffset;
	}

	public void setVerticalPixelOffset(int verticalPixelOffset) {
		this.verticalPixelOffset = verticalPixelOffset;
	}

	public String getAtkSoundFile() {
		return atkSoundFile;
	}

	public void setAtkSoundFile(String atkSoundFile) {
		this.atkSoundFile = atkSoundFile;
	}

	public int getAtkSoundAnimationFrame() {
		return atkSoundAnimationFrame;
	}

	public void setAtkSoundAnimationFrame(int atkSoundAnimationFrame) {
		this.atkSoundAnimationFrame = atkSoundAnimationFrame;
	}

	public int getAtkAnimationRepeatedFramesStart() {
		return atkAnimationRepeatedFramesStart;
	}

	public void setAtkAnimationRepeatedFramesStart(
			int atkAnimationRepeatedFramesStart) {
		this.atkAnimationRepeatedFramesStart = atkAnimationRepeatedFramesStart;
	}

	public int getAtkAnimationRepeatedFramesEnd() {
		return atkAnimationRepeatedFramesEnd;
	}

	public void setAtkAnimationRepeatedFramesEnd(int atkAnimationRepeatedFramesEnd) {
		this.atkAnimationRepeatedFramesEnd = atkAnimationRepeatedFramesEnd;
	}

	public String getShorterName() {
		return shorterName;
	}

	public void setShorterName(String shorterName) {
		this.shorterName = shorterName;
	}

	public float getShadowScaleFactor()
	{
		return shadowScaleFactor;
	}

	public void setShadowScaleFactor( float shadowScaleFactor )
	{
		this.shadowScaleFactor = shadowScaleFactor;
	}

	public int getBaseOffensiveSkillId()
	{
		return baseOffensiveSkillId;
	}

	public void setBaseOffensiveSkillId( int baseOffensiveSkillId )
	{
		this.baseOffensiveSkillId = baseOffensiveSkillId;
	}

	public int getBaseDefensiveSkillId()
	{
		return baseDefensiveSkillId;
	}

	public void setBaseDefensiveSkillId( int baseDefensiveSkillId )
	{
		this.baseDefensiveSkillId = baseDefensiveSkillId;
	}

	public int getPvpMonsterDropId()
	{
		return pvpMonsterDropId;
	}

	public void setPvpMonsterDropId( int pvpMonsterDropId )
	{
		this.pvpMonsterDropId = pvpMonsterDropId;
	}

	@Override
	public String toString()
	{
		return "Monster [id="
			+ id
			+ ", evolutionGroup="
			+ evolutionGroup
			+ ", monsterGroup="
			+ monsterGroup
			+ ", quality="
			+ quality
			+ ", evolutionLevel="
			+ evolutionLevel
			+ ", displayName="
			+ displayName
			+ ", element="
			+ element
			+ ", imagePrefix="
			+ imagePrefix
			+ ", numPuzzlePieces="
			+ numPuzzlePieces
			+ ", minutesToCombinePieces="
			+ minutesToCombinePieces
			+ ", maxLevel="
			+ maxLevel
			+ ", evolutionMonsterId="
			+ evolutionMonsterId
			+ ", evolutionCatalystMonsterId="
			+ evolutionCatalystMonsterId
			+ ", minutesToEvolve="
			+ minutesToEvolve
			+ ", numCatalystsRequired="
			+ numCatalystsRequired
			+ ", carrotRecruited="
			+ carrotRecruited
			+ ", carrotDefeated="
			+ carrotDefeated
			+ ", carrotEvolved="
			+ carrotEvolved
			+ ", description="
			+ description
			+ ", evolutionCost="
			+ evolutionCost
			+ ", animationType="
			+ animationType
			+ ", verticalPixelOffset="
			+ verticalPixelOffset
			+ ", atkSoundFile="
			+ atkSoundFile
			+ ", atkSoundAnimationFrame="
			+ atkSoundAnimationFrame
			+ ", atkAnimationRepeatedFramesStart="
			+ atkAnimationRepeatedFramesStart
			+ ", atkAnimationRepeatedFramesEnd="
			+ atkAnimationRepeatedFramesEnd
			+ ", shorterName="
			+ shorterName
			+ ", shadowScaleFactor="
			+ shadowScaleFactor
			+ ", baseOffensiveSkillId="
			+ baseOffensiveSkillId
			+ ", baseDefensiveSkillId="
			+ baseDefensiveSkillId
			+ ", pvpMonsterDropId="
			+ pvpMonsterDropId
			+ "]";
	}

}
