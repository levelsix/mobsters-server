//package com.lvl6.events.response;
//
//import java.nio.ByteBuffer;
//
//import com.google.protobuf.ByteString;
//import com.lvl6.events.NormalResponseEvent;
//import com.lvl6.proto.EventMonsterProto.EnhanceMonsterResponseProto;
//import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;
//
//public class EnhanceMonsterResponseEvent extends NormalResponseEvent<EnhanceMonsterResponseProto> {
//
//  private EnhanceMonsterResponseProto enhanceMonsterResponseProto;
//  
//  public EnhanceMonsterResponseEvent(String playerId){
//    super(playerId);
//    eventType = EventProtocolResponse.S_ENHANCE_MONSTER_EVENT;
//  }
//  
//  @Override
//  public int write(ByteBuffer bb) {
//    ByteString b = enhanceMonsterResponseProto.toByteString();
//    b.copyTo(bb);
//    return b.size();
//  }
//
//  public void setEnhanceMonsterResponseProto(EnhanceMonsterResponseProto enhanceMonsterResponseProto) {
//    this.enhanceMonsterResponseProto = enhanceMonsterResponseProto;
//  }
//
//}
//TODO: CONSIDER DELETE