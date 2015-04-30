//package com.lvl6.events.response;
//
//import java.nio.ByteBuffer;
//
//import com.google.protobuf.ByteString;
//import com.lvl6.events.NormalResponseEvent;
//import com.lvl6.proto.EventCityProto.PurchaseCityExpansionResponseProto;
//import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;
//
//public class PurchaseCityExpansionResponseEvent extends NormalResponseEvent<PurchaseCityExpansionResponseProto> {
//
//  private PurchaseCityExpansionResponseProto purchaseCityExpansionResponseProto;
//  
//  public PurchaseCityExpansionResponseEvent(String playerId){
//    super(playerId);
//    eventType = EventProtocolResponse.S_PURCHASE_CITY_EXPANSION_EVENT;
//  }
//  
//  @Override
//  public int write(ByteBuffer bb) {
//    ByteString b = purchaseCityExpansionResponseProto.toByteString();
//    b.copyTo(bb);
//    return b.size();
//  }
//
//  public void setPurchaseCityExpansionResponseProto(PurchaseCityExpansionResponseProto purchaseCityExpansionResponseProto) {
//    this.purchaseCityExpansionResponseProto = purchaseCityExpansionResponseProto;
//  }
//
//}
//TODO: CONSIDER DELETING