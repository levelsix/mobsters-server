package com.lvl6.events.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventChatProto.RetrievePrivateChatPostsRequestProto;

public class RetrievePrivateChatPostsRequestEvent extends RequestEvent{
	
	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private RetrievePrivateChatPostsRequestProto retrievePrivateChatPostsRequestProto;
  /**
   * read the event from the given ByteBuffer to populate this event
   */ 
  @Override
  public void read(ByteBuffer buff) {
    try {
      retrievePrivateChatPostsRequestProto = RetrievePrivateChatPostsRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = retrievePrivateChatPostsRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      log.error("retireve private chat posts request exception", e);
    }
  }

  public RetrievePrivateChatPostsRequestProto getRetrievePrivateChatPostsRequestProto() {
    return retrievePrivateChatPostsRequestProto;
  }
  
}//RetrievePrivateChatPostsRequestProto