package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventMiniEventProto.RetrieveMiniEventRequestProto;

public class RetrieveMiniEventRequestEvent extends RequestEvent {

	private Logger log = LoggerFactory.getLogger(RetrieveMiniEventRequestEvent.class);

	private RetrieveMiniEventRequestProto retrieveMiniEventRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			retrieveMiniEventRequestProto = RetrieveMiniEventRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = retrieveMiniEventRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("RetrieveMiniEvent request exception", e);
		}
	}

	public RetrieveMiniEventRequestProto getRetrieveMiniEventRequestProto() {
		return retrieveMiniEventRequestProto;
	}

	public void setRetrieveMiniEventRequestProto(
			RetrieveMiniEventRequestProto retrieveMiniEventRequestProto) {
		this.retrieveMiniEventRequestProto = retrieveMiniEventRequestProto;
	}

}
