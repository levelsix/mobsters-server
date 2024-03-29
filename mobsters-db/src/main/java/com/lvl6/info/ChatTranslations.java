package com.lvl6.info;

import java.io.Serializable;

import com.lvl6.proto.ChatProto.ChatScope;
import com.lvl6.proto.ChatProto.TranslateLanguages;

public class ChatTranslations implements Serializable {

	private static final long serialVersionUID = -4978138161949882111L;

	private String id;
	private ChatScope chatType;
	private String chatId;
	private TranslateLanguages translateLanguage;
	private String text;

	public ChatTranslations() {
		super();
	}

	public ChatTranslations(String id, ChatScope chatType, String chatId,
			TranslateLanguages translateLanguage, String text) {
		super();
		this.id = id;
		this.chatType = chatType;
		this.chatId = chatId;
		this.translateLanguage = translateLanguage;
		this.text = text;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public ChatScope getChatType() {
		return chatType;
	}
	public void setChatType(ChatScope chatType) {
		this.chatType = chatType;
	}
	public String getChatId() {
		return chatId;
	}
	public void setChatId(String chatId) {
		this.chatId = chatId;
	}
	public TranslateLanguages getTranslateLanguage() {
		return translateLanguage;
	}
	public void setTranslateLanguage(TranslateLanguages translateLanguage) {
		this.translateLanguage = translateLanguage;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return "ChatTranslations [id=" + id + ", chatType=" + chatType
				+ ", chatId=" + chatId + ", translateLanguage="
				+ translateLanguage + ", text=" + text + "]";
	}

}
