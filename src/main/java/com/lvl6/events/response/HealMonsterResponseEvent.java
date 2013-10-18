package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventMonsterProto.HealMonsterResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class HealMonsterResponseEvent extends NormalResponseEvent {

  private HealMonsterResponseProto healMonsterResponseProto;
  
  public HealMonsterResponseEvent(int playerId){
    super(playerId);
    eventType = EventProtocolResponse.S_HEAL_MONSTER_EVENT;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = healMonsterResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }

  public void setHealMonsterResponseProto(HealMonsterResponseProto healMonsterResponseProto) {
    this.healMonsterResponseProto = healMonsterResponseProto;
  }

}
