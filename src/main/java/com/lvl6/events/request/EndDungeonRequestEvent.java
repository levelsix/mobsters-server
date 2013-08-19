package com.lvl6.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventProto.EndDungeonRequestProto;

public class EndDungeonRequestEvent extends RequestEvent {

  private EndDungeonRequestProto endDungeonRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      endDungeonRequestProto = EndDungeonRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = endDungeonRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }

  public EndDungeonRequestProto getEndDungeonRequestProto() {
    return endDungeonRequestProto;
  }
}
