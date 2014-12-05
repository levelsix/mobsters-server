package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventUserProto.UpdateUserCurrencyRequestProto;

public class UpdateUserCurrencyRequestEvent extends RequestEvent {
	
	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private UpdateUserCurrencyRequestProto updateUserCurrencyRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      updateUserCurrencyRequestProto = UpdateUserCurrencyRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = updateUserCurrencyRequestProto.getSender().getUserUuid();
    } catch (InvalidProtocolBufferException e) {
      log.error("UpdateUserCurrencyRequest exception", e);
    }
  }

  public UpdateUserCurrencyRequestProto getUpdateUserCurrencyRequestProto() {
    return updateUserCurrencyRequestProto;
  }

  @Override
  public String toString()
  {
	  return "UpdateUserCurrencyRequestEvent [updateUserCurrencyRequestProto="
		  + updateUserCurrencyRequestProto
		  + "]";
  }

}
