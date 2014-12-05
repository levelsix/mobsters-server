package com.lvl6.info;

import java.io.Serializable;

public class ItemForUser implements Serializable {

	private static final long serialVersionUID = 586799286341148237L;
	
	private String userId;
	private int itemId;
	private int quantity;
	
	public ItemForUser() {
		super();
	}
	
	public ItemForUser(String userId, int itemId, int quantity) {
		super();
		this.userId = userId;
		this.itemId = itemId;
		this.quantity = quantity;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
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
