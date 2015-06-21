package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventPvpProto.RetrieveBattleReplayRequestProto;

public class RetrieveBattleReplayRequestEvent extends RequestEvent {

	private Logger log = LoggerFactory.getLogger(RetrieveBattleReplayRequestEvent.class);

	private RetrieveBattleReplayRequestProto retrieveBattleReplayRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			retrieveBattleReplayRequestProto = RetrieveBattleReplayRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = retrieveBattleReplayRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("RetrieveBattleReplay request exception", e);
		}
	}

	public RetrieveBattleReplayRequestProto getRetrieveBattleReplayRequestProto() {
		return retrieveBattleReplayRequestProto;
	}

	public void setRetrieveBattleReplayRequestProto(
			RetrieveBattleReplayRequestProto retrieveBattleReplayRequestProto) {
		this.retrieveBattleReplayRequestProto = retrieveBattleReplayRequestProto;
	}

}
