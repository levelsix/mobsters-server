package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class ClanHelpCountForUser implements Serializable {
	
	private static final long serialVersionUID = -8014423674849277015L;
	private String userId;
	private String clanId;
	private Date date;
	private int solicited;
	private int given;	
	
	public ClanHelpCountForUser() {
		super();
	}

	public ClanHelpCountForUser(
		String userId,
		String clanId,
		Date date,
		int solicited,
		int given )
	{
		super();
		this.userId = userId;
		this.clanId = clanId;
		this.date = date;
		this.solicited = solicited;
		this.given = given;
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

	public Date getDate()
	{
		return date;
	}

	public void setDate( Date date )
	{
		this.date = date;
	}

	public int getSolicited()
	{
		return solicited;
	}

	public void setSolicited( int solicited )
	{
		this.solicited = solicited;
	}

	public int getGiven()
	{
		return given;
	}

	public void setGiven( int given )
	{
		this.given = given;
	}

	@Override
	public String toString()
	{
		return "ClanHelpCountForUser [userId="
			+ userId
			+ ", clanId="
			+ clanId
			+ ", date="
			+ date
			+ ", solicited="
			+ solicited
			+ ", given="
			+ given
			+ "]";
	}

}
