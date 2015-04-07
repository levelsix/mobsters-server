package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventStructureProto.UpgradeNormStructureRequestProto;
import com.lvl6.proto.EventUserProto.UpdateUserStrengthRequestProto;

public class UpdateUserStrengthRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private UpdateUserStrengthRequestProto updateUserStrengthRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			updateUserStrengthRequestProto = UpdateUserStrengthRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = updateUserStrengthRequestProto.getSender()
					.getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("UpgradeNormStructureRequest exception", e);
		}
	}

	public UpdateUserStrengthRequestProto getUpdateUserStrengthRequestProto() {
		return updateUserStrengthRequestProto;
	}

	@Override
	public String toString() {
		return "updateUserStrengthRequestEvent [updateUserStrengthRequestProto="
				+ updateUserStrengthRequestProto + "]";
	}

}
