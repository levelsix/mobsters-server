package com.lvl6.info;

import java.io.Serializable;
import java.util.List;

import com.lvl6.proto.QuestProto.SpecialQuestAction;

public class Quest implements Serializable {
	
	private static final long serialVersionUID = -6765884357882058627L;
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
  private int equipIdGained;
  private List<Integer> questsRequiredForThis;
  private List<Integer> tasksRequired;
  private List<Integer> upgradeStructJobsRequired;
  private List<Integer> buildStructJobsRequired;
  private int coinRetrievalAmountRequired;
  private SpecialQuestAction specialQuestActionRequired;
  private String goodQuestGiverImageSuffix;
  private int priority;
  private String carrotId;



	public Quest(int id, int cityId, String goodName, String goodDescription,
			String goodDoneResponse, Dialogue goodAcceptDialogue,
			int assetNumWithinCity, int coinsGained, int diamondsGained,
			int expGained, int equipIdGained, List<Integer> questsRequiredForThis,
			List<Integer> tasksRequired, List<Integer> upgradeStructJobsRequired,
			List<Integer> buildStructJobsRequired, int coinRetrievalAmountRequired,
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
		this.equipIdGained = equipIdGained;
		this.questsRequiredForThis = questsRequiredForThis;
		this.tasksRequired = tasksRequired;
		this.upgradeStructJobsRequired = upgradeStructJobsRequired;
		this.buildStructJobsRequired = buildStructJobsRequired;
		this.coinRetrievalAmountRequired = coinRetrievalAmountRequired;
		this.specialQuestActionRequired = specialQuestActionRequired;
		this.goodQuestGiverImageSuffix = goodQuestGiverImageSuffix;
		this.priority = priority;
		this.carrotId = carrotId;
	}

	public int getPriority() {
    return priority;
  }

  public void setPriority(int priority) {
    this.priority = priority;
  }

  public int getId() {
    return id;
  }

  public int getCityId() {
    return cityId;
  }

  public String getGoodName() {
    return goodName;
  }

  public String getGoodDescription() {
    return goodDescription;
  }

  public String getGoodDoneResponse() {
    return goodDoneResponse;
  }

  public Dialogue getGoodAcceptDialogue() {
    return goodAcceptDialogue;
  }

  public int getAssetNumWithinCity() {
    return assetNumWithinCity;
  }

  public int getCoinsGained() {
    return coinsGained;
  }

  public int getDiamondsGained() {
    return diamondsGained;
  }

  public int getExpGained() {
    return expGained;
  }

  public int getEquipIdGained() {
    return equipIdGained;
  }

  public List<Integer> getQuestsRequiredForThis() {
    return questsRequiredForThis;
  }

  public List<Integer> getTasksRequired() {
    return tasksRequired;
  }

  public List<Integer> getUpgradeStructJobsRequired() {
    return upgradeStructJobsRequired;
  }

  public List<Integer> getBuildStructJobsRequired() {
    return buildStructJobsRequired;
  }

  public int getCoinRetrievalAmountRequired() {
    return coinRetrievalAmountRequired;
  }

  public SpecialQuestAction getSpecialQuestActionRequired() {
    return specialQuestActionRequired;
  }

  public String getGoodQuestGiverImageSuffix() {
    return goodQuestGiverImageSuffix;
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

	public void setId(int id) {
		this.id = id;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public void setGoodName(String goodName) {
		this.goodName = goodName;
	}

	public void setGoodDescription(String goodDescription) {
		this.goodDescription = goodDescription;
	}

	public void setGoodDoneResponse(String goodDoneResponse) {
		this.goodDoneResponse = goodDoneResponse;
	}

	public void setGoodAcceptDialogue(Dialogue goodAcceptDialogue) {
		this.goodAcceptDialogue = goodAcceptDialogue;
	}

	public void setAssetNumWithinCity(int assetNumWithinCity) {
		this.assetNumWithinCity = assetNumWithinCity;
	}

	public void setCoinsGained(int coinsGained) {
		this.coinsGained = coinsGained;
	}

	public void setDiamondsGained(int diamondsGained) {
		this.diamondsGained = diamondsGained;
	}

	public void setExpGained(int expGained) {
		this.expGained = expGained;
	}

	public void setEquipIdGained(int equipIdGained) {
		this.equipIdGained = equipIdGained;
	}

	public void setQuestsRequiredForThis(List<Integer> questsRequiredForThis) {
		this.questsRequiredForThis = questsRequiredForThis;
	}

	public void setTasksRequired(List<Integer> tasksRequired) {
		this.tasksRequired = tasksRequired;
	}

	public void setUpgradeStructJobsRequired(List<Integer> upgradeStructJobsRequired) {
		this.upgradeStructJobsRequired = upgradeStructJobsRequired;
	}

	public void setBuildStructJobsRequired(List<Integer> buildStructJobsRequired) {
		this.buildStructJobsRequired = buildStructJobsRequired;
	}

	public void setCoinRetrievalAmountRequired(int coinRetrievalAmountRequired) {
		this.coinRetrievalAmountRequired = coinRetrievalAmountRequired;
	}

	public void setSpecialQuestActionRequired(
			SpecialQuestAction specialQuestActionRequired) {
		this.specialQuestActionRequired = specialQuestActionRequired;
	}

	public void setGoodQuestGiverImageSuffix(String goodQuestGiverImageSuffix) {
		this.goodQuestGiverImageSuffix = goodQuestGiverImageSuffix;
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
				+ expGained + ", equipIdGained=" + equipIdGained
				+ ", questsRequiredForThis=" + questsRequiredForThis
				+ ", tasksRequired=" + tasksRequired + ", upgradeStructJobsRequired="
				+ upgradeStructJobsRequired + ", buildStructJobsRequired="
				+ buildStructJobsRequired + ", coinRetrievalAmountRequired="
				+ coinRetrievalAmountRequired + ", specialQuestActionRequired="
				+ specialQuestActionRequired + ", goodQuestGiverImageSuffix="
				+ goodQuestGiverImageSuffix + ", priority=" + priority + ", carrotId="
				+ carrotId + "]";
	}

}
