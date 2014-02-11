package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventMonsterProto.UpdateMonsterHealthRequestProto;

public class UpdateMonsterHealthRequestEvent extends RequestEvent {

	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private UpdateMonsterHealthRequestProto updateMonsterHealthRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      updateMonsterHealthRequestProto = UpdateMonsterHealthRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = updateMonsterHealthRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      log.error("update monster health request exception", e);
    }
  }

  public UpdateMonsterHealthRequestProto getUpdateMonsterHealthRequestProto() {
    return updateMonsterHealthRequestProto;
  }
}
