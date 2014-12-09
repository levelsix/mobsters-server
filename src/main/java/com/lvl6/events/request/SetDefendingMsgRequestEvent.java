package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventPvpProto.SetDefendingMsgRequestProto;

public class SetDefendingMsgRequestEvent extends RequestEvent {

	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private SetDefendingMsgRequestProto setDefendingMsgRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      setDefendingMsgRequestProto = SetDefendingMsgRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = setDefendingMsgRequestProto.getSender().getUserUuid();
    } catch (InvalidProtocolBufferException e) {
      log.error("SetDefendingMsg request exception", e);
    }
  }

  public SetDefendingMsgRequestProto getSetDefendingMsgRequestProto() {
    return setDefendingMsgRequestProto;
  }

  public void setSetDefendingMsgRequestProto( SetDefendingMsgRequestProto setDefendingMsgRequestProto )
  {
	  this.setDefendingMsgRequestProto = setDefendingMsgRequestProto;
  }

}
