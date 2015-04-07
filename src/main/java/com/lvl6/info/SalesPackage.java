package com.lvl6.info;

import java.util.Date;

public class SalesPackage {
	
<<<<<<< HEAD
	public SalesPackage() {
		super();
		// TODO Auto-generated constructor stub
	}
=======
>>>>>>> d735b6a62f536b42e8bf5dd47b958cf6a43e1bc9
	public SalesPackage(int id, String name, double price, String uuid,
			Date timeStart, Date timeEnd, int predId) {
		super();
		this.id = id;
		this.name = name;
		this.price = price;
		this.uuid = uuid;
		this.timeStart = timeStart;
		this.timeEnd = timeEnd;
		this.predId = predId;
	}
<<<<<<< HEAD

=======
>>>>>>> d735b6a62f536b42e8bf5dd47b958cf6a43e1bc9
	private static final long serialVersionUID = 1549953377153488834L;

	private int id;
	private String name;
	private double price;
	private String uuid;
	private Date timeStart;
	private Date timeEnd;
	private int predId;
	
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
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	@Override
	public String toString() {
		return "SalesPackage [id=" + id + ", name=" + name + ", price=" + price
				+ ", uuid=" + uuid + ", timeStart=" + timeStart + ", timeEnd="
				+ timeEnd + ", predId=" + predId + "]";
	}
	
	public Date getTimeStart() {
		return timeStart;
	}
	public void setTimeStart(Date timeStart) {
		this.timeStart = timeStart;
	}
	public Date getTimeEnd() {
		return timeEnd;
	}
	public void setTimeEnd(Date timeEnd) {
		this.timeEnd = timeEnd;
	}
	public int getPredId() {
		return predId;
	}
	public void setPredId(int predId) {
		this.predId = predId;
	}
	
	

}
