package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventDungeonProto.EndDungeonRequestProto;

public class EndDungeonRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(EndDungeonRequestEvent.class);

	private EndDungeonRequestProto endDungeonRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			endDungeonRequestProto = EndDungeonRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = endDungeonRequestProto.getSender().getMinUserProto()
					.getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("EndDungeonRequest exception", e);
		}
	}

	public EndDungeonRequestProto getEndDungeonRequestProto() {
		return endDungeonRequestProto;
	}

	@Override
	public String toString() {
		return "EndDungeonRequestEvent [endDungeonRequestProto="
				+ endDungeonRequestProto + "]";
	}

}
