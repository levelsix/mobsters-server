package com.lvl6.info;

import java.io.Serializable;
import java.util.List;

public class Quest implements Serializable {

	private static final long serialVersionUID = 943974595064267438L;

	private int id;
	//	private int cityId;
	private String questName;
	private String description;
	private String doneResponse;
	private Dialogue acceptDialogue;
	//	private String questType;
	//	private String jobDescription;
	//	private int staticDataId;
	//	private int quantity;
	private int cashReward;
	private int oilReward;
	private int gemReward;
	private int expReward;
	private int monsterIdReward;
	private boolean isCompleteMonster;
	private List<Integer> questsRequiredForThis;
	private String questGiverName;
	private String questGiverImagePrefix;
	private int priority;
	private String carrotId;
	//	private boolean isAchievement;
	private String monsterElement;

	public Quest(int id, String questName, String description,
			String doneResponse, Dialogue acceptDialogue, int cashReward,
			int oilReward, int gemReward, int expReward, int monsterIdReward,
			boolean isCompleteMonster, List<Integer> questsRequiredForThis,
			String questGiverName, String questGiverImagePrefix, int priority,
			String carrotId, String monsterElement) {
		super();
		this.id = id;
		this.questName = questName;
		this.description = description;
		this.doneResponse = doneResponse;
		this.acceptDialogue = acceptDialogue;
		this.cashReward = cashReward;
		this.oilReward = oilReward;
		this.gemReward = gemReward;
		this.expReward = expReward;
		this.monsterIdReward = monsterIdReward;
		this.isCompleteMonster = isCompleteMonster;
		this.questsRequiredForThis = questsRequiredForThis;
		this.questGiverName = questGiverName;
		this.questGiverImagePrefix = questGiverImagePrefix;
		this.priority = priority;
		this.carrotId = carrotId;
		this.monsterElement = monsterElement;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getQuestName() {
		return questName;
	}

	public void setQuestName(String questName) {
		this.questName = questName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDoneResponse() {
		return doneResponse;
	}

	public void setDoneResponse(String doneResponse) {
		this.doneResponse = doneResponse;
	}

	public Dialogue getAcceptDialogue() {
		return acceptDialogue;
	}

	public void setAcceptDialogue(Dialogue acceptDialogue) {
		this.acceptDialogue = acceptDialogue;
	}

	public int getCashReward() {
		return cashReward;
	}

	public void setCashReward(int cashReward) {
		this.cashReward = cashReward;
	}

	public int getOilReward() {
		return oilReward;
	}

	public void setOilReward(int oilReward) {
		this.oilReward = oilReward;
	}

	public int getGemReward() {
		return gemReward;
	}

	public void setGemReward(int gemReward) {
		this.gemReward = gemReward;
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

	public String getQuestGiverName() {
		return questGiverName;
	}

	public void setQuestGiverName(String questGiverName) {
		this.questGiverName = questGiverName;
	}

	public String getQuestGiverImagePrefix() {
		return questGiverImagePrefix;
	}

	public void setQuestGiverImagePrefix(String questGiverImagePrefix) {
		this.questGiverImagePrefix = questGiverImagePrefix;
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

	public String getMonsterElement() {
		return monsterElement;
	}

	public void setMonsterElement(String monsterElement) {
		this.monsterElement = monsterElement;
	}

	@Override
	public String toString() {
		return "Quest [id=" + id + ", questName=" + questName
				+ ", description=" + description + ", doneResponse="
				+ doneResponse + ", acceptDialogue=" + acceptDialogue
				+ ", cashReward=" + cashReward + ", oilReward=" + oilReward
				+ ", gemReward=" + gemReward + ", expReward=" + expReward
				+ ", monsterIdReward=" + monsterIdReward
				+ ", isCompleteMonster=" + isCompleteMonster
				+ ", questsRequiredForThis=" + questsRequiredForThis
				+ ", questGiverName=" + questGiverName
				+ ", questGiverImagePrefix=" + questGiverImagePrefix
				+ ", priority=" + priority + ", carrotId=" + carrotId
				+ ", monsterElement=" + monsterElement + "]";
	}

}
