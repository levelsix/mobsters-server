//package com.lvl6.events.response;
//
//import java.nio.ByteBuffer;
//
//import com.google.protobuf.ByteString;
//import com.lvl6.events.NormalResponseEvent;
//import responseProto;
//import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;
//
//public class EndPersistentEventCoolDownTimerResponseEvent extends NormalResponseEvent<EndPersistentEventCoolDownTimerResponseProto> {
//
//  
//  
//  public EndPersistentEventCoolDownTimerResponseEvent(int playerId){
//    super(playerId);
//    eventType = EventProtocolResponse.S_END_PERSISTENT_EVENT_COOL_DOWN_TIMER_EVENT;
//  }
//  
//  @Override
//  public int write(ByteBuffer bb) {
//    ByteString b =  responseProto.toByteString();
//    b.copyTo(bb);
//    return b.size();
//  }
//
//  public void setEndPersistentEventCoolDownTimerResponseProto(EndPersistentEventCoolDownTimerResponseProto responseProto) {
//    this.responseProto = responseProto;
//  }
//
//  public EndPersistentEventCoolDownTimerResponseProto getEndPersistentEventCoolDownTimerResponseProto() {   //because APNS required
//    return responseProto;
//  }
//  
//}
