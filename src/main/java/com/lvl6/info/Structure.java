package com.lvl6.info;

import java.io.Serializable;

public class Structure implements Serializable {

	private static final long serialVersionUID = -149493349319465229L;
	private int id;
	private String name;
	private int level;
	private String structType;
	private String buildResourceType;
	private int buildCost;
	private int minutesToBuild;
	private int requiredTownHallId;
	private int width;
	private int height;
	private String spriteImgName;
	private int predecessorStructId;
	private int successorStructId;
	
	public Structure(int id, String name, int level, String structType,
			String buildResourceType, int buildCost, int minutesToBuild,
			int requiredTownHallId, int width, int height, String spriteImgName,
			int predecessorStructId, int successorStructId) {
		super();
		this.id = id;
		this.name = name;
		this.level = level;
		this.structType = structType;
		this.buildResourceType = buildResourceType;
		this.buildCost = buildCost;
		this.minutesToBuild = minutesToBuild;
		this.requiredTownHallId = requiredTownHallId;
		this.width = width;
		this.height = height;
		this.spriteImgName = spriteImgName;
		this.predecessorStructId = predecessorStructId;
		this.successorStructId = successorStructId;
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

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getStructType() {
		return structType;
	}

	public void setStructType(String structType) {
		this.structType = structType;
	}

	public String getBuildResourceType() {
		return buildResourceType;
	}

	public void setBuildResourceType(String buildResourceType) {
		this.buildResourceType = buildResourceType;
	}

	public int getBuildCost() {
		return buildCost;
	}

	public void setBuildCost(int buildCost) {
		this.buildCost = buildCost;
	}

	public int getMinutesToBuild() {
		return minutesToBuild;
	}

	public void setMinutesToBuild(int minutesToBuild) {
		this.minutesToBuild = minutesToBuild;
	}

	public int getRequiredTownHallId() {
		return requiredTownHallId;
	}

	public void setRequiredTownHallId(int requiredTownHallId) {
		this.requiredTownHallId = requiredTownHallId;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getSpriteImgName() {
		return spriteImgName;
	}

	public void setSpriteImgName(String spriteImgName) {
		this.spriteImgName = spriteImgName;
	}

	public int getPredecessorStructId() {
		return predecessorStructId;
	}

	public void setPredecessorStructId(int predecessorStructId) {
		this.predecessorStructId = predecessorStructId;
	}

	public int getSuccessorStructId() {
		return successorStructId;
	}

	public void setSuccessorStructId(int successorStructId) {
		this.successorStructId = successorStructId;
	}

	@Override
	public String toString() {
		return "Structure [id=" + id + ", name=" + name + ", level=" + level
				+ ", structType=" + structType + ", buildResourceType="
				+ buildResourceType + ", buildCost=" + buildCost + ", minutesToBuild="
				+ minutesToBuild + ", requiredTownHallId=" + requiredTownHallId
				+ ", width=" + width + ", height=" + height + ", spriteImgName="
				+ spriteImgName + ", predecessorStructId=" + predecessorStructId
				+ ", successorStructId=" + successorStructId + "]";
	}

}