package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventClanProto.RetrieveClanInfoRequestProto;

public class RetrieveClanInfoRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private RetrieveClanInfoRequestProto retrieveClanInfoRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			retrieveClanInfoRequestProto = RetrieveClanInfoRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = retrieveClanInfoRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("RetrieveClanInfoRequest exception", e);
		}
	}

	public RetrieveClanInfoRequestProto getRetrieveClanInfoRequestProto() {
		return retrieveClanInfoRequestProto;
	}

	public void setRetrieveClanInfoRequestProto(
			RetrieveClanInfoRequestProto retrieveClanInfoRequestProto) {
		this.retrieveClanInfoRequestProto = retrieveClanInfoRequestProto;
	}

	@Override
	public String toString() {
		return "RetrieveClanInfoRequestEvent [retrieveClanInfoRequestProto="
				+ retrieveClanInfoRequestProto + "]";
	}

}
