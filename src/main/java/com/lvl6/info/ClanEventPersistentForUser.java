package com.lvl6.info;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//user can have multiple of these (different clanIds)
public class ClanEventPersistentForUser implements Serializable {
	
	private static final long serialVersionUID = -2682088060406725220L;
	
	private String userId;
	private String clanId;
	private int crId;
	private int crDmgDone;
	private int crsId;
	private int crsDmgDone;
	private int crsmId;//primary key in clan raid stage monster
	private int crsmDmgDone;
	private String userMonsterIdOne;
	private String userMonsterIdTwo;
	private String userMonsterIdThree;
	
	public ClanEventPersistentForUser()
	{
		super();
	}

	public ClanEventPersistentForUser(
		String userId,
		String clanId,
		int crId,
		int crDmgDone,
		int crsId,
		int crsDmgDone,
		int crsmId,
		int crsmDmgDone,
		String userMonsterIdOne,
		String userMonsterIdTwo,
		String userMonsterIdThree )
	{
		super();
		this.userId = userId;
		this.clanId = clanId;
		this.crId = crId;
		this.crDmgDone = crDmgDone;
		this.crsId = crsId;
		this.crsDmgDone = crsDmgDone;
		this.crsmId = crsmId;
		this.crsmDmgDone = crsmDmgDone;
		this.userMonsterIdOne = userMonsterIdOne;
		this.userMonsterIdTwo = userMonsterIdTwo;
		this.userMonsterIdThree = userMonsterIdThree;
	}

	//convenience methods
	public List<String> getUserMonsterIds() {
		List<String> userMonsterIds = new ArrayList<String>();
		
		if (null != userMonsterIdOne && !userMonsterIdOne.isEmpty()) {
			userMonsterIds.add(userMonsterIdOne);
		}
		if (null != userMonsterIdTwo && !userMonsterIdTwo.isEmpty()) {
			userMonsterIds.add(userMonsterIdTwo);
		}
		if (null != userMonsterIdThree && !userMonsterIdThree.isEmpty()) {
			userMonsterIds.add(userMonsterIdThree);
		}
		
		return userMonsterIds;
	}
	//---------------------------

	public String getUserId()
	{
		return userId;
	}

	public void setUserId( String userId )
	{
		this.userId = userId;
	}

	public String getClanId()
	{
		return clanId;
	}

	public void setClanId( String clanId )
	{
		this.clanId = clanId;
	}

	public int getCrId()
	{
		return crId;
	}

	public void setCrId( int crId )
	{
		this.crId = crId;
	}

	public int getCrDmgDone()
	{
		return crDmgDone;
	}

	public void setCrDmgDone( int crDmgDone )
	{
		this.crDmgDone = crDmgDone;
	}

	public int getCrsId()
	{
		return crsId;
	}

	public void setCrsId( int crsId )
	{
		this.crsId = crsId;
	}

	public int getCrsDmgDone()
	{
		return crsDmgDone;
	}

	public void setCrsDmgDone( int crsDmgDone )
	{
		this.crsDmgDone = crsDmgDone;
	}

	public int getCrsmId()
	{
		return crsmId;
	}

	public void setCrsmId( int crsmId )
	{
		this.crsmId = crsmId;
	}

	public int getCrsmDmgDone()
	{
		return crsmDmgDone;
	}

	public void setCrsmDmgDone( int crsmDmgDone )
	{
		this.crsmDmgDone = crsmDmgDone;
	}

	public String getUserMonsterIdOne()
	{
		return userMonsterIdOne;
	}

	public void setUserMonsterIdOne( String userMonsterIdOne )
	{
		this.userMonsterIdOne = userMonsterIdOne;
	}

	public String getUserMonsterIdTwo()
	{
		return userMonsterIdTwo;
	}

	public void setUserMonsterIdTwo( String userMonsterIdTwo )
	{
		this.userMonsterIdTwo = userMonsterIdTwo;
	}

	public String getUserMonsterIdThree()
	{
		return userMonsterIdThree;
	}

	public void setUserMonsterIdThree( String userMonsterIdThree )
	{
		this.userMonsterIdThree = userMonsterIdThree;
	}

	@Override
	public String toString() {
		return "ClanEventPersistentForUser [userId=" + userId + ", clanId="
				+ clanId + ", crId=" + crId + ", crDmgDone=" + crDmgDone + ", crsId="
				+ crsId + ", crsDmgDone=" + crsDmgDone + ", crsmId=" + crsmId
				+ ", crsmDmgDone=" + crsmDmgDone + ", userMonsterIdOne="
				+ userMonsterIdOne + ", userMonsterIdTwo=" + userMonsterIdTwo
				+ ", userMonsterIdThree=" + userMonsterIdThree + "]";
	}
  
}
