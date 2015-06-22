package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventClanProto.AvengeClanMateRequestProto;

public class AvengeClanMateRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(AvengeClanMateRequestEvent.class);

	private AvengeClanMateRequestProto avengeClanMateRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			avengeClanMateRequestProto = AvengeClanMateRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = avengeClanMateRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("AvengeClanMateRequest exception", e);
		}
	}

	public AvengeClanMateRequestProto getAvengeClanMateRequestProto() {
		return avengeClanMateRequestProto;
	}

	@Override
	public String toString() {
		return "AvengeClanMateRequestEvent [avengeClanMateRequestProto="
				+ avengeClanMateRequestProto + "]";
	}

}
