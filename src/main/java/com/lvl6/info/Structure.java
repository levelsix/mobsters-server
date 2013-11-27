package com.lvl6.info;

import java.io.Serializable;

public class Structure implements Serializable {

	private static final long serialVersionUID = 5378386129555703159L;
	private int id;
	private String name;
	private int level;
	private String structType;
	private String buildResourceType;
	private int buildCost;
	private int minutesToBuild;
	private int requiredTownHallLvl;
	private int width;
	private int height;
	private int predecessorStructId;
	private int successorStructId;
	private String imgName;
	private float imgVerticalPixelOffset;
	private String description;
	private String shortDescription;
	
	public Structure(int id, String name, int level, String structType,
			String buildResourceType, int buildCost, int minutesToBuild,
			int requiredTownHallLvl, int width, int height, int predecessorStructId,
			int successorStructId, String imgName, float imgVerticalPixelOffset,
			String description, String shortDescription) {
		super();
		this.id = id;
		this.name = name;
		this.level = level;
		this.structType = structType;
		this.buildResourceType = buildResourceType;
		this.buildCost = buildCost;
		this.minutesToBuild = minutesToBuild;
		this.requiredTownHallLvl = requiredTownHallLvl;
		this.width = width;
		this.height = height;
		this.predecessorStructId = predecessorStructId;
		this.successorStructId = successorStructId;
		this.imgName = imgName;
		this.imgVerticalPixelOffset = imgVerticalPixelOffset;
		this.description = description;
		this.shortDescription = shortDescription;
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

	public int getRequiredTownHallLvl() {
		return requiredTownHallLvl;
	}

	public void setRequiredTownHallLvl(int requiredTownHallLvl) {
		this.requiredTownHallLvl = requiredTownHallLvl;
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

	public String getImgName() {
		return imgName;
	}

	public void setImgName(String imgName) {
		this.imgName = imgName;
	}

	public float getImgVerticalPixelOffset() {
		return imgVerticalPixelOffset;
	}

	public void setImgVerticalPixelOffset(float imgVerticalPixelOffset) {
		this.imgVerticalPixelOffset = imgVerticalPixelOffset;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	@Override
	public String toString() {
		return "Structure [id=" + id + ", name=" + name + ", level=" + level
				+ ", structType=" + structType + ", buildResourceType="
				+ buildResourceType + ", buildCost=" + buildCost + ", minutesToBuild="
				+ minutesToBuild + ", requiredTownHallLvl=" + requiredTownHallLvl
				+ ", width=" + width + ", height=" + height + ", predecessorStructId="
				+ predecessorStructId + ", successorStructId=" + successorStructId
				+ ", imgName=" + imgName + ", imgVerticalPixelOffset="
				+ imgVerticalPixelOffset + ", description=" + description
				+ ", shortDescription=" + shortDescription + "]";
	}
	
}