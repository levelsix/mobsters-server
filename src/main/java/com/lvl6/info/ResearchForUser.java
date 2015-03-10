package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class ResearchForUser implements Serializable {

	private static final long serialVersionUID = 1231410779566628623L;
	
	private String id;
	private String userId;
	private int researchId;
	private Date timePurchased;
	private boolean isComplete;
	 
	public ResearchForUser() {
		super();
	}

	public ResearchForUser(
		String id,
		String userId,
		int researchId,
		Date timePurchased,
		boolean isComplete )
	{
		super();
		this.id = id;
		this.userId = userId;
		this.researchId = researchId;
		this.timePurchased = timePurchased;
		this.isComplete = isComplete;
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

	public int getResearchId()
	{
		return researchId;
	}

	public void setResearchId( int researchId )
	{
		this.researchId = researchId;
	}

	public Date getTimePurchased()
	{
		return timePurchased;
	}

	public void setTimePurchased( Date timePurchased )
	{
		this.timePurchased = timePurchased;
	}

	public boolean isComplete()
	{
		return isComplete;
	}

	public void setComplete( boolean isComplete )
	{
		this.isComplete = isComplete;
	}

	@Override
	public String toString()
	{
		return "ResearchForUser [id="
			+ id
			+ ", userId="
			+ userId
			+ ", researchId="
			+ researchId
			+ ", timePurchased="
			+ timePurchased
			+ ", isComplete="
			+ isComplete
			+ "]";
	}
	
}
