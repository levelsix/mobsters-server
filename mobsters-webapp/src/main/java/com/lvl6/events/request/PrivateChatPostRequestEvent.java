package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventChatProto.PrivateChatPostRequestProto;

public class PrivateChatPostRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(PrivateChatPostRequestEvent.class);

	private PrivateChatPostRequestProto privateChatPostRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			privateChatPostRequestProto = PrivateChatPostRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = privateChatPostRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("PrivateChatPostRequest exception", e);
		}
	}

	public PrivateChatPostRequestProto getPrivateChatPostRequestProto() {
		return privateChatPostRequestProto;
	}

	public void setPrivateChatPostRequestProto(PrivateChatPostRequestProto pcprp) {
		this.privateChatPostRequestProto = pcprp;
	}

	@Override
	public String toString() {
		return "PrivateChatPostRequestEvent [privateChatPostRequestProto="
				+ privateChatPostRequestProto + "]";
	}

}
