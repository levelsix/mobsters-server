package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventPvpProto.EndPvpBattleResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class EndPvpBattleResponseEvent extends NormalResponseEvent {

  private EndPvpBattleResponseProto endPvpBattleResponseProto;
  
  public EndPvpBattleResponseEvent(int playerId){
    super(playerId);
    eventType = EventProtocolResponse.S_END_PVP_BATTLE_EVENT;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = endPvpBattleResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }

  public void setEndPvpBattleResponseProto(EndPvpBattleResponseProto endPvpBattleResponseProto) {
    this.endPvpBattleResponseProto = endPvpBattleResponseProto;
  }

  public EndPvpBattleResponseProto getEndPvpBattleResponseProto() {   //because APNS required
    return endPvpBattleResponseProto;
  }
  
}
