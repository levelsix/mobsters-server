package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class ClanInvite implements Serializable {
	
	private static final long serialVersionUID = -2190113788315863774L;
	
	private int id;
	private int userId;
	private int inviterId; 
	private int clanId;
	private Date timeOfInvite;
	
	public ClanInvite() {
		super();
	}

	public ClanInvite( int id, int userId, int inviterId, int clanId, Date timeOfInvite )
	{
		super();
		this.id = id;
		this.userId = userId;
		this.inviterId = inviterId;
		this.clanId = clanId;
		this.timeOfInvite = timeOfInvite;
	}

	public int getId()
	{
		return id;
	}

	public void setId( int id )
	{
		this.id = id;
	}

	public int getUserId()
	{
		return userId;
	}

	public void setUserId( int userId )
	{
		this.userId = userId;
	}

	public int getInviterId()
	{
		return inviterId;
	}

	public void setInviterId( int inviterId )
	{
		this.inviterId = inviterId;
	}

	public int getClanId()
	{
		return clanId;
	}

	public void setClanId( int clanId )
	{
		this.clanId = clanId;
	}

	public Date getTimeOfInvite()
	{
		return timeOfInvite;
	}

	public void setTimeOfInvite( Date timeOfInvite )
	{
		this.timeOfInvite = timeOfInvite;
	}

	@Override
	public String toString()
	{
		return "ClanInvite [id="
			+ id
			+ ", userId="
			+ userId
			+ ", inviterId="
			+ inviterId
			+ ", clanId="
			+ clanId
			+ ", timeOfInvite="
			+ timeOfInvite
			+ "]";
	}

}