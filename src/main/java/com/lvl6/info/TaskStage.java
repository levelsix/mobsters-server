package com.lvl6.info;

import java.io.Serializable;

public class TaskStage implements Serializable {

	private static final long serialVersionUID = -4429232440756191289L;
	private int id;
	private int taskId;
	private int stageNum;
	private float equipDropRate;

	public TaskStage(int id, int taskId, int stageNum, float equipDropRate) {
		super();
		this.id = id;
		this.taskId = taskId;
		this.stageNum = stageNum;
		this.equipDropRate = equipDropRate;
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

	public float getEquipDropRate() {
		return equipDropRate;
	}

	public void setEquipDropRate(float equipDropRate) {
		this.equipDropRate = equipDropRate;
	}

	@Override
	public String toString() {
		return "TaskStage [id=" + id + ", taskId=" + taskId + ", stageNum="
				+ stageNum + ", equipDropRate=" + equipDropRate + "]";
	}

}
