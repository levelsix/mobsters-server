package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventPvpProto.BeginPvpBattleRequestProto;

public class BeginPvpBattleRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private BeginPvpBattleRequestProto beginPvpBattleRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			beginPvpBattleRequestProto = BeginPvpBattleRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = beginPvpBattleRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("BeginPvpBattleRequest exception", e);
		}
	}

	public BeginPvpBattleRequestProto getBeginPvpBattleRequestProto() {
		return beginPvpBattleRequestProto;
	}

	@Override
	public String toString() {
		return "BeginPvpBattleRequestEvent [beginPvpBattleRequestProto="
				+ beginPvpBattleRequestProto + "]";
	}

}
