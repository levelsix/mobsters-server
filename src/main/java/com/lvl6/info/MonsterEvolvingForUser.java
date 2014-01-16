package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class MonsterEvolvingForUser implements Serializable {

	private static final long serialVersionUID = -8663041420598844646L;
	private long catalystMonsterForUserId;
	private long monsterForUserIdOne;
	private long monsterForUserIdTwo;
	private int userId;
  private Date startTime;

  public MonsterEvolvingForUser(long catalystMonsterForUserId,
			long monsterForUserIdOne, long monsterForUserIdTwo, int userId,
			Date startTime) {
		super();
		this.catalystMonsterForUserId = catalystMonsterForUserId;
		this.monsterForUserIdOne = monsterForUserIdOne;
		this.monsterForUserIdTwo = monsterForUserIdTwo;
		this.userId = userId;
		this.startTime = startTime;
	}

	public long getCatalystMonsterForUserId() {
		return catalystMonsterForUserId;
	}

	public void setCatalystMonsterForUserId(long catalystMonsterForUserId) {
		this.catalystMonsterForUserId = catalystMonsterForUserId;
	}

	public long getMonsterForUserIdOne() {
		return monsterForUserIdOne;
	}

	public void setMonsterForUserIdOne(long monsterForUserIdOne) {
		this.monsterForUserIdOne = monsterForUserIdOne;
	}

	public long getMonsterForUserIdTwo() {
		return monsterForUserIdTwo;
	}

	public void setMonsterForUserIdTwo(long monsterForUserIdTwo) {
		this.monsterForUserIdTwo = monsterForUserIdTwo;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	@Override
	public String toString() {
		return "MonsterEvolvingForUser [catalystMonsterForUserId="
				+ catalystMonsterForUserId + ", monsterForUserIdOne="
				+ monsterForUserIdOne + ", monsterForUserIdTwo=" + monsterForUserIdTwo
				+ ", userId=" + userId + ", startTime=" + startTime + "]";
	}
  
}
