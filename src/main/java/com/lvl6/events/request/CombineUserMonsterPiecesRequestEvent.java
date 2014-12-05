package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventMonsterProto.CombineUserMonsterPiecesRequestProto;

public class CombineUserMonsterPiecesRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private CombineUserMonsterPiecesRequestProto combineMonsterPiecesRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      combineMonsterPiecesRequestProto = CombineUserMonsterPiecesRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = combineMonsterPiecesRequestProto.getSender().getUserUuid();
    } catch (InvalidProtocolBufferException e) {
      log.error("CombineUserMonsterPiecesRequest exeption", e);
    }
  }

  public CombineUserMonsterPiecesRequestProto getCombineUserMonsterPiecesRequestProto() {
    return combineMonsterPiecesRequestProto;
  }

  @Override
  public String toString()
  {
	  return "CombineUserMonsterPiecesRequestEvent [combineMonsterPiecesRequestProto="
		  + combineMonsterPiecesRequestProto
		  + "]";
  }
}
