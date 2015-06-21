package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventMiniEventProto.UpdateMiniEventRequestProto;

public class UpdateMiniEventRequestEvent extends RequestEvent {

	private Logger log = LoggerFactory.getLogger(UpdateMiniEventRequestEvent.class);

	private UpdateMiniEventRequestProto updateMiniEventRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			updateMiniEventRequestProto = UpdateMiniEventRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = updateMiniEventRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("UpdateMiniEvent request exception", e);
		}
	}

	public UpdateMiniEventRequestProto getUpdateMiniEventRequestProto() {
		return updateMiniEventRequestProto;
	}

	public void setUpdateMiniEventRequestProto(
			UpdateMiniEventRequestProto updateMiniEventRequestProto) {
		this.updateMiniEventRequestProto = updateMiniEventRequestProto;
	}

}
