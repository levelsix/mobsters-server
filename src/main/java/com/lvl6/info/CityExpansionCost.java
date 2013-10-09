package com.lvl6.info;

import java.io.Serializable;

public class CityExpansionCost implements Serializable {
	
	private static final long serialVersionUID = 4684737334322852956L;
	private int id;
	private int expansionCost;

	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getExpansionCost() {
		return expansionCost;
	}
	public void setExpansionCost(int expansionCost) {
		this.expansionCost = expansionCost;
	}
	

	public CityExpansionCost(int id, int expansionCost) {
		super();
		this.id = id;
		this.expansionCost = expansionCost;
	}
	

	@Override
	public String toString() {
		return "CityExpansionCost [id=" + id + ", expansionCost="
				+ expansionCost + "]";
	}




}

