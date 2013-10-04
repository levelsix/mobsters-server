package com.lvl6.info;

import java.io.Serializable;

public class UserAndEquipFail implements Serializable{
	
	private static final long serialVersionUID = 8745617855614161372L;
	private int userId;
  private int equipId;
  private int numFails;
  
	public UserAndEquipFail(int userId, int equipId, int numFails) {
		super();
		this.userId = userId;
		this.equipId = equipId;
		this.numFails = numFails;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getEquipId() {
		return equipId;
	}

	public void setEquipId(int equipId) {
		this.equipId = equipId;
	}

	public int getNumFails() {
		return numFails;
	}

	public void setNumFails(int numFails) {
		this.numFails = numFails;
	}

	@Override
	public String toString() {
		return "UserAndEquipFail [userId=" + userId + ", equipId=" + equipId
				+ ", numFails=" + numFails + "]";
	}
	
}
