package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventChatProto.SendGroupChatRequestProto;

public class SendGroupChatRequestEvent extends RequestEvent {

	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private SendGroupChatRequestProto sendGroupChatRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      sendGroupChatRequestProto = SendGroupChatRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = sendGroupChatRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      log.error("send group chat request exception", e);
    }
  }

  public SendGroupChatRequestProto getSendGroupChatRequestProto() {
    return sendGroupChatRequestProto;
  }
}
