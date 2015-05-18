package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventLeaderBoardProto.RetrieveStrengthLeaderBoardRequestProto;

public class RetrieveStrengthLeaderBoardRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private RetrieveStrengthLeaderBoardRequestProto retrieveStrengthLeaderBoardRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			retrieveStrengthLeaderBoardRequestProto = RetrieveStrengthLeaderBoardRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = retrieveStrengthLeaderBoardRequestProto.getSender()
					.getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("RetrieveStrengthLeaderBoardRequest exception", e);
		}
	}

	public RetrieveStrengthLeaderBoardRequestProto getRetrieveStrengthLeaderBoardRequestProto() {
		return retrieveStrengthLeaderBoardRequestProto;
	}

	@Override
	public String toString() {
		return "RetrieveStrengthLeaderBoardRequestEvent [retrieveStrengthLeaderBoardRequestProto="
				+ retrieveStrengthLeaderBoardRequestProto + "]";
	}

}
