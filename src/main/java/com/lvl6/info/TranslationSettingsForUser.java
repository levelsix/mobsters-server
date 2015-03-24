package com.lvl6.info;

public class TranslationSettingsForUser {

	public TranslationSettingsForUser() {
		super();
		// TODO Auto-generated constructor stub
	}
	private String id;
	private String receiverUserId;
	private String senderUserId;
	private String language;
	private String chatType;
	
	@Override
	public String toString() {
		return "TranslationSettingsForUser [id=" + id + ", receiverUserId="
				+ receiverUserId + ", senderUserId=" + senderUserId
				+ ", language=" + language + ", chatType=" + chatType + "]";
	}
	public TranslationSettingsForUser(String id, String receiverUserId,
			String senderUserId, String language, String chatType) {
		super();
		this.id = id;
		this.receiverUserId = receiverUserId;
		this.senderUserId = senderUserId;
		this.language = language;
		this.chatType = chatType;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getReceiverUserId() {
		return receiverUserId;
	}
	public void setReceiverUserId(String receiverUserId) {
		this.receiverUserId = receiverUserId;
	}
	public String getSenderUserId() {
		return senderUserId;
	}
	public void setSenderUserId(String senderUserId) {
		this.senderUserId = senderUserId;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getChatType() {
		return chatType;
	}
	public void setChatType(String chatType) {
		this.chatType = chatType;
	}
	
	
}
