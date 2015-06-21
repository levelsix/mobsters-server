package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventMonsterProto.IncreaseMonsterInventorySlotRequestProto;

public class IncreaseMonsterInventorySlotRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(IncreaseMonsterInventorySlotRequestEvent.class);

	private IncreaseMonsterInventorySlotRequestProto increaseMonsterInventorySlotRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			increaseMonsterInventorySlotRequestProto = IncreaseMonsterInventorySlotRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = increaseMonsterInventorySlotRequestProto.getSender()
					.getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("IncreaseMonsterInventorySlotRequest exception", e);
		}
	}

	public IncreaseMonsterInventorySlotRequestProto getIncreaseMonsterInventorySlotRequestProto() {
		return increaseMonsterInventorySlotRequestProto;
	}

	@Override
	public String toString() {
		return "IncreaseMonsterInventorySlotRequestEvent [increaseMonsterInventorySlotRequestProto="
				+ increaseMonsterInventorySlotRequestProto + "]";
	}

}
