package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventMonsterProto.UnrestrictUserMonsterRequestProto;


public class UnrestrictUserMonsterRequestEvent extends RequestEvent {
	
	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private UnrestrictUserMonsterRequestProto unrestrictUserMonsterRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      unrestrictUserMonsterRequestProto = UnrestrictUserMonsterRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = unrestrictUserMonsterRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      log.error("UnrestrictUserMonsterRequest exception", e);
    }
  }

  public UnrestrictUserMonsterRequestProto getUnrestrictUserMonsterRequestProto() {
    return unrestrictUserMonsterRequestProto;
  }
}
