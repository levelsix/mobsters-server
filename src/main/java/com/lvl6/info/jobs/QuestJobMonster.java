package com.lvl6.info.jobs;

public class QuestJobMonster {
  private int id;
  private int monsterId;
  private int quantity;
  private int monsterJobType;

  public QuestJobMonster(int id, int monsterId, int quantity, int monsterJobType) {
		super();
		this.id = id;
		this.monsterId = monsterId;
		this.quantity = quantity;
		this.monsterJobType = monsterJobType;
	}
  
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMonsterId() {
		return monsterId;
	}

	public void setMonsterId(int monsterId) {
		this.monsterId = monsterId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getMonsterJobType() {
		return monsterJobType;
	}

	public void setMonsterJobType(int monsterJobType) {
		this.monsterJobType = monsterJobType;
	}

	@Override
	public String toString() {
		return "QuestJobMonster [id=" + id + ", monsterId=" + monsterId
				+ ", quantity=" + quantity + ", monsterJobType=" + monsterJobType + "]";
	}

}
