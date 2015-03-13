package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventClanProto.PromoteDemoteClanMemberRequestProto;

public class PromoteDemoteClanMemberRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private PromoteDemoteClanMemberRequestProto promoteDemoteClanMemberRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			promoteDemoteClanMemberRequestProto = PromoteDemoteClanMemberRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = promoteDemoteClanMemberRequestProto.getSender()
					.getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("PromoteDemoteClanMemberRequest exception", e);
		}
	}

	public PromoteDemoteClanMemberRequestProto getPromoteDemoteClanMemberRequestProto() {
		return promoteDemoteClanMemberRequestProto;
	}

	@Override
	public String toString() {
		return "PromoteDemoteClanMemberRequestEvent [promoteDemoteClanMemberRequestProto="
				+ promoteDemoteClanMemberRequestProto + "]";
	}

}
