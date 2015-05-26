//package com.lvl6.events.request;
//
//import java.nio.ByteBuffer;
//
//import com.google.protobuf.ByteString;
//import com.google.protobuf.InvalidProtocolBufferException;
//import com.lvl6.events.RequestEvent;
//import com.lvl6.proto.EventDungeonProto.EndPersistentEventCoolDownTimerRequestProto;
//
//public class EndPersistentEventCoolDownTimerRequestEvent extends RequestEvent {
//
//  private EndPersistentEventCoolDownTimerRequestProto endPersistentEventCoolDownTimerRequestProto;
//  
//  /**
//   * read the event from the given ByteBuffer to populate this event
//   */
//  public void read(ByteBuffer buff) {
//    try {
//      endPersistentEventCoolDownTimerRequestProto = EndPersistentEventCoolDownTimerRequestProto.parseFrom(ByteString.copyFrom(buff));
//      playerId = endPersistentEventCoolDownTimerRequestProto.getSender().getUserUuid();
//    } catch (InvalidProtocolBufferException e) {
//      e.printStackTrace();
//    }
//  }
//
//  public EndPersistentEventCoolDownTimerRequestProto getEndPersistentEventCoolDownTimerRequestProto() {
//    return endPersistentEventCoolDownTimerRequestProto;
//  }
//}
