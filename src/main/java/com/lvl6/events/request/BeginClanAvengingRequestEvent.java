package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventClanProto.BeginClanAvengingRequestProto;

public class BeginClanAvengingRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private BeginClanAvengingRequestProto beginClanAvengingRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      beginClanAvengingRequestProto = BeginClanAvengingRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = beginClanAvengingRequestProto.getSender().getUserUuid();
    } catch (InvalidProtocolBufferException e) {
      log.error("BeginClanAvengingRequest exception", e);
    }
  }

  public BeginClanAvengingRequestProto getBeginClanAvengingRequestProto() {
    return beginClanAvengingRequestProto;
  }

  @Override
  public String toString()
  {
	  return "BeginClanAvengingRequestEvent [beginClanAvengingRequestProto="
		  + beginClanAvengingRequestProto
		  + "]";
  }

}
