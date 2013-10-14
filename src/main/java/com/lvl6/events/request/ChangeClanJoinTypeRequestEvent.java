package com.lvl6.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventClanProto.ChangeClanJoinTypeRequestProto;

public class ChangeClanJoinTypeRequestEvent extends RequestEvent {

  private ChangeClanJoinTypeRequestProto changeClanJoinTypeRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      changeClanJoinTypeRequestProto = ChangeClanJoinTypeRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = changeClanJoinTypeRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }

  public ChangeClanJoinTypeRequestProto getChangeClanJoinTypeRequestProto() {
    return changeClanJoinTypeRequestProto;
  }
}
