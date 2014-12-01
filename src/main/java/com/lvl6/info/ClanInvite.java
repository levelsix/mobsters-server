package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class ClanInvite implements Serializable {
	
	private static final long serialVersionUID = -2542433676672978497L;
	
	private String id;
	private String userId;
	private String inviterId; 
	private String clanId;
	private Date timeOfInvite;
	
	public ClanInvite() {
		super();
	}

	public ClanInvite(
		String id,
		String userId,
		String inviterId,
		String clanId,
		Date timeOfInvite )
	{
		super();
		this.id = id;
		this.userId = userId;
		this.inviterId = inviterId;
		this.clanId = clanId;
		this.timeOfInvite = timeOfInvite;
	}

	public String getId()
	{
		return id;
	}

	public void setId( String id )
	{
		this.id = id;
	}

	public String getUserId()
	{
		return userId;
	}

	public void setUserId( String userId )
	{
		this.userId = userId;
	}

	public String getInviterId()
	{
		return inviterId;
	}

	public void setInviterId( String inviterId )
	{
		this.inviterId = inviterId;
	}

	public String getClanId()
	{
		return clanId;
	}

	public void setClanId( String clanId )
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