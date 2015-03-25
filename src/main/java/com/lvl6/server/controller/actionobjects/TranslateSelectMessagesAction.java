package com.lvl6.server.controller.actionobjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.PrivateChatPost;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.proto.ChatProto.TranslateLanguages;
import com.lvl6.proto.ChatProto.TranslatedTextProto;
import com.lvl6.proto.EventChatProto.TranslateSelectMessagesResponseProto.Builder;
import com.lvl6.proto.EventChatProto.TranslateSelectMessagesResponseProto.TranslateSelectMessagesStatus;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;
import com.memetix.mst.language.Language;

public class TranslateSelectMessagesAction {
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String recipientUserId;
	private String senderUserId;
	private TranslateLanguages languageEnum;
	private List<PrivateChatPost> listOfPrivateChatPosts;
	protected InsertUtil insertUtil;
	protected UpdateUtil updateUtil;

	public TranslateSelectMessagesAction(String recipientUserId,
			String senderUserId, TranslateLanguages languageEnum,
			List<PrivateChatPost> listOfPrivateChatPosts, InsertUtil insertUtil,
			UpdateUtil updateUtil) {
		super();
		this.recipientUserId = recipientUserId;
		this.senderUserId = senderUserId;
		this.languageEnum = languageEnum;
		this.listOfPrivateChatPosts = listOfPrivateChatPosts;
		this.insertUtil = insertUtil;
		this.updateUtil = updateUtil;
	}

	private User recipientUser;
	private User senderUser;
	private Language language;
	private List<String> originalMessages;
	private List<TranslatedTextProto> messagesTranslated;

	public void execute(Builder resBuilder) {
		resBuilder.setStatus(TranslateSelectMessagesStatus.FAIL_OTHER);

		boolean valid = false;
		valid = verifySemantics(resBuilder);

		if (!valid) {
			return;
		}

		boolean success = writeChangesToDB(resBuilder);
		if (!success) {
			return;
		}

		resBuilder.setStatus(TranslateSelectMessagesStatus.SUCCESS);
	}



	private boolean verifySemantics(Builder resBuilder) {
		language = MiscMethods.convertFromEnumToLanguage(languageEnum);
		if (null == language) {
			resBuilder.setStatus(TranslateSelectMessagesStatus.FAIL_NOT_VALID_LANGUAGE);
			log.error("not valid language for TranslationLanguage: " + languageEnum);
			return false;
		}
		return true;
	}

	private boolean writeChangesToDB(Builder resBuilder) {
		boolean successfulUpdate = updateUtil.updateUserTranslationSetting(recipientUserId, senderUserId, languageEnum.toString());

		if (!successfulUpdate) {
			log.error("failed to update user language setting");
			return false;
		}

		Map<String, String> chatIdsToTranslations = new HashMap<String, String>();
		originalMessages = new ArrayList<String>();
		for(PrivateChatPost pcp : listOfPrivateChatPosts) {
			String message = pcp.getContent();
			originalMessages.add(message);
			chatIdsToTranslations.put(pcp.getId(), message);
			Map<TranslateLanguages, String> translatedMessage = MiscMethods.translate(language, message);
			messagesTranslated = new ArrayList<TranslatedTextProto>();

			for(TranslateLanguages tl : translatedMessage.keySet()) {
				TranslatedTextProto.Builder ttpb = TranslatedTextProto.newBuilder();
				ttpb.setLanguage(tl);
				ttpb.setText(translatedMessage.get(tl));
				messagesTranslated.add(ttpb.build());
			}

		}

		boolean successfulTranslationInsertion = insertUtil.insertMultipleTranslationsForPrivateChat(
				chatIdsToTranslations, language);

		if(successfulTranslationInsertion) {
			return true;
		}
		else return false;
	}

	public static Logger getLog() {
		return log;
	}

	public static void setLog(Logger log) {
		TranslateSelectMessagesAction.log = log;
	}

	public String getRecipientUserId() {
		return recipientUserId;
	}

	public void setRecipientUserId(String recipientUserId) {
		this.recipientUserId = recipientUserId;
	}

	public String getSenderUserId() {
		return senderUserId;
	}

	public void setSenderUserId(String senderUserId) {
		this.senderUserId = senderUserId;
	}

	public TranslateLanguages getLanguageEnum() {
		return languageEnum;
	}

	public void setLanguageEnum(TranslateLanguages languageEnum) {
		this.languageEnum = languageEnum;
	}

	public List<PrivateChatPost> getListOfPrivateChatPosts() {
		return listOfPrivateChatPosts;
	}

	public void setListOfPrivateChatPosts(
			List<PrivateChatPost> listOfPrivateChatPosts) {
		this.listOfPrivateChatPosts = listOfPrivateChatPosts;
	}

	public InsertUtil getInsertUtil() {
		return insertUtil;
	}

	public void setInsertUtil(InsertUtil insertUtil) {
		this.insertUtil = insertUtil;
	}

	public UpdateUtil getUpdateUtil() {
		return updateUtil;
	}

	public void setUpdateUtil(UpdateUtil updateUtil) {
		this.updateUtil = updateUtil;
	}

	public User getRecipientUser() {
		return recipientUser;
	}

	public void setRecipientUser(User recipientUser) {
		this.recipientUser = recipientUser;
	}

	public User getSenderUser() {
		return senderUser;
	}

	public void setSenderUser(User senderUser) {
		this.senderUser = senderUser;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public List<String> getOriginalMessages() {
		return originalMessages;
	}

	public void setOriginalMessages(List<String> originalMessages) {
		this.originalMessages = originalMessages;
	}

	public List<TranslatedTextProto> getMessagesTranslated() {
		return messagesTranslated;
	}

	public void setMessagesTranslated(List<TranslatedTextProto> messagesTranslated) {
		this.messagesTranslated = messagesTranslated;
	}



}
