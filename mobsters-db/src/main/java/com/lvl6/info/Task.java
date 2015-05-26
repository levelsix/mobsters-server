package com.lvl6.info;

import java.io.Serializable;

public class Task implements Serializable {

	private static final long serialVersionUID = -8868875499547952534L;

	private int id;
	private String goodName;
	private String description;
	private int cityId;
	private int assetNumberWithinCity;
	private int prerequisiteTaskId;
	private int prerequisiteQuestId;
	private int boardWidth;
	private int boardHeight;
	private String groundImgPrefix;
	private String initDefeatedDialogue;
	private int expReward;
	private int boardId;

	//non persisted information
	private Dialogue initDefeatedD;

	public Task(int id, String goodName, String description, int cityId,
			int assetNumberWithinCity, int prerequisiteTaskId,
			int prerequisiteQuestId, int boardWidth, int boardHeight,
			String groundImgPrefix, String initDefeatedDialogue,
			Dialogue initDefeatedD, int expReward, int boardId) {
		super();
		this.id = id;
		this.goodName = goodName;
		this.description = description;
		this.cityId = cityId;
		this.assetNumberWithinCity = assetNumberWithinCity;
		this.prerequisiteTaskId = prerequisiteTaskId;
		this.prerequisiteQuestId = prerequisiteQuestId;
		this.boardWidth = boardWidth;
		this.boardHeight = boardHeight;
		this.groundImgPrefix = groundImgPrefix;
		this.initDefeatedDialogue = initDefeatedDialogue;
		this.initDefeatedD = initDefeatedD;
		this.expReward = expReward;
		this.boardId = boardId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getGoodName() {
		return goodName;
	}

	public void setGoodName(String goodName) {
		this.goodName = goodName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public int getAssetNumberWithinCity() {
		return assetNumberWithinCity;
	}

	public void setAssetNumberWithinCity(int assetNumberWithinCity) {
		this.assetNumberWithinCity = assetNumberWithinCity;
	}

	public int getPrerequisiteTaskId() {
		return prerequisiteTaskId;
	}

	public void setPrerequisiteTaskId(int prerequisiteTaskId) {
		this.prerequisiteTaskId = prerequisiteTaskId;
	}

	public int getPrerequisiteQuestId() {
		return prerequisiteQuestId;
	}

	public void setPrerequisiteQuestId(int prerequisiteQuestId) {
		this.prerequisiteQuestId = prerequisiteQuestId;
	}

	public int getBoardWidth() {
		return boardWidth;
	}

	public void setBoardWidth(int boardWidth) {
		this.boardWidth = boardWidth;
	}

	public int getBoardHeight() {
		return boardHeight;
	}

	public void setBoardHeight(int boardHeight) {
		this.boardHeight = boardHeight;
	}

	public String getGroundImgPrefix() {
		return groundImgPrefix;
	}

	public void setGroundImgPrefix(String groundImgPrefix) {
		this.groundImgPrefix = groundImgPrefix;
	}

	public int getExpReward() {
		return expReward;
	}

	public void setExpReward(int expReward) {
		this.expReward = expReward;
	}

	public String getInitDefeatedDialogue() {
		return initDefeatedDialogue;
	}

	public void setInitDefeatedDialogue(String initDefeatedDialogue) {
		this.initDefeatedDialogue = initDefeatedDialogue;
	}

	public Dialogue getInitDefeatedD() {
		return initDefeatedD;
	}

	public void setInitDefeatedD(Dialogue initDefeatedD) {
		this.initDefeatedD = initDefeatedD;
	}

	public int getBoardId() {
		return boardId;
	}

	public void setBoardId(int boardId) {
		this.boardId = boardId;
	}

	@Override
	public String toString() {
		return "Task [id=" + id + ", goodName=" + goodName + ", description="
				+ description + ", cityId=" + cityId
				+ ", assetNumberWithinCity=" + assetNumberWithinCity
				+ ", prerequisiteTaskId=" + prerequisiteTaskId
				+ ", prerequisiteQuestId=" + prerequisiteQuestId
				+ ", boardWidth=" + boardWidth + ", boardHeight=" + boardHeight
				+ ", groundImgPrefix=" + groundImgPrefix
				+ ", initDefeatedDialogue=" + initDefeatedDialogue
				+ ", expReward=" + expReward + ", boardId=" + boardId
				+ ", initDefeatedD=" + initDefeatedD + "]";
	}

}
