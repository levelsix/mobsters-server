package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventPvpProto.EndPvpBattleRequestProto;

public class EndPvpBattleRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(EndPvpBattleRequestEvent.class);

	private EndPvpBattleRequestProto endPvpBattleRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			endPvpBattleRequestProto = EndPvpBattleRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = endPvpBattleRequestProto.getSender().getMinUserProto()
					.getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("EndPvpBattleRequest exception", e);
		}
	}

	public EndPvpBattleRequestProto getEndPvpBattleRequestProto() {
		return endPvpBattleRequestProto;
	}

	@Override
	public String toString() {
		return "EndPvpBattleRequestEvent [endPvpBattleRequestProto="
				+ endPvpBattleRequestProto + "]";
	}

}
