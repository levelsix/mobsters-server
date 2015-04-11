package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventClanProto.AttackClanRaidMonsterRequestProto;

public class AttackClanRaidMonsterRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private AttackClanRaidMonsterRequestProto attackClanRaidMonsterRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			attackClanRaidMonsterRequestProto = AttackClanRaidMonsterRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = attackClanRaidMonsterRequestProto.getSender()
					.getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("AttackClanRaidMonsterRequest exception", e);
		}
	}

	public AttackClanRaidMonsterRequestProto getAttackClanRaidMonsterRequestProto() {
		return attackClanRaidMonsterRequestProto;
	}

	@Override
	public String toString() {
		return "AttackClanRaidMonsterRequestEvent [attackClanRaidMonsterRequestProto="
				+ attackClanRaidMonsterRequestProto + "]";
	}

}
