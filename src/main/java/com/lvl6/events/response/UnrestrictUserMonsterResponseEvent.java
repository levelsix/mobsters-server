package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventMonsterProto.UnrestrictUserMonsterResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class UnrestrictUserMonsterResponseEvent extends NormalResponseEvent {

  private UnrestrictUserMonsterResponseProto unrestrictUserMonsterResponseProto;
  
  public UnrestrictUserMonsterResponseEvent(String playerId){
    super(playerId);
    eventType = EventProtocolResponse.S_UNRESTRICT_USER_MONSTER_EVENT;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = unrestrictUserMonsterResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }

  public void setUnrestrictUserMonsterResponseProto(UnrestrictUserMonsterResponseProto unrestrictUserMonsterResponseProto) {
    this.unrestrictUserMonsterResponseProto = unrestrictUserMonsterResponseProto;
  }

  public UnrestrictUserMonsterResponseProto getUnrestrictUserMonsterResponseProto() {   //because APNS required
    return unrestrictUserMonsterResponseProto;
  }
  
}
