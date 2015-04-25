package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventClanProto.CollectClanGiftsRequestProto;

public class CollectClanGiftsRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private CollectClanGiftsRequestProto collectClanGiftsRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			collectClanGiftsRequestProto = CollectClanGiftsRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = collectClanGiftsRequestProto.getSender()
					.getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("UpgradeNormStructureRequest exception", e);
		}
	}

	public CollectClanGiftsRequestProto getCollectClanGiftsRequestProto() {
		return collectClanGiftsRequestProto;
	}

	@Override
	public String toString() {
		return "collectClanGiftsRequestEvent [collectClanGiftsRequestProto="
				+ collectClanGiftsRequestProto + "]";
	}

}
