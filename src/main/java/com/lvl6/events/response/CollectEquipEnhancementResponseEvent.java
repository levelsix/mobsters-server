//package com.lvl6.events.response;
//
//import java.nio.ByteBuffer;
//
//import com.google.protobuf.ByteString;
//import com.lvl6.events.NormalResponseEvent;
//import responseProto;
//import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;
//
//public class CollectEquipEnhancementResponseEvent extends NormalResponseEvent<CollectEquipEnhancementResponseProto> {
//
//  
//  
//  public CollectEquipEnhancementResponseEvent(int playerId){
//    super(playerId);
//    eventType = EventProtocolResponse.S_COLLECT_EQUIP_ENHANCEMENT_EVENT;
//  }
//  
//  @Override
//  public int write(ByteBuffer bb) {
//    ByteString b =  responseProto.toByteString();
//    b.copyTo(bb);
//    return b.size();
//  }
//
//  public void setCollectEquipEnhancementResponseProto(CollectEquipEnhancementResponseProto responseProto) {
//    this.responseProto = responseProto;
//  }
//
//}
