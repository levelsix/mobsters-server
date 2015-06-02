//package com.lvl6.events.response;
//
//import java.nio.ByteBuffer;
//
//import com.google.protobuf.ByteString;
//import com.lvl6.events.NormalResponseEvent;
//import com.lvl6.proto.EventItemProto.PurchaseItemsWithGemsResponseProto;
//import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;
//
//public class PurchaseItemsWithGemsResponseEvent extends NormalResponseEvent {
//
//	private PurchaseItemsWithGemsResponseProto purchaseItemsWithGemsResponseProto;
//
//	public PurchaseItemsWithGemsResponseEvent(String playerId) {
//		super(playerId);
//		eventType = EventProtocolResponse.S_PURCHASE_ITEMS_WITH_GEMS_EVENT;
//	}
//
//	@Override
//	public int write(ByteBuffer bb) {
//		ByteString b = purchaseItemsWithGemsResponseProto.toByteString();
//		b.copyTo(bb);
//		return b.size();
//	}
//
//	public void setPurchaseItemsWithGemsResponseProto(
//			PurchaseItemsWithGemsResponseProto purchaseItemsWithGemsResponseProto) {
//		this.purchaseItemsWithGemsResponseProto = purchaseItemsWithGemsResponseProto;
//	}
//
//}
