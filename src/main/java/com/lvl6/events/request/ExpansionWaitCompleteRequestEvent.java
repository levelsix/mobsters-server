package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventStructureProto.ExpansionWaitCompleteRequestProto;

public class ExpansionWaitCompleteRequestEvent extends RequestEvent {

	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private ExpansionWaitCompleteRequestProto expansionWaitCompleteRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      expansionWaitCompleteRequestProto = ExpansionWaitCompleteRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = expansionWaitCompleteRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      log.error("expansion wait complete request exception", e);
    }
  }

  public ExpansionWaitCompleteRequestProto getExpansionWaitCompleteRequestProto() {
    return expansionWaitCompleteRequestProto;
  }
}
