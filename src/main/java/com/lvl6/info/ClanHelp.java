package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ClanHelp implements Serializable {
	
	private static final long serialVersionUID = 3928951416077703182L;
	
	private long id;
	private int userId;
	private long userDataId;
	private String helpType;
	private int clanId;
	private Date timeOfEntry;
	private int maxHelpers; 
	private List<Integer> helpers;
	private boolean open;
	
	public ClanHelp() {
		super();
	}

	public ClanHelp(
		long id,
		int userId,
		long userDataId,
		String helpType,
		int clanId,
		Date timeOfEntry,
		int maxHelpers,
		List<Integer> helpers,
		boolean open )
	{
		super();
		this.id = id;
		this.userId = userId;
		this.userDataId = userDataId;
		this.helpType = helpType;
		this.clanId = clanId;
		this.timeOfEntry = timeOfEntry;
		this.maxHelpers = maxHelpers;
		this.helpers = helpers;
		this.open = open;
	}

	public long getId()
	{
		return id;
	}

	public void setId( long id )
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

	public long getUserDataId()
	{
		return userDataId;
	}

	public void setUserDataId( long userDataId )
	{
		this.userDataId = userDataId;
	}

	public String getHelpType()
	{
		return helpType;
	}

	public void setHelpType( String helpType )
	{
		this.helpType = helpType;
	}

	public int getClanId()
	{
		return clanId;
	}

	public void setClanId( int clanId )
	{
		this.clanId = clanId;
	}

	public Date getTimeOfEntry()
	{
		return timeOfEntry;
	}

	public void setTimeOfEntry( Date timeOfEntry )
	{
		this.timeOfEntry = timeOfEntry;
	}

	public int getMaxHelpers()
	{
		return maxHelpers;
	}

	public void setMaxHelpers( int maxHelpers )
	{
		this.maxHelpers = maxHelpers;
	}

	public List<Integer> getHelpers()
	{
		return helpers;
	}

	public void setHelpers( List<Integer> helpers )
	{
		this.helpers = helpers;
	}

	public boolean isOpen()
	{
		return open;
	}

	public void setOpen( boolean open )
	{
		this.open = open;
	}

	@Override
	public String toString()
	{
		return "ClanHelp [id="
			+ id
			+ ", userId="
			+ userId
			+ ", userDataId="
			+ userDataId
			+ ", helpType="
			+ helpType
			+ ", clanId="
			+ clanId
			+ ", timeOfEntry="
			+ timeOfEntry
			+ ", maxHelpers="
			+ maxHelpers
			+ ", helpers="
			+ helpers
			+ ", open="
			+ open
			+ "]";
	}

}
