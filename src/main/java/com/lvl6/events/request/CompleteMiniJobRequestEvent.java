package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventMiniJobProto.CompleteMiniJobRequestProto;

public class CompleteMiniJobRequestEvent extends RequestEvent {

	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private CompleteMiniJobRequestProto completeMiniJobRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      completeMiniJobRequestProto = CompleteMiniJobRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = completeMiniJobRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      log.error("CompleteMiniJob request exception", e);
    }
  }

  public CompleteMiniJobRequestProto getCompleteMiniJobRequestProto() {
    return completeMiniJobRequestProto;
  }
  //added for testing purposes
  public void setCompleteMiniJobRequestProto(CompleteMiniJobRequestProto sorp) {
	  this.completeMiniJobRequestProto = sorp;
  }
  
}
