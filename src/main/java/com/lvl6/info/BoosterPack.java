package com.lvl6.info;

import java.io.Serializable;

public class BoosterPack implements Serializable {
	
	private static final long serialVersionUID = 3734360412980331157L;
	private int id;
  private String name;
  private int gemPrice;
  
	public BoosterPack(int id, String name, int gemPrice) {
		super();
		this.id = id;
		this.name = name;
		this.gemPrice = gemPrice;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getGemPrice() {
		return gemPrice;
	}

	public void setGemPrice(int gemPrice) {
		this.gemPrice = gemPrice;
	}

	@Override
	public String toString() {
		return "BoosterPack [id=" + id + ", name=" + name + ", gemPrice="
				+ gemPrice + "]";
	}

}
