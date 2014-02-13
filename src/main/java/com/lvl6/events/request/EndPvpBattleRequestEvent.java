package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventPvpProto.EndPvpBattleRequestProto;

public class EndPvpBattleRequestEvent extends RequestEvent {
	
	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private EndPvpBattleRequestProto endPvpBattleRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      endPvpBattleRequestProto = EndPvpBattleRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = endPvpBattleRequestProto.getSender().getMinUserProto().getUserId();
    } catch (InvalidProtocolBufferException e) {
      log.error("update user currency request exception", e);
    }
  }

  public EndPvpBattleRequestProto getEndPvpBattleRequestProto() {
    return endPvpBattleRequestProto;
  }
}
