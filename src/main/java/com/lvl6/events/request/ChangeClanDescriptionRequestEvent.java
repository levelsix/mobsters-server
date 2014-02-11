package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventClanProto.ChangeClanDescriptionRequestProto;

public class ChangeClanDescriptionRequestEvent extends RequestEvent {

	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private ChangeClanDescriptionRequestProto changeClanDescriptionRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      changeClanDescriptionRequestProto = ChangeClanDescriptionRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = changeClanDescriptionRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      log.error("change clan description request exception", e);
    }
  }

  public ChangeClanDescriptionRequestProto getChangeClanDescriptionRequestProto() {
    return changeClanDescriptionRequestProto;
  }
}
