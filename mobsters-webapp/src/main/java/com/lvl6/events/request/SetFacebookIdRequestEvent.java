package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventUserProto.SetFacebookIdRequestProto;

public class SetFacebookIdRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(SetFacebookIdRequestEvent.class);

	private SetFacebookIdRequestProto setFacebookIdRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			setFacebookIdRequestProto = SetFacebookIdRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = setFacebookIdRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("SetFacebookIdRequest", e);
		}
	}

	public SetFacebookIdRequestProto getSetFacebookIdRequestProto() {
		return setFacebookIdRequestProto;
	}

	@Override
	public String toString() {
		return "SetFacebookIdRequestEvent [setFacebookIdRequestProto="
				+ setFacebookIdRequestProto + "]";
	}

}
