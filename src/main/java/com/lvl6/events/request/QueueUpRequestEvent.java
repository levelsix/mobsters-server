package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventPvpProto.QueueUpRequestProto;

public class QueueUpRequestEvent extends RequestEvent{
	
  private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private QueueUpRequestProto queueUpRequestProto;
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  @Override
  public void read(ByteBuffer buff) {
    try {
      queueUpRequestProto = QueueUpRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = queueUpRequestProto.getAttacker().getUserId();
    } catch (InvalidProtocolBufferException e) {
      log.error("queue up request exception", e);
    }
  }

  public QueueUpRequestProto getQueueUpRequestProto() {
    return queueUpRequestProto;
  }
  
}//QueueUpRequestProto