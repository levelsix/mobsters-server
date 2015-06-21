package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventDungeonProto.ReviveInDungeonRequestProto;

public class ReviveInDungeonRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(ReviveInDungeonRequestEvent.class);

	private ReviveInDungeonRequestProto reviveInDungeonRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			reviveInDungeonRequestProto = ReviveInDungeonRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = reviveInDungeonRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("ReviveInDungeonRequest exception", e);
		}
	}

	public ReviveInDungeonRequestProto getReviveInDungeonRequestProto() {
		return reviveInDungeonRequestProto;
	}

	@Override
	public String toString() {
		return "ReviveInDungeonRequestEvent [reviveInDungeonRequestProto="
				+ reviveInDungeonRequestProto + "]";
	}

}
