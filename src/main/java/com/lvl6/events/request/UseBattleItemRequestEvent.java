package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventPvpProto.UseBattleItemRequestProto;

public class UseBattleItemRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private UseBattleItemRequestProto useBattleItemRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      useBattleItemRequestProto = UseBattleItemRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = useBattleItemRequestProto.getSender().getUserUuid();
    } catch (InvalidProtocolBufferException e) {
      log.error("UseBattleItemRequest exception", e);
    }
  }

  public UseBattleItemRequestProto getUseBattleItemRequestProto() {
    return useBattleItemRequestProto;
  }
  //added for testing purposes
  public void setUseBattleItemRequestProto(UseBattleItemRequestProto sorp) {
	  this.useBattleItemRequestProto = sorp;
  }

  @Override
  public String toString()
  {
	  return "UseBattleItemRequestEvent [useBattleItemRequestProto="
		  + useBattleItemRequestProto
		  + "]";
  }
  
}
