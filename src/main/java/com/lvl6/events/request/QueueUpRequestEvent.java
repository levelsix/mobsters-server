package com.lvl6.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventProto.QueueUpRequestProto;

public class QueueUpRequestEvent extends RequestEvent{
  private QueueUpRequestProto QueueUpRequestProto;
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  @Override
  public void read(ByteBuffer buff) {
    try {
      QueueUpRequestProto = QueueUpRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = QueueUpRequestProto.getAttacker().getUserId();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }

  public QueueUpRequestProto getQueueUpRequestProto() {
    return QueueUpRequestProto;
  }
  
}//QueueUpRequestProto