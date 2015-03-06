package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventMonsterProto.SubmitMonsterEnhancementRequestProto;

public class SubmitMonsterEnhancementRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private SubmitMonsterEnhancementRequestProto submitMonsterEnhancementRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	public void read(ByteBuffer buff) {
		try {
			submitMonsterEnhancementRequestProto = SubmitMonsterEnhancementRequestProto.parseFrom(ByteString.copyFrom(buff));
			playerId = submitMonsterEnhancementRequestProto.getSender().getMinUserProto().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("SubmitMonsterEnhancementRequest exception", e);
		}
	}

	public SubmitMonsterEnhancementRequestProto getSubmitMonsterEnhancementRequestProto() {
		return submitMonsterEnhancementRequestProto;
	}

	public void setSubmitMonsterEnhancementRequestProto(SubmitMonsterEnhancementRequestProto smerp) {
		this.submitMonsterEnhancementRequestProto = smerp;
	}

	@Override
	public String toString()
	{
		return "SubmitMonsterEnhancementRequestEvent [submitMonsterEnhancementRequestProto="
				+ submitMonsterEnhancementRequestProto
				+ "]";
	}

}
