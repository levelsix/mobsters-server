package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventChatProto.TranslateSelectMessagesRequestProto;

public class TranslateSelectMessagesRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(TranslateSelectMessagesRequestEvent.class);

	private TranslateSelectMessagesRequestProto translateSelectMessagesRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			translateSelectMessagesRequestProto = TranslateSelectMessagesRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = translateSelectMessagesRequestProto.getSender()
					.getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("TranslateSelectMessagesRequest exception", e);
		}
	}

	public TranslateSelectMessagesRequestProto getTranslateSelectMessagesRequestProto() {
		return translateSelectMessagesRequestProto;
	}

	public void setTranslateSelectMessagesRequestProto(
			TranslateSelectMessagesRequestProto translateSelectMessagesRequestProto) {
		this.translateSelectMessagesRequestProto = translateSelectMessagesRequestProto;
	}

	@Override
	public String toString() {
		return "TranslateSelectMessagesRequestEvent [translateSelectMessagesRequestProto="
				+ translateSelectMessagesRequestProto + "]";
	}

}
