//package com.lvl6.events.request;
//
//import java.nio.ByteBuffer;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.google.protobuf.ByteString;
//import com.google.protobuf.InvalidProtocolBufferException;
//import com.lvl6.events.RequestEvent;
//import com.lvl6.proto.EventItemProto.PurchaseItemsWithGemsRequestProto;
//
//public class PurchaseItemsWithGemsRequestEvent extends RequestEvent {
//
//	private static Logger log = LoggerFactory.getLogger(new Object() {
//	}.getClass().getEnclosingClass());
//
//	private PurchaseItemsWithGemsRequestProto purchaseItemsWithGemsRequestProto;
//
//	/**
//	 * read the event from the given ByteBuffer to populate this event
//	 */
//	@Override
//	public void read(ByteBuffer buff) {
//		try {
//			purchaseItemsWithGemsRequestProto = PurchaseItemsWithGemsRequestProto
//					.parseFrom(ByteString.copyFrom(buff));
//			playerId = purchaseItemsWithGemsRequestProto.getSender()
//					.getUserUuid();
//		} catch (InvalidProtocolBufferException e) {
//			log.error("PurchaseItemsWithGemsRequest exception", e);
//		}
//	}
//
//	public PurchaseItemsWithGemsRequestProto getPurchaseItemsWithGemsRequestProto() {
//		return purchaseItemsWithGemsRequestProto;
//	}
//
//	@Override
//	public String toString() {
//		return "PurchaseItemsWithGemsRequestEvent [purchaseItemsWithGemsRequestProto="
//				+ purchaseItemsWithGemsRequestProto + "]";
//	}
//
//}
