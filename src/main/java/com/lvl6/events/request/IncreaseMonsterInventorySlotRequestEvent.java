package com.lvl6.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventMonsterProto.IncreaseMonsterInventorySlotRequestProto;

public class IncreaseMonsterInventorySlotRequestEvent extends RequestEvent {

  private IncreaseMonsterInventorySlotRequestProto increaseMonsterInventorySlotRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      increaseMonsterInventorySlotRequestProto = IncreaseMonsterInventorySlotRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = increaseMonsterInventorySlotRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }

  public IncreaseMonsterInventorySlotRequestProto getIncreaseMonsterInventorySlotRequestProto() {
    return increaseMonsterInventorySlotRequestProto;
  }
}
