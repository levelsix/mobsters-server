//package com.lvl6.events.response;
//
//import java.nio.ByteBuffer;
//
//import com.google.protobuf.ByteString;
//import com.lvl6.events.NormalResponseEvent;
//import responseProto;
//import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;
//
//public class EnhanceMonsterResponseEvent extends NormalResponseEvent<EnhanceMonsterResponseProto> {
//
//  
//  
//  public EnhanceMonsterResponseEvent(String playerId){
//    super(playerId);
//    eventType = EventProtocolResponse.S_ENHANCE_MONSTER_EVENT;
//  }
//  
//  @Override
//  public int write(ByteBuffer bb) {
//    ByteString b =  responseProto.toByteString();
//    b.copyTo(bb);
//    return b.size();
//  }
//
//  public void setEnhanceMonsterResponseProto(EnhanceMonsterResponseProto responseProto) {
//    this.responseProto = responseProto;
//  }
//
//}
//TODO: CONSIDER DELETE