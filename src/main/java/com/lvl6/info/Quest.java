package com.lvl6.info;

import java.io.Serializable;
import java.util.List;

import com.lvl6.proto.QuestProto.SpecialQuestAction;

public class Quest implements Serializable {
	
	private static final long serialVersionUID = 8566942575371304994L;
	private int id;
  private int cityId;
  private String goodName;
  private String goodDescription;
  private String goodDoneResponse;
  private Dialogue goodAcceptDialogue;
  private int assetNumWithinCity;
  private int coinsGained;
  private int diamondsGained;
  private int expGained;
  private int monsterIdGained;
  private List<Integer> questsRequiredForThis;
  private List<Integer> tasksRequired;
  private List<Integer> upgradeStructJobsRequired;
  private List<Integer> buildStructJobsRequired;
  private List<Integer> monsterJobsRequired;
  private int coinRetrievalAmountRequired;
  private SpecialQuestAction specialQuestActionRequired;
  private String goodQuestGiverImageSuffix;
  private int priority;
  private String carrotId;



	public Quest(int id, int cityId, String goodName, String goodDescription,
			String goodDoneResponse, Dialogue goodAcceptDialogue,
			int assetNumWithinCity, int coinsGained, int diamondsGained,
			int expGained, int monsterIdGained, List<Integer> questsRequiredForThis,
			List<Integer> tasksRequired, List<Integer> upgradeStructJobsRequired,
			List<Integer> buildStructJobsRequired, List<Integer> monsterJobsRequired,
			int coinRetrievalAmountRequired,
			SpecialQuestAction specialQuestActionRequired,
			String goodQuestGiverImageSuffix, int priority, String carrotId) {
		super();
		this.id = id;
		this.cityId = cityId;
		this.goodName = goodName;
		this.goodDescription = goodDescription;
		this.goodDoneResponse = goodDoneResponse;
		this.goodAcceptDialogue = goodAcceptDialogue;
		this.assetNumWithinCity = assetNumWithinCity;
		this.coinsGained = coinsGained;
		this.diamondsGained = diamondsGained;
		this.expGained = expGained;
		this.monsterIdGained = monsterIdGained;
		this.questsRequiredForThis = questsRequiredForThis;
		this.tasksRequired = tasksRequired;
		this.upgradeStructJobsRequired = upgradeStructJobsRequired;
		this.buildStructJobsRequired = buildStructJobsRequired;
		this.monsterJobsRequired = monsterJobsRequired;
		this.coinRetrievalAmountRequired = coinRetrievalAmountRequired;
		this.specialQuestActionRequired = specialQuestActionRequired;
		this.goodQuestGiverImageSuffix = goodQuestGiverImageSuffix;
		this.priority = priority;
		this.carrotId = carrotId;
	}

	//public int getNumComponents(boolean isGoodSide) {}
  public int getNumComponents() {
    int numComponents = 0;
    if (specialQuestActionRequired != null)
      return 1;
    if (tasksRequired != null)
      numComponents += tasksRequired.size();
    if (upgradeStructJobsRequired != null)
      numComponents += upgradeStructJobsRequired.size();
    if (buildStructJobsRequired != null)
      numComponents += buildStructJobsRequired.size();
    if (coinRetrievalAmountRequired > 0)
      numComponents++;
    return numComponents;
  }
  
  public boolean isSpecialQuest() {
  	if (null == specialQuestActionRequired) {
  		return false;
  	} else {
  		return true;
  	}
  }
  
  public boolean hasBuildStructJobs() {
  	if (null == buildStructJobsRequired ||
  			buildStructJobsRequired.isEmpty()) {
  		return false;
  	} else {
  		return true;
  	}
  }
  
  public boolean hasUpgradeStructJobs() {
  	if (null == upgradeStructJobsRequired ||
  		upgradeStructJobsRequired.isEmpty()) {
  		return false;
  	} else {
  		return true;
  	}
  }


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public String getGoodName() {
		return goodName;
	}

	public void setGoodName(String goodName) {
		this.goodName = goodName;
	}

	public String getGoodDescription() {
		return goodDescription;
	}

	public void setGoodDescription(String goodDescription) {
		this.goodDescription = goodDescription;
	}

	public String getGoodDoneResponse() {
		return goodDoneResponse;
	}

	public void setGoodDoneResponse(String goodDoneResponse) {
		this.goodDoneResponse = goodDoneResponse;
	}

	public Dialogue getGoodAcceptDialogue() {
		return goodAcceptDialogue;
	}

