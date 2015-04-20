package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventClanProto.ClearExpiredClanGiftsRequestProto;

public class ClearExpiredClanGiftsRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private ClearExpiredClanGiftsRequestProto clearExpiredClanGiftsRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			clearExpiredClanGiftsRequestProto = ClearExpiredClanGiftsRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = clearExpiredClanGiftsRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("ClearExpiredClanGiftsRequest exception");
		}
	}

	public ClearExpiredClanGiftsRequestProto getClearExpiredClanGiftsRequestProto() {
		return clearExpiredClanGiftsRequestProto;
	}

	public void setClearExpiredClanGiftsRequestProto(ClearExpiredClanGiftsRequestProto ccsrp) {
		this.clearExpiredClanGiftsRequestProto = ccsrp;
	}

	@Override
	public String toString() {
		return "ClearExpiredClanGiftsRequestEvent [clearExpiredClanGiftsRequestProto="
				+ clearExpiredClanGiftsRequestProto + "]";
	}
}
