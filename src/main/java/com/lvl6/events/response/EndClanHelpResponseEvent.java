package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventClanProto.EndClanHelpResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class EndClanHelpResponseEvent extends NormalResponseEvent {

  private EndClanHelpResponseProto endClanHelpResponseProto;
  
  public EndClanHelpResponseEvent(String playerId){
    super(playerId);
    eventType = EventProtocolResponse.S_END_CLAN_HELP_EVENT;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = endClanHelpResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }

  public void setEndClanHelpResponseProto(EndClanHelpResponseProto endClanHelpResponseProto) {
    this.endClanHelpResponseProto = endClanHelpResponseProto;
  }

}
