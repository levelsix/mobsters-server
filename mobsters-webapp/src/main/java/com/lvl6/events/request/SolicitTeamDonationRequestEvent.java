package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventClanProto.SolicitTeamDonationRequestProto;

public class SolicitTeamDonationRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(SolicitTeamDonationRequestEvent.class);

	private SolicitTeamDonationRequestProto solicitTeamDonationRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			solicitTeamDonationRequestProto = SolicitTeamDonationRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = solicitTeamDonationRequestProto.getSender()
					.getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("SolicitTeamDonationRequest exception", e);
		}
	}

	public SolicitTeamDonationRequestProto getSolicitTeamDonationRequestProto() {
		return solicitTeamDonationRequestProto;
	}

	@Override
	public String toString() {
		return "SolicitTeamDonationRequestEvent [solicitTeamDonationRequestProto="
				+ solicitTeamDonationRequestProto + "]";
	}

}
