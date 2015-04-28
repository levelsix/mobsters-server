package com.lvl6.info;

import java.util.Date;

public class SalesPackage {
	
	public SalesPackage() {
		super();
		// TODO Auto-generated constructor stub
	}
	public SalesPackage(int id, String name, double price, String uuid,
			Date timeStart, Date timeEnd, int predId) {
		super();
		this.id = id;
		this.name = name;
		this.price = price;
		this.uuid = uuid;
	}

	private static final long serialVersionUID = 1549953377153488834L;

	private int id;
	private String name;
	private double price;
	private String uuid;
	
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
	
	public double getPrice() {
		return price;
	}
	
	public void setPrice(double price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "SalesPackage [id=" + id + ", name=" + name + ", price=" + price
				+ ", uuid=" + uuid + "]";
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

}
