package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventClanProto.BeginClanRaidRequestProto;

public class BeginClanRaidRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(BeginClanRaidRequestEvent.class);

	private BeginClanRaidRequestProto beginClanRaidRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			beginClanRaidRequestProto = BeginClanRaidRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = beginClanRaidRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("BeginClanRaidRequest exception", e);
		}
	}

	public BeginClanRaidRequestProto getBeginClanRaidRequestProto() {
		return beginClanRaidRequestProto;
	}

	@Override
	public String toString() {
		return "BeginClanRaidRequestEvent [beginClanRaidRequestProto="
				+ beginClanRaidRequestProto + "]";
	}

}
