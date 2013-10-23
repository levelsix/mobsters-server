package com.lvl6.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventQuestProto.QuestProgressRequestProto;

public class QuestProgressRequestEvent extends RequestEvent {

  private QuestProgressRequestProto questProgressRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      questProgressRequestProto = QuestProgressRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = questProgressRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }

  public QuestProgressRequestProto getQuestProgressRequestProto() {
    return questProgressRequestProto;
  }
}
