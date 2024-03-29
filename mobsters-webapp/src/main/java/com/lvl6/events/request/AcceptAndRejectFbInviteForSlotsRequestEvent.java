package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventMonsterProto.AcceptAndRejectFbInviteForSlotsRequestProto;

public class AcceptAndRejectFbInviteForSlotsRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(AcceptAndRejectFbInviteForSlotsRequestEvent.class);

	private AcceptAndRejectFbInviteForSlotsRequestProto acceptAndRejectFbInviteForSlotsRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			acceptAndRejectFbInviteForSlotsRequestProto = AcceptAndRejectFbInviteForSlotsRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = acceptAndRejectFbInviteForSlotsRequestProto.getSender()
					.getMinUserProto().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("AcceptAndRejectFbInviteForSlotsRequest exception", e);
		}
	}

	public AcceptAndRejectFbInviteForSlotsRequestProto getAcceptAndRejectFbInviteForSlotsRequestProto() {
		return acceptAndRejectFbInviteForSlotsRequestProto;
	}

	@Override
	public String toString() {
		return "AcceptAndRejectFbInviteForSlotsRequestEvent [acceptAndRejectFbInviteForSlotsRequestProto="
				+ acceptAndRejectFbInviteForSlotsRequestProto + "]";
	}

}
