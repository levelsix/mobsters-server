package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventChatProto.SendGroupChatRequestProto;

public class SendGroupChatRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(SendGroupChatRequestEvent.class);

	private SendGroupChatRequestProto sendGroupChatRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			sendGroupChatRequestProto = SendGroupChatRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = sendGroupChatRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("SendGroupChatRequest exception", e);
		}
	}

	public SendGroupChatRequestProto getSendGroupChatRequestProto() {
		return sendGroupChatRequestProto;
	}
	
	public void setSendGroupChatRequestProto(SendGroupChatRequestProto sgcrp) {
		this.sendGroupChatRequestProto = sgcrp;
	}

	@Override
	public String toString() {
		return "SendGroupChatRequestEvent [sendGroupChatRequestProto="
				+ sendGroupChatRequestProto + "]";
	}

}
