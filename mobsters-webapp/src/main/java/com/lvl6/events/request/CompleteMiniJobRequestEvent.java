package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventMiniJobProto.CompleteMiniJobRequestProto;

public class CompleteMiniJobRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(CompleteMiniJobRequestEvent.class);

	private CompleteMiniJobRequestProto completeMiniJobRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			completeMiniJobRequestProto = CompleteMiniJobRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = completeMiniJobRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("CompleteMiniJobRequest exception", e);
		}
	}

	public CompleteMiniJobRequestProto getCompleteMiniJobRequestProto() {
		return completeMiniJobRequestProto;
	}

	//added for testing purposes
	public void setCompleteMiniJobRequestProto(CompleteMiniJobRequestProto sorp) {
		this.completeMiniJobRequestProto = sorp;
	}

	@Override
	public String toString() {
		return "CompleteMiniJobRequestEvent [completeMiniJobRequestProto="
				+ completeMiniJobRequestProto + "]";
	}

}
