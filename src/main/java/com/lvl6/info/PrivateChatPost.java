package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class PrivateChatPost implements Serializable {
	
	private static final long serialVersionUID = 10600088114205561L;
	
	protected String id;
	protected String posterId;
	protected String recipientId;
	protected Date timeOfPost;
	protected String content;

	public PrivateChatPost()
	{
		super();
	}

	public PrivateChatPost(String id, String posterId, String recipientId,
			Date timeOfPost, String content) {
		super();
		this.id = id;
		this.posterId = posterId;
		this.recipientId = recipientId;
		this.timeOfPost = timeOfPost;
		this.content = content;
	}

	public String getId()
	{
		return id;
	}

	public void setId( String id )
	{
		this.id = id;
	}

	public String getPosterId()
	{
		return posterId;
	}

	public void setPosterId( String posterId )
	{
		this.posterId = posterId;
	}

	public String getRecipientId()
	{
		return recipientId;
	}

	public void setRecipientId( String recipientId )
	{
		this.recipientId = recipientId;
	}

	public Date getTimeOfPost()
	{
		return timeOfPost;
	}

	public void setTimeOfPost( Date timeOfPost )
	{
		this.timeOfPost = timeOfPost;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent( String content )
	{
		this.content = content;
	}

	@Override
	public String toString() {
		return "PlayerWallPost [id=" + id + ", posterId=" + posterId
				+ ", recipientId=" + recipientId + ", timeOfPost=" + timeOfPost
				+ ", content=" + content + "]";
	}
}
