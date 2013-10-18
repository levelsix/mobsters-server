package com.lvl6.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventMonsterProto.HealMonsterWaitTimeCompleteRequestProto;

public class HealMonsterWaitTimeCompleteRequestEvent extends RequestEvent {

  private HealMonsterWaitTimeCompleteRequestProto healMonsteWaitTimeCompleteRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
    	healMonsteWaitTimeCompleteRequestProto = HealMonsterWaitTimeCompleteRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = healMonsteWaitTimeCompleteRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }

  public HealMonsterWaitTimeCompleteRequestProto getHealMonsterWaitTimeCompleteRequestProto() {
    return healMonsteWaitTimeCompleteRequestProto;
  }
}
