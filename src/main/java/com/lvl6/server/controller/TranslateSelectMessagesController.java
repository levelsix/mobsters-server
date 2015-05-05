package com.lvl6.server.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.TranslateSelectMessagesRequestEvent;
import com.lvl6.events.response.TranslateSelectMessagesResponseEvent;
import com.lvl6.info.PrivateChatPost;
import com.lvl6.info.TranslatedText;
import com.lvl6.misc.MiscMethods;
import com.lvl6.proto.ChatProto.ChatScope;
import com.lvl6.proto.ChatProto.PrivateChatPostProto;
import com.lvl6.proto.ChatProto.TranslateLanguages;
import com.lvl6.proto.ChatProto.TranslatedTextProto;
import com.lvl6.proto.EventChatProto.TranslateSelectMessagesRequestProto;
import com.lvl6.proto.EventChatProto.TranslateSelectMessagesResponseProto;
import com.lvl6.proto.EventChatProto.TranslateSelectMessagesResponseProto.TranslateSelectMessagesStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ResearchForUserRetrieveUtils;
import com.lvl6.retrieveutils.TranslationSettingsForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.ChatTranslationsRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ResearchRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.TranslateSelectMessagesAction;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

@Component
@DependsOn("gameServer")
public class TranslateSelectMessagesController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	protected Locker locker;

	@Autowired
	protected TimeUtils timeUtils;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtils;

	@Autowired
	protected MiscMethods miscMethods;

	@Autowired
	protected ResearchForUserRetrieveUtils researchForUserRetrieveUtils;

	@Autowired
	protected ResearchRetrieveUtils researchRetrieveUtils;

	@Autowired
	protected TranslationSettingsForUserRetrieveUtil translationSettingsForUserRetrieveUtil;

	@Autowired
	protected UpdateUtil updateUtil;

	@Autowired
	protected InsertUtil insertUtil;

	@Autowired
	protected ChatTranslationsRetrieveUtils chatTranslationsRetrieveUtils;

	public TranslateSelectMessagesController() {
		numAllocatedThreads = 4;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new TranslateSelectMessagesRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_TRANSLATE_SELECT_MESSAGES_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		TranslateSelectMessagesRequestProto reqProto = ((TranslateSelectMessagesRequestEvent) event)
				.getTranslateSelectMessagesRequestProto();
		log.info("reqProto={}", reqProto);

		MinimumUserProto senderProto = reqProto.getSender();
		String recipientUserId = senderProto.getUserUuid();
		//this guy sent the msgs
		String senderUserId = reqProto.getOtherUserUuid();
		boolean translateOn = reqProto.getTranslateOn();

		ChatScope ct = reqProto.getChatType();

		TranslateLanguages language = reqProto.getLanguage();
		List<PrivateChatPostProto> listOfPrivateChatProtos = reqProto.getMessagesToBeTranslatedList();
		List<PrivateChatPost> listOfPrivateChatPosts = new ArrayList<PrivateChatPost>();

		if(listOfPrivateChatProtos != null && !listOfPrivateChatProtos.isEmpty()) {
			listOfPrivateChatPosts = convertFromProtos(listOfPrivateChatProtos);
		}


		//values to send to client
		TranslateSelectMessagesResponseProto.Builder resBuilder = TranslateSelectMessagesResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(TranslateSelectMessagesStatus.FAIL_OTHER);

		UUID recipientUserUuid = null;
		UUID senderUserUuid = null;
		boolean invalidUuids = true;
		try {
			recipientUserUuid = UUID.fromString(recipientUserId);

			if(senderUserId != null && !senderUserId.isEmpty()) {
				senderUserUuid = UUID.fromString(senderUserId);
			}

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format("UUID error. incorrect recipientUserId=%s or senderUserId=%s",
					recipientUserId, senderUserId),
					e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(TranslateSelectMessagesStatus.FAIL_OTHER);
			TranslateSelectMessagesResponseEvent resEvent = new TranslateSelectMessagesResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setTranslateSelectMessagesResponseProto(resBuilder.build());
			server.writeEvent(resEvent);
			return;
		}

		locker.lockPlayer(recipientUserUuid, this.getClass().getSimpleName());
		try {

			TranslateSelectMessagesAction tsma = new TranslateSelectMessagesAction(recipientUserId,
					senderUserId, language, listOfPrivateChatPosts, ct, translationSettingsForUserRetrieveUtil,
					translateOn, insertUtil, updateUtil, miscMethods, chatTranslationsRetrieveUtils);

			tsma.execute(resBuilder);

			Map<String, PrivateChatPost> privateChatPostMap;
			if (TranslateSelectMessagesStatus.SUCCESS.equals(resBuilder.getStatus()) && ct.equals(ChatScope.PRIVATE)) {
				privateChatPostMap = tsma.getPrivateChatPostMap();
				log.info("PRIVATE CHAT POST MAP: " + privateChatPostMap);

				if (null != privateChatPostMap) {
					List<PrivateChatPostProto> pcppList = createNewPrivateChatPostProtoWithTranslations(
							listOfPrivateChatProtos, privateChatPostMap);
					log.info("pcppList" + pcppList);
					resBuilder.addAllMessagesTranslated(pcppList);
				}
			}
			TranslateSelectMessagesResponseProto resProto = resBuilder.build();
			TranslateSelectMessagesResponseEvent resEvent = new TranslateSelectMessagesResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setTranslateSelectMessagesResponseProto(resProto);
			server.writeEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in TranslateSelectMessagesController processEvent", e);
			// don't let the client hang
			try {
				resBuilder.setStatus(TranslateSelectMessagesStatus.FAIL_OTHER);
				TranslateSelectMessagesResponseEvent resEvent = new TranslateSelectMessagesResponseEvent(
						senderProto.getUserUuid());
				resEvent.setTag(event.getTag());
				resEvent.setTranslateSelectMessagesResponseProto(resBuilder.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in SellUserMonsterController processEvent",
						e);
			}
		} finally {
			locker.unlockPlayer(recipientUserUuid, this.getClass().getSimpleName());
		}
	}

	private List<PrivateChatPost> convertFromProtos(List<PrivateChatPostProto> list) {
		List<PrivateChatPost> returnList = new ArrayList<PrivateChatPost>();
		for(PrivateChatPostProto pcpp : list) {
			PrivateChatPost pcp = new PrivateChatPost();
			pcp.setId(pcpp.getPrivateChatPostUuid());
			pcp.setPosterId(pcpp.getPoster().getMinUserProto().getUserUuid());
			pcp.setRecipientId(pcpp.getRecipient().getMinUserProto().getUserUuid());
			pcp.setTimeOfPost(new Date(pcpp.getTimeOfPost()));
			pcp.setContent(pcpp.getContent());
			returnList.add(pcp);
		}
		return returnList;
	}

	private List<PrivateChatPostProto> createNewPrivateChatPostProtoWithTranslations(
			List<PrivateChatPostProto> list, Map<String, PrivateChatPost> privateChatPostMap) {

		List<PrivateChatPostProto> returnList = new ArrayList<PrivateChatPostProto>();
		for(PrivateChatPostProto pcpp : list) {
			PrivateChatPostProto.Builder pcppb = PrivateChatPostProto.newBuilder();
			String id = pcpp.getPrivateChatPostUuid();
			pcppb.setPrivateChatPostUuid(id);
			pcppb.setPoster(pcpp.getPoster());
			pcppb.setRecipient(pcpp.getRecipient());
			pcppb.setTimeOfPost(pcpp.getTimeOfPost());
			pcppb.setContent(pcpp.getContent());
			TranslatedText tt = privateChatPostMap.get(id).getTranslatedText();
			TranslatedTextProto.Builder ttpb = TranslatedTextProto.newBuilder();
			ttpb.setLanguage(TranslateLanguages.valueOf(tt.getLanguage()));
			ttpb.setText(tt.getText());
			pcppb.addTranslatedContent(ttpb.build());
			returnList.add(pcppb.build());
		}
		return returnList;
	}


	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public TimeUtils getTimeUtils() {
		return timeUtils;
	}

	public void setTimeUtils(TimeUtils timeUtils) {
		this.timeUtils = timeUtils;
	}

	public UserRetrieveUtils2 getUserRetrieveUtils() {
		return userRetrieveUtils;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtils) {
		this.userRetrieveUtils = userRetrieveUtils;
	}

	public UpdateUtil getUpdateUtil() {
		return updateUtil;
	}

	public void setUpdateUtil(UpdateUtil updateUtil) {
		this.updateUtil = updateUtil;
	}

}
