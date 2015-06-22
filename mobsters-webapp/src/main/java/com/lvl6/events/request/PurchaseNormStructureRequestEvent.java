package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventStructureProto.PurchaseNormStructureRequestProto;

public class PurchaseNormStructureRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(PurchaseNormStructureRequestEvent.class);

	private PurchaseNormStructureRequestProto purchaseNormStructureRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			purchaseNormStructureRequestProto = PurchaseNormStructureRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = purchaseNormStructureRequestProto.getSender()
					.getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("PurchaseNormStructureRequest exception", e);
		}
	}

	public PurchaseNormStructureRequestProto getPurchaseNormStructureRequestProto() {
		return purchaseNormStructureRequestProto;
	}

	@Override
	public String toString() {
		return "PurchaseNormStructureRequestEvent [purchaseNormStructureRequestProto="
				+ purchaseNormStructureRequestProto + "]";
	}

}
