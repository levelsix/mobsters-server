package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventMonsterProto.SellUserMonsterRequestProto;

public class SellUserMonsterRequestEvent extends RequestEvent {

	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private SellUserMonsterRequestProto sellUserMonsterRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      sellUserMonsterRequestProto = SellUserMonsterRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = sellUserMonsterRequestProto.getSender().getMinUserProto().getUserId();
    } catch (InvalidProtocolBufferException e) {
      log.error("sell user monster request exception", e);
    }
  }

  public SellUserMonsterRequestProto getSellUserMonsterRequestProto() {
    return sellUserMonsterRequestProto;
  }
}
