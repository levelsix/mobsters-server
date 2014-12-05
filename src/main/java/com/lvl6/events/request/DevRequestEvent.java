package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventDevProto.DevRequestProto;

public class DevRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private DevRequestProto devRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      devRequestProto = DevRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = devRequestProto.getSender().getUserUuid();
    } catch (InvalidProtocolBufferException e) {
      log.error("DevRequest exception", e);
    }
  }

  public DevRequestProto getDevRequestProto() {
    return devRequestProto;
  }

  public void setDevRequestProto( DevRequestProto devRequestProto )
  {
	  this.devRequestProto = devRequestProto;
  }

  @Override
  public String toString()
  {
	  return "DevRequestEvent [devRequestProto="
		  + devRequestProto
		  + "]";
  }

}
