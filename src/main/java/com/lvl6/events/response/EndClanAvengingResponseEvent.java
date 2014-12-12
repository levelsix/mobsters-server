package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventClanProto.EndClanAvengingResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class EndClanAvengingResponseEvent extends NormalResponseEvent {

  private EndClanAvengingResponseProto endClanAvengingResponseProto;
  
  public EndClanAvengingResponseEvent(String playerId){
    super(playerId);
    eventType = EventProtocolResponse.S_END_CLAN_AVENGING_EVENT;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = endClanAvengingResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }

  public void setEndClanAvengingResponseProto(EndClanAvengingResponseProto endClanAvengingResponseProto) {
    this.endClanAvengingResponseProto = endClanAvengingResponseProto;
  }

}
