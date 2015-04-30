//package com.lvl6.events.response;
//
//import java.nio.ByteBuffer;
//
//import com.google.protobuf.ByteString;
//import com.lvl6.events.NormalResponseEvent;
//import com.lvl6.proto.EventStructureProto.ExpansionWaitCompleteResponseProto;
//import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;
//
//public class ExpansionWaitCompleteResponseEvent extends NormalResponseEvent<ExpansionWaitCompleteResponseProto> {
//
//  private ExpansionWaitCompleteResponseProto expansionWaitCompleteResponseProto;
//  
//  public ExpansionWaitCompleteResponseEvent(String playerId){
//    super(playerId);
//    eventType = EventProtocolResponse.S_EXPANSION_WAIT_COMPLETE_EVENT;
//  }
//  
//  @Override
//  public int write(ByteBuffer bb) {
//    ByteString b = expansionWaitCompleteResponseProto.toByteString();
//    b.copyTo(bb);
//    return b.size();
//  }
//
//  public void setExpansionWaitCompleteResponseProto(ExpansionWaitCompleteResponseProto expansionWaitCompleteResponseProto) {
//    this.expansionWaitCompleteResponseProto = expansionWaitCompleteResponseProto;
//  }
//
//}
//TODO: DELETE