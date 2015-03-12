package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventBattleItemProto.CompleteBattleItemRequestProto;

public class CompleteBattleItemRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private CompleteBattleItemRequestProto completeBattleItemRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      completeBattleItemRequestProto = CompleteBattleItemRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = completeBattleItemRequestProto.getSender().getUserUuid();
    } catch (InvalidProtocolBufferException e) {
      log.error("CompleteBattleItemRequest exception", e);
    }
  }

  public CompleteBattleItemRequestProto getCompleteBattleItemRequestProto() {
    return completeBattleItemRequestProto;
  }
  //added for testing purposes
  public void setCompleteBattleItemRequestProto(CompleteBattleItemRequestProto sorp) {
	  this.completeBattleItemRequestProto = sorp;
  }

  @Override
  public String toString()
  {
	  return "CompleteBattleItemRequestEvent [completeBattleItemRequestProto="
		  + completeBattleItemRequestProto
		  + "]";
  }
  
}
