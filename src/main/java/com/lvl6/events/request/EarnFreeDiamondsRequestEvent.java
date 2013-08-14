package com.lvl6.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventProto.EarnFreeDiamondsRequestProto;

public class EarnFreeDiamondsRequestEvent extends RequestEvent {

  private EarnFreeDiamondsRequestProto earnFreeDiamondsRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      earnFreeDiamondsRequestProto = EarnFreeDiamondsRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = earnFreeDiamondsRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }

  public EarnFreeDiamondsRequestProto getEarnFreeDiamondsRequestProto() {
    return earnFreeDiamondsRequestProto;
  }
}
