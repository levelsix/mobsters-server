package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ClanHelp implements Serializable {
	
	private String id;
	private String userId;
	private String userDataId;
	private String helpType;
	private String clanId;
	private Date timeOfEntry;
	private int maxHelpers; 
	private List<String> helpers;
	private boolean open;
	private int staticDataId;
	
	public ClanHelp() {
		super();
	}

	public ClanHelp(
		String id,
		String userId,
		String userDataId,
		String helpType,
		String clanId,
		Date timeOfEntry,
		int maxHelpers,
		List<String> helpers,
		boolean open,
		int staticDataId )
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
		this.staticDataId = staticDataId;
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

	public String getUserDataId()
	{
		return userDataId;
	}

	public void setUserDataId( String userDataId )
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

	public String getClanId()
	{
		return clanId;
	}

	public void setClanId( String clanId )
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

	public List<String> getHelpers()
	{
		return helpers;
	}

	public void setHelpers( List<String> helpers )
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

	public int getStaticDataId()
	{
		return staticDataId;
	}

	public void setStaticDataId( int staticDataId )
	{
		this.staticDataId = staticDataId;
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
			+ ", staticDataId="
			+ staticDataId
			+ "]";
	}

}