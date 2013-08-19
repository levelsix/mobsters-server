package com.lvl6.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventProto.ReviveInDungeonRequestProto;

public class ReviveInDungeonRequestEvent extends RequestEvent {

  private ReviveInDungeonRequestProto reviveInDungeonRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      reviveInDungeonRequestProto = ReviveInDungeonRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = reviveInDungeonRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }

  public ReviveInDungeonRequestProto getReviveInDungeonRequestProto() {
    return reviveInDungeonRequestProto;
  }
}
