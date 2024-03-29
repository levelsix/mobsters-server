package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventClanProto.LeaveClanRequestProto;

public class LeaveClanRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(LeaveClanRequestEvent.class);

	private LeaveClanRequestProto leaveClanRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			leaveClanRequestProto = LeaveClanRequestProto.parseFrom(ByteString
					.copyFrom(buff));
			playerId = leaveClanRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("LeaveClanRequest exception", e);
		}
	}

	public LeaveClanRequestProto getLeaveClanRequestProto() {
		return leaveClanRequestProto;
	}
	
	public void setLeaveClanRequestProto(LeaveClanRequestProto lcrp) {
		this.leaveClanRequestProto = lcrp;
	}

	@Override
	public String toString() {
		return "LeaveClanRequestEvent [leaveClanRequestProto="
				+ leaveClanRequestProto + "]";
	}

}
