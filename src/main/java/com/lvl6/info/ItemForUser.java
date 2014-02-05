package com.lvl6.info;

import java.io.Serializable;

public class ItemForUser implements Serializable {

	private static final long serialVersionUID = 7590495608689889244L;
	private int userId;
	private int itemId;
	private int quantity;
	
	public ItemForUser(int userId, int itemId, int quantity) {
		super();
		this.userId = userId;
		this.itemId = itemId;
		this.quantity = quantity;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "ItemForUser [userId=" + userId + ", itemId=" + itemId
				+ ", quantity=" + quantity + "]";
	}
	
	
}
