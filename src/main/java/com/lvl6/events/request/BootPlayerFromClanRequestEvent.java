package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventClanProto.BootPlayerFromClanRequestProto;

public class BootPlayerFromClanRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private BootPlayerFromClanRequestProto bootPlayerFromClanRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      bootPlayerFromClanRequestProto = BootPlayerFromClanRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = bootPlayerFromClanRequestProto.getSender().getUserUuid();
    } catch (InvalidProtocolBufferException e) {
      log.error("BootPlayerFromClanRequest exception", e);
    }
  }

  public BootPlayerFromClanRequestProto getBootPlayerFromClanRequestProto() {
    return bootPlayerFromClanRequestProto;
  }

  @Override
  public String toString()
  {
	  return "BootPlayerFromClanRequestEvent [bootPlayerFromClanRequestProto="
		  + bootPlayerFromClanRequestProto
		  + "]";
  }

}
