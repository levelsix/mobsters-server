package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventClanProto.FulfillTeamDonationSolicitationRequestProto;

public class FulfillTeamDonationSolicitationRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(FulfillTeamDonationSolicitationRequestEvent.class);

	private FulfillTeamDonationSolicitationRequestProto fulfillTeamDonationSolicitationRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			fulfillTeamDonationSolicitationRequestProto = FulfillTeamDonationSolicitationRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = fulfillTeamDonationSolicitationRequestProto.getSender()
					.getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("FulfillTeamDonationSolicitationRequest exception", e);
		}
	}

	public FulfillTeamDonationSolicitationRequestProto getFulfillTeamDonationSolicitationRequestProto() {
		return fulfillTeamDonationSolicitationRequestProto;
	}

	@Override
	public String toString() {
		return "FulfillTeamDonationSolicitationRequestEvent [fulfillTeamDonationSolicitationRequestProto="
				+ fulfillTeamDonationSolicitationRequestProto + "]";
	}

}
