package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class ClanChatPost implements Serializable {
	private String id;
	private String posterId;
	private String clanId;
	private Date timeOfPost;
	private String content;

	public ClanChatPost()
	{
		super();
	}

	public ClanChatPost(
		String id,
		String posterId,
		String clanId,
		Date timeOfPost,
		String content )
	{
		super();
		this.id = id;
		this.posterId = posterId;
		this.clanId = clanId;
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

  public String getClanId()
  {
	  return clanId;
  }

  public void setClanId( String clanId )
  {
	  this.clanId = clanId;
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
	  return "ClanChatPost [id=" + id + ", posterId=" + posterId
		  + ", clanID=" + clanId + ", timeOfPost=" + timeOfPost
		  + ", content=" + content + "]";
  }
}
