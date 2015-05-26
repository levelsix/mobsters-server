package com.lvl6.stats;

public class Spender {
	protected String userId;
	protected Double amountSpent;
	protected String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserId() {

		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Double getAmountSpent() {
		return amountSpent;
	}

	public void setAmountSpent(Double amountSpent) {
		this.amountSpent = amountSpent;
	}

}
