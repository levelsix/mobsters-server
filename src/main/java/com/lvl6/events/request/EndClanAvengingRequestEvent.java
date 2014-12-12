package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventClanProto.EndClanAvengingRequestProto;

public class EndClanAvengingRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private EndClanAvengingRequestProto endClanAvengingRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      endClanAvengingRequestProto = EndClanAvengingRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = endClanAvengingRequestProto.getSender().getUserUuid();
    } catch (InvalidProtocolBufferException e) {
      log.error("EndClanAvengingRequest exception", e);
    }
  }

  public EndClanAvengingRequestProto getEndClanAvengingRequestProto() {
    return endClanAvengingRequestProto;
  }

  @Override
  public String toString()
  {
	  return "EndClanAvengingRequestEvent [endClanAvengingRequestProto="
		  + endClanAvengingRequestProto
		  + "]";
  }

}
