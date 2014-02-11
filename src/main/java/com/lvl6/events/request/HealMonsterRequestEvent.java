package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventMonsterProto.HealMonsterRequestProto;

public class HealMonsterRequestEvent extends RequestEvent {

	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private HealMonsterRequestProto healMonsterRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      healMonsterRequestProto = HealMonsterRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = healMonsterRequestProto.getSender().getMinUserProto().getUserId();
    } catch (InvalidProtocolBufferException e) {
      log.error("heal monster request exception", e);
    }
  }

  public HealMonsterRequestProto getHealMonsterRequestProto() {
    return healMonsterRequestProto;
  }
}
