package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventInAppPurchaseProto.ExchangeGemsForResourcesRequestProto;

public class ExchangeGemsForResourcesRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(ExchangeGemsForResourcesRequestEvent.class);

	private ExchangeGemsForResourcesRequestProto exchangeGemsForResourcesRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			exchangeGemsForResourcesRequestProto = ExchangeGemsForResourcesRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = exchangeGemsForResourcesRequestProto.getSender()
					.getMinUserProto().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("ExchangeGemsForResourcesRequest exception", e);
		}
	}

	public ExchangeGemsForResourcesRequestProto getExchangeGemsForResourcesRequestProto() {
		return exchangeGemsForResourcesRequestProto;
	}

	@Override
	public String toString() {
		return "ExchangeGemsForResourcesRequestEvent [exchangeGemsForResourcesRequestProto="
				+ exchangeGemsForResourcesRequestProto + "]";
	}

}
