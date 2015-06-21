package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventMiniJobProto.RefreshMiniJobRequestProto;

public class RefreshMiniJobRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(RefreshMiniJobRequestEvent.class);

	private RefreshMiniJobRequestProto refreshMiniJobRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			refreshMiniJobRequestProto = RefreshMiniJobRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = refreshMiniJobRequestProto
					.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("RefreshMiniJobRequest exception", e);
		}
	}

	public RefreshMiniJobRequestProto getRefreshMiniJobRequestProto() {
		return refreshMiniJobRequestProto;
	}

	//added for testing purposes
	public void setRefreshMiniJobRequestProto(RefreshMiniJobRequestProto sorp) {
		this.refreshMiniJobRequestProto = sorp;
	}

	@Override
	public String toString() {
		return "RefreshMiniJobRequestEvent [refreshMiniJobRequestProto="
				+ refreshMiniJobRequestProto + "]";
	}

}
