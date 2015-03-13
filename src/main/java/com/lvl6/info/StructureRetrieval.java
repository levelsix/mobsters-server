package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class StructureRetrieval implements Serializable {

	private static final long serialVersionUID = -3115943757909999684L;

	private String userStructId;
	private Date timeOfRetrieval;
	private int amountCollected;

	public StructureRetrieval() {
		super();
	}

	public StructureRetrieval(String userStructId, Date timeOfRetrieval,
			int amountCollected) {
		super();
		this.userStructId = userStructId;
		this.timeOfRetrieval = timeOfRetrieval;
		this.amountCollected = amountCollected;
	}

	public String getUserStructId() {
		return userStructId;
	}

	public void setUserStructId(String userStructId) {
		this.userStructId = userStructId;
	}

	public Date getTimeOfRetrieval() {
		return timeOfRetrieval;
	}

	public void setTimeOfRetrieval(Date timeOfRetrieval) {
		this.timeOfRetrieval = timeOfRetrieval;
	}

	public int getAmountCollected() {
		return amountCollected;
	}

	public void setAmountCollected(int amountCollected) {
		this.amountCollected = amountCollected;
	}

	@Override
	public String toString() {
		return "StructRetrieval [userStructId=" + userStructId
				+ ", amountCollected=" + amountCollected + "]";
	}

}
