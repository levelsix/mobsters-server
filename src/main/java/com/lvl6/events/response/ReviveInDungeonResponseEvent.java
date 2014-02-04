package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventDungeonProto.ReviveInDungeonResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class ReviveInDungeonResponseEvent extends NormalResponseEvent {

  private ReviveInDungeonResponseProto reviveInDungeonResponseProto;
  
  public ReviveInDungeonResponseEvent(int playerId){
    super(playerId);
    eventType = EventProtocolResponse.S_REVIVE_IN_DUNGEON_EVENT;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = reviveInDungeonResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }

  public void setReviveInDungeonResponseProto(ReviveInDungeonResponseProto reviveInDungeonResponseProto) {
    this.reviveInDungeonResponseProto = reviveInDungeonResponseProto;
  }

  public ReviveInDungeonResponseProto getReviveInDungeonResponseProto() {   //because APNS required
    return reviveInDungeonResponseProto;
  }
  
}
