package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventClanProto.CreateClanRequestProto;

public class CreateClanRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private CreateClanRequestProto createClanRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      createClanRequestProto = CreateClanRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = createClanRequestProto.getSender().getUserUuid();
    } catch (InvalidProtocolBufferException e) {
      log.error("CreateClanRequest exception", e);
    }
  }

  public CreateClanRequestProto getCreateClanRequestProto() {
    return createClanRequestProto;
  }

  @Override
  public String toString()
  {
	  return "CreateClanRequestEvent [createClanRequestProto="
		  + createClanRequestProto
		  + "]";
  }

}
