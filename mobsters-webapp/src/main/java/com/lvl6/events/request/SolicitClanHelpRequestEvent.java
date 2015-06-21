package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventClanProto.SolicitClanHelpRequestProto;

public class SolicitClanHelpRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(SolicitClanHelpRequestEvent.class);

	private SolicitClanHelpRequestProto solicitClanHelpRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			solicitClanHelpRequestProto = SolicitClanHelpRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = solicitClanHelpRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("SolicitClanHelpRequest exception", e);
		}
	}

	public SolicitClanHelpRequestProto getSolicitClanHelpRequestProto() {
		return solicitClanHelpRequestProto;
	}

	@Override
	public String toString() {
		return "SolicitClanHelpRequestEvent [solicitClanHelpRequestProto="
				+ solicitClanHelpRequestProto + "]";
	}

}
