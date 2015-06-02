package com.lvl6.server.controller.actionobjects;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.PrivateChatPost;
import com.lvl6.info.TranslatedText;
import com.lvl6.info.TranslationSettingsForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.proto.ChatProto.ChatScope;
import com.lvl6.proto.ChatProto.TranslateLanguages;
import com.lvl6.proto.EventChatProto.TranslateSelectMessagesResponseProto.Builder;
import com.lvl6.proto.EventChatProto.TranslateSelectMessagesResponseProto.TranslateSelectMessagesStatus;
import com.lvl6.retrieveutils.TranslationSettingsForUserRetrieveUtil;
import com.lvl6.retrieveutils.rarechange.ChatTranslationsRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ServerToggleRetrieveUtils;
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
	private ChatScope chatType;
	private TranslationSettingsForUserRetrieveUtil translationSettingsForUserRetrieveUtil;
	private boolean translateOn;
	protected InsertUtil insertUtil;
	protected UpdateUtil updateUtil;
	private MiscMethods miscMethods;
	private ChatTranslationsRetrieveUtils chatTranslationsRetrieveUtils;
	private TranslationUtils translationUtils;
	private ServerToggleRetrieveUtils serverToggleRetrieveUtils;


	public TranslateSelectMessagesAction(String recipientUserId,
			String senderUserId, TranslateLanguages languageEnum,
			List<PrivateChatPost> listOfPrivateChatPosts, ChatScope chatType,
			TranslationSettingsForUserRetrieveUtil translationSettingsForUserRetrieveUtil,
			boolean translateOn, InsertUtil insertUtil, UpdateUtil updateUtil,
			MiscMethods miscMethods,
			ChatTranslationsRetrieveUtils chatTranslationsRetrieveUtils,
			TranslationUtils translationUtils,
			ServerToggleRetrieveUtils serverToggleRetrieveUtils) {
		super();
		this.recipientUserId = recipientUserId;
		this.senderUserId = senderUserId;
		this.languageEnum = languageEnum;
		this.listOfPrivateChatPosts = listOfPrivateChatPosts;
		this.chatType = chatType;
		this.translationSettingsForUserRetrieveUtil = translationSettingsForUserRetrieveUtil;
		this.translateOn = translateOn;
		this.insertUtil = insertUtil;
		this.updateUtil = updateUtil;
		this.miscMethods = miscMethods;
		this.chatTranslationsRetrieveUtils = chatTranslationsRetrieveUtils;
		this.translationUtils = translationUtils;
		this.serverToggleRetrieveUtils = serverToggleRetrieveUtils;
	}

	private User recipientUser;
	private User senderUser;
	private Language language;
	private Map<String, PrivateChatPost> privateChatPostMap;


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
		if(languageEnum.toString().equalsIgnoreCase("NO_TRANSLATION")) {
			log.info("no translation");
			return true;
		}

		language = translationUtils.convertFromEnumToLanguage(languageEnum);
		if (null == language) {
			resBuilder.setStatus(TranslateSelectMessagesStatus.FAIL_NOT_VALID_LANGUAGE);
			log.error("not valid language for TranslationLanguage: " + languageEnum);
			return false;
		}
		return true;
	}

	private boolean writeChangesToDB(Builder resBuilder) {
		boolean successfulUpdate = false;
		if(chatType.equals(ChatScope.PRIVATE)) {
			
			successfulUpdate = insertUtil.insertTranslateSettings(recipientUserId, senderUserId,
					languageEnum.toString(), ChatScope.PRIVATE.toString(), translateOn);
		}
		else if(chatType.equals(ChatScope.GLOBAL)) {
			List<TranslationSettingsForUser> tsfuList = translationSettingsForUserRetrieveUtil.
					getUserTranslationSettingsForUserGlobal(recipientUserId);
			if(tsfuList.isEmpty()) {

				successfulUpdate = insertUtil.insertTranslateSettings(recipientUserId, senderUserId,
						languageEnum.toString(), chatType.toString(), translateOn);
			}
			else {
				successfulUpdate = updateUtil.updateUserTranslationSettingGlobalLanguage(recipientUserId,
						chatType.toString(), languageEnum.toString(), translateOn) ;
			}
		}

		if (!successfulUpdate) {
			log.error("failed to update user language setting");
			return false;
		}

		log.info(languageEnum.toString());
		if(!languageEnum.toString().equalsIgnoreCase("NO_TRANSLATION")) {
			if(!listOfPrivateChatPosts.isEmpty()) {
				privateChatPostMap = new HashMap<String, PrivateChatPost>();
				LinkedHashMap<String, String> chatIdsToTranslations = new LinkedHashMap<String, String>();
				for(PrivateChatPost pcp : listOfPrivateChatPosts) {
					String message = pcp.getContent();
					chatIdsToTranslations.put(pcp.getId(), message);
				}
				Collection<String> messages = chatIdsToTranslations.values();
				
				String[] textArray = (String[]) messages.toArray();
				String[] translations = translationUtils.translateInBulk(textArray, language, serverToggleRetrieveUtils);

				for(PrivateChatPost pcp : listOfPrivateChatPosts) {
					for(int i=0; i<translations.length; i++) {
						TranslatedText tt = new TranslatedText();
						tt.setLanguage(language.toString());
						tt.setText(translations[i]);
						pcp.setTranslatedText(tt);
					}
					privateChatPostMap.put(pcp.getId(), pcp);
				}

				boolean successfulTranslationInsertion = insertUtil.insertMultipleTranslationsForPrivateChat(
						listOfPrivateChatPosts, chatTranslationsRetrieveUtils);

				if(successfulTranslationInsertion) {
					return true;
				}
				else return false;
			}
		}

		return successfulUpdate;
	}


	public Map<String, PrivateChatPost> getPrivateChatPostMap() {
		return privateChatPostMap;
	}

	public void setPrivateChatPostMap(
			Map<String, PrivateChatPost> privateChatPostMap) {
		this.privateChatPostMap = privateChatPostMap;
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



}
