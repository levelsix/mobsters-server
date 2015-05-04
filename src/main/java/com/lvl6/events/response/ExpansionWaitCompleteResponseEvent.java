//package com.lvl6.events.response;
//
//import java.nio.ByteBuffer;
//
//import com.google.protobuf.ByteString;
//import com.lvl6.events.NormalResponseEvent;
//import responseProto;
//import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;
//
//public class ExpansionWaitCompleteResponseEvent extends NormalResponseEvent<ExpansionWaitCompleteResponseProto> {
//
//  private ExpansionWaitCompleteResponseProto responseProto;
//  
//  public ExpansionWaitCompleteResponseEvent(String playerId){
//    super(playerId);
//    eventType = EventProtocolResponse.S_EXPANSION_WAIT_COMPLETE_EVENT;
//  }
//  
//  @Override
//  public int write(ByteBuffer bb) {
//    ByteString b =  responseProto.toByteString();
//    b.copyTo(bb);
//    return b.size();
//  }
//
//  public void setExpansionWaitCompleteResponseProto(ExpansionWaitCompleteResponseProto responseProto) {
//    this.responseProto = responseProto;
//  }
//
//}
//TODO: DELETE