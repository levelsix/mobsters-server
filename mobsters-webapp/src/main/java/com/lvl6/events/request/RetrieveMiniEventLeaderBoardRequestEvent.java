package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventLeaderBoardProto.RetrieveMiniEventLeaderBoardRequestProto;

public class RetrieveMiniEventLeaderBoardRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(RetrieveMiniEventLeaderBoardRequestEvent.class);

	private RetrieveMiniEventLeaderBoardRequestProto retrieveMiniEventLeaderBoardRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			retrieveMiniEventLeaderBoardRequestProto = RetrieveMiniEventLeaderBoardRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = retrieveMiniEventLeaderBoardRequestProto.getSender()
					.getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("RetrieveMiniEventLeaderBoardRequest exception", e);
		}
	}

	public RetrieveMiniEventLeaderBoardRequestProto getRetrieveMiniEventLeaderBoardRequestProto() {
		return retrieveMiniEventLeaderBoardRequestProto;
	}

	@Override
	public String toString() {
		return "RetrieveMiniEventLeaderBoardRequestEvent [retrieveMiniEventLeaderBoardRequestProto="
				+ retrieveMiniEventLeaderBoardRequestProto + "]";
	}

}
