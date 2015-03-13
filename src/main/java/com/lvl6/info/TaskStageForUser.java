package com.lvl6.info;

import java.io.Serializable;

public class TaskStageForUser implements Serializable {

	private static final long serialVersionUID = -5818927353360595314L;

	private String id;
	private String userTaskId;
	private int stageNum;
	private int taskStageMonsterId; //not task stage monster monster id
	private String monsterType;
	private int expGained;
	private int cashGained;
	private int oilGained;
	private boolean monsterPieceDropped;
	private int itemIdDropped;
	//maybe should specify the (enhancement) level of monster dropped
	private boolean attackedFirst;

	public TaskStageForUser() {
	}

	public TaskStageForUser(String id, String userTaskId, int stageNum,
			int taskStageMonsterId, String monsterType, int expGained,
			int cashGained, int oilGained, boolean monsterPieceDropped,
			int itemIdDropped, boolean attackedFirst) {
		super();
		this.id = id;
		this.userTaskId = userTaskId;
		this.stageNum = stageNum;
		this.taskStageMonsterId = taskStageMonsterId;
		this.monsterType = monsterType;
		this.expGained = expGained;
		this.cashGained = cashGained;
		this.oilGained = oilGained;
		this.monsterPieceDropped = monsterPieceDropped;
		this.itemIdDropped = itemIdDropped;
		this.attackedFirst = attackedFirst;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserTaskId() {
		return userTaskId;
	}

	public void setUserTaskId(String userTaskId) {
		this.userTaskId = userTaskId;
	}

	public int getStageNum() {
		return stageNum;
	}

	public void setStageNum(int stageNum) {
		this.stageNum = stageNum;
	}

	public int getTaskStageMonsterId() {
		return taskStageMonsterId;
	}

	public void setTaskStageMonsterId(int taskStageMonsterId) {
		this.taskStageMonsterId = taskStageMonsterId;
	}

	public String getMonsterType() {
		return monsterType;
	}

	public void setMonsterType(String monsterType) {
		this.monsterType = monsterType;
	}

	public int getExpGained() {
		return expGained;
	}

	public void setExpGained(int expGained) {
		this.expGained = expGained;
	}

	public int getCashGained() {
		return cashGained;
	}

	public void setCashGained(int cashGained) {
		this.cashGained = cashGained;
	}

	public int getOilGained() {
		return oilGained;
	}

	public void setOilGained(int oilGained) {
		this.oilGained = oilGained;
	}

	public boolean isMonsterPieceDropped() {
		return monsterPieceDropped;
	}

	public void setMonsterPieceDropped(boolean monsterPieceDropped) {
		this.monsterPieceDropped = monsterPieceDropped;
	}

	public int getItemIdDropped() {
		return itemIdDropped;
	}

	public void setItemIdDropped(int itemIdDropped) {
		this.itemIdDropped = itemIdDropped;
	}

	public boolean isAttackedFirst() {
		return attackedFirst;
	}

	public void setAttackedFirst(boolean attackedFirst) {
		this.attackedFirst = attackedFirst;
	}

	@Override
	public String toString() {
		return "TaskStageForUser [id=" + id + ", userTaskId=" + userTaskId
				+ ", stageNum=" + stageNum + ", taskStageMonsterId="
				+ taskStageMonsterId + ", monsterType=" + monsterType
				+ ", expGained=" + expGained + ", cashGained=" + cashGained
				+ ", oilGained=" + oilGained + ", monsterPieceDropped="
				+ monsterPieceDropped + ", itemIdDropped=" + itemIdDropped
				+ ", attackedFirst=" + attackedFirst + "]";
	}

}
