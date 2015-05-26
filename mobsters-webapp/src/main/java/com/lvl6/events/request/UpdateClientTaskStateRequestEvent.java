package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventUserProto.UpdateClientTaskStateRequestProto;

public class UpdateClientTaskStateRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private UpdateClientTaskStateRequestProto updateClientTaskStateRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			updateClientTaskStateRequestProto = UpdateClientTaskStateRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = updateClientTaskStateRequestProto.getSender()
					.getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("UpdateClientTaskStateRequest exception", e);
		}
	}

	public UpdateClientTaskStateRequestProto getUpdateClientTaskStateRequestProto() {
		return updateClientTaskStateRequestProto;
	}

	@Override
	public String toString() {
		return "UpdateClientTaskStateRequestEvent [updateClientTaskStateRequestProto="
				+ updateClientTaskStateRequestProto + "]";
	}

}
