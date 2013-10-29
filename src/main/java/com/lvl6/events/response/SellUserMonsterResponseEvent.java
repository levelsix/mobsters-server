package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventMonsterProto.SellUserMonsterResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class SellUserMonsterResponseEvent extends NormalResponseEvent {

  private SellUserMonsterResponseProto sellUserMonsterResponseProto;
  
  public SellUserMonsterResponseEvent(int playerId){
    super(playerId);
    eventType = EventProtocolResponse.S_SELL_USER_MONSTER_EVENT;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = sellUserMonsterResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }

  public void setSellUserMonsterResponseProto(SellUserMonsterResponseProto sellUserMonsterResponseProto) {
    this.sellUserMonsterResponseProto = sellUserMonsterResponseProto;
  }

  public SellUserMonsterResponseProto getSellUserMonsterResponseProto() {   //because APNS required
    return sellUserMonsterResponseProto;
  }
  
}
