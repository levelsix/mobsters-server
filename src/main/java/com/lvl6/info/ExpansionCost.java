package com.lvl6.info;

import java.io.Serializable;

public class ExpansionCost implements Serializable {
	
	private static final long serialVersionUID = -6063293628759189565L;
	private int id;
	private int expansionCostCash;
	private int numMinutesToExpand;
	private int speedupExpansionGemCost;
	
	public ExpansionCost(int id, int expansionCostCash, int numMinutesToExpand,
			int speedupExpansionGemCost) {
		super();
		this.id = id;
		this.expansionCostCash = expansionCostCash;
		this.numMinutesToExpand = numMinutesToExpand;
		this.speedupExpansionGemCost = speedupExpansionGemCost;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getExpansionCostCash() {
		return expansionCostCash;
	}

	public void setExpansionCostCash(int expansionCostCash) {
		this.expansionCostCash = expansionCostCash;
	}

	public int getNumMinutesToExpand() {
		return numMinutesToExpand;
	}

	public void setNumMinutesToExpand(int numMinutesToExpand) {
		this.numMinutesToExpand = numMinutesToExpand;
	}

	public int getSpeedupExpansionGemCost() {
		return speedupExpansionGemCost;
	}

	public void setSpeedupExpansionGemCost(int speedupExpansionGemCost) {
		this.speedupExpansionGemCost = speedupExpansionGemCost;
	}

	@Override
	public String toString() {
		return "ExpansionCost [id=" + id + ", expansionCostCash="
				+ expansionCostCash + ", numMinutesToExpand=" + numMinutesToExpand
				+ ", speedupExpansionGemCost=" + speedupExpansionGemCost + "]";
	}	
	
}
