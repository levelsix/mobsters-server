package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventClanProto.ChangeClanSettingsRequestProto;

public class ChangeClanSettingsRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(ChangeClanSettingsRequestEvent.class);

	private ChangeClanSettingsRequestProto changeClanSettingsRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			changeClanSettingsRequestProto = ChangeClanSettingsRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = changeClanSettingsRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("ChangeClanSettingsRequest exception");
		}
	}

	public ChangeClanSettingsRequestProto getChangeClanSettingsRequestProto() {
		return changeClanSettingsRequestProto;
	}

	public void setChangeClanSettingsRequestProto(ChangeClanSettingsRequestProto ccsrp) {
		this.changeClanSettingsRequestProto = ccsrp;
	}
	
	@Override
	public String toString() {
		return "ChangeClanSettingsRequestEvent [changeClanSettingsRequestProto="
				+ changeClanSettingsRequestProto + "]";
	}
}
