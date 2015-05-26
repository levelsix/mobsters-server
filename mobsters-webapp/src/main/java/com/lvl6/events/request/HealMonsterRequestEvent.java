package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventMonsterProto.HealMonsterRequestProto;

public class HealMonsterRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private HealMonsterRequestProto healMonsterRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			healMonsterRequestProto = HealMonsterRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = healMonsterRequestProto.getSender().getMinUserProto()
					.getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("HealMonsterRequest exception", e);
		}
	}

	public HealMonsterRequestProto getHealMonsterRequestProto() {
		return healMonsterRequestProto;
	}

	@Override
	public String toString() {
		return "HealMonsterRequestEvent [healMonsterRequestProto="
				+ healMonsterRequestProto + "]";
	}

}
