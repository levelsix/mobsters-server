package com.lvl6.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventProto.BeginDungeonRequestProto;

public class BeginDungeonRequestEvent extends RequestEvent {

  private BeginDungeonRequestProto beginDungeonRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      beginDungeonRequestProto = BeginDungeonRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = beginDungeonRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }

  public BeginDungeonRequestProto getBeginDungeonRequestProto() {
    return beginDungeonRequestProto;
  }
}
