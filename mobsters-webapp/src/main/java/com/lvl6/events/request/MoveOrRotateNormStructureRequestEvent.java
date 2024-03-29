package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventStructureProto.MoveOrRotateNormStructureRequestProto;

public class MoveOrRotateNormStructureRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(MoveOrRotateNormStructureRequestEvent.class);

	private MoveOrRotateNormStructureRequestProto moveOrRotateNormStructureRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			moveOrRotateNormStructureRequestProto = MoveOrRotateNormStructureRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = moveOrRotateNormStructureRequestProto.getSender()
					.getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("MoveOrRotateNormStructureRequest exception", e);
		}
	}

	public MoveOrRotateNormStructureRequestProto getMoveOrRotateNormStructureRequestProto() {
		return moveOrRotateNormStructureRequestProto;
	}

	@Override
	public String toString() {
		return "MoveOrRotateNormStructureRequestEvent [moveOrRotateNormStructureRequestProto="
				+ moveOrRotateNormStructureRequestProto + "]";
	}

}
