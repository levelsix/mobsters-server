package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventApnsProto.EnableAPNSRequestProto;

public class EnableAPNSRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private EnableAPNSRequestProto enableAPNSRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			enableAPNSRequestProto = EnableAPNSRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = enableAPNSRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("EnableAPNSRequest exception", e);
		}
	}

	public EnableAPNSRequestProto getEnableAPNSRequestProto() {
		return enableAPNSRequestProto;
	}

	@Override
	public String toString() {
		return "EnableAPNSRequestEvent [enableAPNSRequestProto="
				+ enableAPNSRequestProto + "]";
	}

}
