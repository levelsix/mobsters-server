package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventUserProto.SetGameCenterIdRequestProto;

public class SetGameCenterIdRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private SetGameCenterIdRequestProto setGameCenterIdRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			setGameCenterIdRequestProto = SetGameCenterIdRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = setGameCenterIdRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("SetGameCenterIdRequest exception", e);
		}
	}

	public SetGameCenterIdRequestProto getSetGameCenterIdRequestProto() {
		return setGameCenterIdRequestProto;
	}

	@Override
	public String toString() {
		return "SetGameCenterIdRequestEvent [setGameCenterIdRequestProto="
				+ setGameCenterIdRequestProto + "]";
	}

}
