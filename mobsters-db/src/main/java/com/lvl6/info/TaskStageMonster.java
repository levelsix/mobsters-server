package com.lvl6.info;

import java.io.Serializable;
import java.util.Random;

public class TaskStageMonster implements Serializable {

	private static final long serialVersionUID = 3961242446373775607L;

	private int id;
	private int stageId;
	private int monsterId;
	private String monsterType;
	private int expReward;
	private int minCashDrop;
	private int maxCashDrop;
	private int minOilDrop;
	private int maxOilDrop;
	private float puzzlePieceDropRate;
	private int level;
	private float chanceToAppear;
	private float dmgMultiplier;
	private int monsterIdDrop;
	private int monsterDropLvl; //the level of the monster that is dropped
	private int defensiveSkillId;
	private String initDialogue;
	private String defaultDialogue;
	private float userToonHpScale;//used to determine the value of level, specifically in the case of cake kids.
	private float userToonAtkScale;//used to determine the value of level, specifically in the case of cake kids.

	//non persisted information
	private int offensiveSkillId;
	private Dialogue initD;
	private Dialogue defaultD;

	private Random rand;

	public TaskStageMonster(int id, int stageId, int monsterId,
			String monsterType, int expReward, int minCashDrop,
			int maxCashDrop, int minOilDrop, int maxOilDrop,
			float puzzlePieceDropRate, int level, float chanceToAppear,
			float dmgMultiplier, int monsterIdDrop, int monsterDropLvl,
			int defensiveSkillId, int offensiveSkillId, String initDialogue,
			String defaultDialogue, Dialogue initD, Dialogue defaultD,
			float userToonHpScale, float userToonAtkScale) {
		super();
		this.id = id;
		this.stageId = stageId;
		this.monsterId = monsterId;
		this.monsterType = monsterType;
		this.expReward = expReward;
		this.minCashDrop = minCashDrop;
		this.maxCashDrop = maxCashDrop;
		this.minOilDrop = minOilDrop;
		this.maxOilDrop = maxOilDrop;
		this.puzzlePieceDropRate = puzzlePieceDropRate;
		this.level = level;
		this.chanceToAppear = chanceToAppear;
		this.dmgMultiplier = dmgMultiplier;
		this.monsterIdDrop = monsterIdDrop;
		this.monsterDropLvl = monsterDropLvl;
		this.defensiveSkillId = defensiveSkillId;
		this.initDialogue = initDialogue;
		this.defaultDialogue = defaultDialogue;
		this.offensiveSkillId = offensiveSkillId;
		this.initD = initD;
		this.defaultD = defaultD;
		this.userToonHpScale = userToonHpScale;
		this.userToonAtkScale = userToonAtkScale;
	}

	//covenience methods--------------------------------------------------------
	public Random getRand() {
		return rand;
	}

	public void setRand(Random rand) {
		this.rand = rand;
	}

	public int getCashDrop() {
		//example goal: [min,max]=[5, 10], transform range to start at 0.
		//[min-min, max-min] = [0,max-min] = [0,10-5] = [0,5]
		//this means there are (10-5)+1 possible numbers

		int minMaxDiff = getMaxCashDrop() - getMinCashDrop();

		if (minMaxDiff <= 0) {
			return 0;
		}

		int randCash = rand.nextInt(minMaxDiff + 1);

		//number generated in [0, max-min] range, but need to transform
		//back to original range [min, max]. so add min. [0+min, max-min+min]
		return randCash + getMinCashDrop();
	}

	public int getOilDrop() {
		int minMaxDiff = getMaxOilDrop() - getMinOilDrop();

		if (minMaxDiff <= 0) {
			return 0;
		}

		int randOil = rand.nextInt(minMaxDiff + 1);

		//number generated in [0, max-min] range, but need to transform
		//back to original range [min, max]. so add min. [0+min, max-min+min]
		return randOil + getMinOilDrop();
	}

	public boolean didPuzzlePieceDrop() {
		float randFloat = getRand().nextFloat();

		if (randFloat < getPuzzlePieceDropRate()) {
			return true;
		} else {
			return false;
		}
	}

	//end covenience methods--------------------------------------------------------

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStageId() {
		return stageId;
	}

	public void setStageId(int stageId) {
		this.stageId = stageId;
	}

	public int getMonsterId() {
		return monsterId;
	}

	public void setMonsterId(int monsterId) {
		this.monsterId = monsterId;
	}

	public String getMonsterType() {
		return monsterType;
	}

	public void setMonsterType(String monsterType) {
		this.monsterType = monsterType;
	}

