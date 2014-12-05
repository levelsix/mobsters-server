package com.lvl6.info;

import java.io.Serializable;

public class TaskStage implements Serializable {

	private static final long serialVersionUID = -4429232440756191289L;
	
	private int id;
	private int taskId;
	private int stageNum;
	
	public TaskStage(int id, int taskId, int stageNum) {
		super();
		this.id = id;
		this.taskId = taskId;
		this.stageNum = stageNum;
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

	@Override
	public String toString() {
		return "TaskStage [id=" + id + ", taskId=" + taskId + ", stageNum="
				+ stageNum + "]";
	}

}
