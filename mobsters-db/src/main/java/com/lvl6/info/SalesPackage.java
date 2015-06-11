package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class SalesPackage implements Serializable {

    private static final long serialVersionUID = 1549953377153488834L;

    private int id;
    private String productId;
    private int price;
    private String uuid;
    private int succId;
    private int priority;
    private int customMenuId;
    private String name;
    private String animatingIcon;
    private String slamIcon;
    private String titleColor;
    

	public SalesPackage() {
		super();
	}

	public SalesPackage(int id, String productId, String name, int price, String uuid,
			int succId, int priority, int customMenuId,
			String animatingIcon, String slamIcon, String titleColor) {
		super();
		this.id = id;
		this.productId = productId;
		this.name = name;
		this.price = price;
		this.uuid = uuid;
		this.succId = succId;
		this.priority = priority;
		this.customMenuId = customMenuId;
		this.animatingIcon = animatingIcon;
		this.slamIcon = slamIcon;
		this.titleColor = titleColor;
	}

	public String getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(String titleColor) {
        this.titleColor = titleColor;
    }

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

	public int getPrice() {
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
				+ ", price=" + price + ", uuid=" + uuid + ", succId=" + succId
				+ ", priority=" + priority + ", customMenuId=" + customMenuId
				+ ", name=" + name + ", animatingIcon=" + animatingIcon
				+ ", slamIcon=" + slamIcon + ", titleColor=" + titleColor + "]";
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAnimatingIcon() {
		return animatingIcon;
	}

	public void setAnimatingIcon(String animatingIcon) {
		this.animatingIcon = animatingIcon;
	}

	public String getSlamIcon() {
		return slamIcon;
	}

	public void setSlamIcon(String slamIcon) {
		this.slamIcon = slamIcon;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}



}
