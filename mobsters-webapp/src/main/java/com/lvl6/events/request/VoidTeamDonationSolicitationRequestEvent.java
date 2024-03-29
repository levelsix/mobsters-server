package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventClanProto.VoidTeamDonationSolicitationRequestProto;

public class VoidTeamDonationSolicitationRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(VoidTeamDonationSolicitationRequestEvent.class);

	private VoidTeamDonationSolicitationRequestProto voidTeamDonationSolicitationRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			voidTeamDonationSolicitationRequestProto = VoidTeamDonationSolicitationRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = voidTeamDonationSolicitationRequestProto.getSender()
					.getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("VoidTeamDonationSolicitationRequest exception", e);
		}
	}

	public VoidTeamDonationSolicitationRequestProto getVoidTeamDonationSolicitationRequestProto() {
		return voidTeamDonationSolicitationRequestProto;
	}

	@Override
	public String toString() {
		return "VoidTeamDonationSolicitationRequestEvent [voidTeamDonationSolicitationRequestProto="
				+ voidTeamDonationSolicitationRequestProto + "]";
	}

}
