package com.lvl6.info;

public class SalesPackage {
	
	public SalesPackage(int id, String name, int price, String uuid) {
		super();
		this.id = id;
		this.name = name;
		this.price = price;
		this.uuid = uuid;
	}

	private static final long serialVersionUID = 1549953377153488834L;

	private int id;
	private String name;
	private int price;
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
	
	public int getPrice() {
		return price;
	}
	
	public void setPrice(int price) {
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
