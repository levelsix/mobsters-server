package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventMonsterProto.IncreaseMonsterInventorySlotResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class IncreaseMonsterInventorySlotResponseEvent extends NormalResponseEvent {

  private IncreaseMonsterInventorySlotResponseProto increaseMonsterInventorySlotResponseProto;
  
  public IncreaseMonsterInventorySlotResponseEvent(int playerId){
    super(playerId);
    eventType = EventProtocolResponse.S_INCREASE_MONSTER_INVENTORY_SLOT_EVENT;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = increaseMonsterInventorySlotResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }

  public void setIncreaseMonsterInventorySlotResponseProto(IncreaseMonsterInventorySlotResponseProto increaseMonsterInventorySlotResponseProto) {
    this.increaseMonsterInventorySlotResponseProto = increaseMonsterInventorySlotResponseProto;
  }

  public IncreaseMonsterInventorySlotResponseProto getIncreaseMonsterInventorySlotResponseProto() {   //because APNS required
    return increaseMonsterInventorySlotResponseProto;
  }
  
}
