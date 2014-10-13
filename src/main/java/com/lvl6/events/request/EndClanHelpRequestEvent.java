package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventClanProto.EndClanHelpRequestProto;

public class EndClanHelpRequestEvent extends RequestEvent {

	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private EndClanHelpRequestProto endClanHelpRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      endClanHelpRequestProto = EndClanHelpRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = endClanHelpRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      log.error("endClanHelp request exception", e);
    }
  }

  public EndClanHelpRequestProto getEndClanHelpRequestProto() {
    return endClanHelpRequestProto;
  }
}
