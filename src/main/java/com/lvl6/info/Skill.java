package com.lvl6.info;

import java.io.Serializable;

public class Skill implements Serializable {

	private static final long serialVersionUID = -6123159130339183801L;

	private int id;
	private String name;
	private int orbCost;
	private String type;
	private String activationType;
	private int predecId;
	private int successorId;
	private String defensiveDesc;
	private String offensiveDesc;
	private String imgNamePrefix;
	private int skillEffectDuration;
	private String shortDefDesc;
	private String shortOffDesc;

	public Skill(int id, String name, int orbCost, String type,
			String activationType, int predecId, int successorId,
			String defensiveDesc, String offensiveDesc, String imgNamePrefix,
			int skillEffectDuration, String shortDefDesc, String shortOffDesc) {
		super();
		this.id = id;
		this.name = name;
		this.orbCost = orbCost;
		this.type = type;
		this.activationType = activationType;
		this.predecId = predecId;
		this.successorId = successorId;
		this.defensiveDesc = defensiveDesc;
		this.offensiveDesc = offensiveDesc;
		this.imgNamePrefix = imgNamePrefix;
		this.skillEffectDuration = skillEffectDuration;
		this.shortDefDesc = shortDefDesc;
		this.shortOffDesc = shortOffDesc;
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

	public int getOrbCost() {
		return orbCost;
	}

	public void setOrbCost(int orbCost) {
		this.orbCost = orbCost;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getActivationType() {
		return activationType;
	}

	public void setActivationType(String activationType) {
		this.activationType = activationType;
	}

	public int getPredecId() {
		return predecId;
	}

	public void setPredecId(int predecId) {
		this.predecId = predecId;
	}

	public int getSuccessorId() {
		return successorId;
	}

	public void setSuccessorId(int successorId) {
		this.successorId = successorId;
	}

	public String getDefensiveDesc() {
		return defensiveDesc;
	}

	public void setDefensiveDesc(String defensiveDesc) {
		this.defensiveDesc = defensiveDesc;
	}

	public String getOffensiveDesc() {
		return offensiveDesc;
	}

	public void setOffensiveDesc(String offensiveDesc) {
		this.offensiveDesc = offensiveDesc;
	}

	public String getImgNamePrefix() {
		return imgNamePrefix;
	}

	public void setImgNamePrefix(String imgNamePrefix) {
		this.imgNamePrefix = imgNamePrefix;
	}

	public int getSkillEffectDuration() {
		return skillEffectDuration;
	}

	public void setSkillEffectDuration(int skillEffectDuration) {
		this.skillEffectDuration = skillEffectDuration;
	}

	public String getShortDefDesc() {
		return shortDefDesc;
	}

	public void setShortDefDesc(String shortDefDesc) {
		this.shortDefDesc = shortDefDesc;
	}

	public String getShortOffDesc() {
		return shortOffDesc;
	}

	public void setShortOffDesc(String shortOffDesc) {
		this.shortOffDesc = shortOffDesc;
	}

	@Override
	public String toString() {
		return "Skill [id=" + id + ", name=" + name + ", orbCost=" + orbCost
				+ ", type=" + type + ", activationType=" + activationType
				+ ", predecId=" + predecId + ", successorId=" + successorId
				+ ", defensiveDesc=" + defensiveDesc + ", offensiveDesc="
				+ offensiveDesc + ", imgNamePrefix=" + imgNamePrefix
				+ ", skillEffectDuration=" + skillEffectDuration
				+ ", shortDefDesc=" + shortDefDesc + ", shortOffDesc="
				+ shortOffDesc + "]";
	}

}
