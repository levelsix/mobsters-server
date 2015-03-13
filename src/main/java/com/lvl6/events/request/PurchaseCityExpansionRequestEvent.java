package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventCityProto.PurchaseCityExpansionRequestProto;

public class PurchaseCityExpansionRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private PurchaseCityExpansionRequestProto purchaseCityExpansionRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			purchaseCityExpansionRequestProto = PurchaseCityExpansionRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = purchaseCityExpansionRequestProto.getSender()
					.getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("PurchaseCityExpansionRequest exception", e);
		}
	}

	public PurchaseCityExpansionRequestProto getPurchaseCityExpansionRequestProto() {
		return purchaseCityExpansionRequestProto;
	}

	public void setPurchaseCityExpansionRequestProto(
			PurchaseCityExpansionRequestProto purchaseCityExpansionRequestProto) {
		this.purchaseCityExpansionRequestProto = purchaseCityExpansionRequestProto;
		playerId = purchaseCityExpansionRequestProto.getSender().getUserUuid();
	}

	@Override
	public String toString() {
		return "PurchaseCityExpansionRequestEvent [purchaseCityExpansionRequestProto="
				+ purchaseCityExpansionRequestProto + "]";
	}

}
