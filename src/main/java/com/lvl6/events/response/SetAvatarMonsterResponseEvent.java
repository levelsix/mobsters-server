package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventUserProto.SetAvatarMonsterResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class SetAvatarMonsterResponseEvent extends NormalResponseEvent {

  private SetAvatarMonsterResponseProto setAvatarMonsterResponseProto;
  
  public SetAvatarMonsterResponseEvent(int playerId){
    super(playerId);
    eventType = EventProtocolResponse.S_SET_GAME_CENTER_ID_EVENT;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = setAvatarMonsterResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }

  public void setSetAvatarMonsterResponseProto(SetAvatarMonsterResponseProto setAvatarMonsterResponseProto) {
    this.setAvatarMonsterResponseProto = setAvatarMonsterResponseProto;
  }

}
