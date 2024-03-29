package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventMiniJobProto.BeginMiniJobRequestProto;

public class BeginMiniJobRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(BeginMiniJobRequestEvent.class);

	private BeginMiniJobRequestProto beginMiniJobRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			beginMiniJobRequestProto = BeginMiniJobRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = beginMiniJobRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("BeginMiniJobRequest exception", e);
		}
	}

	public BeginMiniJobRequestProto getBeginMiniJobRequestProto() {
		return beginMiniJobRequestProto;
	}

	//added for testing purposes
	public void setBeginMiniJobRequestProto(BeginMiniJobRequestProto sorp) {
		this.beginMiniJobRequestProto = sorp;
	}

	@Override
	public String toString() {
		return "BeginMiniJobRequestEvent [beginMiniJobRequestProto="
				+ beginMiniJobRequestProto + "]";
	}

}
