package com.lvl6.info;

import java.io.Serializable;
import java.util.List;

public class Quest implements Serializable {
	
	private static final long serialVersionUID = -1107519920555385732L;
	private int id;
  private int cityId;
  private String goodName;
  private String goodDescription;
  private String goodDoneResponse;
  private Dialogue goodAcceptDialogue;
  private int questType;
  private String jobDescription;
  private int staticDataId;
  private int quantity;
  private int coinReward;
  private int diamondReward;
  private int expReward;
  private int monsterIdReward;
  private boolean isCompleteMonster;
  private List<Integer> questsRequiredForThis;
  private String goodQuestGiverImageSuffix;
  private int priority;
  private String carrotId;
  
	public Quest(int id, int cityId, String goodName, String goodDescription,
			String goodDoneResponse, Dialogue goodAcceptDialogue, int questType,
			String jobDescription, int staticDataId, int quantity, int coinReward,
			int diamondReward, int expReward, int monsterIdReward,
			boolean isCompleteMonster, List<Integer> questsRequiredForThis,
			String goodQuestGiverImageSuffix, int priority, String carrotId) {
		super();
		this.id = id;
		this.cityId = cityId;
		this.goodName = goodName;
		this.goodDescription = goodDescription;
		this.goodDoneResponse = goodDoneResponse;
		this.goodAcceptDialogue = goodAcceptDialogue;
		this.questType = questType;
		this.jobDescription = jobDescription;
		this.staticDataId = staticDataId;
		this.quantity = quantity;
		this.coinReward = coinReward;
		this.diamondReward = diamondReward;
		this.expReward = expReward;
		this.monsterIdReward = monsterIdReward;
		this.isCompleteMonster = isCompleteMonster;
		this.questsRequiredForThis = questsRequiredForThis;
		this.goodQuestGiverImageSuffix = goodQuestGiverImageSuffix;
		this.priority = priority;
		this.carrotId = carrotId;
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

	public int getQuestType() {
		return questType;
	}

	public void setQuestType(int questType) {
		this.questType = questType;
	}

	public String getJobDescription() {
		return jobDescription;
	}

	public void setJobDescription(String jobDescription) {
		this.jobDescription = jobDescription;
	}

	public int getStaticDataId() {
		return staticDataId;
	}

	public void setStaticDataId(int staticDataId) {
		this.staticDataId = staticDataId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getCoinReward() {
		return coinReward;
	}

	public void setCoinReward(int coinReward) {
		this.coinReward = coinReward;
	}

	public int getDiamondReward() {
		return diamondReward;
	}

	public void setDiamondReward(int diamondReward) {
		this.diamondReward = diamondReward;
	}

	public int getExpReward() {
		return expReward;
	}

	public void setExpReward(int expReward) {
		this.expReward = expReward;
	}

	public int getMonsterIdReward() {
		return monsterIdReward;
	}

	public void setMonsterIdReward(int monsterIdReward) {
		this.monsterIdReward = monsterIdReward;
	}

	public boolean isCompleteMonster() {
		return isCompleteMonster;
	}

	public void setCompleteMonster(boolean isCompleteMonster) {
		this.isCompleteMonster = isCompleteMonster;
	}

	public List<Integer> getQuestsRequiredForThis() {
		return questsRequiredForThis;
	}

	public void setQuestsRequiredForThis(List<Integer> questsRequiredForThis) {
		this.questsRequiredForThis = questsRequiredForThis;
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
				+ ", questType=" + questType + ", jobDescription=" + jobDescription
				+ ", staticDataId=" + staticDataId + ", quantity=" + quantity
				+ ", coinReward=" + coinReward + ", diamondReward=" + diamondReward
				+ ", expReward=" + expReward + ", monsterIdReward=" + monsterIdReward
				+ ", isCompleteMonster=" + isCompleteMonster
				+ ", questsRequiredForThis=" + questsRequiredForThis
				+ ", goodQuestGiverImageSuffix=" + goodQuestGiverImageSuffix
				+ ", priority=" + priority + ", carrotId=" + carrotId + "]";
	}
  
}
