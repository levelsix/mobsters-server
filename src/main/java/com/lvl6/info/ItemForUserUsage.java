package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class ItemForUserUsage implements Serializable {

	private static final long serialVersionUID = -8426227433650264999L;
	
	private long id;
	private int userId;
	private int itemId;
	private Date timeOfEntry;
	private long userDataId;
	private String actionType;
	
	public ItemForUserUsage() {
		super();
	}
	
	public ItemForUserUsage(
		long id,
		int userId,
		int itemId,
		Date timeOfEntry,
		long userDataId,
		String actionType )
	{
		super();
		this.id = id;
		this.userId = userId;
		this.itemId = itemId;
		this.timeOfEntry = timeOfEntry;
		this.userDataId = userDataId;
		this.actionType = actionType;
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

	public int getItemId()
	{
		return itemId;
	}

	public void setItemId( int itemId )
	{
		this.itemId = itemId;
	}

	public Date getTimeOfEntry()
	{
		return timeOfEntry;
	}

	public void setTimeOfEntry( Date timeOfEntry )
	{
		this.timeOfEntry = timeOfEntry;
	}

	public long getUserDataId()
	{
		return userDataId;
	}

	public void setUserDataId( long userDataId )
	{
		this.userDataId = userDataId;
	}

	public String getActionType()
	{
		return actionType;
	}

	public void setActionType( String actionType )
	{
		this.actionType = actionType;
	}

	@Override
	public String toString()
	{
		return "ItemForUserUsage [id="
			+ id
			+ ", userId="
			+ userId
			+ ", itemId="
			+ itemId
			+ ", timeOfEntry="
			+ timeOfEntry
			+ ", userDataId="
			+ userDataId
			+ ", actionType="
			+ actionType
			+ "]";
	}
	
}
