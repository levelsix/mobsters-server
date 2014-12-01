package com.lvl6.info;

import java.util.Date;

public class AdminChatPost extends PrivateChatPost {
	
	private static final long serialVersionUID = 4846135061272439890L;

	public AdminChatPost(String id, String posterId, String recipientId, Date timeOfPost, String content) {
		super(id, posterId, recipientId, timeOfPost, content);
		//setUsername(username);
	}
	
	public AdminChatPost(String id, String posterId, String recipientId, Date timeOfPost, String content, String username) {
		super(id, posterId, recipientId, timeOfPost, content);
		setUsername(username);
	}
	
	protected String username = "";
	

	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}

}
