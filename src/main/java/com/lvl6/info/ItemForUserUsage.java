package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class ItemForUserUsage implements Serializable {

	private static final long serialVersionUID = -1882610447601120418L;
	
	private String id;
	private String userId;
	private int itemId;
	private Date timeOfEntry;
	private String userDataId;
	private String actionType;
	
	public ItemForUserUsage() {
		super();
	}

	public ItemForUserUsage(String id, ItemForUserUsage ifuu) {
		super();
		this.id = id;
		this.userId = ifuu.userId;
		this.itemId = ifuu.itemId;
		this.timeOfEntry = ifuu.timeOfEntry;
		this.userDataId = ifuu.userDataId;
		this.actionType = ifuu.actionType;
	}
	
	public ItemForUserUsage(
		String id,
		String userId,
		int itemId,
		Date timeOfEntry,
		String userDataId,
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

	public String getUserDataId()
	{
		return userDataId;
	}

	public void setUserDataId( String userDataId )
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
