package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventClanProto.GiveClanHelpResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class GiveClanHelpResponseEvent extends NormalResponseEvent {

  private GiveClanHelpResponseProto giveClanHelpResponseProto;
  
  public GiveClanHelpResponseEvent(int playerId){
    super(playerId);
    eventType = EventProtocolResponse.S_GIVE_CLAN_HELP_EVENT;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = giveClanHelpResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }

  public void setGiveClanHelpResponseProto(GiveClanHelpResponseProto giveClanHelpResponseProto) {
    this.giveClanHelpResponseProto = giveClanHelpResponseProto;
  }

}
