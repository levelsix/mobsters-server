package com.lvl6.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventMonsterProto.HealMonsterRequestProto;

public class HealMonsterRequestEvent extends RequestEvent {

  private HealMonsterRequestProto healMonsterRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      healMonsterRequestProto = HealMonsterRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = healMonsterRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }

  public HealMonsterRequestProto getHealMonsterRequestProto() {
    return healMonsterRequestProto;
  }
}
