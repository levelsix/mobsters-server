package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventChatProto.RetrievePrivateChatPostsRequestProto;

public class RetrievePrivateChatPostsRequestEvent extends RequestEvent{
	
	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private RetrievePrivateChatPostsRequestProto retrievePrivateChatPostsRequestProto;
  /**
   * read the event from the given ByteBuffer to populate this event
   */ 
  @Override
  public void read(ByteBuffer buff) {
    try {
      retrievePrivateChatPostsRequestProto = RetrievePrivateChatPostsRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = retrievePrivateChatPostsRequestProto.getSender().getUserUuid();
    } catch (InvalidProtocolBufferException e) {
      log.error("RetrievePrivateChatPostsRequest exception", e);
    }
  }

  public RetrievePrivateChatPostsRequestProto getRetrievePrivateChatPostsRequestProto() {
    return retrievePrivateChatPostsRequestProto;
  }

  @Override
  public String toString()
  {
	  return "RetrievePrivateChatPostsRequestEvent [retrievePrivateChatPostsRequestProto="
		  + retrievePrivateChatPostsRequestProto
		  + "]";
  }
  
}//RetrievePrivateChatPostsRequestProto
