package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventRewardProto.CollectGiftRequestProto;

public class CollectGiftRequestEvent extends RequestEvent {

	private Logger log = LoggerFactory.getLogger(CollectGiftRequestEvent.class);

	private CollectGiftRequestProto collectGiftRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			collectGiftRequestProto = CollectGiftRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = collectGiftRequestProto.getSender().getMinUserProto().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("CollectGift request exception", e);
		}
	}

	public CollectGiftRequestProto getCollectGiftRequestProto() {
		return collectGiftRequestProto;
	}

	public void setCollectGiftRequestProto(
			CollectGiftRequestProto collectGiftRequestProto) {
		this.collectGiftRequestProto = collectGiftRequestProto;
	}

}
