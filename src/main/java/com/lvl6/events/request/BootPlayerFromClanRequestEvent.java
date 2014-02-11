package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventClanProto.BootPlayerFromClanRequestProto;

public class BootPlayerFromClanRequestEvent extends RequestEvent {

	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private BootPlayerFromClanRequestProto bootPlayerFromClanRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      bootPlayerFromClanRequestProto = BootPlayerFromClanRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = bootPlayerFromClanRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      log.error("boot player from clan request exception", e);
    }
  }

  public BootPlayerFromClanRequestProto getBootPlayerFromClanRequestProto() {
    return bootPlayerFromClanRequestProto;
  }
}
