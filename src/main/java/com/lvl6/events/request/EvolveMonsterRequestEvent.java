package com.lvl6.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventMonsterProto.EvolveMonsterRequestProto;

public class EvolveMonsterRequestEvent extends RequestEvent {

  private EvolveMonsterRequestProto evolveMonsterRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      evolveMonsterRequestProto = EvolveMonsterRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = evolveMonsterRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }

  public EvolveMonsterRequestProto getEvolveMonsterRequestProto() {
    return evolveMonsterRequestProto;
  }
}
