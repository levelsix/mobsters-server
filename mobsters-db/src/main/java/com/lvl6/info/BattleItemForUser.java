package com.lvl6.info;

import java.io.Serializable;

public class BattleItemForUser implements Serializable {

	private static final long serialVersionUID = -2832799382867678845L;

	private String id;
	private String userId;
	private int battleItemId;
	private int quantity;

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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
