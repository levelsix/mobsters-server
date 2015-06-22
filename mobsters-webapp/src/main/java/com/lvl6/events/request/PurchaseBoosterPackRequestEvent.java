package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventBoosterPackProto.PurchaseBoosterPackRequestProto;

public class PurchaseBoosterPackRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(PurchaseBoosterPackRequestEvent.class);

	private PurchaseBoosterPackRequestProto purchaseBoosterPackRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			purchaseBoosterPackRequestProto = PurchaseBoosterPackRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = purchaseBoosterPackRequestProto.getSender()
					.getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("PurchaseBoosterPackRequest exception", e);
		}
	}

	public PurchaseBoosterPackRequestProto getPurchaseBoosterPackRequestProto() {
		return purchaseBoosterPackRequestProto;
	}

	public void setPurchaseBoosterPackRequestProto(PurchaseBoosterPackRequestProto pbprp) {
		this.purchaseBoosterPackRequestProto = pbprp;
	}

	@Override
	public String toString() {
		return "PurchaseBoosterPackRequestEvent [purchaseBoosterPackRequestProto="
				+ purchaseBoosterPackRequestProto + "]";
	}

}
