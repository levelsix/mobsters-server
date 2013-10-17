package com.lvl6.info;

import java.io.Serializable;

public class TaskStageMonster implements Serializable {

	private static final long serialVersionUID = 6187452147997498630L;
	private int stageId;
  private int monsterId;
  
  public TaskStageMonster(int stageId, int monsterId) {
		super();
		this.stageId = stageId;
		this.monsterId = monsterId;
	}

  public int getStageId() {
		return stageId;
	}

	public void setStageId(int stageId) {
		this.stageId = stageId;
	}

	public int getMonsterId() {
		return monsterId;
	}

	public void setMonsterId(int monsterId) {
		this.monsterId = monsterId;
	}

	@Override
	public String toString() {
		return "TaskStageMonster [stageId=" + stageId + ", monsterId=" + monsterId
				+ "]";
	}

}
