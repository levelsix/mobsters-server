package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventItemProto.TradeItemForBoosterRequestProto;

public class TradeItemForBoosterRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(TradeItemForBoosterRequestEvent.class);

	private TradeItemForBoosterRequestProto tradeItemForBoosterRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			tradeItemForBoosterRequestProto = TradeItemForBoosterRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = tradeItemForBoosterRequestProto.getSender()
					.getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("TradeItemForBoosterRequest exception", e);
		}
	}

	public TradeItemForBoosterRequestProto getTradeItemForBoosterRequestProto() {
		return tradeItemForBoosterRequestProto;
	}

	public void setTradeItemForBoosterRequestProto(
			TradeItemForBoosterRequestProto tradeItemForBoosterRequestProto) {
		this.tradeItemForBoosterRequestProto = tradeItemForBoosterRequestProto;
	}

	@Override
	public String toString() {
		return "TradeItemForBoosterRequestEvent [tradeItemForBoosterRequestProto="
				+ tradeItemForBoosterRequestProto + "]";
	}

}
