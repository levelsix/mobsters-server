package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventResearchProto.PerformResearchRequestProto;

public class PerformResearchRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(PerformResearchRequestEvent.class);

	private PerformResearchRequestProto performResearchRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			performResearchRequestProto = PerformResearchRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = performResearchRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("PerformResearchRequest exception", e);
		}
	}

	public PerformResearchRequestProto getPerformResearchRequestProto() {
		return performResearchRequestProto;
	}

	public void setPerformResearchRequestProto(
			PerformResearchRequestProto performResearchRequestProto) {
		this.performResearchRequestProto = performResearchRequestProto;
	}

	@Override
	public String toString() {
		return "PerformResearchRequestEvent [performResearchRequestProto="
				+ performResearchRequestProto + "]";
	}

}
