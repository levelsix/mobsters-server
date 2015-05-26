package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventResearchProto.FinishPerformingResearchRequestProto;

public class FinishPerformingResearchRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private FinishPerformingResearchRequestProto finishPerformingResearchRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			finishPerformingResearchRequestProto = FinishPerformingResearchRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = finishPerformingResearchRequestProto.getSender()
					.getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("FinishPerformingResearchRequest exception", e);
		}
	}

	public FinishPerformingResearchRequestProto getFinishPerformingResearchRequestProto() {
		return finishPerformingResearchRequestProto;
	}

	public void setFinishPerformingResearchRequestProto(
			FinishPerformingResearchRequestProto finishPerformingResearchRequestProto) {
		this.finishPerformingResearchRequestProto = finishPerformingResearchRequestProto;
	}

	@Override
	public String toString() {
		return "FinishPerformingResearchRequestEvent [finishPerformingResearchRequestProto="
				+ finishPerformingResearchRequestProto + "]";
	}

}
