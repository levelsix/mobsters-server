package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventItemProto.TradeItemForResourcesRequestProto;

public class TradeItemForResourcesRequestEvent extends RequestEvent {

	private Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private TradeItemForResourcesRequestProto tradeItemForResourcesRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			tradeItemForResourcesRequestProto = TradeItemForResourcesRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = tradeItemForResourcesRequestProto.getSender()
					.getMinUserProto().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("TradeItemForResources request exception", e);
		}
	}

	public TradeItemForResourcesRequestProto getTradeItemForResourcesRequestProto() {
		return tradeItemForResourcesRequestProto;
	}

	public void setTradeItemForResourcesRequestProto(
			TradeItemForResourcesRequestProto tradeItemForResourcesRequestProto) {
		this.tradeItemForResourcesRequestProto = tradeItemForResourcesRequestProto;
	}

}
