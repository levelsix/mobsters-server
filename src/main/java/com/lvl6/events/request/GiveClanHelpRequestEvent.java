package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventClanProto.GiveClanHelpRequestProto;

public class GiveClanHelpRequestEvent extends RequestEvent {

	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private GiveClanHelpRequestProto giveClanHelpRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      giveClanHelpRequestProto = GiveClanHelpRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = giveClanHelpRequestProto.getSender().getUserUuid();
    } catch (InvalidProtocolBufferException e) {
      log.error("giveClanHelp request exception", e);
    }
  }

  public GiveClanHelpRequestProto getGiveClanHelpRequestProto() {
    return giveClanHelpRequestProto;
  }
}
