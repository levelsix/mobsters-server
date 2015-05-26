package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventItemProto.TradeItemForSpeedUpsRequestProto;

public class TradeItemForSpeedUpsRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private TradeItemForSpeedUpsRequestProto tradeItemForSpeedUpsRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			tradeItemForSpeedUpsRequestProto = TradeItemForSpeedUpsRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = tradeItemForSpeedUpsRequestProto.getSender()
					.getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("TradeItemForSpeedUpsRequest exception", e);
		}
	}

	public TradeItemForSpeedUpsRequestProto getTradeItemForSpeedUpsRequestProto() {
		return tradeItemForSpeedUpsRequestProto;
	}

	public void setTradeItemForSpeedUpsRequestProto(
			TradeItemForSpeedUpsRequestProto tradeItemForSpeedUpsRequestProto) {
		this.tradeItemForSpeedUpsRequestProto = tradeItemForSpeedUpsRequestProto;
	}

	@Override
	public String toString() {
		return "TradeItemForSpeedUpsRequestEvent [tradeItemForSpeedUpsRequestProto="
				+ tradeItemForSpeedUpsRequestProto + "]";
	}

}
