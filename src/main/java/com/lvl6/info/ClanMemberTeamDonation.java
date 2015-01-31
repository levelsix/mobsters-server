package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class ClanMemberTeamDonation implements Serializable {
	
	private static final long serialVersionUID = -8755436112635166487L;
	
	private String id;
	private String userId;
	private String clanId;
	private int powerLimit; 
	private boolean fulfilled;
	private String msg;
	private Date timeOfSolicitation;
	
	public ClanMemberTeamDonation() {
		super();
	}

	public ClanMemberTeamDonation(
		String id,
		String userId,
		String clanId,
		int powerLimit,
		boolean fulfilled,
		String msg,
		Date timeOfSolicitation )
	{
		super();
		this.id = id;
		this.userId = userId;
		this.clanId = clanId;
		this.powerLimit = powerLimit;
		this.fulfilled = fulfilled;
		this.msg = msg;
		this.timeOfSolicitation = timeOfSolicitation;
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

	public String getClanId()
	{
		return clanId;
	}

	public void setClanId( String clanId )
	{
		this.clanId = clanId;
	}

	public int getPowerLimit()
	{
		return powerLimit;
	}

	public void setPowerLimit( int powerLimit )
	{
		this.powerLimit = powerLimit;
	}

	public boolean isFulfilled()
	{
		return fulfilled;
	}

	public void setFulfilled( boolean fulfilled )
	{
		this.fulfilled = fulfilled;
	}

	public String getMsg()
	{
		return msg;
	}

	public void setMsg( String msg )
	{
		this.msg = msg;
	}

	public Date getTimeOfSolicitation()
	{
		return timeOfSolicitation;
	}

	public void setTimeOfSolicitation( Date timeOfSolicitation )
	{
		this.timeOfSolicitation = timeOfSolicitation;
	}

	@Override
	public String toString()
	{
		return "ClanMemberTeamDonation [id="
			+ id
			+ ", userId="
			+ userId
			+ ", clanId="
			+ clanId
			+ ", powerLimit="
			+ powerLimit
			+ ", fulfilled="
			+ fulfilled
			+ ", msg="
			+ msg
			+ ", timeOfSolicitation="
			+ timeOfSolicitation
			+ "]";
	}

}