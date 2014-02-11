package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventChatProto.PrivateChatPostRequestProto;

public class PrivateChatPostRequestEvent extends RequestEvent {

	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	private PrivateChatPostRequestProto privateChatPostRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      privateChatPostRequestProto = PrivateChatPostRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = privateChatPostRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      log.error("private chat post request exception", e);
    }
  }

  public PrivateChatPostRequestProto getPrivateChatPostRequestProto() {
    return privateChatPostRequestProto;
  }
}
