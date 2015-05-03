package com.lvl6.server.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.RetrievePrivateChatPostsRequestEvent;
import com.lvl6.events.response.RetrievePrivateChatPostsResponseEvent;
import com.lvl6.info.ChatTranslations;
import com.lvl6.info.Clan;
import com.lvl6.info.PrivateChatPost;
import com.lvl6.info.TranslationSettingsForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.ChatProto.GroupChatMessageProto;
import com.lvl6.proto.ChatProto.TranslateLanguages;
import com.lvl6.proto.EventChatProto.RetrievePrivateChatPostsRequestProto;
import com.lvl6.proto.EventChatProto.RetrievePrivateChatPostsResponseProto;
import com.lvl6.proto.EventChatProto.RetrievePrivateChatPostsResponseProto.RetrievePrivateChatPostsStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.proto.UserProto.MinimumUserProtoWithLevel;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.retrieveutils.PrivateChatPostRetrieveUtils2;
import com.lvl6.retrieveutils.TranslationSettingsForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.ChatTranslationsRetrieveUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.memetix.mst.language.Language;

@Component
@DependsOn("gameServer")
public class RetrievePrivateChatPostsController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	protected PrivateChatPostRetrieveUtils2 privateChatPostRetrieveUtils;

	@Autowired
	protected MiscMethods miscMethods;

	@Autowired
	protected ClanRetrieveUtils2 clanRetrieveUtils;

	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtils;

	@Autowired
	protected ChatTranslationsRetrieveUtils chatTranslationsRetrieveUtils;

	@Autowired
	protected TranslationSettingsForUserRetrieveUtil translationSettingsForUserRetrieveUtil;

	public RetrievePrivateChatPostsController() {
		numAllocatedThreads = 5;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new RetrievePrivateChatPostsRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_RETRIEVE_PRIVATE_CHAT_POST_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		RetrievePrivateChatPostsRequestProto reqProto = ((RetrievePrivateChatPostsRequestEvent) event)
				.getRetrievePrivateChatPostsRequestProto();

		log.info(""+ reqProto);

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		String otherUserId = reqProto.getOtherUserUuid();
		TranslateLanguages translateLanguage = null;
		translateLanguage = reqProto.getLanguage();

		RetrievePrivateChatPostsResponseProto.Builder resBuilder = RetrievePrivateChatPostsResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setOtherUserUuid(otherUserId);

		UUID userUuid = null;
		UUID otherUserUuid = null;
		boolean invalidUuids = true;
		try {
			userUuid = UUID.fromString(userId);
			otherUserUuid = UUID.fromString(otherUserId);

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
					"UUID error. incorrect userId=%s, otherUserId=%s", userId,
					otherUserId), e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(RetrievePrivateChatPostsStatus.FAIL);
			RetrievePrivateChatPostsResponseEvent resEvent = new RetrievePrivateChatPostsResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setRetrievePrivateChatPostsResponseProto(resBuilder
					.build());
			server.writeEvent(resEvent);
			return;
		}

		try {
			resBuilder.setStatus(RetrievePrivateChatPostsStatus.SUCCESS);

			List<PrivateChatPost> recentPrivateChatPosts = getPrivateChatPostRetrieveUtils()
					.getPrivateChatPostsBetweenUsersBeforePostId(
							ControllerConstants.RETRIEVE_PLAYER_WALL_POSTS__NUM_POSTS_CAP,
							userId, otherUserId);

			if (recentPrivateChatPosts != null) {
				if (recentPrivateChatPosts != null
						&& recentPrivateChatPosts.size() > 0) {
					List<String> userIds = new ArrayList<String>();
					userIds.add(userId);
					userIds.add(otherUserId);
					Map<String, User> usersByIds = null;
					if (userIds.size() > 0) {
						usersByIds = getUserRetrieveUtils().getUsersByIds(
								userIds);

						//for not hitting the db for every private chat post
						Map<String, MinimumUserProtoWithLevel> userIdsToMups = generateUserIdsToMupsWithLevel(
								usersByIds, userId, senderProto, otherUserId);

						List<String> chatIds = new ArrayList<String>();
						for(PrivateChatPost pcp : recentPrivateChatPosts) {
							chatIds.add(pcp.getId());
						}

						Map<String, String> returnMap = new HashMap<String, String>();
						Map<String, List<ChatTranslations>> chatIdsToTranslations = new HashMap<String, List<ChatTranslations>>();
						List<String> chatIdsToBeTranslated = new ArrayList<String>();

						//CHECK IF USER EVEN wants translations
						if(translateLanguage == null || translateLanguage.equals(TranslateLanguages.NO_TRANSLATION)) {
							for (PrivateChatPost pwp : recentPrivateChatPosts) {
								String posterId = pwp.getPosterId();
								String contentLanguage = pwp.getContentLanguage();
								TranslateLanguages tl = null;

								if(contentLanguage != null) {
									tl = TranslateLanguages.valueOf(contentLanguage);
								}

								long time = pwp.getTimeOfPost().getTime();
								MinimumUserProtoWithLevel user = userIdsToMups
										.get(posterId);
								String content = pwp.getContent();
								boolean isAdmin = false;

								String chatId = pwp.getId();

								GroupChatMessageProto gcmp = createInfoProtoUtils
										.createGroupChatMessageProto(time, user, content, 
												isAdmin, chatId, tl, null, null);
								resBuilder.addPosts(gcmp);
							}
						}
						else {

							//CHECK IF the contentLanguage of the private chat matches translateLanguage						
							for(PrivateChatPost pcp3 : recentPrivateChatPosts) {
								if(pcp3.getContentLanguage() != null) {
									if(pcp3.getContentLanguage().toString().equalsIgnoreCase(translateLanguage.toString())) {
										returnMap.put(pcp3.getId(), pcp3.getContent());
									}
								}
							}

							//CHECK DB if we've translated before, if so grab save that into returnMap
							if(translateLanguage != null && !translateLanguage.equals(TranslateLanguages.NO_TRANSLATION)) {
								chatIdsToTranslations =
										chatTranslationsRetrieveUtils.getChatTranslationsForSpecificChatIds(chatIds);
								log.info("{}", chatIdsToTranslations);

								for(String chatId : chatIdsToTranslations.keySet()) {
									if(!returnMap.containsKey(chatId)) {
										List<ChatTranslations> chatTranslationsList = chatIdsToTranslations.get(chatId);
										for(ChatTranslations ct : chatTranslationsList) {
											if(ct.getTranslateLanguage().toString().equalsIgnoreCase(translateLanguage.toString())) {
												returnMap.put(chatId, ct.getText());
											}
										}
										//if we did not already have a translation
										if(!returnMap.containsKey(chatId)) {
											chatIdsToBeTranslated.add(chatId);
										}
									}
								}
							}

							List<String> allUserIds = new ArrayList<String>();
							List<String> userIdsWithoutContentLanguage = new ArrayList<String>();
							Map<String, String> userIdsToContentLanguage = new HashMap<String, String>();

							//at this point, if a privatechatpost isn't in returnMap yet, it either has a null content language or has a
							//content language that doesn't match translateLanguage/what we want and hasnt been translated before
							for(PrivateChatPost pwp : recentPrivateChatPosts) {
								if(!returnMap.containsKey(pwp.getId())) {
									String posterId = pwp.getPosterId();
									allUserIds.add(posterId);
									String contentLanguage = pwp.getContentLanguage();
									if(contentLanguage == null || contentLanguage.isEmpty()) {
										userIdsWithoutContentLanguage.add(posterId);
									}	
								}
							}

							Map<String, TranslationSettingsForUser> tsfuMap = translationSettingsForUserRetrieveUtil.
									getUserTranslationSettingsMapForUsersGlobal(userIdsWithoutContentLanguage);

							//grab the content language from tsfu's, if there isn't a tsfu for a user, he has no global, give him default lang
							for(String uId : allUserIds) {
								if(!tsfuMap.containsKey(uId)) {
									userIdsToContentLanguage.put(uId, ControllerConstants.TRANSLATION_SETTINGS__DEFAULT_LANGUAGE);
								}
								else userIdsToContentLanguage.put(uId, tsfuMap.get(uId).getLanguage()); 
							}

							//now check the private chat posts with null content language, if the user's global language setting matches 
							//translateLanguage, we'll use the text
							for(PrivateChatPost pcp2 : recentPrivateChatPosts) {
								if(!returnMap.containsKey(pcp2.getId())) {
									if(pcp2.getContentLanguage() == null) {
										String posterId = pcp2.getPosterId();
										String posterGlobalLanguage = userIdsToContentLanguage.get(posterId);
										if(posterGlobalLanguage.equalsIgnoreCase(translateLanguage.toString())) {
											returnMap.put(pcp2.getId(), pcp2.getContent());
										}	
									}
								}
							}

							List<String> chatIdsToBeTranslated2 = new ArrayList<String>();
							Map<String, PrivateChatPost> chatIdToPcP = new HashMap<String, PrivateChatPost>();

							//at this point, any privatechatpost not in returnMap needs to be translated
							for(PrivateChatPost pcp4 : recentPrivateChatPosts) {
								if(!returnMap.containsKey(pcp4.getId())) {
									chatIdsToBeTranslated2.add(pcp4.getId());
									chatIdToPcP.put(pcp4.getId(), pcp4);
								}
							}

							String[] chatIdsArray = chatIdsToBeTranslated2.toArray(new String[chatIdsToBeTranslated2.size()]);
							String[] textArray = new String[chatIdsArray.length];
							for(int i=0; i<chatIdsArray.length; i++) {
								textArray[i] = chatIdToPcP.get(chatIdsArray[i]).getContent();
							}

							String[] translatedTextArray = miscMethods.translateInBulk(textArray, miscMethods.convertFromEnumToLanguage(translateLanguage));

							//add results to returnMap
							for(int i=0; i<chatIdsArray.length; i++) {
								returnMap.put(chatIdsArray[i], translatedTextArray[i]);
							}

							//convert private chat post to group chat message proto
							for (PrivateChatPost pwp : recentPrivateChatPosts) {
								String posterId = pwp.getPosterId();
								String contentLanguage = pwp.getContentLanguage();
								TranslateLanguages tl = null;

								if(contentLanguage != null) {
									tl = TranslateLanguages.valueOf(contentLanguage);
								}

								long time = pwp.getTimeOfPost().getTime();
								MinimumUserProtoWithLevel user = userIdsToMups
										.get(posterId);
								String content = pwp.getContent();
								boolean isAdmin = false;

								String chatId = pwp.getId();

								GroupChatMessageProto gcmp = createInfoProtoUtils
										.createGroupChatMessageProto(time, user, content, 
												isAdmin, chatId, tl, translateLanguage,
												returnMap.get(chatId));
								resBuilder.addPosts(gcmp);

							}
						}
					}
				}
			} else {
				log.info("No private chat posts found for userId=" + userId
						+ " and otherUserId=" + otherUserId);
			}

			RetrievePrivateChatPostsResponseProto resProto = resBuilder.build();

			RetrievePrivateChatPostsResponseEvent resEvent = new RetrievePrivateChatPostsResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setRetrievePrivateChatPostsResponseProto(resProto);

			server.writeEvent(resEvent);
		} catch (Exception e) {
			log.error(
					"exception in RetrievePrivateChatPostsController processEvent",
					e);
			//don't let the client hang
			try {
				resBuilder.setStatus(RetrievePrivateChatPostsStatus.FAIL);
				RetrievePrivateChatPostsResponseEvent resEvent = new RetrievePrivateChatPostsResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setRetrievePrivateChatPostsResponseProto(resBuilder
						.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in RetrievePrivateChatPostsController processEvent",
						e);
			}
		}

	}

	private Map<String, MinimumUserProtoWithLevel> generateUserIdsToMupsWithLevel(
			Map<String, User> usersByIds, String userId,
			MinimumUserProto userMup, String otherUserId) {
		Map<String, MinimumUserProtoWithLevel> userIdsToMups = new HashMap<String, MinimumUserProtoWithLevel>();

		User aUser = usersByIds.get(userId);
		User otherUser = usersByIds.get(otherUserId);

		MinimumUserProtoWithLevel mup1 = createInfoProtoUtils
				.createMinimumUserProtoWithLevel(aUser, null, userMup);
		userIdsToMups.put(userId, mup1);

		Clan otherUserClan = null;
		if (otherUser.getClanId() != null) {
			otherUserClan = getClanRetrieveUtils().getClanWithId(
					otherUser.getClanId());
		}

		MinimumUserProtoWithLevel mup2 = createInfoProtoUtils
				.createMinimumUserProtoWithLevel(otherUser, otherUserClan, null);
		userIdsToMups.put(otherUserId, mup2);

		return userIdsToMups;
	}

	public PrivateChatPostRetrieveUtils2 getPrivateChatPostRetrieveUtils() {
		return privateChatPostRetrieveUtils;
	}

	public void setPrivateChatPostRetrieveUtils(
			PrivateChatPostRetrieveUtils2 privateChatPostRetrieveUtils) {
		this.privateChatPostRetrieveUtils = privateChatPostRetrieveUtils;
	}

	public ClanRetrieveUtils2 getClanRetrieveUtils() {
		return clanRetrieveUtils;
	}

	public void setClanRetrieveUtils(ClanRetrieveUtils2 clanRetrieveUtils) {
		this.clanRetrieveUtils = clanRetrieveUtils;
	}

	public UserRetrieveUtils2 getUserRetrieveUtils() {
		return userRetrieveUtils;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtils) {
		this.userRetrieveUtils = userRetrieveUtils;
	}

}