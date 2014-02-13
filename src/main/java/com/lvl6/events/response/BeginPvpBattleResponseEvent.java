package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventPvpProto.BeginPvpBattleResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class BeginPvpBattleResponseEvent extends NormalResponseEvent {

  private BeginPvpBattleResponseProto beginPvpBattleResponseProto;
  
  public BeginPvpBattleResponseEvent(int playerId){
    super(playerId);
    eventType = EventProtocolResponse.S_BEGIN_PVP_BATTLE_EVENT;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = beginPvpBattleResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }

  public void setBeginPvpBattleResponseProto(BeginPvpBattleResponseProto beginPvpBattleResponseProto) {
    this.beginPvpBattleResponseProto = beginPvpBattleResponseProto;
  }

  public BeginPvpBattleResponseProto getBeginPvpBattleResponseProto() {   //because APNS required
    return beginPvpBattleResponseProto;
  }
  
}
