package com.lvl6.info.jobs;

public class QuestJobUpgradeStruct {
  private int id;
  private int structId;
  private int levelReq;
  
  public QuestJobUpgradeStruct(int id, int structId, int levelReq) {
    this.id = id;
    this.structId = structId;
    this.levelReq = levelReq;
  }
  public int getId() {
    return id;
  }
  public int getStructId() {
    return structId;
  }
  public int getLevelReq() {
    return levelReq;
  }
	@Override
	public String toString() {
		return "QuestJobUpgradeStruct [id=" + id + ", structId=" + structId
				+ ", levelReq=" + levelReq + "]";
	}
  
}
