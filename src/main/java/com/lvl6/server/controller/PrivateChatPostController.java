package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.PrivateChatPostRequestEvent;
import com.lvl6.events.response.PrivateChatPostResponseEvent;
import com.lvl6.info.AdminChatPost;
import com.lvl6.info.Clan;
import com.lvl6.info.PrivateChatPost;
import com.lvl6.info.TranslatedText;
import com.lvl6.info.TranslationSettingsForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.ChatProto.ChatType;
import com.lvl6.proto.ChatProto.GroupChatMessageProto;
import com.lvl6.proto.ChatProto.PrivateChatDefaultLanguageProto;
import com.lvl6.proto.ChatProto.PrivateChatPostProto;
import com.lvl6.proto.ChatProto.TranslateLanguages;
import com.lvl6.proto.EventChatProto.PrivateChatPostRequestProto;
import com.lvl6.proto.EventChatProto.PrivateChatPostResponseProto;
import com.lvl6.proto.EventChatProto.PrivateChatPostResponseProto.Builder;
import com.lvl6.proto.EventChatProto.PrivateChatPostResponseProto.PrivateChatPostStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.retrieveutils.TranslationSettingsForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.BannedUserRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ChatTranslationsRetrieveUtils;
import com.lvl6.utils.AdminChatUtil;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.memetix.mst.language.Language;

