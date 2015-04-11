package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventMonsterProto.RemoveMonsterFromBattleTeamRequestProto;

public class RemoveMonsterFromBattleTeamRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private RemoveMonsterFromBattleTeamRequestProto removeMonsterFromBattleTeamRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			removeMonsterFromBattleTeamRequestProto = RemoveMonsterFromBattleTeamRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = removeMonsterFromBattleTeamRequestProto.getSender()
					.getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("RemoveMonsterFromBattleTeamRequest exception", e);
		}
	}

	public RemoveMonsterFromBattleTeamRequestProto getRemoveMonsterFromBattleTeamRequestProto() {
		return removeMonsterFromBattleTeamRequestProto;
	}

	@Override
	public String toString() {
		return "RemoveMonsterFromBattleTeamRequestEvent [removeMonsterFromBattleTeamRequestProto="
				+ removeMonsterFromBattleTeamRequestProto + "]";
	}

}
