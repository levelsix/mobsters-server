package com.lvl6.info;

import java.io.Serializable;

public class TaskStageMonster implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 6187452147997498630L;
  private int monsterId;
  private int stageId;
  
  public TaskStageMonster(int monsterId, int stageId) {
    super();
    this.monsterId = monsterId;
    this.stageId = stageId;
  }

  public int getMonsterId() {
    return monsterId;
  }

  public void setMonsterId(int monsterId) {
    this.monsterId = monsterId;
  }

  public int getStageId() {
    return stageId;
  }

  public void setStageId(int stageId) {
    this.stageId = stageId;
  }

  @Override
  public String toString() {
    return "TaskStageMonster [monsterId=" + monsterId + ", stageId=" + stageId
        + "]";
  }
  
}
