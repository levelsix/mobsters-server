package com.lvl6.info;

import java.util.Date;

public class SalesPackage {

	public SalesPackage() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SalesPackage(int id, String productId, int price, String uuid,
			Date timeStart, Date timeEnd, int succId, int customMenuId) {
		super();
		this.id = id;
		this.productId = productId;
		this.price = price;
		this.uuid = uuid;
		this.timeStart = timeStart;
		this.timeEnd = timeEnd;
		this.succId = succId;
		this.customMenuId = customMenuId;
	}

	private static final long serialVersionUID = 1549953377153488834L;

	private int id;
	private String productId;
	private int price;
	private String uuid;
	private Date timeStart;
	private Date timeEnd;
	private int succId;
	private int customMenuId;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}


	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public double getPrice() {
		return price;
	}
	public void setPrice(int price) {
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
        return "SalesPackage [id=" + id + ", productId=" + productId
                + ", price=" + price + ", uuid=" + uuid + ", timeStart="
                + timeStart + ", timeEnd=" + timeEnd + ", succId=" + succId
                + ", customMenuId=" + customMenuId + "]";
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
	public int getSuccId() {
		return succId;
	}
	public void setSuccId(int succId) {
		this.succId = succId;
	}

    public int getCustomMenuId() {
        return customMenuId;
    }

    public void setCustomMenuId(int customMenuId) {
        this.customMenuId = customMenuId;
    }



}
