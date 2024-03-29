package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventTournamentProto.RetrieveTournamentRankingsRequestProto;

public class RetrieveTournamentRankingsRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(RetrieveTournamentRankingsRequestEvent.class);

	private RetrieveTournamentRankingsRequestProto retrieveTournamentRankingsRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			retrieveTournamentRankingsRequestProto = RetrieveTournamentRankingsRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = retrieveTournamentRankingsRequestProto.getSender()
					.getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("RetrieveTournamentRankingsRequest exception", e);
		}
	}

	public RetrieveTournamentRankingsRequestProto getRetrieveTournamentRankingsRequestProto() {
		return retrieveTournamentRankingsRequestProto;
	}

	@Override
	public String toString() {
		return "RetrieveTournamentRankingsRequestEvent [retrieveTournamentRankingsRequestProto="
				+ retrieveTournamentRankingsRequestProto + "]";
	}

}//RetrieveTournamentRankingsRequestProto
