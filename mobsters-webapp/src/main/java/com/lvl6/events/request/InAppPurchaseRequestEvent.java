package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventInAppPurchaseProto.InAppPurchaseRequestProto;

public class InAppPurchaseRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private InAppPurchaseRequestProto inAppPurchaseRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			inAppPurchaseRequestProto = InAppPurchaseRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = inAppPurchaseRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("InAppPurchaseRequest exception", e);
		}
	}

	public InAppPurchaseRequestProto getInAppPurchaseRequestProto() {
		return inAppPurchaseRequestProto;
	}

	public void setInAppPurchaseRequestProto(InAppPurchaseRequestProto iaprp) {
		inAppPurchaseRequestProto = iaprp;
	}

	@Override
	public String toString() {
		return "InAppPurchaseRequestEvent [inAppPurchaseRequestProto="
				+ inAppPurchaseRequestProto + "]";
	}

}
