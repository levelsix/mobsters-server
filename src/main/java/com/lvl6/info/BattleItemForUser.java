package com.lvl6.info;

import java.io.Serializable;

public class BattleItemForUser implements Serializable {

	public BattleItemForUser() {
		super();
	}
	
	public BattleItemForUser(String id, String userId, int battleItemId,
			int quantity) {
		super();
		this.id = id;
		this.userId = userId;
		this.battleItemId = battleItemId;
		this.quantity = quantity;
	}
	private static final long serialVersionUID = -1293698119576984508L;
	
	private String id;
	private String userId;
	private int battleItemId;
	private int quantity;
	public String getId() {
		return id;
	}
	
	
	public void setId(String id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getBattleItemId() {
		return battleItemId;
	}
	public void setBattleItemId(int battleItemId) {
		this.battleItemId = battleItemId;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	
	@Override
	public String toString() {
		return "BattleItemForUser [id=" + id + ", userId=" + userId
				+ ", battleItemId=" + battleItemId + ", quantity=" + quantity
				+ "]";
	}
	

}