	public int getExpReward() {
		return expReward;
	}

	public void setExpReward(int expReward) {
		this.expReward = expReward;
	}

	public int getMinCashDrop() {
		return minCashDrop;
	}

	public void setMinCashDrop(int minCashDrop) {
		this.minCashDrop = minCashDrop;
	}

	public int getMaxCashDrop() {
		return maxCashDrop;
	}

	public void setMaxCashDrop(int maxCashDrop) {
		this.maxCashDrop = maxCashDrop;
	}

	public int getMinOilDrop() {
		return minOilDrop;
	}

	public void setMinOilDrop(int minOilDrop) {
		this.minOilDrop = minOilDrop;
	}

	public int getMaxOilDrop() {
		return maxOilDrop;
	}

	public void setMaxOilDrop(int maxOilDrop) {
		this.maxOilDrop = maxOilDrop;
	}

	public float getPuzzlePieceDropRate() {
		return puzzlePieceDropRate;
	}

	public void setPuzzlePieceDropRate(float puzzlePieceDropRate) {
		this.puzzlePieceDropRate = puzzlePieceDropRate;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public float getChanceToAppear() {
		return chanceToAppear;
	}

	public void setChanceToAppear(float chanceToAppear) {
		this.chanceToAppear = chanceToAppear;
	}

	public float getDmgMultiplier() {
		return dmgMultiplier;
	}

	public void setDmgMultiplier(float dmgMultiplier) {
		this.dmgMultiplier = dmgMultiplier;
	}

	public int getMonsterIdDrop() {
		return monsterIdDrop;
	}

	public void setMonsterIdDrop(int monsterIdDrop) {
		this.monsterIdDrop = monsterIdDrop;
	}

	public int getMonsterDropLvl() {
		return monsterDropLvl;
	}

	public void setMonsterDropLvl(int monsterDropLvl) {
		this.monsterDropLvl = monsterDropLvl;
	}

	public int getDefensiveSkillId() {
		return defensiveSkillId;
	}

	public void setDefensiveSkillId(int defensiveSkillId) {
		this.defensiveSkillId = defensiveSkillId;
	}

	public int getOffensiveSkillId() {
		return offensiveSkillId;
	}

	public void setOffensiveSkillId(int offensiveSkillId) {
		this.offensiveSkillId = offensiveSkillId;
	}

	public String getInitDialogue() {
		return initDialogue;
	}

	public void setInitDialogue(String initDialogue) {
		this.initDialogue = initDialogue;
	}

	public String getDefaultDialogue() {
		return defaultDialogue;
	}

	public void setDefaultDialogue(String defaultDialogue) {
		this.defaultDialogue = defaultDialogue;
	}

	public Dialogue getInitD() {
		return initD;
	}

	public void setInitD(Dialogue initD) {
		this.initD = initD;
	}

	public Dialogue getDefaultD() {
		return defaultD;
	}

	public void setDefaultD(Dialogue defaultD) {
		this.defaultD = defaultD;
	}

	public float getUserToonHpScale() {
		return userToonHpScale;
	}

	public void setUserToonHpScale(int userToonHpScale) {
		this.userToonHpScale = userToonHpScale;
	}

	public float getUserToonAtkScale() {
		return userToonAtkScale;
	}

	public void setUserToonAtkScale(int userToonAtkScale) {
		this.userToonAtkScale = userToonAtkScale;
	}

	@Override
	public String toString() {
		return "TaskStageMonster [id=" + id + ", stageId=" + stageId
				+ ", monsterId=" + monsterId + ", monsterType=" + monsterType
				+ ", expReward=" + expReward + ", minCashDrop=" + minCashDrop
				+ ", maxCashDrop=" + maxCashDrop + ", minOilDrop=" + minOilDrop
				+ ", maxOilDrop=" + maxOilDrop + ", puzzlePieceDropRate="
				+ puzzlePieceDropRate + ", level=" + level
				+ ", chanceToAppear=" + chanceToAppear + ", dmgMultiplier="
				+ dmgMultiplier + ", monsterIdDrop=" + monsterIdDrop
				+ ", monsterDropLvl=" + monsterDropLvl + ", defensiveSkillId="
				+ defensiveSkillId + ", initDialogue=" + initDialogue
				+ ", defaultDialogue=" + defaultDialogue
				+ ", offensiveSkillId=" + offensiveSkillId + ", initD=" + initD
				+ ", defaultD=" + defaultD +
				", userToonHpScale=" + userToonHpScale
				+ ", userToonAtkScale=" + userToonAtkScale + "]";
	}

}
