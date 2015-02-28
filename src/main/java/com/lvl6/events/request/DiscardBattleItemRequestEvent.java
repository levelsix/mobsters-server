package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventBattleItemProto.DiscardBattleItemRequestProto;

public class DiscardBattleItemRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private DiscardBattleItemRequestProto discardBattleItemRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      discardBattleItemRequestProto = DiscardBattleItemRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = discardBattleItemRequestProto.getSender().getUserUuid();
    } catch (InvalidProtocolBufferException e) {
      log.error("DiscardBattleItemRequest exception", e);
    }
  }

  public DiscardBattleItemRequestProto getDiscardBattleItemRequestProto() {
    return discardBattleItemRequestProto;
  }
  //added for testing purposes
  public void setDiscardBattleItemRequestProto(DiscardBattleItemRequestProto sorp) {
	  this.discardBattleItemRequestProto = sorp;
  }

  @Override
  public String toString()
  {
	  return "DiscardBattleItemRequestEvent [discardBattleItemRequestProto="
		  + discardBattleItemRequestProto
		  + "]";
  }
  
}
