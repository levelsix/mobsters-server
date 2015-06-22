package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventUserProto.LevelUpRequestProto;

public class LevelUpRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(LevelUpRequestEvent.class);

	private LevelUpRequestProto levelUpRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			levelUpRequestProto = LevelUpRequestProto.parseFrom(ByteString
					.copyFrom(buff));
			playerId = levelUpRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("LevelUpRequest exception", e);
		}
	}

	public LevelUpRequestProto getLevelUpRequestProto() {
		return levelUpRequestProto;
	}

	@Override
	public String toString() {
		return "LevelUpRequestEvent [levelUpRequestProto="
				+ levelUpRequestProto + "]";
	}

}
