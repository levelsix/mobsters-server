package com.lvl6.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventUserProto.SetFacebookIdRequestProto;

public class SetFacebookIdRequestEvent extends RequestEvent {

  private SetFacebookIdRequestProto setFacebookIdRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      setFacebookIdRequestProto = SetFacebookIdRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = setFacebookIdRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }

  public SetFacebookIdRequestProto getSetFacebookIdRequestProto() {
    return setFacebookIdRequestProto;
  }
}
