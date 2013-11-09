package com.lvl6.info;

import java.io.Serializable;

public class ExpansionCost implements Serializable {
	
	private static final long serialVersionUID = 1486947262199932773L;
	private int id;
	private int expansionCostCash;
	private int numMinutesToExpand;
	
	public ExpansionCost(int id, int expansionCostCash, int numMinutesToExpand) {
		super();
		this.id = id;
		this.expansionCostCash = expansionCostCash;
		this.numMinutesToExpand = numMinutesToExpand;
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

	@Override
	public String toString() {
		return "ExpansionCost [id=" + id + ", expansionCostCash="
				+ expansionCostCash + ", numMinutesToExpand=" + numMinutesToExpand
				+ "]";
	}
	
}
