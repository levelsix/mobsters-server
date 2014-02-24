package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventClanProto.AttackClanRaidMonsterResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class AttackClanRaidMonsterResponseEvent extends NormalResponseEvent {

  private AttackClanRaidMonsterResponseProto attackClanRaidMonsterResponseProto;
  
  public AttackClanRaidMonsterResponseEvent(int playerId){
    super(playerId);
    eventType = EventProtocolResponse.S_ATTACK_CLAN_RAID_MONSTER_EVENT;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = attackClanRaidMonsterResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }

  public void setAttackClanRaidMonsterResponseProto(AttackClanRaidMonsterResponseProto attackClanRaidMonsterResponseProto) {
    this.attackClanRaidMonsterResponseProto = attackClanRaidMonsterResponseProto;
  }

}
