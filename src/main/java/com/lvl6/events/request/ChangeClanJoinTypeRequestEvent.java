package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventClanProto.ChangeClanJoinTypeRequestProto;

public class ChangeClanJoinTypeRequestEvent extends RequestEvent {

	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private ChangeClanJoinTypeRequestProto changeClanJoinTypeRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      changeClanJoinTypeRequestProto = ChangeClanJoinTypeRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = changeClanJoinTypeRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      log.error("change clan join type request exception");
    }
  }

  public ChangeClanJoinTypeRequestProto getChangeClanJoinTypeRequestProto() {
    return changeClanJoinTypeRequestProto;
  }
}
