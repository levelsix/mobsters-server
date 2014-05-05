package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventMiniJobProto.BeginMiniJobRequestProto;

public class BeginMiniJobRequestEvent extends RequestEvent {

	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private BeginMiniJobRequestProto beginMiniJobRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      beginMiniJobRequestProto = BeginMiniJobRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = beginMiniJobRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      log.error("BeginMiniJob request exception", e);
    }
  }

  public BeginMiniJobRequestProto getBeginMiniJobRequestProto() {
    return beginMiniJobRequestProto;
  }
  //added for testing purposes
  public void setBeginMiniJobRequestProto(BeginMiniJobRequestProto sorp) {
	  this.beginMiniJobRequestProto = sorp;
  }
  
}
