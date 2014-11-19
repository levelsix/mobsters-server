package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventItemProto.RemoveUserItemUsedRequestProto;

public class RemoveUserItemUsedRequestEvent extends RequestEvent {

	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private RemoveUserItemUsedRequestProto removeUserItemUsedRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      removeUserItemUsedRequestProto = RemoveUserItemUsedRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = removeUserItemUsedRequestProto.getSender().getUserUuid();
    } catch (InvalidProtocolBufferException e) {
      log.error("RemoveUserItemUsed request exception", e);
    }
  }

  public RemoveUserItemUsedRequestProto getRemoveUserItemUsedRequestProto() {
    return removeUserItemUsedRequestProto;
  }

  public void setRemoveUserItemUsedRequestProto( RemoveUserItemUsedRequestProto removeUserItemUsedRequestProto )
  {
	  this.removeUserItemUsedRequestProto = removeUserItemUsedRequestProto;
  }

}
