package com.lvl6.info;

import java.io.Serializable;

public class UserTaskStage implements Serializable {

	private static final long serialVersionUID = 5794056622341011286L;
	private long id;
	private long userTaskId;
	private int stageNum;
	public int monsterId;
	public int expGained;
	public int silverGained;
	public boolean monsterPieceDropped;
	
	public UserTaskStage(long id, long userTaskId, int stageNum, int monsterId,
			int expGained, int silverGained, boolean monsterPieceDropped) {
		super();
		this.id = id;
		this.userTaskId = userTaskId;
		this.stageNum = stageNum;
		this.monsterId = monsterId;
		this.expGained = expGained;
		this.silverGained = silverGained;
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

	public int getMonsterId() {
		return monsterId;
	}

	public void setMonsterId(int monsterId) {
		this.monsterId = monsterId;
	}

	public int getExpGained() {
		return expGained;
	}

	public void setExpGained(int expGained) {
		this.expGained = expGained;
	}

	public int getSilverGained() {
		return silverGained;
	}

	public void setSilverGained(int silverGained) {
		this.silverGained = silverGained;
	}

	public boolean isMonsterPieceDropped() {
		return monsterPieceDropped;
	}

	public void setMonsterPieceDropped(boolean monsterPieceDropped) {
		this.monsterPieceDropped = monsterPieceDropped;
	}

	@Override
	public String toString() {
		return "UserTaskStage [id=" + id + ", userTaskId=" + userTaskId
				+ ", stageNum=" + stageNum + ", monsterId=" + monsterId
				+ ", expGained=" + expGained + ", silverGained=" + silverGained
				+ ", monsterPieceDropped=" + monsterPieceDropped + "]";
	}
	
}
