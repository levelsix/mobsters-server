package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.hazelcast.core.IList;
import com.lvl6.clansearch.ClanSearch;
import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.SendGroupChatRequestEvent;
import com.lvl6.events.response.ReceivedGroupChatResponseEvent;
import com.lvl6.events.response.SendGroupChatResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.Clan;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.CustomTranslationsDao;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.CustomTranslations;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.ChatProto.ChatScope;
import com.lvl6.proto.ChatProto.GroupChatMessageProto;
import com.lvl6.proto.ChatProto.TranslateLanguages;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.EventChatProto.ReceivedGroupChatResponseProto;
import com.lvl6.proto.EventChatProto.SendGroupChatRequestProto;
import com.lvl6.proto.EventChatProto.SendGroupChatResponseProto;
import com.lvl6.proto.EventChatProto.SendGroupChatResponseProto.Builder;
import com.lvl6.proto.EventChatProto.SendGroupChatResponseProto.SendGroupChatStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.proto.UserProto.MinimumUserProtoWithLevel;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.retrieveutils.TranslationSettingsForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.BannedUserRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.CustomTranslationRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ServerToggleRetrieveUtils;
import com.lvl6.server.EventWriter;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.utils.TranslationUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.InsertUtils;
import com.memetix.mst.language.Language;

