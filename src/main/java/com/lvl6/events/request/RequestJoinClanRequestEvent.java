package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventClanProto.RequestJoinClanRequestProto;

public class RequestJoinClanRequestEvent extends RequestEvent {

	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private RequestJoinClanRequestProto requestJoinClanRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      requestJoinClanRequestProto = RequestJoinClanRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = requestJoinClanRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      log.error("request join clan request exception", e);
    }
  }

  public RequestJoinClanRequestProto getRequestJoinClanRequestProto() {
    return requestJoinClanRequestProto;
  }
}
