package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventUserProto.UpdateUserCurrencyRequestProto;

public class UpdateUserCurrencyRequestEvent extends RequestEvent {
	
	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private UpdateUserCurrencyRequestProto updateUserCurrencyRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      updateUserCurrencyRequestProto = UpdateUserCurrencyRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = updateUserCurrencyRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      log.error("update user currency request exception", e);
    }
  }

  public UpdateUserCurrencyRequestProto getUpdateUserCurrencyRequestProto() {
    return updateUserCurrencyRequestProto;
  }
}
