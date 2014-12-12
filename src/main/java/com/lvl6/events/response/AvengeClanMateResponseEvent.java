package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventClanProto.AvengeClanMateResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class AvengeClanMateResponseEvent extends NormalResponseEvent {

  private AvengeClanMateResponseProto avengeClanMateResponseProto;
  
  public AvengeClanMateResponseEvent(String playerId){
    super(playerId);
    eventType = EventProtocolResponse.S_AVENGE_CLAN_MATE_EVENT;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = avengeClanMateResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }

  public void setAvengeClanMateResponseProto(AvengeClanMateResponseProto avengeClanMateResponseProto) {
    this.avengeClanMateResponseProto = avengeClanMateResponseProto;
  }

}
