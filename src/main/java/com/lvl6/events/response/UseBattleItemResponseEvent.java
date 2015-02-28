package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventPvpProto.UseBattleItemResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class UseBattleItemResponseEvent extends NormalResponseEvent {

  private UseBattleItemResponseProto useBattleItemResponseProto;
  
  public UseBattleItemResponseEvent(String playerId){
    super(playerId);
    eventType = EventProtocolResponse.S_USE_BATTLE_ITEM_EVENT;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = useBattleItemResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }

  public void setUseBattleItemResponseProto(UseBattleItemResponseProto useBattleItemResponseProto) {
    this.useBattleItemResponseProto = useBattleItemResponseProto;
  }

}