@Component
@DependsOn("gameServer")
public class PrivateChatPostController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	protected AdminChatUtil adminChatUtil;
	
	@Autowired
	protected MiscMethods miscMethods;
	
	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;
	
	@Autowired
	BannedUserRetrieveUtils bannedUserRetrieveUtils;

	@Autowired
	protected InsertUtil insertUtils;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtils;

	@Autowired
	protected ClanRetrieveUtils2 clanRetrieveUtils;
	
	@Autowired
	protected ChatTranslationsRetrieveUtils chatTranslationsRetrieveUtils;
	
	@Autowired
	protected TranslationSettingsForUserRetrieveUtil translationSettingsForUserRetrieveUtil;

	public PrivateChatPostController() {
		numAllocatedThreads = 4;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new PrivateChatPostRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_PRIVATE_CHAT_POST_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		PrivateChatPostRequestProto reqProto = ((PrivateChatPostRequestEvent) event)
				.getPrivateChatPostRequestProto();

		// from client
		MinimumUserProto senderProto = reqProto.getSender();
		String posterId = senderProto.getUserUuid();
		String recipientId = reqProto.getRecipientUuid();
		String content = (reqProto.hasContent()) ? reqProto.getContent() : "";
		TranslateLanguages contentLanguage = reqProto.getContentLanguage();

		// to client
		PrivateChatPostResponseProto.Builder resBuilder = PrivateChatPostResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);

		List<String> userIds = new ArrayList<String>();
		userIds.add(posterId);
		userIds.add(recipientId);

		UUID userUuid = null;
		UUID recipientUuid = null;
		boolean invalidUuids = true;
		try {
			userUuid = UUID.fromString(posterId);
			recipientUuid = UUID.fromString(recipientId);

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
					"UUID error. incorrect posterId=%s, recipientId=%s",
					posterId, recipientId), e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(PrivateChatPostStatus.OTHER_FAIL);
			PrivateChatPostResponseEvent resEvent = new PrivateChatPostResponseEvent(
					posterId);
			resEvent.setTag(event.getTag());
			resEvent.setPrivateChatPostResponseProto(resBuilder.build());
			server.writeEvent(resEvent);
			return;
		}

		try {
			Map<String, User> users = getUserRetrieveUtils().getUsersByIds(
					userIds);
			boolean legitPost = checkLegitPost(resBuilder, posterId,
					recipientId, content, users);

			PrivateChatPostResponseEvent resEvent = new PrivateChatPostResponseEvent(
					posterId);
			resEvent.setTag(event.getTag());

			if (legitPost) {
				// record in db
				Timestamp timeOfPost = new Timestamp(new Date().getTime());
				String censoredContent = miscMethods.censorUserInput(content);
				String privateChatPostId = insertUtils
						.insertIntoPrivateChatPosts(posterId, recipientId,
								censoredContent, timeOfPost, contentLanguage.toString());
				if (privateChatPostId == null) {
					legitPost = false;
					resBuilder.setStatus(PrivateChatPostStatus.OTHER_FAIL);
					log.error("problem with inserting private chat post into db. posterId="
							+ posterId
							+ ", recipientId="
							+ recipientId
							+ ", content="
							+ content
							+ ", censoredContent="
							+ censoredContent + ", timeOfPost=" + timeOfPost);
				} else {
					
					//check the language settings between the two
					boolean translationRequired = true;
					TranslationSettingsForUser settingForRecipient = translationSettingsForUserRetrieveUtil.
							getSpecificUserTranslationSettings(recipientId, posterId);
//					TranslationSettingsForUser settingForPoster = translationSettingsForUserRetrieveUtil.
//							getSpecificUserTranslationSettings(posterId, recipientId);
					Map<TranslateLanguages, String> translatedMessage = new HashMap<TranslateLanguages, String>();

					Language recipientLanguage;
					Language posterLanguage = null;
					String recipientLanguageString = "";
					String posterLanguageString = "";
					TranslatedText tt = new TranslatedText();


					//get recipient's language setting
					if(settingForRecipient == null) {
						TranslationSettingsForUser globalChatSettingsForRecipient = translationSettingsForUserRetrieveUtil.
								getSpecificUserGlobalTranslationSettings(recipientId, ChatType.GLOBAL_CHAT);
						if(globalChatSettingsForRecipient != null) {
							insertUtils.insertTranslateSettings(recipientId, posterId, globalChatSettingsForRecipient.getLanguage(), 
									ChatType.PRIVATE_CHAT.toString(), true);
							recipientLanguageString = globalChatSettingsForRecipient.getLanguage();
						}
						else {
							insertUtils.insertTranslateSettings(recipientId, posterId, TranslateLanguages.ENGLISH.toString(), 
									ChatType.PRIVATE_CHAT.toString(), true);
							recipientLanguageString = TranslateLanguages.ENGLISH.toString();
						}
					}
					else {
						translationRequired = settingForRecipient.isTranslationsOn();
						recipientLanguageString = settingForRecipient.getLanguage();
					}
					
					recipientLanguage = Language.valueOf(recipientLanguageString);

					//checking if user even wants stuff translated
					if(translationRequired) {
						//get poster's language setting, if translationrequired still
						if(contentLanguage == null) {
							TranslationSettingsForUser globalChatSettingsForPoster = translationSettingsForUserRetrieveUtil.
									getSpecificUserGlobalTranslationSettings(posterId, ChatType.GLOBAL_CHAT);
							if(globalChatSettingsForPoster != null) {
								insertUtils.insertTranslateSettings(posterId, recipientId, globalChatSettingsForPoster.getLanguage(), 
										ChatType.PRIVATE_CHAT.toString(), true);
								posterLanguageString = globalChatSettingsForPoster.getLanguage();
							}
							else {
								insertUtils.insertTranslateSettings(recipientId, posterId, TranslateLanguages.ENGLISH.toString(), 
										ChatType.PRIVATE_CHAT.toString(), true);
								posterLanguageString = TranslateLanguages.ENGLISH.toString();
							}
						}
						else {
							posterLanguageString = contentLanguage.toString();
						}
						posterLanguage = Language.valueOf(posterLanguageString);
					}

					//detect the language of msg, if it matches language setting of recipient, dont translate anything
					//					Language detectedLanguage = MiscMethods.detectedLanguage(censoredContent);
					//instead of using microsoft detect, which is ass, infer from the sender's default language setting

					if(recipientLanguageString.equalsIgnoreCase(posterLanguageString)) {
						translationRequired = false;
					}
					else {
						translatedMessage = miscMethods.translate(posterLanguage, recipientLanguage, censoredContent);
						
						for(TranslateLanguages tl : translatedMessage.keySet()) {
							ChatType chatType = ChatType.PRIVATE_CHAT;
							insertUtils.insertIntoChatTranslations(chatType, privateChatPostId, tl, translatedMessage.get(tl),
									chatTranslationsRetrieveUtils);
							tt.setLanguage(recipientLanguage.toString());
							tt.setText(translatedMessage.get(tl));
						}
					}
					
					if (recipientId == ControllerConstants.STARTUP__ADMIN_CHAT_USER_ID) {
						AdminChatPost acp = new AdminChatPost(
								privateChatPostId, posterId, recipientId,
								new Date(), censoredContent);
						acp.setUsername(users.get(posterId).getName());
						adminChatUtil.sendAdminChatEmail(acp);
						
						GroupChatMessageProto.Builder gcmpb = GroupChatMessageProto.newBuilder();
						//this is where you create the translated admin messages
//						gcmpb.setContent(value)
//						resBuilder.setAdminMessage(value);
						
						
					}
					PrivateChatPost pwp = new PrivateChatPost(
							privateChatPostId, posterId, recipientId,
							timeOfPost, censoredContent, tt, contentLanguage.toString());
					User poster = users.get(posterId);
					User recipient = users.get(recipientId);

					Clan posterClan = null;
					Clan recipientClan = null;

					//TODO: not sure if necessary to get the clans
					Set<String> clanIds = new HashSet<String>();
					String posterClanId = poster.getClanId();
					String recipientClanId = recipient.getClanId();
					if (null != posterClanId) {
						clanIds.add(posterClanId);
					}
					if (null != recipientClanId) {
						clanIds.add(recipientClanId);
					}
					if (!clanIds.isEmpty()) {
						Map<String, Clan> clanIdsToClans = getClanRetrieveUtils()
								.getClansByIds(clanIds);
						if (clanIdsToClans.containsKey(posterClanId)) {
							posterClan = clanIdsToClans.get(posterClanId);
						}
						if (clanIdsToClans.containsKey(recipientClanId)) {
							recipientClan = clanIdsToClans.get(recipientClanId);
						}
					}
					
					PrivateChatPostProto pcpp;
					
					if(translationRequired) {
						pcpp = createInfoProtoUtils
								.createPrivateChatPostProtoFromPrivateChatPost(pwp,
										poster, posterClan, recipient,
										recipientClan, translatedMessage, contentLanguage);
						
					}
					else {
						pcpp = createInfoProtoUtils
								.createPrivateChatPostProtoFromPrivateChatPost(pwp,
										poster, posterClan, recipient,
										recipientClan, null, contentLanguage);
					}
					resBuilder.setPost(pcpp);
					
					PrivateChatDefaultLanguageProto.Builder pcdlpb = PrivateChatDefaultLanguageProto.newBuilder();
					pcdlpb.setRecipientUserId(recipientId);
					pcdlpb.setSenderUserId(posterId);
					pcdlpb.setDefaultLanguage(TranslateLanguages.valueOf(recipientLanguageString));
					pcdlpb.setTranslateOn(translationRequired);
					resBuilder.setTranslationSetting(pcdlpb.build());

					// send to recipient of the private chat post
					PrivateChatPostResponseEvent resEvent2 = new PrivateChatPostResponseEvent(
							recipientId);
					resEvent2.setPrivateChatPostResponseProto(resBuilder
							.build());
					server.writeAPNSNotificationOrEvent(resEvent2);
				}
			}
			// send to sender of the private chat post
			resEvent.setPrivateChatPostResponseProto(resBuilder.build());
			server.writeEvent(resEvent);

			// if (legitPost && recipientId != posterId) {
			// User wallOwner = users.get(recipientId);
			// User poster = users.get(posterId);
			// if (MiscMethods.checkIfGoodSide(wallOwner.getType()) ==
			// !MiscMethods.checkIfGoodSide(poster.getType())) {
			// QuestUtils.checkAndSendQuestsCompleteBasic(server, posterId,
			// senderProto, SpecialQuestAction.WRITE_ON_ENEMY_WALL, true);
			// }
			// }
		} catch (Exception e) {
			log.error("exception in PrivateChatPostController processEvent", e);
			try {
				resBuilder.setStatus(PrivateChatPostStatus.OTHER_FAIL);
				PrivateChatPostResponseEvent resEvent = new PrivateChatPostResponseEvent(
						posterId);
				resEvent.setTag(event.getTag());
				resEvent.setPrivateChatPostResponseProto(resBuilder.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in PrivateChatPostController processEvent",
						e);
			}
		}

	}

	private boolean checkLegitPost(Builder resBuilder, String posterId,
			String recipientId, String content, Map<String, User> users) {
		if (users == null) {
			resBuilder.setStatus(PrivateChatPostStatus.OTHER_FAIL);
			log.error("users are null- posterId=" + posterId + ", recipientId="
					+ recipientId);
			return false;
		}
		if (users.size() != 2 && !posterId.equals(recipientId)) {
			resBuilder.setStatus(PrivateChatPostStatus.OTHER_FAIL);
			log.error("error retrieving one of the users. posterId=" + posterId
					+ ", recipientId=" + recipientId);
			return false;
		}
		if (users.size() != 1 && posterId.equals(recipientId)) {
			resBuilder.setStatus(PrivateChatPostStatus.OTHER_FAIL);
			log.error("error retrieving one of the users. posterId=" + posterId
					+ ", recipientId=" + recipientId);
			return false;
		}
		if (content == null || content.length() == 0) {
			resBuilder.setStatus(PrivateChatPostStatus.NO_CONTENT_SENT);
			log.error("no content when posterId " + posterId
					+ " tries to post on wall with owner " + recipientId);
			return false;
		}
		// maybe use different controller constants...
		if (content.length() >= ControllerConstants.SEND_GROUP_CHAT__MAX_LENGTH_OF_CHAT_STRING) {
			resBuilder.setStatus(PrivateChatPostStatus.POST_TOO_LARGE);
			log.error("wall post is too long. content length is "
					+ content.length()
					+ ", max post length="
					+ ControllerConstants.SEND_GROUP_CHAT__MAX_LENGTH_OF_CHAT_STRING
					+ ", posterId " + posterId
					+ " tries to post on wall with owner " + recipientId);
			return false;
		}
		Set<Integer> banned = bannedUserRetrieveUtils.getAllBannedUsers();
		if (null != banned && banned.contains(posterId)) {
			resBuilder.setStatus(PrivateChatPostStatus.BANNED);
			log.warn("banned user tried to send a post. posterId=" + posterId);
			return false;
		}
		resBuilder.setStatus(PrivateChatPostStatus.SUCCESS);
		return true;
	}

	public AdminChatUtil getAdminChatUtil() {
		return adminChatUtil;
	}

	public void setAdminChatUtil(AdminChatUtil adminChatUtil) {
		this.adminChatUtil = adminChatUtil;
	}

	public void setInsertUtils(InsertUtil insertUtils) {
		this.insertUtils = insertUtils;
	}

	public UserRetrieveUtils2 getUserRetrieveUtils() {
		return userRetrieveUtils;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtils) {
		this.userRetrieveUtils = userRetrieveUtils;
	}

	public ClanRetrieveUtils2 getClanRetrieveUtils() {
		return clanRetrieveUtils;
	}

	public void setClanRetrieveUtils(ClanRetrieveUtils2 clanRetrieveUtils) {
		this.clanRetrieveUtils = clanRetrieveUtils;
	}

}
