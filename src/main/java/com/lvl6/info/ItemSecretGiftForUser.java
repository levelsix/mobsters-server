package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class ItemSecretGiftForUser implements Serializable {

	private String id;
	private String userId;
	private int itemId;
	private int minsTillCollection;
	private Date createTime;
	
	public ItemSecretGiftForUser() {
		super();
	}

	public ItemSecretGiftForUser(
		String id,
		String userId,
		int itemId,
		int minsTillCollection,
		Date createTime )
	{
		super();
		this.id = id;
		this.userId = userId;
		this.itemId = itemId;
		this.minsTillCollection = minsTillCollection;
		this.createTime = createTime;
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

	public int getMinsTillCollection()
	{
		return minsTillCollection;
	}

	public void setMinsTillCollection( int minsTillCollection )
	{
		this.minsTillCollection = minsTillCollection;
	}

	public Date getCreateTime()
	{
		return createTime;
	}

	public void setCreateTime( Date createTime )
	{
		this.createTime = createTime;
	}

	@Override
	public String toString()
	{
		return "ItemSecretGiftForUser [id="
			+ id
			+ ", userId="
			+ userId
			+ ", itemId="
			+ itemId
			+ ", minsTillCollection="
			+ minsTillCollection
			+ ", createTime="
			+ createTime
			+ "]";
	}
	
}