@Component
@DependsOn("gameServer")
public class SendGroupChatController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	public static int CHAT_MESSAGES_MAX_SIZE = 50;

	@Resource(name = "globalChat")
	protected IList<GroupChatMessageProto> chatMessages;

	@Resource
	protected EventWriter eventWriter;

	@Autowired
	protected MiscMethods miscMethods;

	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;
	
	@Autowired
	BannedUserRetrieveUtils bannedUserRetrieveUtils;

	@Autowired
	protected Locker locker;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtils;

	@Autowired
	protected UserClanRetrieveUtils2 userClanRetrieveUtil;

	@Autowired
	protected ClanRetrieveUtils2 clanRetrieveUtil;

	@Autowired
	protected TranslationSettingsForUserRetrieveUtil translationSettingsForUserRetrieveUtil;

	@Autowired
	protected ClanSearch clanSearch;
	
	@Autowired
	protected TranslationUtils translationUtils;
	
	@Autowired
	protected ServerToggleRetrieveUtils toggle;
	
	@Autowired
	protected CustomTranslationsDao customTranslationsDao;
	
	@Autowired
	protected CustomTranslationRetrieveUtils customTranslationRetrieveUtils;

	public SendGroupChatController() {
		numAllocatedThreads = 4;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new SendGroupChatRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_SEND_GROUP_CHAT_EVENT;
	}

	@Override
	protected void processRequestEvent(final RequestEvent event)
			throws Exception {
		final SendGroupChatRequestProto reqProto = ((SendGroupChatRequestEvent) event)
				.getSendGroupChatRequestProto();

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		final ChatScope scope = reqProto.getScope();
		String chatMessage = reqProto.getChatMessage();
		final Timestamp timeOfPost = new Timestamp(new Date().getTime());
		TranslateLanguages globalLanguage = reqProto.getGlobalLanguage();

		SendGroupChatResponseProto.Builder resBuilder = SendGroupChatResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(SendGroupChatStatus.OTHER_FAIL);
		SendGroupChatResponseEvent resEvent = new SendGroupChatResponseEvent(
				senderProto.getUserUuid());
		resEvent.setTag(event.getTag());

		UUID userUuid = null;
		boolean invalidUuids = true;
		try {
			userUuid = UUID.fromString(userId);

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format("UUID error. incorrect userId=%s", userId),
					e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(SendGroupChatStatus.OTHER_FAIL);
			resEvent.setSendGroupChatResponseProto(resBuilder.build());
			server.writeEvent(resEvent);
			return;
		}

		getLocker().lockPlayer(userUuid, this.getClass().getSimpleName());
		try {
			final User user = getUserRetrieveUtils().getUserById(
					senderProto.getUserUuid());

			boolean legitSend = checkLegitSend(resBuilder, user, scope,
					chatMessage);

			resEvent.setSendGroupChatResponseProto(resBuilder.build());
			server.writeEvent(resEvent);

			if (legitSend) {
				log.info("Group chat message is legit... sending to group");
				String censoredChatMessage = miscMethods
						.censorUserInput(chatMessage);
				writeChangesToDB(user, scope, censoredChatMessage, timeOfPost);

				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = miscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								user, null, null);
				resEventUpdate.setTag(event.getTag());
				server.writeEvent(resEventUpdate);
				final ReceivedGroupChatResponseProto.Builder chatProto = ReceivedGroupChatResponseProto
						.newBuilder();

				Language detectedLanguage = translationUtils.detectedLanguage(censoredChatMessage, toggle);
				log.info("detected language={}", detectedLanguage);
				
				if (detectedLanguage == null) {
					// Default to the content language
					detectedLanguage = translationUtils.convertFromEnumToLanguage(globalLanguage);
					log.info("defaulting to content language={}", detectedLanguage);
				}
				
//				Map<TranslateLanguages, String> translateMap = translationUtils.translateForGlobal(detectedLanguage, censoredChatMessage);
//				String customTranslationLanguage = null;
				Map<Integer, CustomTranslations> ctMap = customTranslationRetrieveUtils.getIdsToCustomTranslationss();
			
				for(int id : ctMap.keySet()) {
					String phrase = ctMap.get(id).getPhrase();
					if(phrase.equalsIgnoreCase(censoredChatMessage)) {
						detectedLanguage = Language.valueOf(ctMap.get(id).getLanguage());
					}
				}
//				Map<TranslateLanguages, String> translateMap = null;
//				if(customTranslationLanguage == null) {
//					translateMap = translationUtils.translate(null, null, censoredChatMessage, toggle);
//				}
//				else {
//					translateMap = translationUtils.translate(Language.valueOf(customTranslationLanguage),
//							null, censoredChatMessage, toggle);
//				}
				Map<TranslateLanguages, String> translateMap = translationUtils.translate(detectedLanguage, null, 
						censoredChatMessage, toggle);
				
				for(TranslateLanguages tl : translateMap.keySet()) {
					String translatedContent = translateMap.get(tl);
					if(translatedContent.toLowerCase().contains("ArgumentOutOfRangeException".toLowerCase())) {
						translateMap.put(tl, censoredChatMessage);
						log.error("argumentoutofrangeexception for translating, word was {}", chatMessage);
					}
				}

				MinimumUserProtoWithLevel mupWithLvl = createInfoProtoUtils
						.createMinimumUserProtoWithLevel(user, null,
								senderProto);
				chatProto.setSender(mupWithLvl);
				chatProto.setScope(scope);
				
				GroupChatMessageProto gcmp = createInfoProtoUtils
						.createGroupChatMessageProto(timeOfPost.getTime(), mupWithLvl,
								censoredChatMessage, user.isAdmin(), "global msg", translateMap, 
								translationUtils.convertFromLanguageToEnum(detectedLanguage, toggle),
								translationUtils);
//				GroupChatMessageProto gcmp = createInfoProtoUtils
//						.createGroupChatMessageProto(timeOfPost.getTime(), mupWithLvl,
//								censoredChatMessage, user.isAdmin(), "global msg", translateMap, globalLanguage,
//								translationUtils);
				
				chatProto.setMessage(gcmp);

                //legacy implementation
                chatProto.setChatMessage(censoredChatMessage);

				ReceivedGroupChatResponseProto rgcrp = chatProto.build();

				log.info("receive group chat response proto : {}", rgcrp);

				sendChatMessage(userId, scope == ChatScope.CLAN, user.getClanId(), rgcrp);

				// send messages in background so sending player can unlock
				/*
				 * executor.execute(new Runnable() {
				 *
				 * @Override public void run() {
				 * sendChatMessageToConnectedPlayers(chatProto, event.getTag(),
				 * timeOfPost.getTime(), scope == GroupChatScope.CLAN,
				 * user.getClanId(), user.isAdmin()); } });
				 */
			}
		} catch (Exception e) {
			log.error("exception in SendGroupChat processEvent", e);
			//don't let the client hang
			try {
				resBuilder.setStatus(SendGroupChatStatus.OTHER_FAIL);
				resEvent.setSendGroupChatResponseProto(resBuilder.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in SendGroupChat processEvent", e);
			}
		} finally {
			getLocker().unlockPlayer(userUuid, this.getClass().getSimpleName());
		}
	}

	protected void sendChatMessage(String senderId,
			boolean isForClan, String clanId, ReceivedGroupChatResponseProto rgcr) {
		ReceivedGroupChatResponseEvent ce = new ReceivedGroupChatResponseEvent(
				senderId);
		ce.setReceivedGroupChatResponseProto(rgcr);
		if (isForClan) {
			log.info("Sending event to clan " + clanId);
			eventWriter.handleClanEvent(ce, clanId);
		} else {
			log.info("Sending global chat ");
			//add new message to front of list
			chatMessages.add(0, rgcr.getMessage());
			//remove older messages
			try {
				while (chatMessages.size() > CHAT_MESSAGES_MAX_SIZE) {
					chatMessages.remove(CHAT_MESSAGES_MAX_SIZE);
				}
			} catch (Exception e) {
				log.error("Error sending chat message", e);
			}
			eventWriter.processGlobalChatResponseEvent(ce);
		}
	}

	/*
	 * protected void
	 * sendChatMessageToConnectedPlayers(ReceivedGroupChatResponseProto.Builder
	 * chatProto, int tag, long time, boolean forClan, int clanId, boolean
	 * isAdmin) { Collection<ConnectedPlayer> players = new
	 * ArrayList<ConnectedPlayer>(); if (forClan) { List<UserClan> clanMembers =
	 * RetrieveUtils.userClanRetrieveUtils().getUserClanMembersInClan( clanId);
	 * for (UserClan uc : clanMembers) { ConnectedPlayer cp =
	 * playersByPlayerId.get(uc.getUserId()); if (cp != null) { players.add(cp);
	 * } } } else { players = playersByPlayerId.values(); // add new message to
	 * front of list chatMessages.add( 0,
	 * CreateInfoProtoUtils.createGroupChatMessageProto(time,
	 * chatProto.getSender(), chatProto.getChatMessage(), isAdmin)); // remove
	 * older messages try { while (chatMessages.size() > CHAT_MESSAGES_MAX_SIZE)
	 * { chatMessages.remove(CHAT_MESSAGES_MAX_SIZE); } } catch (Exception e) {
	 * log.error(e); } } for (ConnectedPlayer player : players) {
	 * log.info("Sending chat message to player: " + player.getPlayerId());
	 * ReceivedGroupChatResponseEvent ce = new
	 * ReceivedGroupChatResponseEvent(player.getPlayerId());
	 * ce.setReceivedGroupChatResponseProto(chatProto.build()); ce.setTag(tag);
	 * try { server.writeEvent(ce); } catch (Exception e) { log.error(e); } } }
	 */

	private void writeChangesToDB(User user, ChatScope scope,
			String content, Timestamp timeOfPost) {
		// if (!user.updateRelativeNumGroupChatsRemainingAndDiamonds(-1, 0)) {
		// log.error("problem with decrementing a global chat");
		// }

		Map<TranslateLanguages, String> translatedTextMap;

		if (scope == ChatScope.CLAN) {
			String clanId = user.getClanId();

//			translatedTextMap = MiscMethods.translate(null, content);

			String clanChatId = InsertUtils.get().insertClanChatPost(user.getId(), clanId, content,
					timeOfPost);

//			ChatType chatType = ChatType.CLAN_CHAT;
//
//			boolean success = InsertUtils.get().insertTranslatedText(chatType, clanChatId, translatedTextMap);
//			if(!success) {
//				log.error("error inserting translated texts into table");
//			}

			//update clan cache
			Clan c = clanRetrieveUtil.getClanWithId(clanId);
			int clanSize = ClanSearch.penalizedClanSize;
			Date lastChatTime = ControllerConstants.INCEPTION_DATE;

			if (!c.isRequestToJoinRequired()) {
				//people can join clan freely
				List<String> clanIdList = Collections.singletonList(clanId);
				List<String> statuses = new ArrayList<String>();
				statuses.add(UserClanStatus.LEADER.name());
				statuses.add(UserClanStatus.JUNIOR_LEADER.name());
				statuses.add(UserClanStatus.CAPTAIN.name());
				statuses.add(UserClanStatus.MEMBER.name());
				Map<String, Integer> clanIdToSize = userClanRetrieveUtil
						.getClanSizeForClanIdsAndStatuses(clanIdList, statuses);

				clanSize = clanIdToSize.get(clanId);
				lastChatTime = new Date(timeOfPost.getTime());
			}

			clanSearch.updateClanSearchRank(clanId, clanSize, lastChatTime);
		}

	}

	private boolean checkLegitSend(Builder resBuilder, User user,
			ChatScope scope, String chatMessage) {
		if (user == null || scope == null || chatMessage == null
				|| chatMessage.length() == 0) {
			resBuilder.setStatus(SendGroupChatStatus.OTHER_FAIL);
			log.error("user is " + user + ", scope is " + scope
					+ ", chatMessage=" + chatMessage);
			return false;
		}

		if (chatMessage.length() > ControllerConstants.SEND_GROUP_CHAT__MAX_LENGTH_OF_CHAT_STRING) {
			resBuilder.setStatus(SendGroupChatStatus.TOO_LONG);
			log.error("chat message is too long. allowed is "
					+ ControllerConstants.SEND_GROUP_CHAT__MAX_LENGTH_OF_CHAT_STRING
					+ ", length is " + chatMessage.length()
					+ ", chatMessage is " + chatMessage);
			return false;
		}

		Set<Integer> banned = bannedUserRetrieveUtils.getAllBannedUsers();
		if (banned.contains(user.getId())) {
			resBuilder.setStatus(SendGroupChatStatus.BANNED);
			log.warn("banned user tried to send a post. user=" + user);
			return false;
		}

		resBuilder.setStatus(SendGroupChatStatus.SUCCESS);
		return true;
	}

	public IList<GroupChatMessageProto> getChatMessages() {
		return chatMessages;
	}

	public void setChatMessages(IList<GroupChatMessageProto> chatMessages) {
		this.chatMessages = chatMessages;
	}

	@Override
	public EventWriter getEventWriter() {
		return eventWriter;
	}

	@Override
	public void setEventWriter(EventWriter eventWriter) {
		this.eventWriter = eventWriter;
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public UserRetrieveUtils2 getUserRetrieveUtils() {
		return userRetrieveUtils;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtils) {
		this.userRetrieveUtils = userRetrieveUtils;
	}

	public UserClanRetrieveUtils2 getUserClanRetrieveUtil() {
		return userClanRetrieveUtil;
	}

	public void setUserClanRetrieveUtil(
			UserClanRetrieveUtils2 userClanRetrieveUtil) {
		this.userClanRetrieveUtil = userClanRetrieveUtil;
	}

	public ClanRetrieveUtils2 getClanRetrieveUtil() {
		return clanRetrieveUtil;
	}

	public void setClanRetrieveUtil(ClanRetrieveUtils2 clanRetrieveUtil) {
		this.clanRetrieveUtil = clanRetrieveUtil;
	}

	public ClanSearch getClanSearch() {
		return clanSearch;
	}

	public void setClanSearch(ClanSearch clanSearch) {
		this.clanSearch = clanSearch;
	}


}
