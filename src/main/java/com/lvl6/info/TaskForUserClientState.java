package com.lvl6.info;

import java.io.Serializable;

public class TaskForUserClientState implements Serializable {

	private static final long serialVersionUID = -2313375485900498783L;
	
	private String userId;
	private byte[] clientState;
	
	public TaskForUserClientState() {
		super();
	}

	public TaskForUserClientState( String userId, byte[] clientState )
	{
		super();
		this.userId = userId;
		this.clientState = clientState;
	}

	public String getUserId()
	{
		return userId;
	}

	public void setUserId( String userId )
	{
		this.userId = userId;
	}

	public byte[] getClientState()
	{
		return clientState;
	}

	public void setClientState( byte[] clientState )
	{
		this.clientState = clientState;
	}

	@Override
	public String toString()
	{
		return "TaskForUserClientState [userId="
			+ userId
			+ "]";
	}

}
