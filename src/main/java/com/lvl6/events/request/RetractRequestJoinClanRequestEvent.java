package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventClanProto.RetractRequestJoinClanRequestProto;

public class RetractRequestJoinClanRequestEvent extends RequestEvent {
	
	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private RetractRequestJoinClanRequestProto retractRequestJoinClanRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      retractRequestJoinClanRequestProto = RetractRequestJoinClanRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = retractRequestJoinClanRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      log.error("retract request join clan request exception", e);
    }
  }

  public RetractRequestJoinClanRequestProto getRetractRequestJoinClanRequestProto() {
    return retractRequestJoinClanRequestProto;
  }
}
