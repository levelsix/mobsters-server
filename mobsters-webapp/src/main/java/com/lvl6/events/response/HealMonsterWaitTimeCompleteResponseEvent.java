//package com.lvl6.events.response;
//
//import java.nio.ByteBuffer;
//
//import com.google.protobuf.ByteString;
//import com.lvl6.events.NormalResponseEvent;
//import responseProto;
//import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;
//
//public class HealMonsterWaitTimeCompleteResponseEvent extends NormalResponseEvent<HealMonsterWaitTimeCompleteResponseProto> {
//
//  
//  
//  public HealMonsterWaitTimeCompleteResponseEvent(int playerId){
//    super(playerId);
//    eventType = EventProtocolResponse.S_HEAL_MONSTER_WAIT_TIME_COMPLETE_EVENT;
//  }
//  
//  @Override
//  public int write(ByteBuffer bb) {
//    ByteString b =  responseProto.toByteString();
//    b.copyTo(bb);
//    return b.size();
//  }
//
//  public void setHealMonsterWaitTimeCompleteResponseProto(HealMonsterWaitTimeCompleteResponseProto responseProto) {
//    this.responseProto = responseProto;
//  }
//
//}
