package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventCityProto.LoadCityRequestProto;

public class LoadCityRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(LoadCityRequestEvent.class);

	private LoadCityRequestProto loadCityRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			loadCityRequestProto = LoadCityRequestProto.parseFrom(ByteString
					.copyFrom(buff));
			playerId = loadCityRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("LoadCityRequest exception", e);
		}
	}

	public LoadCityRequestProto getLoadCityRequestProto() {
		return loadCityRequestProto;
	}

	@Override
	public String toString() {
		return "LoadCityRequestEvent [loadCityRequestProto="
				+ loadCityRequestProto + "]";
	}

}
