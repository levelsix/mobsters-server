package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventClanProto.InviteToClanRequestProto;

public class InviteToClanRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(InviteToClanRequestEvent.class);

	private InviteToClanRequestProto inviteToClanRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			inviteToClanRequestProto = InviteToClanRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = inviteToClanRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("InviteToClanRequest exception", e);
		}
	}

	public InviteToClanRequestProto getInviteToClanRequestProto() {
		return inviteToClanRequestProto;
	}

	public void setInviteToClanRequestProto(
			InviteToClanRequestProto inviteToClanRequestProto) {
		this.inviteToClanRequestProto = inviteToClanRequestProto;
	}

	@Override
	public String toString() {
		return "InviteToClanRequestEvent [inviteToClanRequestProto="
				+ inviteToClanRequestProto + "]";
	}

}
