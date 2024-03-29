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
import com.lvl6.proto.ChatProto.ChatScope;
import com.lvl6.proto.ChatProto.GroupChatMessageProto;
import com.lvl6.proto.EventChatProto.ReceivedGroupChatResponseProto;
import com.lvl6.proto.EventChatProto.SendAdminMessageResponseProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.server.eventsender.EventWriter;
import com.lvl6.util.EventParser;

@Component
public class MessagingUtil {
	private static final Logger log = LoggerFactory
			.getLogger(MessagingUtil.class);


	//TODO: Fix this whole class to work with websockets


	@Autowired
	EventWriter eventWriter;

	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;

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
		return createInfoProtoUtils.createMinimumUserProtoFromUserAndClan(alex,
				null);
	}

	public byte[] getMaintanenceModeMessageUdid(String message, String udid, String uuid) {
		log.info("Sending maintenance mode message: \"{}\" to player {}",
				message, udid);
		//send admin message
		SendAdminMessageResponseProto.Builder samrp = SendAdminMessageResponseProto
				.newBuilder();
		samrp.setMessage(message);
		samrp.setSenderUuid(ControllerConstants.USER_CREATE__ID_OF_POSTER_OF_FIRST_WALL);
		SendAdminMessageResponseEvent ev = new SendAdminMessageResponseEvent("");
		ev.setResponseProto(samrp.build());
		return EventParser.getResponseBytes(uuid, ev);
		//eventWriter.processPreDBResponseEvent(ev, udid);
	}

	public byte[] getMaintanenceModeMessage(String message, String playerId, String uuid) {
		log.info("Sending maintenance mode message: \"{}\" to player {}",
				message, playerId);
		//send admin message
		SendAdminMessageResponseProto.Builder samrp = SendAdminMessageResponseProto
				.newBuilder();
		samrp.setMessage(message);
		samrp.setSenderUuid(ControllerConstants.USER_CREATE__ID_OF_POSTER_OF_FIRST_WALL);
		SendAdminMessageResponseEvent ev = new SendAdminMessageResponseEvent(playerId);
		ev.setResponseProto(samrp.build());
		return EventParser.getResponseBytes(uuid, ev);
	}

	public void sendAdminMessage(String message, String uuid) {
		log.info("Sending admin message: {}", message);
		//send admin message
		SendAdminMessageResponseProto.Builder samrp = SendAdminMessageResponseProto
				.newBuilder();
		samrp.setMessage(message);
		samrp.setSenderUuid(ControllerConstants.USER_CREATE__ID_OF_POSTER_OF_FIRST_WALL);
		SendAdminMessageResponseEvent ev = new SendAdminMessageResponseEvent(
				samrp.getSenderUuid());
		ev.setResponseProto(samrp.build());
		//eventWriter.processGlobalChatResponseEvent(ev);
		//send regular global˙ chat
		log.info("Sending admin message global chat");
		final ReceivedGroupChatResponseProto.Builder chatProto = ReceivedGroupChatResponseProto
				.newBuilder();
		MinimumUserProto senderProto = getAlexUserProto();
		final ChatScope scope = ChatScope.GLOBAL;
		final Timestamp timeOfPost = new Timestamp(new Date().getTime());
		chatProto.setChatMessage(message);
		chatProto.setSender(senderProto);
		chatProto.setScope(scope);
		sendChatMessage(senderProto.getUserUuid(), chatProto,
				1, timeOfPost.getTime());
	}

	protected void sendChatMessage(String senderId,
			ReceivedGroupChatResponseProto.Builder chatProto, int tag, long time) {
		ReceivedGroupChatResponseEvent ce = new ReceivedGroupChatResponseEvent(
				senderId);
		ce.setResponseProto(chatProto.build());
		ce.setTag(tag);
		log.info("Sending global chat ");
		//add new message to front of list
		chatMessages.add(0, createInfoProtoUtils.createGroupChatMessageProto(
				time, chatProto.getSender(), chatProto.getChatMessage(), true,
				null, null, null, ""));
		//TODO: Fix this
		//eventWriter.processGlobalChatResponseEvent(ce);
	}

	public void sendGlobalMessage(ResponseEvent re) {
		//eventWriter.processGlobalChatResponseEvent(re);
	}
}
