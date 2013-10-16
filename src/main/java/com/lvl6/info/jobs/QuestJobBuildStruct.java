package com.lvl6.info.jobs;

public class QuestJobBuildStruct {
  private int id;
  private int structId;
  private int quantity;
  
  public QuestJobBuildStruct(int id, int structId, int quantity) {
    this.id = id;
    this.structId = structId;
    this.quantity = quantity;
  }

  public int getId() {
    return id;
  }
  public int getStructId() {
    return structId;
  }
  public int getQuantity() {
    return quantity;
  }
	@Override
	public String toString() {
		return "QuestJobBuildStruct [id=" + id + ", structId=" + structId
				+ ", quantity=" + quantity + "]";
	}
}
