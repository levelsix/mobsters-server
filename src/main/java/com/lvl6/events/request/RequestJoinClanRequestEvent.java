package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventClanProto.RequestJoinClanRequestProto;

public class RequestJoinClanRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private RequestJoinClanRequestProto requestJoinClanRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			requestJoinClanRequestProto = RequestJoinClanRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = requestJoinClanRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("RequestJoinClanRequest exception", e);
		}
	}

	public RequestJoinClanRequestProto getRequestJoinClanRequestProto() {
		return requestJoinClanRequestProto;
	}

	@Override
	public String toString() {
		return "RequestJoinClanRequestEvent [requestJoinClanRequestProto="
				+ requestJoinClanRequestProto + "]";
	}

}
