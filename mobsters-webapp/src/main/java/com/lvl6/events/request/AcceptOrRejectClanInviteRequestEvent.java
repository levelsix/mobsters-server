package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventClanProto.AcceptOrRejectClanInviteRequestProto;

public class AcceptOrRejectClanInviteRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(AcceptOrRejectClanInviteRequestEvent.class);

	private AcceptOrRejectClanInviteRequestProto acceptOrRejectClanInviteRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			acceptOrRejectClanInviteRequestProto = AcceptOrRejectClanInviteRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = acceptOrRejectClanInviteRequestProto.getSender()
					.getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("AcceptOrRejectClanInviteRequest exception", e);
		}
	}

	public AcceptOrRejectClanInviteRequestProto getAcceptOrRejectClanInviteRequestProto() {
		return acceptOrRejectClanInviteRequestProto;
	}

	@Override
	public String toString() {
		return "AcceptOrRejectClanInviteRequestEvent [acceptOrRejectClanInviteRequestProto="
				+ acceptOrRejectClanInviteRequestProto + "]";
	}

}
