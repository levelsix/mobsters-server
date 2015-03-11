package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventMonsterProto.CollectMonsterEnhancementRequestProto;

public class CollectMonsterEnhancementRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private CollectMonsterEnhancementRequestProto collectMonsterEnhancementRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	public void read(ByteBuffer buff) {
		try {
			collectMonsterEnhancementRequestProto = CollectMonsterEnhancementRequestProto.parseFrom(ByteString.copyFrom(buff));
			playerId = collectMonsterEnhancementRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("CollectMonsterEnhancementRequest exception", e);
		}
	}

	public CollectMonsterEnhancementRequestProto getCollectMonsterEnhancementRequestProto() {
		return collectMonsterEnhancementRequestProto;
	}

	public void setCollectMonsterEnhancementRequestProto(CollectMonsterEnhancementRequestProto cmerp) {
		this.collectMonsterEnhancementRequestProto = cmerp;
	}

	@Override
	public String toString()
	{
		return "CollectMonsterEnhancementRequestEvent [collectMonsterEnhancementRequestProto="
				+ collectMonsterEnhancementRequestProto
				+ "]";
	}
}
