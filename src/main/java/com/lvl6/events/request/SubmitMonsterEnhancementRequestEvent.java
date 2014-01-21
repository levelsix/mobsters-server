package com.lvl6.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventMonsterProto.SubmitMonsterEnhancementRequestProto;

public class SubmitMonsterEnhancementRequestEvent extends RequestEvent {

  private SubmitMonsterEnhancementRequestProto submitMonsterEnhancementRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      submitMonsterEnhancementRequestProto = SubmitMonsterEnhancementRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = submitMonsterEnhancementRequestProto.getSender().getMinUserProto().getUserId();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }

  public SubmitMonsterEnhancementRequestProto getSubmitMonsterEnhancementRequestProto() {
    return submitMonsterEnhancementRequestProto;
  }
}
