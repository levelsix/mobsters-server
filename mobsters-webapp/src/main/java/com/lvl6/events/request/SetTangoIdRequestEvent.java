package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventUserProto.SetTangoIdRequestProto;

public class SetTangoIdRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private SetTangoIdRequestProto setTangoIdRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			setTangoIdRequestProto = SetTangoIdRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = setTangoIdRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("SetTangoIdRequest exception", e);
		}
	}

	public SetTangoIdRequestProto getSetTangoIdRequestProto() {
		return setTangoIdRequestProto;
	}

	@Override
	public String toString() {
		return "SetTangoIdRequestEvent [setTangoIdRequestProto="
				+ setTangoIdRequestProto + "]";
	}

}
