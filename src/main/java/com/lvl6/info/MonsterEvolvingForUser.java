package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class MonsterEvolvingForUser implements Serializable {

	private String catalystMonsterForUserId;
	private String monsterForUserIdOne;
	private String monsterForUserIdTwo;
	private String userId;
	private Date startTime;
	
	public MonsterEvolvingForUser()
	{
		super();
	}

	public MonsterEvolvingForUser(
		String catalystMonsterForUserId,
		String monsterForUserIdOne,
		String monsterForUserIdTwo,
		String userId,
		Date startTime )
	{
		super();
		this.catalystMonsterForUserId = catalystMonsterForUserId;
		this.monsterForUserIdOne = monsterForUserIdOne;
		this.monsterForUserIdTwo = monsterForUserIdTwo;
		this.userId = userId;
		this.startTime = startTime;
	}

	public String getCatalystMonsterForUserId()
	{
		return catalystMonsterForUserId;
	}

	public void setCatalystMonsterForUserId( String catalystMonsterForUserId )
	{
		this.catalystMonsterForUserId = catalystMonsterForUserId;
	}

	public String getMonsterForUserIdOne()
	{
		return monsterForUserIdOne;
	}

	public void setMonsterForUserIdOne( String monsterForUserIdOne )
	{
		this.monsterForUserIdOne = monsterForUserIdOne;
	}

	public String getMonsterForUserIdTwo()
	{
		return monsterForUserIdTwo;
	}

	public void setMonsterForUserIdTwo( String monsterForUserIdTwo )
	{
		this.monsterForUserIdTwo = monsterForUserIdTwo;
	}

	public String getUserId()
	{
		return userId;
	}

	public void setUserId( String userId )
	{
		this.userId = userId;
	}

	public Date getStartTime()
	{
		return startTime;
	}

	public void setStartTime( Date startTime )
	{
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
