package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventMonsterProto.RemoveMonsterFromBattleTeamResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class RemoveMonsterFromBattleTeamResponseEvent extends NormalResponseEvent {

  private RemoveMonsterFromBattleTeamResponseProto removeMonsterFromBattleTeamResponseProto;
  
  public RemoveMonsterFromBattleTeamResponseEvent(int playerId){
    super(playerId);
    eventType = EventProtocolResponse.S_REMOVE_MONSTER_FROM_BATTLE_TEAM_EVENT;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = removeMonsterFromBattleTeamResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }

  public void setRemoveMonsterFromBattleTeamResponseProto(RemoveMonsterFromBattleTeamResponseProto removeMonsterFromBattleTeamResponseProto) {
    this.removeMonsterFromBattleTeamResponseProto = removeMonsterFromBattleTeamResponseProto;
  }

  public RemoveMonsterFromBattleTeamResponseProto getRemoveMonsterFromBattleTeamResponseProto() {   //because APNS required
    return removeMonsterFromBattleTeamResponseProto;
  }
  
}
