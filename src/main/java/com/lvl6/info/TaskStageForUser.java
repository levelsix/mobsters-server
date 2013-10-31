package com.lvl6.info;

import java.io.Serializable;

public class TaskStageForUser implements Serializable {

	private static final long serialVersionUID = -8056132207020136517L;
	private long id;
	private long userTaskId;
	private int stageNum;
	public int taskStageMonsterId;
	public int expGained;
	public int cashGained;
	public boolean monsterPieceDropped;
	
	public TaskStageForUser(long id, long userTaskId, int stageNum, int taskStageMonsterId,
			int expGained, int cashGained, boolean monsterPieceDropped) {
		super();
		this.id = id;
		this.userTaskId = userTaskId;
		this.stageNum = stageNum;
		this.taskStageMonsterId = taskStageMonsterId;
		this.expGained = expGained;
		this.cashGained = cashGained;
		this.monsterPieceDropped = monsterPieceDropped;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUserTaskId() {
		return userTaskId;
	}

	public void setUserTaskId(long userTaskId) {
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

	public boolean isMonsterPieceDropped() {
		return monsterPieceDropped;
	}

	public void setMonsterPieceDropped(boolean monsterPieceDropped) {
		this.monsterPieceDropped = monsterPieceDropped;
	}

	@Override
	public String toString() {
		return "TaskStageForUser [id=" + id + ", userTaskId=" + userTaskId
				+ ", stageNum=" + stageNum + ", taskStageMonsterId=" + taskStageMonsterId
				+ ", expGained=" + expGained + ", cashGained=" + cashGained
				+ ", monsterPieceDropped=" + monsterPieceDropped + "]";
	}

}
