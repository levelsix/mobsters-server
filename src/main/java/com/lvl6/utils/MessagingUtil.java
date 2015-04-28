package com.lvl6.utils;

import java.sql.Timestamp;
import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hazelcast.core.IList;
import com.lvl6.events.ResponseEvent;
import com.lvl6.events.response.ReceivedGroupChatResponseEvent;
import com.lvl6.events.response.SendAdminMessageResponseEvent;
import com.lvl6.info.User;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.ChatProto.GroupChatMessageProto;
import com.lvl6.proto.ChatProto.GroupChatScope;
import com.lvl6.proto.EventChatProto.ReceivedGroupChatResponseProto;
import com.lvl6.proto.EventChatProto.SendAdminMessageResponseProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.proto.UserProto.MinimumUserProtoWithLevel;
import com.lvl6.server.EventWriter;

@Component
public class MessagingUtil {
	private static final Logger log = LoggerFactory
			.getLogger(MessagingUtil.class);

	@Autowired
	EventWriter eventWriter;

	public EventWriter getEventWriter() {
		return eventWriter;
	}

	public void setEventWriter(EventWriter eventWriter) {
		this.eventWriter = eventWriter;
	}

	@Resource(name = "globalChat")
	protected IList<GroupChatMessageProto> chatMessages;

	public IList<GroupChatMessageProto> getChatMessages() {
		return chatMessages;
	}

	public void setChatMessages(IList<GroupChatMessageProto> chatMessages) {
		this.chatMessages = chatMessages;
	}

	public MinimumUserProto getAlexUserProto() {
		User alex = RetrieveUtils.userRetrieveUtils().getUserById(
				ControllerConstants.USER_CREATE__ID_OF_POSTER_OF_FIRST_WALL);
		return CreateInfoProtoUtils.createMinimumUserProtoFromUserAndClan(alex,
				null);
	}

	public MinimumUserProtoWithLevel getAlexUserProtoWithLvl() {
		User alex = RetrieveUtils.userRetrieveUtils().getUserById(
				ControllerConstants.USER_CREATE__ID_OF_POSTER_OF_FIRST_WALL);
		return CreateInfoProtoUtils.createMinimumUserProtoWithLevel(alex, null,
				null);
	}

	public void sendMaintanenceModeMessageUdid(String message, String udid) {
		log.info("Sending maintenance mode message: \"{}\" to player {}",
				message, udid);
		//send admin message
		SendAdminMessageResponseProto.Builder samrp = SendAdminMessageResponseProto
				.newBuilder();
		samrp.setMessage(message);
		samrp.setSenderUuid(ControllerConstants.USER_CREATE__ID_OF_POSTER_OF_FIRST_WALL);
		SendAdminMessageResponseEvent ev = new SendAdminMessageResponseEvent("");
		ev.setSendAdminMessageResponseProto(samrp.build());
		eventWriter.processPreDBResponseEvent(ev, udid);
	}

	public void sendMaintanenceModeMessage(String message, String playerId) {
		log.info("Sending maintenance mode message: \"{}\" to player {}",
				message, playerId);
		//send admin message
		SendAdminMessageResponseProto.Builder samrp = SendAdminMessageResponseProto
				.newBuilder();
		samrp.setMessage(message);
		samrp.setSenderUuid(ControllerConstants.USER_CREATE__ID_OF_POSTER_OF_FIRST_WALL);
		SendAdminMessageResponseEvent ev = new SendAdminMessageResponseEvent(
				playerId);
		ev.setSendAdminMessageResponseProto(samrp.build());
		eventWriter.handleEvent(ev);
	}

	public void sendAdminMessage(String message) {
		log.info("Sending admin message: {}", message);
		//send admin message
		SendAdminMessageResponseProto.Builder samrp = SendAdminMessageResponseProto
				.newBuilder();
		samrp.setMessage(message);
		samrp.setSenderUuid(ControllerConstants.USER_CREATE__ID_OF_POSTER_OF_FIRST_WALL);
		SendAdminMessageResponseEvent ev = new SendAdminMessageResponseEvent(
				samrp.getSenderUuid());
		ev.setSendAdminMessageResponseProto(samrp.build());
		eventWriter.processGlobalChatResponseEvent(ev);
		//send regular global chat
		log.info("Sending admin message global chat");
		final ReceivedGroupChatResponseProto.Builder chatProto = ReceivedGroupChatResponseProto
				.newBuilder();
		MinimumUserProtoWithLevel senderProto = getAlexUserProtoWithLvl();
		final GroupChatScope scope = GroupChatScope.GLOBAL;
		final Timestamp timeOfPost = new Timestamp(new Date().getTime());
		chatProto.setChatMessage(message);
		chatProto.setSender(senderProto);
		chatProto.setScope(scope);
		sendChatMessage(senderProto.getMinUserProto().getUserUuid(), chatProto,
				1, timeOfPost.getTime());
	}

	protected void sendChatMessage(String senderId,
			ReceivedGroupChatResponseProto.Builder chatProto, int tag, long time) {
		ReceivedGroupChatResponseEvent ce = new ReceivedGroupChatResponseEvent(
				senderId);
		ce.setReceivedGroupChatResponseProto(chatProto.build());
		ce.setTag(tag);
		log.info("Sending global chat ");
		//add new message to front of list
		chatMessages.add(0, CreateInfoProtoUtils.createGroupChatMessageProto(
				time, chatProto.getSender(), chatProto.getChatMessage(), true,
				null));
		eventWriter.processGlobalChatResponseEvent(ce);
	}

	public void sendGlobalMessage(ResponseEvent re) {
		eventWriter.processGlobalChatResponseEvent(re);
	}
}
