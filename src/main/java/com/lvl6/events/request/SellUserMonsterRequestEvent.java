package com.lvl6.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventMonsterProto.SellUserMonsterRequestProto;

public class SellUserMonsterRequestEvent extends RequestEvent {

  private SellUserMonsterRequestProto sellUserMonsterRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      sellUserMonsterRequestProto = SellUserMonsterRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = sellUserMonsterRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }

  public SellUserMonsterRequestProto getSellUserMonsterRequestProto() {
    return sellUserMonsterRequestProto;
  }
}
