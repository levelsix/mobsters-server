package com.lvl6.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventMonsterProto.CombineUserMonsterPiecesRequestProto;

public class CombineUserMonsterPiecesRequestEvent extends RequestEvent {

  private CombineUserMonsterPiecesRequestProto combineMonsterPiecesRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      combineMonsterPiecesRequestProto = CombineUserMonsterPiecesRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = combineMonsterPiecesRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }

  public CombineUserMonsterPiecesRequestProto getCombineUserMonsterPiecesRequestProto() {
    return combineMonsterPiecesRequestProto;
  }
}
