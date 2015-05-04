//package com.lvl6.events.response;
//
//import java.nio.ByteBuffer;
//
//import com.google.protobuf.ByteString;
//import com.lvl6.events.NormalResponseEvent;
//import responseProto;
//import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;
//
//public class EarnFreeDiamondsResponseEvent extends NormalResponseEvent<EarnFreeDiamondsResponseProto> {
//
//  private EarnFreeDiamondsResponseProto responseProto;
//  
//  public EarnFreeDiamondsResponseEvent(int playerId){
//    super(playerId);
//    eventType = EventProtocolResponse.S_EARN_FREE_DIAMONDS_EVENT;
//  }
//  
//  @Override
//  public int write(ByteBuffer bb) {
//    ByteString b =  responseProto.toByteString();
//    b.copyTo(bb);
//    return b.size();
//  }
//
//  public void setEarnFreeDiamondsResponseProto(EarnFreeDiamondsResponseProto responseProto) {
//    this.responseProto = responseProto;
//  }
//
//}
//TODO: CONSIDER DELETING