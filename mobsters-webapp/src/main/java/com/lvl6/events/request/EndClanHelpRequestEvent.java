package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventClanProto.EndClanHelpRequestProto;

public class EndClanHelpRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(EndClanHelpRequestEvent.class);

	private EndClanHelpRequestProto endClanHelpRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			endClanHelpRequestProto = EndClanHelpRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = endClanHelpRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("EndClanHelpRequest exception", e);
		}
	}

	public EndClanHelpRequestProto getEndClanHelpRequestProto() {
		return endClanHelpRequestProto;
	}

	@Override
	public String toString() {
		return "EndClanHelpRequestEvent [endClanHelpRequestProto="
				+ endClanHelpRequestProto + "]";
	}

}
