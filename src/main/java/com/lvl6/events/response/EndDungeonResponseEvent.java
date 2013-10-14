package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventDungeonProto.EndDungeonResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class EndDungeonResponseEvent extends NormalResponseEvent {

  private EndDungeonResponseProto endDungeonResponseProto;
  
  public EndDungeonResponseEvent(int playerId){
    super(playerId);
    eventType = EventProtocolResponse.S_END_DUNGEON_EVENT;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = endDungeonResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }

  public void setEndDungeonResponseProto(EndDungeonResponseProto endDungeonResponseProto) {
    this.endDungeonResponseProto = endDungeonResponseProto;
  }

  public EndDungeonResponseProto getEndDungeonResponseProto() {   //because APNS required
    return endDungeonResponseProto;
  }
  
}
