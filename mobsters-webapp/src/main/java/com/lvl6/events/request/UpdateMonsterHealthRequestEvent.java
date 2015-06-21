package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventMonsterProto.UpdateMonsterHealthRequestProto;

public class UpdateMonsterHealthRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(UpdateMonsterHealthRequestEvent.class);

	private UpdateMonsterHealthRequestProto updateMonsterHealthRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			updateMonsterHealthRequestProto = UpdateMonsterHealthRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = updateMonsterHealthRequestProto.getSender()
					.getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("UpdateMonsterHealthRequest exception", e);
		}
	}

	public UpdateMonsterHealthRequestProto getUpdateMonsterHealthRequestProto() {
		return updateMonsterHealthRequestProto;
	}

	@Override
	public String toString() {
		return "UpdateMonsterHealthRequestEvent [updateMonsterHealthRequestProto="
				+ updateMonsterHealthRequestProto + "]";
	}

}
