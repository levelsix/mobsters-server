package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventClanProto.BeginClanRaidResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class BeginClanRaidResponseEvent extends NormalResponseEvent {

  private BeginClanRaidResponseProto beginClanRaidResponseProto;
  
  public BeginClanRaidResponseEvent(int playerId){
    super(playerId);
    eventType = EventProtocolResponse.S_BEGIN_CLAN_RAID_EVENT;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = beginClanRaidResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }

  public void setBeginClanRaidResponseProto(BeginClanRaidResponseProto beginClanRaidResponseProto) {
    this.beginClanRaidResponseProto = beginClanRaidResponseProto;
  }

}
