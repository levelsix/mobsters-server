package com.lvl6.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventMonsterProto.EvolutionFinishedRequestProto;

public class EvolutionFinishedRequestEvent extends RequestEvent {

  private EvolutionFinishedRequestProto evolutionFinishedRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      evolutionFinishedRequestProto = EvolutionFinishedRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = evolutionFinishedRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }

  public EvolutionFinishedRequestProto getEvolutionFinishedRequestProto() {
    return evolutionFinishedRequestProto;
  }
}
