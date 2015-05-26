package com.lvl6.info;

import java.io.Serializable;

public class TaskStage implements Serializable {

	private static final long serialVersionUID = -8572346492851995969L;

	private int id;
	private int taskId;
	private int stageNum;
	private boolean attackerAlwaysHitsFirst;

	public TaskStage(int id, int taskId, int stageNum,
			boolean attackerAlwaysHitsFirst) {
		super();
		this.id = id;
		this.taskId = taskId;
		this.stageNum = stageNum;
		this.attackerAlwaysHitsFirst = attackerAlwaysHitsFirst;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public int getStageNum() {
		return stageNum;
	}

	public void setStageNum(int stageNum) {
		this.stageNum = stageNum;
	}

	public boolean isAttackerAlwaysHitsFirst() {
		return attackerAlwaysHitsFirst;
	}

	public void setAttackerAlwaysHitsFirst(boolean attackerAlwaysHitsFirst) {
		this.attackerAlwaysHitsFirst = attackerAlwaysHitsFirst;
	}

	@Override
	public String toString() {
		return "TaskStage [id=" + id + ", taskId=" + taskId + ", stageNum="
				+ stageNum + ", attackerAlwaysHitsFirst="
				+ attackerAlwaysHitsFirst + "]";
	}

}
