package com.lvl6.info;

import java.io.Serializable;

public class Achievement implements Serializable {

	private static final long serialVersionUID = 1739660744843110613L;
	
	private int id;
	private String achievementName;
	private String description;
	private int gemReward;
	private int lvl; 				//max value most likely 3
	private String achievementType;
	private String resourceType; 	//could be null
	private String monsterElement; 	//could be null
	private String monsterQuality;	//could be null
	private int staticDataId;		//could be 0, as in not set
	private int quantity;
	private int priority;
	private int prerequisiteId;
	private int successorId;
	private int expReward;
	
	public Achievement(int id, String achievementName, String description,
			int gemReward, int lvl, String achievementType,
			String resourceType, String monsterElement, String monsterQuality,
			int staticDataId, int quantity, int priority, int prerequisiteId,
			int successorId, int expReward) {
		super();
		this.id = id;
		this.achievementName = achievementName;
		this.description = description;
		this.gemReward = gemReward;
		this.lvl = lvl;
		this.achievementType = achievementType;
		this.resourceType = resourceType;
		this.monsterElement = monsterElement;
		this.monsterQuality = monsterQuality;
		this.staticDataId = staticDataId;
		this.quantity = quantity;
		this.priority = priority;
		this.prerequisiteId = prerequisiteId;
		this.successorId = successorId;
		this.expReward = expReward;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAchievementName() {
		return achievementName;
	}

	public void setAchievementName(String achievementName) {
		this.achievementName = achievementName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getGemReward() {
		return gemReward;
	}

	public void setGemReward(int gemReward) {
		this.gemReward = gemReward;
	}

	public int getLvl() {
		return lvl;
	}

	public void setLvl(int lvl) {
		this.lvl = lvl;
	}

	public String getAchievementType() {
		return achievementType;
	}

	public void setAchievementType(String achievementType) {
		this.achievementType = achievementType;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public String getMonsterElement() {
		return monsterElement;
	}

	public void setMonsterElement(String monsterElement) {
		this.monsterElement = monsterElement;
	}

	public String getMonsterQuality() {
		return monsterQuality;
	}

	public void setMonsterQuality(String monsterQuality) {
		this.monsterQuality = monsterQuality;
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

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getPrerequisiteId() {
		return prerequisiteId;
	}

	public void setPrerequisiteId(int prerequisiteId) {
		this.prerequisiteId = prerequisiteId;
	}

	public int getSuccessorId() {
		return successorId;
	}

	public void setSuccessorId(int successorId) {
		this.successorId = successorId;
	}

	public int getExpReward()
	{
		return expReward;
	}

	public void setExpReward( int expReward )
	{
		this.expReward = expReward;
	}

	@Override
	public String toString()
	{
		return "Achievement [id="
			+ id
			+ ", achievementName="
			+ achievementName
			+ ", description="
			+ description
			+ ", gemReward="
			+ gemReward
			+ ", lvl="
			+ lvl
			+ ", achievementType="
			+ achievementType
			+ ", resourceType="
			+ resourceType
			+ ", monsterElement="
			+ monsterElement
			+ ", monsterQuality="
			+ monsterQuality
			+ ", staticDataId="
			+ staticDataId
			+ ", quantity="
			+ quantity
			+ ", priority="
			+ priority
			+ ", prerequisiteId="
			+ prerequisiteId
			+ ", successorId="
			+ successorId
			+ ", expReward="
			+ expReward
			+ "]";
	}

}
