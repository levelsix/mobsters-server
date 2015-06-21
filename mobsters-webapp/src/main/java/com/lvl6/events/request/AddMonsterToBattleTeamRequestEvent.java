package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventMonsterProto.AddMonsterToBattleTeamRequestProto;

public class AddMonsterToBattleTeamRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(AddMonsterToBattleTeamRequestEvent.class);

	private AddMonsterToBattleTeamRequestProto addMonsterToBattleTeamRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			addMonsterToBattleTeamRequestProto = AddMonsterToBattleTeamRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = addMonsterToBattleTeamRequestProto.getSender()
					.getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("AddMonsterToBattleTeamRequest exception", e);
		}
	}

	public AddMonsterToBattleTeamRequestProto getAddMonsterToBattleTeamRequestProto() {
		return addMonsterToBattleTeamRequestProto;
	}

	@Override
	public String toString() {
		return "AddMonsterToBattleTeamRequestEvent [addMonsterToBattleTeamRequestProto="
				+ addMonsterToBattleTeamRequestProto + "]";
	}

}
