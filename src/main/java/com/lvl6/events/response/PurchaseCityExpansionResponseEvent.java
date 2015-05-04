//package com.lvl6.events.response;
//
//import java.nio.ByteBuffer;
//
//import com.google.protobuf.ByteString;
//import com.lvl6.events.NormalResponseEvent;
//import responseProto;
//import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;
//
//public class PurchaseCityExpansionResponseEvent extends NormalResponseEvent<PurchaseCityExpansionResponseProto> {
//
//  private PurchaseCityExpansionResponseProto responseProto;
//  
//  public PurchaseCityExpansionResponseEvent(String playerId){
//    super(playerId);
//    eventType = EventProtocolResponse.S_PURCHASE_CITY_EXPANSION_EVENT;
//  }
//  
//  @Override
//  public int write(ByteBuffer bb) {
//    ByteString b =  responseProto.toByteString();
//    b.copyTo(bb);
//    return b.size();
//  }
//
//  public void setPurchaseCityExpansionResponseProto(PurchaseCityExpansionResponseProto responseProto) {
//    this.responseProto = responseProto;
//  }
//
//}
//TODO: CONSIDER DELETING