	public void setGoodAcceptDialogue(Dialogue goodAcceptDialogue) {
		this.goodAcceptDialogue = goodAcceptDialogue;
	}

	public int getAssetNumWithinCity() {
		return assetNumWithinCity;
	}

	public void setAssetNumWithinCity(int assetNumWithinCity) {
		this.assetNumWithinCity = assetNumWithinCity;
	}

	public int getCoinsGained() {
		return coinsGained;
	}

	public void setCoinsGained(int coinsGained) {
		this.coinsGained = coinsGained;
	}

	public int getDiamondsGained() {
		return diamondsGained;
	}

	public void setDiamondsGained(int diamondsGained) {
		this.diamondsGained = diamondsGained;
	}

	public int getExpGained() {
		return expGained;
	}

	public void setExpGained(int expGained) {
		this.expGained = expGained;
	}

	public int getMonsterIdGained() {
		return monsterIdGained;
	}

	public void setMonsterIdGained(int monsterIdGained) {
		this.monsterIdGained = monsterIdGained;
	}

	public List<Integer> getQuestsRequiredForThis() {
		return questsRequiredForThis;
	}

	public void setQuestsRequiredForThis(List<Integer> questsRequiredForThis) {
		this.questsRequiredForThis = questsRequiredForThis;
	}

	public List<Integer> getTasksRequired() {
		return tasksRequired;
	}

	public void setTasksRequired(List<Integer> tasksRequired) {
		this.tasksRequired = tasksRequired;
	}

	public List<Integer> getUpgradeStructJobsRequired() {
		return upgradeStructJobsRequired;
	}

	public void setUpgradeStructJobsRequired(List<Integer> upgradeStructJobsRequired) {
		this.upgradeStructJobsRequired = upgradeStructJobsRequired;
	}

	public List<Integer> getBuildStructJobsRequired() {
		return buildStructJobsRequired;
	}

	public void setBuildStructJobsRequired(List<Integer> buildStructJobsRequired) {
		this.buildStructJobsRequired = buildStructJobsRequired;
	}

	public List<Integer> getMonsterJobsRequired() {
		return monsterJobsRequired;
	}

	public void setMonsterJobsRequired(List<Integer> monsterJobsRequired) {
		this.monsterJobsRequired = monsterJobsRequired;
	}

	public int getCoinRetrievalAmountRequired() {
		return coinRetrievalAmountRequired;
	}

	public void setCoinRetrievalAmountRequired(int coinRetrievalAmountRequired) {
		this.coinRetrievalAmountRequired = coinRetrievalAmountRequired;
	}

	public SpecialQuestAction getSpecialQuestActionRequired() {
		return specialQuestActionRequired;
	}

	public void setSpecialQuestActionRequired(
			SpecialQuestAction specialQuestActionRequired) {
		this.specialQuestActionRequired = specialQuestActionRequired;
	}

	public String getGoodQuestGiverImageSuffix() {
		return goodQuestGiverImageSuffix;
	}

	public void setGoodQuestGiverImageSuffix(String goodQuestGiverImageSuffix) {
		this.goodQuestGiverImageSuffix = goodQuestGiverImageSuffix;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getCarrotId() {
		return carrotId;
	}

	public void setCarrotId(String carrotId) {
		this.carrotId = carrotId;
	}

	@Override
	public String toString() {
		return "Quest [id=" + id + ", cityId=" + cityId + ", goodName=" + goodName
				+ ", goodDescription=" + goodDescription + ", goodDoneResponse="
				+ goodDoneResponse + ", goodAcceptDialogue=" + goodAcceptDialogue
				+ ", assetNumWithinCity=" + assetNumWithinCity + ", coinsGained="
				+ coinsGained + ", diamondsGained=" + diamondsGained + ", expGained="
				+ expGained + ", monsterIdGained=" + monsterIdGained
				+ ", questsRequiredForThis=" + questsRequiredForThis
				+ ", tasksRequired=" + tasksRequired + ", upgradeStructJobsRequired="
				+ upgradeStructJobsRequired + ", buildStructJobsRequired="
				+ buildStructJobsRequired + ", monsterJobsRequired="
				+ monsterJobsRequired + ", coinRetrievalAmountRequired="
				+ coinRetrievalAmountRequired + ", specialQuestActionRequired="
				+ specialQuestActionRequired + ", goodQuestGiverImageSuffix="
				+ goodQuestGiverImageSuffix + ", priority=" + priority + ", carrotId="
				+ carrotId + "]";
	}

}
